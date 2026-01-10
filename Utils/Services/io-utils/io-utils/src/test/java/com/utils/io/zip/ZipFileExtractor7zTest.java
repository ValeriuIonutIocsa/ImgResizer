package com.utils.io.zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.log.progress.ProgressIndicatorConsole;
import com.utils.log.progress.ProgressIndicators;
import com.utils.string.StrUtils;

class ZipFileExtractor7zTest {

	@Test
	void testWork() {

		final String zipArchiveFilePathString;
		final String dstFolderPathString;
		final int input = StrUtils.tryParsePositiveInt("11");
		if (input == 1) {
			zipArchiveFilePathString = "D:\\IVI\\Misc\\mnf\\test\\ChosenPictures.zip";
			dstFolderPathString = "D:\\IVI\\Misc\\mnf\\test";

		} else if (input == 11) {
			zipArchiveFilePathString = "D:\\IVI\\Misc\\mnf\\test\\pic1.jpg.zip";
			dstFolderPathString = "D:\\IVI\\Misc\\mnf\\test";

		} else if (input == 21) {
			zipArchiveFilePathString = "C:\\IVI\\Poli.zip";
			dstFolderPathString = "C:\\IVI";

		} else {
			throw new RuntimeException();
		}

		final boolean deleteExisting = true;

		ProgressIndicators.setInstance(ProgressIndicatorConsole.INSTANCE);

		final ZipFileExtractor7z zipFileExtractor7z = new ZipFileExtractor7z("7z", -1,
				zipArchiveFilePathString, dstFolderPathString, deleteExisting);
		zipFileExtractor7z.work();

		final boolean success = zipFileExtractor7z.isSuccess();
		Assertions.assertTrue(success);
	}
}
