

public class StopWatch {
	private final long start;

	public static StopWatch getInstance() {
		return new StopWatch();
	}

	public StopWatch() {
		start = System.currentTimeMillis();
	}

	public double elapsedTime() {
		long now = System.currentTimeMillis();
		return (now - start) / 1000.0;
	}
}