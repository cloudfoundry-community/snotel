package cf.dropsonde;

import events.LogMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import okio.ByteString;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;

/**
 * @author Mike Heath
 */
public class MetronClientBuilder {

	private final String origin;
	private SocketAddress metronAgent = new InetSocketAddress("localhost", 3457);
	private EventLoopGroup eventLoopGroup;
	private Class<? extends Channel> channelClass;

	private MetronClientBuilder(String origin) {
		this.origin = origin;
	}

	public MetronClientBuilder metronAgent(InetSocketAddress metronAgent) {
		this.metronAgent = metronAgent;
		return this;
	}

	public MetronClientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup, Class<? extends Channel> channelClass) {
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
				final Bootstrap bootstrap = new Bootstrap().handler(new ControlMessageHandler());
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
						final long nanoTimestamp = timestamp.getEpochSecond() * 1000000000 + timestamp.getNano();
						final LogMessage logMessage = new LogMessage.Builder()
								.app_id(applicationGuid)
								.message(ByteString.encodeUtf8(message))
								.source_type(sourceType)
								.source_instance(sourceInstance)
								.timestamp(nanoTimestamp)
								.message_type(type)
								.build();
						channel.writeAndFlush(logMessage);
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
