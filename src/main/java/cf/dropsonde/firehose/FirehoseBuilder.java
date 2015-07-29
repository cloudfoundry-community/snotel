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
package cf.dropsonde.firehose;

import com.squareup.wire.Message;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import rx.Observable;

import java.net.URI;

/**
 * @author Mike Heath
 */
public class FirehoseBuilder {

	private URI firehoseUrl;
	private String token;
	private String subscriptionId;
	private boolean skipTlsValidation;

	private EventLoopGroup eventLoopGroup;
	private Class<? extends Channel> channelClass;

	public static FirehoseBuilder create(String firehoseUrl, String token) {
		return new FirehoseBuilder(firehoseUrl, token);
	}

	public static FirehoseBuilder create(URI firehoseUrl, String token) {
		return new FirehoseBuilder(firehoseUrl, token);
	}

	public FirehoseBuilder(String firehoseUrl, String token) {
		this(URI.create(firehoseUrl), token);
	}

	public FirehoseBuilder(URI firhoseUrl, String token) {
		this.firehoseUrl = firhoseUrl;
		this.token = token;
	}

	public FirehoseBuilder subscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
		return this;
	}

	public FirehoseBuilder eventLoopGroup(EventLoopGroup eventLoopGroup, Class<? extends Channel> channelClass) {
		this.eventLoopGroup = eventLoopGroup;
		this.channelClass = channelClass;
		return this;
	}

	public FirehoseBuilder skipTlsValidation(boolean skipTlsValidation) {
		this.skipTlsValidation = skipTlsValidation;
		return this;
	}

	public Firehose build() {
		return new Firehose() {

			final NettyFirehoseOnSubscribe onSubscribe = null;
			volatile boolean closed = false;

			@Override
			public void close() {
				closed = true;
			}

			@Override
			public boolean isClosed() {
				return closed;
			}

			@Override
			public boolean isConnected() {
				return false; //return channel != null && channel.isActive();
			}

			@Override
			public Observable<Message> connect() {
				if (closed) {
					throw new IllegalStateException("The firehose client is closed.");
				}

				final Observable<Message> observable = Observable.create(onSubscribe);
				observable.lift()
				return observable;
			}
		};
	}

}
