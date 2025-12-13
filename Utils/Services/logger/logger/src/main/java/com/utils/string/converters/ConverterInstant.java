package com.utils.string.converters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.utils.annotations.ApiMethod;
import com.utils.string.StrUtils;

public final class ConverterInstant {

	final static String SIMPLE_INSTANT_FORMAT = "yyyy-MMM-dd HH:mm:ss";
	public final static String FULL_INSTANT_FORMAT = "yyyy-MMM-dd HH:mm:ss.SSS z";

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
			instant = stringToInstantWithFormat(instantString, FULL_INSTANT_FORMAT);
			if (instant == null) {
				instant = stringToInstantWithFormat(instantString, SIMPLE_INSTANT_FORMAT);
			}
		}
		return instant;
	}

	private static Instant tryParseInstantFromEpochMs(
			final long instantEpochMs) {

		Instant instant = null;
		try {
			instant = Instant.ofEpochMilli(instantEpochMs);

		} catch (final Throwable ignored) {
		}
		return instant;
	}

	@ApiMethod
	public static Instant stringToInstantWithFormat(
			final String instantString,
			final String dateFormat) {

		Instant instant = null;
		try {
			final DateTimeFormatter dateTimeFormatter =
					DateTimeFormatter.ofPattern(dateFormat).withLocale(Locale.US);
			final LocalDateTime localDateTime = LocalDateTime.parse(instantString, dateTimeFormatter);
			final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
			instant = zonedDateTime.toInstant();

		} catch (final Throwable ignored) {
		}
		return instant;
	}

	@ApiMethod
	public static String instantToStringWithFormat(
			final Instant instant,
			final String dateFormat) {

		String instantString = null;
		try {
			final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)
					.withLocale(Locale.US).withZone(ZoneId.systemDefault());
			instantString = dateTimeFormatter.format(instant);

		} catch (final Throwable ignored) {
		}
		return instantString;
	}
}
