package cf.dropsonde;

import events.Envelope;
import events.LogMessage;
import okio.ByteString;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.Instant;

/**
 * @author Mike Heath
 */
public class MetronClientBuilder {

	private final String origin;
	private SocketAddress metronAgent = new InetSocketAddress("localhost", 3457);

	private MetronClientBuilder(String origin) {
		this.origin = origin;
	}

	public MetronClientBuilder metronAgent(InetSocketAddress metronAgent) {
		this.metronAgent = metronAgent;
		return this;
	}

	public static MetronClientBuilder create(String origin) {
		return new MetronClientBuilder(origin);
	}

	public MetronClient build() {
		return new MetronClient() {

			private final DatagramChannel channel;
			{
				try {
					channel = DatagramChannel.open().connect(metronAgent);
				} catch (IOException e) {
					throw new DropsondeException(e);
				}
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
						final Envelope envelope = new Envelope.Builder()
								.eventType(Envelope.EventType.LogMessage)
								.timestamp(nanoTimestamp)
								.origin(origin)
								.logMessage(logMessage)
								.build();
						emitEnvelope(envelope);
					}
				};
			}

			private void emitEnvelope(Envelope envelope) {
				final ByteBuffer buffer = ByteBuffer.wrap(envelope.toByteArray());
				try {
					channel.write(buffer);
				} catch (IOException e) {
					throw new DropsondeException(e);
				}
			}

			@Override
			public void close() {
				try {
					channel.close();
				} catch (IOException e) {
					throw new DropsondeException(e);
				}
			}
		};
	}

}
