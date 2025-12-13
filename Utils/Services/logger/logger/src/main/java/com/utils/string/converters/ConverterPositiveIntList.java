package com.utils.string.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

public final class ConverterPositiveIntList {

	private ConverterPositiveIntList() {
	}

	@ApiMethod
	public static String positiveIntListToString(
			final List<Integer> positiveIntList) {

		final String positiveIntListString;
		if (positiveIntList != null) {
			positiveIntListString = StringUtils.join(positiveIntList, ',');
		} else {
			positiveIntListString = "";
		}
		return positiveIntListString;
	}

	@ApiMethod
	public static List<Integer> stringToPositiveIntList(
			final String displayName,
			final String positiveIntListString) {

		final List<Integer> positiveIntList = new ArrayList<>();
		if (StringUtils.isNotBlank(positiveIntListString)) {

			final String[] stringPartArray = StringUtils.split(positiveIntListString, ',');
			for (final String stringPart : stringPartArray) {

				final int n = StrUtils.tryParsePositiveInt(stringPart);
				if (n < 0) {
					Logger.printError("\"" + displayName + "\" string " +
							"does not only contain positive integers: " + positiveIntListString);
				} else {
					positiveIntList.add(n);
				}
			}
		}
		return positiveIntList;
	}
}
