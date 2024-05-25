package com.personal.img_resizer;

import org.apache.commons.lang3.StringUtils;

final class FactoryImageType {

	private FactoryImageType() {
	}

	static ImageType computeImageType(
			final String filePathString) {

		ImageType imageType = null;
		if (StringUtils.endsWithIgnoreCase(filePathString, ".jpg") ||
				StringUtils.endsWithIgnoreCase(filePathString, ".jpeg")) {
			imageType = ImageType.JPG;
		} else if (StringUtils.endsWithIgnoreCase(filePathString, ".png")) {
			imageType = ImageType.PNG;
		} else if (StringUtils.endsWithIgnoreCase(filePathString, ".heic")) {
			imageType = ImageType.HEIC;
		}
		return imageType;
	}
}
