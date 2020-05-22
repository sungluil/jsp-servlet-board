package org.sdf.rgb;

import java.sql.SQLException;

public class SQLError extends Exception {
	String sql;
	static final long serialVersionUID = 200000002L;

	public SQLError(String sql, SQLException e) {
		super(e.getMessage(), e.getCause());
		this.sql = sql;

	}

	public String getSQL() {
		return sql;
	}

	public String toString() {
		return getMessage() + "\n" + getSQL();
	}
}
