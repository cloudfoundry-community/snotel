package cf.dropsonde;

/**
 * @author Mike Heath
 */
public interface MetronClient extends AutoCloseable {

	LogEmitter createLogEmitter(String sourceType, String sourceInstance);

	@Override
	void close();
}
