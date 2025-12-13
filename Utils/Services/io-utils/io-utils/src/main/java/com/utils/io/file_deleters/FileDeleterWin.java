package com.utils.io.file_deleters;

import org.apache.commons.lang3.StringUtils;

import com.utils.log.Logger;

public class FileDeleterWin extends FileDeleterImpl {

	@Override
	public boolean deleteFileNoChecks(
			final String filePathString,
			final boolean verboseProgress,
			final boolean verboseError) {

		final boolean success = false;
		try {
			if (verboseProgress) {

				Logger.printProgress("deleting file:");
				Logger.printLine(filePathString);
			}

			return deleteFileWin(filePathString);

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}

		if (!success) {
			if (verboseError) {
				Logger.printError("failed to delete file:" +
						System.lineSeparator() + filePathString);
			}
		}

		return success;
	}

	public static boolean deleteFileWin(
			final String filePathString) throws Exception {

		boolean success = false;
		final String[] delCommandPartArray =
				{ "cmd", "/c", "del", "/f", "/q", filePathString };

		Logger.printProgress("executing command:");
		Logger.printLine(StringUtils.join(delCommandPartArray, ' '));

		final Process delProcess = new ProcessBuilder()
				.command(delCommandPartArray)
				.redirectOutput(ProcessBuilder.Redirect.DISCARD)
				.redirectError(ProcessBuilder.Redirect.DISCARD)
				.start();

		final int delExitCode = delProcess.waitFor();
		if (delExitCode != 0) {
			Logger.printError("failed to run del command");

		} else {
			success = true;
		}
		return success;
	}
}
