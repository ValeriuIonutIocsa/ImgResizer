package com.personal.img_resizer;

class FactoryImageType {

	private FactoryImageType() {
	}

	static ImageType computeImageType(
			final String filePathString) {

		ImageType imageType = null;
		if (filePathString.endsWith(".jpg") || filePathString.endsWith(".jpeg")) {
			imageType = ImageType.JPG;
		} else if (filePathString.endsWith(".png")) {
			imageType = ImageType.PNG;
		}
		return imageType;
	}
}
