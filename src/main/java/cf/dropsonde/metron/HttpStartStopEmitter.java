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

import events.HttpStartStop;
import events.Method;

import java.net.SocketAddress;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Mike Heath
 */
public abstract class HttpStartStopEmitter {

	public enum PeerType {
		CLIENT(events.PeerType.Client),
		SERVER(events.PeerType.Server);

		private final events.PeerType peerType;

		private PeerType(events.PeerType peerType) {
			this.peerType = peerType;
		}

		private events.PeerType getProtobufPeerType() {
			return peerType;
		}
	}

	protected final HttpStartStop.Builder builder = new HttpStartStop.Builder();

	public HttpStartStopEmitter startTimestamp(Instant timestamp) {
		builder.startTimestamp(Time.timestamp(timestamp));
		return this;
	}

	public HttpStartStopEmitter stopTimestamp(Instant timestamp) {
		builder.stopTimestamp(Time.timestamp(timestamp));
		return this;
	}

	public HttpStartStopEmitter requestId(UUID requestId) {
		builder.requestId(UUIDUtil.javaUUIDtoEventUUID(requestId));
		return this;
	}

	public HttpStartStopEmitter remoteAddress(SocketAddress address) {
		return remoteAddress(address.toString());
	}

	public HttpStartStopEmitter remoteAddress(String address) {
		builder.remoteAddress(address);
		return this;
	}

	public HttpStartStopEmitter peerType(PeerType peerType) {
		builder.peerType(peerType.getProtobufPeerType());
		return this;
	}

	public HttpStartStopEmitter httpMethod(String method) {
		try {
			builder.method(Method.valueOf(method));
		} catch (IllegalArgumentException e) {
			builder.method(null);
		}
		return this;
	}

	public HttpStartStopEmitter uri(String uri) {
		builder.uri(uri);
		return this;
	}

	public HttpStartStopEmitter userAgent(String userAgent) {
		builder.userAgent(userAgent);
		return this;
	}

	public HttpStartStopEmitter statusCode(int statusCode) {
		builder.statusCode(statusCode);
		return this;
	}

	public HttpStartStopEmitter contentLength(long contentLength) {
		builder.contentLength(contentLength);
		return this;
	}

	public HttpStartStopEmitter parentRequestId(UUID parentRequestId) {
		builder.parentRequestId(UUIDUtil.javaUUIDtoEventUUID(parentRequestId));
		return this;
	}

	public HttpStartStopEmitter applicationGuid(UUID applicationGuid) {
		builder.applicationId(UUIDUtil.javaUUIDtoEventUUID(applicationGuid));
		return this;
	}

	public HttpStartStopEmitter applicationIndex(int index) {
		builder.instanceIndex(index);
		return this;
	}

	public HttpStartStopEmitter applicationPrivateInstanceId(String privateInstanceId) {
		builder.instanceId(privateInstanceId);
		return this;
	}

	public abstract void emit();
}
