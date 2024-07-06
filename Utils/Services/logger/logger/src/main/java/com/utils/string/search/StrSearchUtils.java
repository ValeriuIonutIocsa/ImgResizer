package com.utils.string.search;

import com.utils.annotations.ApiMethod;

public final class StrSearchUtils {

	private StrSearchUtils() {
	}

	@ApiMethod
	public static int countOccurrencesOfSubstringInString(
			final String string,
			final String substring) {

		int count = 0;
		final int substringLength = substring.length();
		int index = 0;
		while ((index = string.indexOf(substring, index)) != -1) {

			index += substringLength;
			count++;
		}
		return count;
	}
}
