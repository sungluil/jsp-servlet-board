package org.sdf.rgb;

public class Result {
	RecordSet rs;
	Exception except;
	public boolean isError = false;

	public Result() {

	}

	public Result(RecordSet rs) {
		setRecordSet(rs);
	}

	public Result(Exception e) {
		setException(e);
	}

	public void setRecordSet(RecordSet rs) {
		this.rs = rs;
	}

	public void setException(Exception e) {
		this.except = e;
		isError = true;
	}

	public RecordSet getRecordSet() {
		if (rs == null || isError)
			return new RecordSet();
		return rs;
	}

	public Exception getException() {
		return except;
	}
}
