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

import java.time.Instant;
import java.util.UUID;

/**
 * @author Mike Heath
 */
public interface LogEmitter {

	void emit(Instant timestamp, String applicationGuid, String message);

	void emitError(Instant timestamp, String applicationGuid, String message);

	default void emit(String applicationGuid, String message) {
		emit(Instant.now(), applicationGuid, message);
	}

	default void emitError(String applicationGuid, String message) {
		emitError(Instant.now(), applicationGuid, message);
	}

	default void emit(UUID applicationGuid, String message) {
		emit(applicationGuid.toString(), message);
	}

	default void emitError(UUID applicationGuid, String message) {
		emitError(applicationGuid.toString(), message);
	}

	default ApplicationLogEmitter createApplicationLogEmitter(final String applicationGuid) {
		return new ApplicationLogEmitter() {
			@Override
			public void emit(String message) {
				LogEmitter.this.emit(applicationGuid, message);
			}

			@Override
			public void emitError(String message) {
				LogEmitter.this.emitError(applicationGuid, message);
			}
		};
	}

	default ApplicationLogEmitter createApplicationLogEmitter(final UUID applicationGuid) {
		return createApplicationLogEmitter(applicationGuid.toString());
	}



}
