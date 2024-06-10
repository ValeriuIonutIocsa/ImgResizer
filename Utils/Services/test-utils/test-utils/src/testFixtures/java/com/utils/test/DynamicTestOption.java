package com.utils.test;

public record DynamicTestOption<ObjectT>(
		int index,
		String description,
		ObjectT value) {
}
