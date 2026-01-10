package com.utils.io.zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.log.progress.ProgressIndicatorConsole;
import com.utils.log.progress.ProgressIndicators;
import com.utils.string.StrUtils;

class ZipFileCreator7zTest {

	@Test
	void testWork() {

		final String srcFilePathString;
		final String zipArchiveFilePathString;
		final int input = StrUtils.tryParsePositiveInt("21");
		if (input == 1) {
			srcFilePathString = "D:\\IVI\\Misc\\mnf\\test\\ChosenPictures";
			zipArchiveFilePathString = "D:\\IVI\\Misc\\mnf\\test\\ChosenPictures.zip";

		} else if (input == 11) {
			srcFilePathString = "D:\\IVI\\Misc\\mnf\\test\\pic1.jpg";
			zipArchiveFilePathString = "D:\\IVI\\Misc\\mnf\\test\\pic1.jpg.zip";

		} else if (input == 21) {
			srcFilePathString = "C:\\IVI\\Poli";
			zipArchiveFilePathString = "C:\\IVI\\Poli.zip";

		} else {
			throw new RuntimeException();
		}

		final boolean deleteExisting = true;

		ProgressIndicators.setInstance(ProgressIndicatorConsole.INSTANCE);

		final ZipFileCreator7z zipFileCreator7z = new ZipFileCreator7z("7z", -1,
				srcFilePathString, zipArchiveFilePathString, deleteExisting);
		zipFileCreator7z.work();

		final boolean success = zipFileCreator7z.isSuccess();
		Assertions.assertTrue(success);
	}
}
