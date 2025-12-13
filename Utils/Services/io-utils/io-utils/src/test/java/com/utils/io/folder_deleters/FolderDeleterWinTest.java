package com.utils.io.folder_deleters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FolderDeleterWinTest {

	@Test
	void testDeleteFolder() {

		final String folderPathString = "D:\\IVI\\Tmp\\io-utils\\test_folder";

		final boolean success = new FolderDeleterWin().deleteFolder(folderPathString, true, true);
		Assertions.assertTrue(success);
	}

	@Test
	void testCleanFolder() {

		final String folderPathString = "D:\\IVI\\Tmp\\io-utils\\test_folder";

		final boolean success = new FolderDeleterWin().cleanFolder(folderPathString, true, true);
		Assertions.assertTrue(success);
	}
}
