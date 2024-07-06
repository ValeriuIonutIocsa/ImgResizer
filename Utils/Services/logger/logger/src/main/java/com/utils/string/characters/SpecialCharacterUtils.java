package com.utils.string.characters;

import org.apache.commons.lang3.StringUtils;

import com.utils.annotations.ApiMethod;

public final class SpecialCharacterUtils {

	public static final char MU = 'μ';

	public static final String HTML_MU = "&#181;";

	private SpecialCharacterUtils() {
	}

	@ApiMethod
	public static String createHtmlString(
			final String string) {

		return StringUtils.replace(string,
				String.valueOf(SpecialCharacterUtils.MU), SpecialCharacterUtils.HTML_MU);
	}
}
