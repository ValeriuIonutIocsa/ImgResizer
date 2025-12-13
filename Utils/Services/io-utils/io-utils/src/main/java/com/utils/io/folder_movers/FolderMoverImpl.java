package com.utils.io.folder_movers;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.utils.io.folder_deleters.FactoryFolderDeleter;
import com.utils.log.Logger;

class FolderMoverImpl implements FolderMover {

	FolderMoverImpl() {
	}

	@Override
	public boolean moveFolder(
			final String srcFolderPathString,
			final String dstFolderPathString,
			final boolean deleteDstDirectoryBeforeMoving,
			final boolean verboseProgress,
			final boolean verboseError) {

		boolean success = false;
		try {
			final boolean keepGoing;
			if (deleteDstDirectoryBeforeMoving) {
				keepGoing = FactoryFolderDeleter.getInstance()
						.deleteFolder(dstFolderPathString, verboseProgress, verboseError);
			} else {
				keepGoing = true;
			}
			if (keepGoing) {

				if (verboseProgress) {

					Logger.printProgress("moving folder:");
					Logger.printLine(srcFolderPathString);
					Logger.printLine("to:");
					Logger.printLine(dstFolderPathString);
				}

				final File srcFolder = new File(srcFolderPathString);
				final File dstFolder = new File(dstFolderPathString);
				FileUtils.moveDirectory(srcFolder, dstFolder);

				success = true;
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}

		if (!success) {
			if (verboseError) {
				Logger.printError("failed to move folder " +
						System.lineSeparator() + srcFolderPathString +
						System.lineSeparator() + "to:" +
						System.lineSeparator() + dstFolderPathString);
			}
		}

		return success;
	}
}
