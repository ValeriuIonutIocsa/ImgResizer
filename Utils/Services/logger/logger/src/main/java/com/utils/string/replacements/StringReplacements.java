package com.utils.string.replacements;

import com.utils.annotations.ApiMethod;

public interface StringReplacements {

	@ApiMethod
	void addReplacement(
			String searchString,
			String replacementString);

	@ApiMethod
	String performReplacements(
			String str);
}
