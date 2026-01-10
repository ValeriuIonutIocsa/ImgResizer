package com.utils.io.seven_zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.io.PathUtils;
import com.utils.log.progress.ProgressIndicatorConsole;
import com.utils.log.progress.ProgressIndicators;

class SevenZipFileExtractor7zTest {

	@Test
	void testWork() {

		final String archiveFilePathString = "C:\\IVI\\Poli.7z";
		final String outputParentFolderPathString =
				PathUtils.computeParentPath(archiveFilePathString);

		ProgressIndicators.setInstance(ProgressIndicatorConsole.INSTANCE);

		final boolean deleteExisting = false;

		final SevenZipFileExtractor7z sevenZipFileExtractor7z =
				new SevenZipFileExtractor7z("7z", 1, archiveFilePathString,
						outputParentFolderPathString, deleteExisting);
		sevenZipFileExtractor7z.work();

		final boolean success = sevenZipFileExtractor7z.isSuccess();
		Assertions.assertTrue(success);
	}
}
