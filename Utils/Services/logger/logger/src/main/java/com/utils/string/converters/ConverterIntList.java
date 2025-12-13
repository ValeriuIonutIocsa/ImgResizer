package com.utils.string.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

public final class ConverterIntList {

	private ConverterIntList() {
	}

	@ApiMethod
	public static String intListToString(
			final List<Integer> intList) {

		final String intListString;
		if (intList != null) {
			intListString = StringUtils.join(intList, ',');
		} else {
			intListString = "";
		}
		return intListString;
	}

	@ApiMethod
	public static List<Integer> stringToIntList(
			final String displayName,
			final String intListString) {

		final List<Integer> intList = new ArrayList<>();
		if (StringUtils.isNotBlank(intListString)) {

			final String[] stringPartArray = StringUtils.split(intListString, ',');
			for (final String stringPart : stringPartArray) {

				final Integer n = StrUtils.tryParseInt(stringPart);
				if (n == null) {
					Logger.printError("\"" + displayName + "\" string " +
							"does not only contain integers: " + intListString);
				} else {
					intList.add(n);
				}
			}
		}
		return intList;
	}
}
