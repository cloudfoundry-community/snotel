package cf.dropsonde.metron;

import java.time.Instant;

/**
 * @author Mike Heath
 */
class Time {
	static long timestamp() {
		return timestamp(Instant.now());
	}

	static long timestamp(Instant timestamp) {
		return timestamp.getEpochSecond() * 1_000_000_000 + timestamp.getNano();
	}
}
