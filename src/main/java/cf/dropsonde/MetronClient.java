package cf.dropsonde;

/**
 * @author Mike Heath
 */
public interface MetronClient extends AutoCloseable {

	LogEmitter createLogEmitter(String sourceType, String sourceInstance);

	void emitValueMetric(String name, double value, String unit);

	@Override
	void close();
}
