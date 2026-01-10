package com.utils.concurrency;

import java.util.List;

public interface ConcurrencyUtils {

	int DEFAULT_THREAD_COUNT = 12;

	void executeMultiThreadedTask(
			List<Runnable> runnableList);
}
