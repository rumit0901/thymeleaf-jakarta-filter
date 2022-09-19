package com.bah.util;

public enum ERequestMethod {

	GET("GET"),
	POST("POST");
	
	private final String method;
	
	ERequestMethod(String method) {
		this.method = method;
	}
}
