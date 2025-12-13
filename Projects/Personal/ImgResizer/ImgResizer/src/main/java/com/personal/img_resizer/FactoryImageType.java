package com.personal.img_resizer;

import org.apache.commons.lang3.Strings;

final class FactoryImageType {

	private FactoryImageType() {
	}

	static ImageType computeImageType(
			final String filePathString) {

		ImageType imageType = null;
		if (Strings.CI.endsWith(filePathString, ".jpg") ||
				Strings.CI.endsWith(filePathString, ".jpeg")) {
			imageType = ImageType.JPG;
		} else if (Strings.CI.endsWith(filePathString, ".heic")) {
			imageType = ImageType.HEIC;
		} else if (Strings.CI.endsWith(filePathString, ".png")) {
			imageType = ImageType.PNG;
		} else if (Strings.CI.endsWith(filePathString, ".webp")) {
			imageType = ImageType.WEBP;
		}
		return imageType;
	}
}
