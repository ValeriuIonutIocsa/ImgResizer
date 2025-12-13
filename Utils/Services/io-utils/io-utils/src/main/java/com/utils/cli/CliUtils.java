package com.utils.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public final class CliUtils {

	private CliUtils() {
	}

	@ApiMethod
	public static void fillCliArgsByNameMap(
			final String[] args,
			final Map<String, String> cliArgsByNameMap) {

		for (final String arg : args) {

			if (arg.startsWith("--")) {

				final int indexOf = arg.indexOf('=');
				if (indexOf >= 3) {

					final String argName = arg.substring(2, indexOf);
					final String argValue = arg.substring(indexOf + 1);
					cliArgsByNameMap.put(argName, argValue);
				}
			}
		}
	}

	@ApiMethod
	public static boolean parseBooleanArg(
			final Map<String, String> cliArgsByNameMap,
			final String argName) {

		final String valueString = cliArgsByNameMap.get(argName);
		return Boolean.parseBoolean(valueString);
	}

	@ApiMethod
	public static String[] commandStringToArray(
			final String commandString) {

		String[] commandPartArray = null;
		if (commandString != null && !commandString.trim().isEmpty()) {

			final int normal = 0;
			final int inQuote = 1;
			final int inDoubleQuote = 2;
			int state = normal;
			final StringTokenizer stringTokenizer =
					new StringTokenizer(commandString, "\"' ", true);
			final List<String> commandPartList = new ArrayList<>();
			StringBuilder sbCurrent = new StringBuilder();
			boolean lastTokenHasBeenQuoted = false;
			while (stringTokenizer.hasMoreTokens()) {

				final String nextTok = stringTokenizer.nextToken();
				switch (state) {

					case inQuote:
						if ("'".equals(nextTok)) {
							lastTokenHasBeenQuoted = true;
							state = normal;
						} else {
							sbCurrent.append(nextTok);
						}
						break;

					case inDoubleQuote:
						if ("\"".equals(nextTok)) {
							lastTokenHasBeenQuoted = true;
							state = normal;
						} else {
							sbCurrent.append(nextTok);
						}
						break;

					default:
						switch (nextTok) {
							case "'":
								state = inQuote;
								break;
							case "\"":
								state = inDoubleQuote;
								break;
							case " ":
								if (lastTokenHasBeenQuoted || !sbCurrent.isEmpty()) {
									commandPartList.add(sbCurrent.toString());
									sbCurrent = new StringBuilder();
								}
								break;
							default:
								sbCurrent.append(nextTok);
								break;
						}
						lastTokenHasBeenQuoted = false;
						break;
				}
			}

			if (lastTokenHasBeenQuoted || !sbCurrent.isEmpty()) {
				commandPartList.add(sbCurrent.toString());
			}

			if (state == inQuote || state == inDoubleQuote) {
				Logger.printError("unbalanced quotes in " + commandString);

			} else {
				final String[] args = new String[commandPartList.size()];
				commandPartArray = commandPartList.toArray(args);
			}
		}
		return commandPartArray;
	}
}
