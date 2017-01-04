package cf.dropsonde.metron;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.cloudfoundry.dropsonde.events.CounterEvent;
import org.cloudfoundry.dropsonde.events.Error;
import org.cloudfoundry.dropsonde.events.LogMessage;
import org.cloudfoundry.dropsonde.events.ValueMetric;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import okio.ByteString;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;

/**
 * @author Mike Heath
 */
public class MetronClientBuilder {

	public static final String DEFAULT_METRON_AGENT_HOST = "localhost";
	public static final int DEFAULT_METRON_AGENT_PORT = 3457;

	private final String origin;
	private SocketAddress metronAgent = new InetSocketAddress(DEFAULT_METRON_AGENT_HOST, DEFAULT_METRON_AGENT_PORT);
	private EventLoopGroup eventLoopGroup;
	private Class<? extends DatagramChannel> channelClass;

	private MetronClientBuilder(String origin) {
		this.origin = origin;
	}

	public MetronClientBuilder metronAgent(InetSocketAddress metronAgent) {
		this.metronAgent = metronAgent;
		return this;
	}

	public MetronClientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup, Class<? extends DatagramChannel> channelClass) {
		this.eventLoopGroup = eventLoopGroup;
		this.channelClass = channelClass;
		return this;
	}

	public static MetronClientBuilder create(String origin) {
		return new MetronClientBuilder(origin);
	}

	public MetronClient build() {
		return new MetronClient() {

			private final EventLoopGroup eventLoopGroup;
			private final Channel channel;

			{
				// we are not handling ControlMessages / HeartBeats anymore so a simple
				// ChannelInboundHandlerAdapter to forward to next handler.
				final Bootstrap bootstrap = new Bootstrap().handler(new ChannelInboundHandlerAdapter());
				if (MetronClientBuilder.this.eventLoopGroup == null) {
					eventLoopGroup = new NioEventLoopGroup(1);
					bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class);
				} else {
					eventLoopGroup = null;
					bootstrap.group(MetronClientBuilder.this.eventLoopGroup).channel(channelClass);
				}
				channel = bootstrap.connect(metronAgent).syncUninterruptibly().channel();
				channel.pipeline().addLast(new EnvelopeEncoder());
				channel.pipeline().addLast(new EventWrapperEncoder(origin));
			}

			@Override
			public LogEmitter createLogEmitter(final String sourceType, final String sourceInstance) {
				return new LogEmitter() {
					@Override
					public void emit(Instant timestamp, String applicationGuid, String message) {
						emitLog(timestamp, applicationGuid, message, LogMessage.MessageType.OUT);
					}

					@Override
					public void emitError(Instant timestamp, String applicationGuid, String message) {
						emitLog(timestamp, applicationGuid, message, LogMessage.MessageType.ERR);
					}

					private void emitLog(Instant timestamp, String applicationGuid, String message, LogMessage.MessageType type) {
						final LogMessage logMessage = new LogMessage.Builder()
								.app_id(applicationGuid)
								.message(ByteString.encodeUtf8(message))
								.source_type(sourceType)
								.source_instance(sourceInstance)
								.timestamp(Time.timestamp(timestamp))
								.message_type(type)
								.build();
						channel.writeAndFlush(logMessage);
					}
				};
			}

			@Override
			public void emitCounterEvent(String name, long delta) {
				channel.writeAndFlush(new CounterEvent.Builder().name(name).delta(delta).build());
			}

			@Override
			public void emitError(String source, int code, String message) {
				channel.writeAndFlush(new Error(source, code, message));
			}

			@Override
			public void emitValueMetric(String name, double value, String unit) {
				channel.writeAndFlush(new ValueMetric(name, value, unit));
			}

			@Override
			public HttpStartStopEmitter createHttpStartStopEmitter() {
				return new HttpStartStopEmitter() {
					@Override
					public void emit() {
						channel.writeAndFlush(builder.build());
					}
				};
			}

			@Override
			public void close() {
				channel.close();
				if (eventLoopGroup != null) {
					eventLoopGroup.shutdownGracefully();
				}
			}
		};
	}

}
