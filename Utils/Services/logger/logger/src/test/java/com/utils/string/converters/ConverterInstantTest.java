package com.utils.string.converters;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.utils.log.Logger;
import com.utils.test.DynamicTestOption;
import com.utils.test.DynamicTestOptions;
import com.utils.test.DynamicTestSuite;
import com.utils.test.TestInputUtils;

class ConverterInstantTest {

	private static String timeZoneId;

	@BeforeAll
	static void beforeAll() {

		timeZoneId = configureTimeZoneId();
	}

	private static String configureTimeZoneId() {

		final String timeZoneId;
		final int input = TestInputUtils.parseTestInputNumber("0");
		if (input == 1) {
			timeZoneId = "UTC";
		} else if (input == 2) {
			timeZoneId = "IST";
		} else {
			timeZoneId = null;
		}
		return timeZoneId;
	}

	@TestFactory
	List<DynamicTest> testStringToInstant() {

		if (timeZoneId != null) {
			TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
		}

		final DynamicTestOptions<Instant> instantDynamicTestOptions =
				new DynamicTestOptions<>("instant string", 1);

		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(1, "current instant",
				Instant.now()));

		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(11, "full instant 1",
				ConverterInstant.stringToInstant("2024-Feb-18 18:23:19.674 UTC")));
		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(12, "full instant 2",
				ConverterInstant.stringToInstant("2024-Feb-18 18:23:19.674 EET")));
		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(13, "full instant 3",
				ConverterInstant.stringToInstant("2024-Apr-22 18:32:00.833 Asia/Kolkata")));

		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(21, "simple instant",
				ConverterInstant.stringToInstant("2020-Feb-28 18:07:38")));

		final DynamicTestSuite dynamicTestSuite = new DynamicTestSuite(DynamicTestSuite.Mode.ALL,
				() -> testStringToInstantCommon(instantDynamicTestOptions), instantDynamicTestOptions);

		return dynamicTestSuite.createDynamicTestList();
	}

	private static void testStringToInstantCommon(
			final DynamicTestOptions<Instant> instantDynamicTestOptions) {

		final Instant instant = instantDynamicTestOptions.computeValue();

		final String instantString = ConverterInstant.instantToString(instant);
		Logger.printLine("instantString: " + instantString);

		final Instant secondInstant = ConverterInstant.stringToInstant(instantString);
		final String secondInstantString = ConverterInstant.instantToString(secondInstant);

		Assertions.assertEquals(instantString, secondInstantString);
	}
}
