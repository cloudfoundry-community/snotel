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
package cf.dropsonde.metron;

import com.squareup.wire.Wire;
import control.ControlMessage;
import events.Heartbeat;
import events.UUID;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCounted;

/**
 * @author Mike Heath
 */
class ControlMessageHandler extends ChannelDuplexHandler {

	private final Wire wire = new Wire();

	private long errorCount = 0;
	private long messagesReceived = 0;
	private long messagesSent = 0;

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		try {
			messagesReceived++;
			final ByteBufInputStream input = new ByteBufInputStream(((DatagramPacket) message).content());
			final ControlMessage controlMessage = wire.parseFrom(input, ControlMessage.class);

			final control.UUID identifier = controlMessage.identifier;
			switch (controlMessage.controlType) {
				case HeartbeatRequest:
					final Heartbeat heartbeat = new Heartbeat.Builder()
							.controlMessageIdentifier(new UUID(identifier.low, identifier.high))
							.receivedCount(messagesReceived)
							.errorCount(errorCount)
							.sentCount(messagesSent)
							.build();
					context.writeAndFlush(heartbeat);
					break;
				default:
					throw new IllegalStateException("Don't know how to handle messages of type: " + controlMessage.controlType);
			}
		} finally {
			if (message instanceof ReferenceCounted) {
				((ReferenceCounted)message).release();
			}
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
 		errorCount++;
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		super.flush(ctx);
		messagesSent++;
	}
}
