package com.utils.io;

import org.junit.jupiter.api.Test;

import com.utils.test.TestInputUtils;

class IoUtilsTest {

	@Test
	void testOpenFileWithDefaultApp() {

		final int input = TestInputUtils.parseTestInputNumber("1");
		final String filePathString;
		if (input == 1) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\file.txt";
		} else if (input == 2) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder";

		} else if (input == 11) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\file with spaces.txt";
		} else if (input == 12) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder with spaces";

		} else if (input == 21) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder with spaces\\file.txt";
		} else if (input == 22) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder with spaces\\file in folder with spaces.txt";

		} else {
			throw new RuntimeException();
		}

		IoUtils.openFileWithDefaultApp(filePathString);
	}

	@Test
	void testSelectFileInExplorer() {

		final int input = TestInputUtils.parseTestInputNumber("1");
		final String filePathString;
		if (input == 1) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\file.txt";
		} else if (input == 2) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder";

		} else if (input == 11) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\file with spaces.txt";
		} else if (input == 12) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder with spaces";

		} else if (input == 21) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder with spaces\\file.txt";
		} else if (input == 22) {
			filePathString = "D:\\IVI\\Misc\\mnf\\test\\folder with spaces\\file in folder with spaces.txt";

		} else {
			throw new RuntimeException();
		}

		final String appFolderPathString = PathUtils.createRootPath();
		IoUtils.selectFileInExplorer(filePathString, appFolderPathString);
	}
}
