package org.sdf.lang;

public class ConfigError extends Exception {
	String code;

	public ConfigError(String code, Exception e) {
		super(e);
		this.code = code;
	}

	public ConfigError(String code, String msg, Exception e) {
		super(msg, e);
		this.code = code;
	}

	public ConfigError(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public String getErrorCode() {
		return code;
	}
}
