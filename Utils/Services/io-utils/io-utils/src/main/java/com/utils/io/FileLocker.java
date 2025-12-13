package com.utils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.utils.annotations.ApiMethod;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

public class FileLocker {

	private final String lockerName;
	private final String lockFilePathString;

	private RandomAccessFile randomAccessFile;
	private FileChannel fileChannel;
	private FileLock fileLock;

	public FileLocker(
			final String lockerName,
			final String lockFilePathString) {

		this.lockerName = lockerName;
		this.lockFilePathString = lockFilePathString;
	}

	@ApiMethod
	public boolean lock() {

		boolean success = true;
		try {
			Logger.printProgress("acquiring " + lockerName + " file lock");
			try {
				final String text;
				if (lockerName != null) {
					text = "locked by " + lockerName;
				} else {
					text = "locked";
				}
				try (BufferedWriter bufferedWriter = WriterUtils.openBufferedWriter(lockFilePathString)) {
					bufferedWriter.write(text);
				}

			} catch (final Throwable ignored) {
				success = false;
			}
			if (success) {

				final File lockFile = new File(lockFilePathString);
				randomAccessFile = new RandomAccessFile(lockFile, "rw");
				fileChannel = randomAccessFile.getChannel();
				fileLock = fileChannel.lock();
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to acquire the " + lockerName + " file lock");
			Logger.printThrowable(throwable);
		}
		return success;
	}

	@ApiMethod
	public void unlock() {

		try {
			Logger.printProgress("releasing the " + lockerName + " file lock");

			if (fileLock != null) {

				fileLock.release();
				if (fileChannel == null) {
					Logger.printError("file channel is null");

				} else {
					fileChannel.close();
					if (randomAccessFile == null) {
						Logger.printError("random access file is null");

					} else {
						randomAccessFile.close();
						FactoryFileDeleter.getInstance().deleteFile(lockFilePathString, false, true);
					}
				}
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to release the " + lockerName + " file lock");
			Logger.printThrowable(throwable);
		}
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
