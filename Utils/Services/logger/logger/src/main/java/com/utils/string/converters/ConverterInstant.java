package com.utils.string.converters;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.utils.annotations.ApiMethod;
import com.utils.string.StrUtils;

public final class ConverterInstant {

	private final static String SIMPLE_DATE_FORMAT = "yyyy-MMM-dd HH:mm:ss";
	public final static String FULL_DATE_FORMAT = "yyyy-MMM-dd HH:mm:ss.SSS z";

	private ConverterInstant() {
	}

	@ApiMethod
	public static String instantToString(
			final Instant instant) {

		final long epochMilli = instant.toEpochMilli();
		return String.valueOf(epochMilli);
	}

	@ApiMethod
	public static Instant stringToInstant(
			final String instantString) {

		Instant instant;
		final long instantEpochMs = StrUtils.tryParsePositiveLong(instantString);
		if (instantEpochMs >= 0) {
			instant = tryParseInstantFromEpochMs(instantEpochMs);

		} else {
			instant = tryParseInstant(instantString, FULL_DATE_FORMAT);
			if (instant == null) {
				instant = tryParseInstant(instantString, SIMPLE_DATE_FORMAT);
			}
		}
		return instant;
	}

	private static Instant tryParseInstantFromEpochMs(
			final long instantEpochMs) {

		Instant instant = null;
		try {
			instant = Instant.ofEpochMilli(instantEpochMs);

		} catch (final Exception ignored) {
		}
		return instant;
	}

	private static Instant tryParseInstant(
			final String instantString,
			final String dateFormat) {

		Instant instant = null;
		try {
			final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)
					.withLocale(Locale.US).withZone(ZoneId.systemDefault());
			instant = Instant.from(dateTimeFormatter.parse(instantString));

		} catch (final Exception ignored) {
		}
		return instant;
	}
}
