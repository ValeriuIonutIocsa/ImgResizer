package com.utils.string.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.utils.annotations.ApiMethod;

public final class ConverterDate {

	public final static String DATE_FORMAT = "yyyy-MMM-dd";

	private ConverterDate() {
	}

	@ApiMethod
	public static LocalDate stringToDateWithFormat(
			final String dateString,
			final String dateFormat) {

		LocalDate localDate = null;
		try {
			final DateTimeFormatter dateTimeFormatter =
					DateTimeFormatter.ofPattern(dateFormat).withLocale(Locale.US);
			localDate = LocalDate.parse(dateString, dateTimeFormatter);

		} catch (final Throwable ignored) {
		}
		return localDate;
	}

	@ApiMethod
	public static String dateToStringWithFormat(
			final LocalDate date,
			final String dateFormat) {

		String dateString = null;
		try {
			final DateTimeFormatter dateTimeFormatter =
					DateTimeFormatter.ofPattern(dateFormat).withLocale(Locale.US);
			dateString = dateTimeFormatter.format(date);

		} catch (final Throwable ignored) {
		}
		return dateString;
	}
}
