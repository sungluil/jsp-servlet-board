package org.sdf.lang;

public class BizError extends Exception {
	String code;

	public BizError(String code, Exception e) {
		super(e);
		this.code = code;
	}

	public BizError(String code, String msg, Exception e) {
		super(msg, e);
		this.code = code;
	}

	public BizError(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public String getErrorCode() {
		return code;
	}
}
