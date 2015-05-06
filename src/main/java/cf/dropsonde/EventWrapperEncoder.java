/*
 *   Copyright (c) 2015 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.dropsonde;

import com.squareup.wire.Message;
import events.ContainerMetric;
import events.CounterEvent;
import events.Envelope;
import events.Heartbeat;
import events.HttpStart;
import events.HttpStartStop;
import events.HttpStop;
import events.LogMessage;
import events.ValueMetric;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.time.Instant;
import java.util.List;

/**
 * @author Mike Heath
 */
class EventWrapperEncoder extends MessageToMessageEncoder<Message> {

	public EventWrapperEncoder(String origin) {
		this.origin = origin;
	}

	private final String origin;

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
		if (message instanceof Envelope) {
			list.add(message);
		} else {
			final Instant timestamp = Instant.now();
			final long nanoTimestamp = timestamp.getEpochSecond() * 1000000000 + timestamp.getNano();
			final Envelope.Builder builder = new Envelope.Builder().origin(origin).timestamp(nanoTimestamp);

			if (message instanceof ContainerMetric) {
				builder.eventType(Envelope.EventType.ContainerMetric).containerMetric((ContainerMetric) message);
			} else if (message instanceof CounterEvent) {
				builder.eventType(Envelope.EventType.CounterEvent).counterEvent((CounterEvent) message);
			} else if (message instanceof events.Error) {
				builder.eventType(Envelope.EventType.Error).error((events.Error) message);
			} else if (message instanceof Heartbeat) {
				builder.eventType(Envelope.EventType.Heartbeat).heartbeat((Heartbeat) message);
			} else if (message instanceof HttpStart) {
				builder.eventType(Envelope.EventType.HttpStart).httpStart((HttpStart) message);
			} else if (message instanceof HttpStartStop) {
				builder.eventType(Envelope.EventType.HttpStartStop).httpStartStop((HttpStartStop) message);
			} else if (message instanceof HttpStop) {
				builder.eventType(Envelope.EventType.HttpStop).httpStop((HttpStop) message);
			} else if (message instanceof LogMessage) {
				builder.eventType(Envelope.EventType.LogMessage).logMessage((LogMessage) message);
			} else if (message instanceof ValueMetric) {
				builder.eventType(Envelope.EventType.ValueMetric).valueMetric((ValueMetric) message);
			} else {
				throw new IllegalArgumentException("Unable to encode message of type: " + message.getClass().getName());
			}
			list.add(builder.build());
		}
	}
}
