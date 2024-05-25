package com.utils.io.seven_zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.log.progress.ProgressIndicatorConsole;
import com.utils.log.progress.ProgressIndicators;

class SevenZipFileCreator7zTest {

	@Test
	void testWork() {

		final String inputFilePathString = "C:\\IVI\\Poli";
		final String archiveFilePathString = inputFilePathString + ".7z";

		final boolean deleteExisting = true;

		ProgressIndicators.setInstance(ProgressIndicatorConsole.INSTANCE);

		final SevenZipFileCreator7z sevenZipFileCreator7z =
				new SevenZipFileCreator7z("7z", 1, inputFilePathString, archiveFilePathString, deleteExisting);
		sevenZipFileCreator7z.work();

		final boolean success = sevenZipFileCreator7z.isSuccess();
		Assertions.assertTrue(success);
	}
}
