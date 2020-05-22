package org.sdf.rgb;

import java.sql.Connection;
import java.sql.SQLException;

import org.sdf.log.ILoggerWrapper;
import org.sdf.log.Log;
import org.sdf.slim.config.GlobalResource;
import org.sdf.util.StringUtil;

public class JPreparedStatement {
	String query = null;
	String querySource = null;
	java.sql.PreparedStatement pstmt = null;
	StringBuffer log;
	org.sdf.util.StopWatch sw = new org.sdf.util.StopWatch();

	boolean bBatch = false;
	int level = 3;
	static ILoggerWrapper logger;
	SQLException e;

	ILoggerWrapper llogger;

	public static void setLogDisabled() {
		logger = null;
	}

	public void setLoggerWrapper(ILoggerWrapper logger) {
		llogger = logger;
	}
	
	public static void setStaticLoggerWrapper(ILoggerWrapper l) {
		if(logger !=null) logger.close();
		logger = l;
	}

	boolean logging = true;
	boolean sql_logging = true;

	public void setLogging(boolean b) {
		logging = b;
	}

	public void setSqlLogging(boolean b) {
		sql_logging = b;
	}

	public static void setLogEnabled(boolean b) {
		if (b) {
			logger = Log.sql;
		} else {
			logger = null;
		}
	}

	public void setDepth(int depth) {
		this.level = depth;
	}

	StringBuffer vlog = new StringBuffer();

	public JPreparedStatement(Connection con, String sql) throws Exception {
		this(con, sql, false);
	}

	public JPreparedStatement(Connection con, String sql, boolean bBatch)
			throws Exception {
		sql = org.sdf.util.StringUtil.replace(sql, "#{db_owner}", GlobalResource.getInstance().getDbOwner());
		pstmt = con.prepareStatement(sql);
		this.querySource = sql;
		this.query = new String(querySource);
		this.bBatch = bBatch;
		initialize();
	}

	private void initialize() {
		log = new StringBuffer();

	}

	int from = 0;

	private void log(String value) {
		try {
			int b = query.indexOf("?", from);
			String tmp = query.substring(0, b);
			tmp += value;
			from = tmp.length();
			tmp += query.substring(b + 1);
			query = tmp;

		} catch (Exception e) {
		}
		;
	}

	public void setInt(int index, int value) throws SQLException {
		pstmt.setInt(index, value);
		log(String.valueOf(value));
	}

	public void setLong(int index, long value) throws SQLException {
		pstmt.setLong(index, value);
		log(String.valueOf(value));
	}

	public void setDouble(int index, double value) throws SQLException {
		pstmt.setDouble(index, value);
		log(String.valueOf(value));
	}

	public void setFloat(int index, float value) throws SQLException {
		pstmt.setFloat(index, value);
		log(String.valueOf(value));
	}

	private void _errorlog() {
		if (logger != null)
			query = org.sdf.util.StringUtil.replace(query, "\n", "\n  ");
		query = "  " + query;
		if (e != null) {
			info(log.toString() + "[DML_FAIL:elapsed=" + sw.getElapsed()
					+ "]\n" + " -> " + e + query);
		} else {
			info(log.toString() + "[DML_SUCCESS:elapsed=" + sw.getElapsed()
					+ "]\n" + query);
		}
	}

	public void setBoolean(int index, boolean value) throws SQLException {
		try {
			pstmt.setBoolean(index, value);
			log(String.valueOf(value));
		} catch (SQLException e) {
			_errorlog();
			throw e;
		}
	}

	public void setString(int index, String value) throws SQLException {
		try {
			// null ë³???
			if(StringUtil.invalid(value)) {
				value = null;
			}

			pstmt.setString(index, value);
			if (value == null) {
				log("null");
			} else {
				StringBuffer buf = new StringBuffer();
				buf.append('\'').append(value).append('\'');
				log(buf.toString());
			}

		} catch (SQLException e) {
			_errorlog();
			throw e;
		}
	}

	public void setClob(int index, String value) throws SQLException {
		try {
			pstmt.setString(index, value);
			StringBuffer buf = new StringBuffer();
			buf.append('\'').append(value).append('\'');
			log(buf.toString());
		} catch (SQLException e) {
			_errorlog();
			throw e;
		}
	}

	public boolean execute() throws SQLException {
		from = 0;
		boolean result = false;
		try {
			result = pstmt.execute();

		} catch (SQLException e) {
			this.e = e;
			throw e;
		}
		return result;
	}

	public int executeUpdate() throws SQLException {
		from = 0;
		vlog = new StringBuffer();
		int result = 0;
		try {
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			this.e = e;
			err(e);
			throw e;
		} finally {
			MethodInfo m = new MethodInfo(level);
			String info = "[JPreparedStatement]" + m.getInfo();

			if (logging && logger != null)
				query = org.sdf.util.StringUtil.replace(query, "\n", "\n  ");
			query = "  " + query;
			if (e != null) {
				info(log.toString() + info + "[DML_FAIL:elapsed="
						+ sw.getElapsed() + "]\n" + " -> " + e + query);
			} else {
				if (sql_logging)
					info(log.toString() + info + "[DML_SUCCESS:elapsed="
							+ sw.getElapsed() + ",cnt:" + result + "]\n"
							+ query);
				else
					info(log.toString() + info + "[DML_SUCCESS:elapsed="
							+ sw.getElapsed() + ",cnt:" + result + "]\n");
			}

			this.query = new String(querySource);

			this.e = null;
			// log = new StringBuffer();
		}

		return result;
	}

	protected void err(Exception e) {
		if (llogger != null) {
			llogger.err(e);
		} else if (logger != null) {
			logger.err(e);
		}
	}

	protected void info(String msg) {
		if (llogger != null) {
			if (logging)
				llogger.info(msg);
		} else if (logger != null) {
			if (logging)
				logger.info(msg);
		}
	}

	public void addBatch() throws SQLException {
		vlog = new StringBuffer();
		pstmt.addBatch();
	}

	public int[] executeBatch() throws SQLException {
		vlog = new StringBuffer();
		int[] result = null;
		try {
			result = pstmt.executeBatch();
		} catch (SQLException e) {
			this.e = e;
			throw e;
		}
		return result;
	}

	public void close() {
		try {
			pstmt.close();
		} catch (Exception e) {
		} finally {

		}
	}

	public java.sql.PreparedStatement getPreparedStatement() {
		return pstmt;
	}

	public void printValues() {
		info(vlog.toString());
	}

	public String getQuery() {
		return query;
	}
}
