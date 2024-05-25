package com.utils.string;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.utils.log.Logger;
import com.utils.string.characters.SpecialCharacterUtils;
import com.utils.string.converters.ConverterInstant;
import com.utils.test.DynamicTestOption;
import com.utils.test.DynamicTestOptions;
import com.utils.test.DynamicTestSuite;
import com.utils.test.TestInputUtils;

class StrUtilsTest {

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
	List<DynamicTest> testUnsignedIntToPaddedBinaryString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1", () -> testUnsignedIntToPaddedBinaryStringCommon(
					0x20, 8, "00100000")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2", () -> testUnsignedIntToPaddedBinaryStringCommon(
					0xa0, 8, "10100000")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3", () -> testUnsignedIntToPaddedBinaryStringCommon(
					0b1100110011001100, 32, "00000000000000001100110011001100")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4", () -> testUnsignedIntToPaddedBinaryStringCommon(
					0b11001100110011001100110011001100, 32, "11001100110011001100110011001100")));
		}
		return dynamicTestList;
	}

	private static void testUnsignedIntToPaddedBinaryStringCommon(
			final int n,
			final int size,
			final String expectedStr) {

		final String str = StrUtils.unsignedIntToPaddedBinaryString(n, size);
		Assertions.assertEquals(expectedStr, str);
	}

	@TestFactory
	List<DynamicTest> testDoubleToString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (no decimal digits)",
					() -> testDoubleToStringCommon(20.0, 0, 5, "20")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (one decimal digit zero)",
					() -> testDoubleToStringCommon(20.0, 1, 5, "20.0")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (one decimal digit not zero)",
					() -> testDoubleToStringCommon(12.1, 0, 5, "12.1")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4 (three decimal digits)",
					() -> testDoubleToStringCommon(1.324, 0, 5, "1.324")));
		}
		if (testCaseList.contains(5)) {
			dynamicTestList.add(DynamicTest.dynamicTest("5 (seven decimal digits)",
					() -> testDoubleToStringCommon(127.753_216_500, 0, 10, "127.7532165")));
		}
		if (testCaseList.contains(6)) {
			dynamicTestList.add(DynamicTest.dynamicTest("6 (zero decimal digits)",
					() -> testDoubleToStringCommon(251.537, 0, 0, "252")));
		}
		return dynamicTestList;
	}

	private static void testDoubleToStringCommon(
			final double d,
			final int mandatoryDigitCount,
			final int optionalDigitCount,
			final String expectedDoubleString) {

		final String doubleString = StrUtils.doubleToString(d, mandatoryDigitCount, optionalDigitCount, false);
		Assertions.assertEquals(expectedDoubleString, doubleString);
	}

	@TestFactory
	List<DynamicTest> testDoubleToFloatString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4, 5);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (no decimal digits)",
					() -> testDoubleToFloatStringCommon(20.0, 0, 5, "20f")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (one decimal digit zero)",
					() -> testDoubleToFloatStringCommon(20.0, 1, 5, "20.0f")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (one decimal digit not zero)",
					() -> testDoubleToFloatStringCommon(12.1, 0, 5, "12.1f")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4 (three decimal digits)",
					() -> testDoubleToFloatStringCommon(1.324, 0, 5, "1.324f")));
		}
		if (testCaseList.contains(5)) {
			dynamicTestList.add(DynamicTest.dynamicTest("5 (seven decimal digits)",
					() -> testDoubleToFloatStringCommon(127.753_216_500, 0, 10, "127.7532165f")));
		}
		return dynamicTestList;
	}

	private static void testDoubleToFloatStringCommon(
			final double d,
			final int mandatoryDigitCount,
			final int optionalDigitCount,
			final String expectedFloatString) {

		final String floatString = StrUtils.doubleToFloatString(d, mandatoryDigitCount, optionalDigitCount, false);
		Assertions.assertEquals(expectedFloatString, floatString);
	}

	@TestFactory
	List<DynamicTest> testDurationToString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (short time)",
					() -> testDurationToStringCommon(754_321, "12m 34.321s")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (long time)",
					() -> testDurationToStringCommon(12_754_321, "3h 32m 34.321s")));
		}
		return dynamicTestList;
	}

	private static void testDurationToStringCommon(
			final int millis,
			final String expectedString) {

		final Duration duration = Duration.ofMillis(millis);
		final String durationString = StrUtils.durationToString(duration);
		Assertions.assertEquals(expectedString, durationString);
	}

	@TestFactory
	List<DynamicTest> testNanoTimeToString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (short time)",
					() -> testNanoTimeToStringCommon(75_321,
							"75" + SpecialCharacterUtils.MU + "s 321ns")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (long time)",
					() -> testNanoTimeToStringCommon(1_234_754_321,
							"1s 234ms 754" + SpecialCharacterUtils.MU + "s 321ns")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (negative time)",
					() -> testNanoTimeToStringCommon(-123, "")));
		}
		return dynamicTestList;
	}

	private static void testNanoTimeToStringCommon(
			final long nanoTime,
			final String expectedString) {

		final String nanoTimeString = StrUtils.nanoTimeToString(nanoTime);
		Assertions.assertEquals(expectedString, nanoTimeString);
	}

	@TestFactory
	List<DynamicTest> testRemovePrefix() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (regular)",
					() -> testRemovePrefixCommon("abc_RCSVMP", "abc_", "RCSVMP")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (not starting with)",
					() -> testRemovePrefixCommon("abc_RCSVMP", "bcd", "abc_RCSVMP")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (empty string)",
					() -> testRemovePrefixCommon("", "abc", "")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4 (all null)",
					() -> testRemovePrefixCommon(null, null, null)));
		}
		return dynamicTestList;
	}

	private static void testRemovePrefixCommon(
			final String str,
			final String prefix,
			final String expectedResultStr) {

		final String resultStr = StrUtils.removePrefix(str, prefix);
		Assertions.assertEquals(expectedResultStr, resultStr);
	}

	@TestFactory
	List<DynamicTest> testRemoveSuffix() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (regular)",
					() -> testRemoveSuffixCommon("RCSVMP_nvm", "_nvm", "RCSVMP")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (not ending with)",
					() -> testRemoveSuffixCommon("RCSVMP_nvm", "_abc", "RCSVMP_nvm")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (empty string)",
					() -> testRemoveSuffixCommon("", "abc", "")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4 (all null)",
					() -> testRemoveSuffixCommon(null, null, null)));
		}
		return dynamicTestList;
	}

	private static void testRemoveSuffixCommon(
			final String str,
			final String suffix,
			final String expectedResultStr) {

		final String resultStr = StrUtils.removeSuffix(str, suffix);
		Assertions.assertEquals(expectedResultStr, resultStr);
	}

	@Test
	void testCreateDateTimeString() {

		if (timeZoneId != null) {
			TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
		}

		final String dateTimeString = StrUtils.createDateTimeString();
		Assertions.assertFalse(StringUtils.isBlank(dateTimeString));

		Logger.printNewLine();
		Logger.printLine(dateTimeString);
	}

	@TestFactory
	List<DynamicTest> testCreateDisplayDateTimeString() {

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
				ConverterInstant.stringToInstant("2024-Apr-22 18:32:00.833 IST")));
		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(14, "full instant 4",
				ConverterInstant.stringToInstant("2024-Apr-22 18:32:00.833 Asia/Kolkata")));

		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(21, "simple instant",
				ConverterInstant.stringToInstant("2020-Feb-28 18:07:38")));

		final DynamicTestSuite dynamicTestSuite = new DynamicTestSuite(DynamicTestSuite.Mode.ALL,
				() -> testCreateDisplayDateTimeStringCommon(instantDynamicTestOptions), instantDynamicTestOptions);

		return dynamicTestSuite.createDynamicTestList();
	}

	private static void testCreateDisplayDateTimeStringCommon(
			final DynamicTestOptions<Instant> instantDynamicTestOptions) {

		final Instant instant = instantDynamicTestOptions.computeValue();
		final String instantString = StrUtils.createDisplayDateTimeString(instant);
		Assertions.assertFalse(StringUtils.isBlank(instantString));

		Logger.printLine("instantString: " + instantString);
	}
}
