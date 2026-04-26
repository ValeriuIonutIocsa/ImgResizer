package com.personal.img_resizer;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import com.utils.io.PathUtils;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandler;
import com.utils.io.processes.ReadBytesHandlerLinesCollect;
import com.utils.io.processes.ReadBytesHandlerLinesPrint;
import com.utils.log.Logger;

class MetadataImporter {

	private final String outputFilePathString;
	private final String metadataXmlPathString;
	private final boolean verbose;

	private boolean success;

	MetadataImporter(
			final String outputFilePathString,
			final String metadataXmlPathString,
			final boolean verbose) {

		this.outputFilePathString = outputFilePathString;
		this.metadataXmlPathString = metadataXmlPathString;
		this.verbose = verbose;
	}

	void work() {

		try {
			Logger.printProgress("importing metadata from file:");
			Logger.printLine(metadataXmlPathString);

			final String[] commandPartArray = { "exiftool", "-overwrite_original",
					"-tagsfromfile", metadataXmlPathString, outputFilePathString };
			if (verbose) {

				Logger.printProgress("executing command:");
				Logger.printLine(StringUtils.join(commandPartArray, ' '));
			}

			final String folderPathString = PathUtils.computeParentPath(outputFilePathString);
			final Process process = new ProcessBuilder()
					.command(commandPartArray)
					.directory(new File(folderPathString))
					.redirectErrorStream(true)
					.start();

			final ReadBytesHandler readBytesHandler;
			if (verbose) {
				readBytesHandler = new ReadBytesHandlerLinesPrint();
			} else {
				readBytesHandler = new ReadBytesHandlerLinesCollect();
			}
			final InputStream inputStream = process.getInputStream();
			final InputStreamReaderThread inputStreamReaderThread =
					new InputStreamReaderThread("import metadata", inputStream,
							Charset.defaultCharset(), readBytesHandler);
			inputStreamReaderThread.start();

			final int exitCode = process.waitFor();
			success = exitCode == 0;

			inputStreamReaderThread.join();

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);

		} finally {
			if (!success) {
				Logger.printError("failed to import metadata from file:" +
						System.lineSeparator() + metadataXmlPathString);
			}
		}
	}

	public boolean isSuccess() {
		return success;
	}
}
