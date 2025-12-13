package com.utils.log;

import java.io.PrintStream;

import com.utils.io.StreamUtils;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.io.ro_flag_clearers.FactoryReadOnlyFlagClearer;
import com.utils.obj.RunnableWithException;

public class TestWithConsoleOutputFile {

	private final RunnableWithException testRunnable;
	private final String consoleOutputFilePathString;

	public TestWithConsoleOutputFile(
			final RunnableWithException testRunnable,
			final String consoleOutputFilePathString) {

		this.testRunnable = testRunnable;
		this.consoleOutputFilePathString = consoleOutputFilePathString;
	}

	public void work() throws Exception {

		final MessageConsumer oldMessageConsumer = Logger.getMessageConsumer();

		FactoryFolderCreator.getInstance()
				.createParentDirectories(consoleOutputFilePathString, false, true);
		FactoryReadOnlyFlagClearer.getInstance()
				.clearReadOnlyFlagFile(consoleOutputFilePathString, false, true);

		try (PrintStream printStream = StreamUtils.openPrintStream(consoleOutputFilePathString)) {

			Logger.setMessageConsumer(
					new MessageConsumerConsoleAndPrintStream(printStream, printStream));

			testRunnable.run();

		} finally {
			Logger.setMessageConsumer(oldMessageConsumer);
		}
	}
}
