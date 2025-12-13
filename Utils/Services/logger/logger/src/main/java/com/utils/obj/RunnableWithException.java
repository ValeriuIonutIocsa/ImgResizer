package com.utils.obj;

@FunctionalInterface
public interface RunnableWithException {

	void run() throws Exception;
}
