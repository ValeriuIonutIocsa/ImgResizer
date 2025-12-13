package com.utils.io.folder_copiers;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.utils.annotations.ApiMethod;
import com.utils.io.folder_deleters.FactoryFolderDeleter;
import com.utils.log.Logger;

class FolderCopierImpl implements FolderCopier {

	FolderCopierImpl() {
	}

	@ApiMethod
	@Override
	public boolean copyFolder(
			final String srcFolderPathString,
			final String dstFolderPathString,
			final boolean deleteDstDirectoryBeforeCopying,
			final boolean verboseProgress,
			final boolean verboseError) {

		boolean success = false;
		try {
			final boolean keepGoing;
			if (deleteDstDirectoryBeforeCopying) {
				keepGoing = FactoryFolderDeleter.getInstance()
						.deleteFolder(dstFolderPathString, verboseProgress, verboseError);
			} else {
				keepGoing = true;
			}
			if (keepGoing) {

				if (verboseProgress) {

					Logger.printProgress("copying folder:");
					Logger.printLine(srcFolderPathString);
					Logger.printLine("to:");
					Logger.printLine(dstFolderPathString);
				}

				final File srcFolder = new File(srcFolderPathString);
				final File dstFolder = new File(dstFolderPathString);
				FileUtils.copyDirectory(srcFolder, dstFolder);

				success = true;
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}

		if (!success) {
			if (verboseError) {
				Logger.printError("failed to copy folder " +
						System.lineSeparator() + srcFolderPathString +
						System.lineSeparator() + "to:" +
						System.lineSeparator() + dstFolderPathString);
			}
		}

		return success;
	}
}
