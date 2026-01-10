package com.utils.io.zip;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.utils.io.IoUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_deleters.FactoryFolderDeleter;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.seven_zip.ReadBytesHandler7z;
import com.utils.log.Logger;
import com.utils.log.progress.ProgressIndicators;
import com.utils.string.StrUtils;

public class ZipFileExtractor7z {

	private final String sevenZipExecutablePathString;
	private final int threadCount;
	private final String archiveFilePathString;
	private final String outputParentFolderPathString;
	private final boolean deleteExisting;

	private boolean success;

	public ZipFileExtractor7z(
			final String sevenZipExecutablePathString,
			final int threadCount,
			final String archiveFilePathString,
			final String outputParentFolderPathString,
			final boolean deleteExisting) {

		this.sevenZipExecutablePathString = sevenZipExecutablePathString;
		this.threadCount = threadCount;
		this.archiveFilePathString = archiveFilePathString;
		this.outputParentFolderPathString = outputParentFolderPathString;
		this.deleteExisting = deleteExisting;
	}

	public void work() {

		success = false;
		try {
			if (!IoUtils.fileExists(archiveFilePathString)) {
				Logger.printError("ZIP archive does not exist:" +
						System.lineSeparator() + archiveFilePathString);

			} else {
				final boolean keepGoing;
				if (deleteExisting) {

					final String zipArchiveName = PathUtils.computeFileName(archiveFilePathString);
					final String zipArchiveNameWoExt = StrUtils.removeSuffixIgnoreCase(zipArchiveName, ".zip");
					if (StringUtils.isNotBlank(zipArchiveNameWoExt)) {

						final String extractedFilePathString =
								PathUtils.computePath(outputParentFolderPathString, zipArchiveNameWoExt);
						if (IoUtils.directoryExists(extractedFilePathString)) {
							keepGoing = FactoryFolderDeleter.getInstance()
									.cleanFolder(extractedFilePathString, true, true);
						} else if (IoUtils.regularFileExists(extractedFilePathString)) {
							keepGoing = FactoryFileDeleter.getInstance()
									.deleteFile(extractedFilePathString, true, true);
						} else {
							keepGoing = true;
						}

					} else {
						keepGoing = true;
					}

				} else {
					keepGoing = true;
				}

				if (keepGoing) {

					ProgressIndicators.getInstance().update(0.0);
					Logger.printProgress("extracting ZIP archive");

					final List<String> commandPartList = new ArrayList<>();
					commandPartList.add(sevenZipExecutablePathString);
					commandPartList.add("x");
					commandPartList.add("-bsp1");
					if (threadCount >= 1) {
						commandPartList.add("-mmt=" + threadCount);
					}
					commandPartList.add("-y");
					commandPartList.add(archiveFilePathString);
					commandPartList.add("-o" + outputParentFolderPathString);

					Logger.printProgress("executing command:");
					Logger.printLine(StringUtils.join(commandPartList, ' '));

					final String archiveFolderPathString =
							PathUtils.computeParentPath(archiveFilePathString);
					final File archiveFolder = new File(archiveFolderPathString);

					final Process process = new ProcessBuilder()
							.command(commandPartList)
							.directory(archiveFolder)
							.redirectErrorStream(true)
							.start();

					final InputStreamReaderThread inputStreamReaderThread = new InputStreamReaderThread(
							"extract zip archive input stream reader", process.getInputStream(),
							StandardCharsets.UTF_8, new ReadBytesHandler7z());
					inputStreamReaderThread.start();

					final int exitCode = process.waitFor();
					inputStreamReaderThread.join();

					success = exitCode == 0;
				}
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}

		if (!success) {
			Logger.printError("failed to extract ZIP archive:" +
					System.lineSeparator() + archiveFilePathString);
		}
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public boolean isSuccess() {
		return success;
	}
}
