package cf.dropsonde;

import java.time.Instant;

/**
 * @author Mike Heath
 */
class Time {
	static long timestamp() {
		final Instant now = Instant.now();
		return now.getEpochSecond() * 1_000_000_000 + now.getNano();
	}
}
