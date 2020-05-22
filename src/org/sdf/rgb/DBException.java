package org.sdf.rgb;

import java.util.*;

import org.sdf.lang.*;

public class DBException extends Exception {
	Exception e;

	Data param;

	public DBException() {
		super();
	}

	public DBException(String code) {
		super();
		set("errcode", code);
	}

	public DBException(String code, Exception e) {
		this(code);
		this.e = e;
	}

	public Exception getException() {
		return e;
	}

	public void set(String key, Object val) {
		if (param == null)
			param = new Data();
		param.put(key, val);
	}

	public String get(String key) {
		return param.get(key);
	}

	public String getCode() {
		return get("errcode");
	}
}