package com.personal.img_resizer;

import java.io.File;

import com.utils.io.PathUtils;
import com.utils.log.Logger;

class MetadataImporter {

	private final String outputFilePathString;
	private final String metadataXmlPathString;

	private boolean success;

	MetadataImporter(
			final String outputFilePathString,
			final String metadataXmlPathString) {

		this.outputFilePathString = outputFilePathString;
		this.metadataXmlPathString = metadataXmlPathString;
	}

	void work() {

		try {
			Logger.printProgress("importing metadata from file:");
			Logger.printLine(metadataXmlPathString);

			final String folderPathString = PathUtils.computeParentPath(outputFilePathString);
			final Process process = new ProcessBuilder()
					.command("exiftool", "-overwrite_original",
							"-tagsfromfile", metadataXmlPathString, outputFilePathString)
					.directory(new File(folderPathString))
					.redirectInput(ProcessBuilder.Redirect.INHERIT)
					.redirectError(ProcessBuilder.Redirect.INHERIT)
					.start();
			final int exitCode = process.waitFor();
			success = exitCode == 0;

		} catch (final Exception exc) {
			Logger.printError("failed to import metadata from file:" +
					System.lineSeparator() + metadataXmlPathString);
			Logger.printException(exc);
		}
	}

	public boolean isSuccess() {
		return success;
	}
}
