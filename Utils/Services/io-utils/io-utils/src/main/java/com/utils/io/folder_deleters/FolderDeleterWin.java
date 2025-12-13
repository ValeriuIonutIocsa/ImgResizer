package com.utils.io.folder_deleters;

import org.apache.commons.lang3.StringUtils;

import com.utils.log.Logger;

public class FolderDeleterWin extends FolderDeleterImpl {

	@Override
	public boolean deleteFolderNoChecks(
			final String folderPathString,
			final boolean verboseProgress,
			final boolean verboseError) {

		boolean success = false;
		try {
			if (verboseProgress) {

				Logger.printProgress("deleting folder:");
				Logger.printLine(folderPathString);
			}

			success = deleteFolderWin(folderPathString);

		} catch (final Throwable throwable) {
			if (verboseError) {
				Logger.printError("failed to delete folder:" +
						System.lineSeparator() + folderPathString);
			}
			Logger.printThrowable(throwable);
		}
		return success;
	}

	private static boolean deleteFolderWin(
			final String folderPathString) throws Exception {

		final String emptyFolderPathString = folderPathString + "_empty_folder";
		return executeCommand(new String[] { "cmd", "/c", "mkdir", emptyFolderPathString }, 0) &&
				executeCommand(new String[] { "cmd", "/c",
						"robocopy", "/purge", emptyFolderPathString, folderPathString }, 7) &&
				executeCommand(new String[] { "cmd", "/c", "rmdir", emptyFolderPathString }, 0) &&
				executeCommand(new String[] { "cmd", "/c", "rmdir", folderPathString }, 0);
	}

	@Override
	public boolean cleanFolderNoChecks(
			final String folderPathString,
			final boolean verboseProgress,
			final boolean verboseError) {

		boolean success = false;
		try {
			if (verboseProgress) {

				Logger.printProgress("cleaning folder:");
				Logger.printLine(folderPathString);
			}

			success = cleanFolderWin(folderPathString);

		} catch (final Throwable throwable) {
			if (verboseError) {
				Logger.printError("failed to clean folder:" +
						System.lineSeparator() + folderPathString);
			}
			Logger.printThrowable(throwable);
		}
		return success;
	}

	private static boolean cleanFolderWin(
			final String folderPathString) throws Exception {

		final String emptyFolderPathString = folderPathString + "_empty_folder";
		return executeCommand(new String[] { "cmd", "/c", "mkdir", emptyFolderPathString }, 0) &&
				executeCommand(new String[] { "cmd", "/c",
						"robocopy", "/purge", emptyFolderPathString, folderPathString }, 7) &&
				executeCommand(new String[] { "cmd", "/c", "rmdir", emptyFolderPathString }, 0);
	}

	private static boolean executeCommand(
			final String[] commandPartArray,
			final int maxExitCode) throws Exception {

		boolean success = false;

		Logger.printProgress("executing command:");
		Logger.printLine(StringUtils.join(commandPartArray, ' '));

		final Process rmDirProcess = new ProcessBuilder()
				.command(commandPartArray)
				.redirectOutput(ProcessBuilder.Redirect.DISCARD)
				.redirectError(ProcessBuilder.Redirect.DISCARD)
				.start();

		final int rmDirExitCode = rmDirProcess.waitFor();
		if (rmDirExitCode < 0 || rmDirExitCode > maxExitCode) {
			Logger.printError("failed to run command:" +
					System.lineSeparator() + StringUtils.join(commandPartArray, ' '));
		} else {
			success = true;
		}

		return success;
	}
}
