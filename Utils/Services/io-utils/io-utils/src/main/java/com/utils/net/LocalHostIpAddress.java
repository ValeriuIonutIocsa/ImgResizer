package com.utils.net;

import com.utils.annotations.ApiMethod;
import com.utils.string.StrUtils;

public class LocalHostIpAddress {

	private final String ipAddress;

	LocalHostIpAddress(
			final String ipAddress) {

		this.ipAddress = ipAddress;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	@ApiMethod
	public String getIpAddress() {
		return ipAddress;
	}
}
