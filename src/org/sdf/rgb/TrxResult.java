package org.sdf.rgb;

public class TrxResult {
	int count;
	Exception except;
	public boolean isError = false;
	public String key;

	public TrxResult(Exception e) {
		setException(e);
	}

	public TrxResult(int cnt) {
		setCount(cnt);
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setCount(int cnt) {
		this.count = cnt;
	}

	public void setException(Exception e) {
		this.except = e;
		isError = true;
	}

	public int getCount() {
		return count;
	}

	public String getKey() {
		return key;
	}

	public Exception getException() {
		return except;
	}
}
