/*****************************************************************
 * Class : RecordSet.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.rgb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdf.log.ILoggerWrapper;
import org.sdf.secure.DBSecure;
import org.sdf.slim.config.GlobalResource;

public class ResultSetWrapper extends RecordSet {
	/*
	 * ColumnInfo columnInfo; String query; String orgQuery;
	 */
	private boolean executed = false;

	public ResultSetWrapper() {
		executed = true;

	}

	/**
	 * ???±ì??
	 * 
	 * @param query
	 *            Query SQL
	 */
	public ResultSetWrapper(String query) {
		setQuery(query);
	}

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 *            Connection
	 * @param values
	 *            Binding ë³???
	 * @param cond
	 *            Condition
	 * @throws Exception
	 */
	public void executeQuery(Connection con, String[] values, Condition cond)
			throws Exception {
		this.con = con;
		if (executed)
			return;

		level = 4;

		String[] vals = (cond == null) ? new String[0] : cond.getValues();
		int len = 0;
		int val_len = 0;
		if (vals != null)
			len = vals.length;
		if (values != null)
			val_len = values.length;

		String[] value = new String[val_len + len];
		for (int i = 0; i < val_len; i++)
			value[i] = values[i];

		for (int i = 0; i < len; i++)
			value[i + val_len] = vals[i];

		executeQuery(con, value);
	}

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 *            Connection
	 * @param cond
	 *            Condition
	 * @param values
	 *            Binding ë³???
	 * @throws Exception
	 */
	public void executeQuery(Connection con, Condition cond, String[] values)
			throws Exception {
		this.con = con;
		if (executed)
			return;

		level = 4;

		String[] vals = (cond == null) ? new String[0] : cond.getValues();
		int len = 0;
		int val_len = 0;
		if (vals != null)
			len = vals.length;
		if (values != null)
			val_len = values.length;

		String[] value = new String[val_len + len];
		for (int i = 0; i < len; i++)
			value[i] = vals[i];

		for (int i = 0; i < val_len; i++)
			value[i + len] = values[i];

		executeQuery(con, value);
	}

	Connection con;

	public void executeQuery(Connection con, String[] values, Condition[] conds)
			throws Exception {
		this.con = con;
		if (executed)
			return;

		level = 4;

		int sum_len = 0;
		int val_len = 0;
		String[][] vals = new String[conds.length][];
		for (int i = 0; i < conds.length; i++) {
			vals[i] = conds[i].getValues();
			vals[i] = vals[i] == null ? new String[0] : vals[i];
			sum_len += vals[i].length;
		}

		if (values != null)
			val_len = values.length;

		String[] value = new String[val_len + sum_len];
		for (int i = 0; i < val_len; i++)
			value[i] = values[i];
		int cnt = 0;
		for (int j = 0; j < vals.length; j++) {
			for (int i = 0; i < vals[j].length; i++) {
				value[(cnt++) + val_len] = vals[j][i];
			}
		}
		executeQuery(con, value);
	}

	String sqllog;

	protected boolean isLogging() {
		return logger != null || logWrapperMode;
	}

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void executeQuery(Connection con) throws Exception {
		String[] vals = new String[0];
		executeQuery(con, vals);
	}

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 * @param values
	 *            Binding ë³???
	 * @throws Exception
	 */
	public void executeQuery(Connection con, String value) throws Exception {
		String[] vals = new String[1];
		vals[0] = value;
		executeQuery(con, vals);
	}

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 * @param values
	 *            Binding ë³???
	 * @throws Exception
	 */
	public void executeQuery(Connection con, String[] values) throws Exception {
		this.con = con;
		StringBuffer log = new StringBuffer();
		org.sdf.util.StopWatch sw = new org.sdf.util.StopWatch();
		try {
			if (executed)
				return;
			MethodInfo m = new MethodInfo(level);
			String info = m.getInfo();
			if (isLogging()) {
				if (isDoc) {
					log.append("[ResultSetWrapper]").append(info).append(" ");
				} else {
					log.append("[ResultSetWrapper]").append(info).append(" ");
				}
			}

			int sidx = 0;
			StringBuffer buf = new StringBuffer();

			if (isLogging()) {
				if (values == null || values.length == 0)
					sqllog = query;
				else {
					for (int i = 0; i < values.length; i++) {
						int idx = query.indexOf('?', sidx);
						if (idx != -1) {
							buf.append(query.substring(sidx, idx)).append(" '")
									.append(values[i]).append("' ");
							sidx = idx + 1;
						}
					}
					buf.append(query.substring(sidx));

					sqllog = buf.toString();
				}
			}
			if (isLogging())
				sqllog = org.sdf.util.StringUtil.replace(sqllog, "\n", "\n  ");
			sqllog = "  " + sqllog;

			_executeQuery(con, values);

			this.executed = true;

			if (isLogging()) {
				if (!isDoc) {
					log.append("[Success:elapsed=").append(sw.getElapsed())
							.append("]\n");
				}
			}
			if (isLogging())
				log.append(sqllog);

			if (logWrapperMode) {
				if (logwrap != null)
					logwrap.info(log.toString());
			} else {
				if (logger != null)
					logger.println(log.toString());
			}
		} catch (SQLException e) {

			if (isLogging()) {
				log.append("[SQL_FAIL:elapsed=").append(sw.getElapsed())
						.append("]\n -> " + e).append(sqllog);
				if (logWrapperMode) {
					SQLError ex = new SQLError(log.toString(), e);
					throw ex;
				} else {
					logger.println(log.toString());
				}
			}
		} catch (Exception e) {
			if (isLogging())
				log.append("[LOG_FAIL:elapsed=").append(sw.getElapsed())
						.append("] -> " + e);
			throw e;
		}
	}

	/**
	 * ì»¬ë?¼ì?? ???? Label?? ì§???????
	 * 
	 * @param names
	 * @param labels
	 */
	public void setColumnLabels(String[] names, String[] labels) {
		columnInfo.setColumnLabels(names, labels);
	}

	/**
	 * ì§????? RecordSet?? row ?°ì?´í?°ë? ì»¬ë?¼ì?? ???? Label?? ì§???????
	 * 
	 * @param rs
	 * @param name
	 * @param label
	 */
	public void setColumnLabels(RecordSet rs, String name, String label) {
		columnInfo.setColumnLabels(rs, name, label);
	}

	/**
	 * ì§????? RecordSet?? row ?°ì?´í?°ë? ì»¬ë?¼ì?? ???? Label?? ì§???????
	 * 
	 * @param rs
	 * @param name
	 * @param label
	 */
	public void setColumnLabels(RecordSet rs, int name, int label) {
		columnInfo.setColumnLabels(rs, name, label);
	}

	ResultSet rs = null;
	PreparedStatement pstmt = null;

	private void _executeQuery(Connection con, String[] values)
			throws Exception {
		try {

			pstmt = con.prepareStatement(query);

			int i = 1;
			for (; values != null && i <= values.length; i++) {
				pstmt.setString(i, values[i - 1]);
			}

			rs = pstmt.executeQuery();

			int count = makeColumns(rs);

		} finally {
		}
	}

	public static void setLogger(java.io.PrintStream out) {
		logger = out;
	}

	static ILoggerWrapper logwrap;
	static boolean logWrapperMode = false;

	public static void setLoggerWrapper(ILoggerWrapper l) {
		if(logwrap !=null) logwrap.close();
		logwrap = l;
		logWrapperMode = true;
	}

	protected void setQuery(String query, boolean rownum) {
		setQuery(query);
	}

	protected void setQuery(String query) {
		query = org.sdf.util.StringUtil.replace(query, "#{db_owner}", GlobalResource.getInstance().getDbOwner());
		this.orgQuery = new String(query);

		this.query = query;		
	}

	protected int makeColumns(ResultSet rs) {
		if (columnInfo == null)
			columnInfo = new ColumnInfo();

		return columnInfo.make(rs);
	}

	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}

	/*
	 * private int getTotalCount( Connection con, String[] values ) throws
	 * Exception{ PreparedStatement pstmt = null; ResultSet rs =null; try{
	 * String countQuery = "select count(1) "; String tmp =
	 * orgQuery.toLowerCase(); int from = tmp.indexOf( "from"); int orderby =
	 * tmp.indexOf( "order by");
	 * 
	 * if( orderby == -1) countQuery += orgQuery.substring( from ); else
	 * countQuery += orgQuery.substring( from , orderby -1);
	 * 
	 * 
	 * pstmt = con.prepareStatement(countQuery); int i=1; for( ; values != null
	 * && i<=values.length; i++){ // if( values[i-1] != null) pstmt.setString(
	 * i, values[i-1] ); }
	 * 
	 * rs = pstmt.executeQuery(); if( rs.next() ) return rs.getInt(1);
	 * }catch(Exception e){ e.printStackTrace(); } finally{ try{ rs.close();
	 * }catch(Exception e){} try{ pstmt.close(); }catch(Exception e){} } return
	 * 0; }
	 */
	/**
	 * ì¡°í???? ê²°ê³¼?? ì»¬ë?? Countë¥? ë¦¬í??
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		if (columnInfo == null)
			return 0;
		return columnInfo.getColumnCount();
	}

	/**
	 * ì»¬ë?? Label?? ë¦¬í??
	 * 
	 * @param index
	 * @return
	 */
	public String getColumnLabel(int index) {
		if (columnInfo == null)
			return "";
		return columnInfo.getColumnLabel(index - 1);
	}

	/**
	 * ??ì²? ì»¬ë?? Label?? ë¦¬í??
	 * 
	 * @return
	 */
	public String[] getColumnLabels() {
		if (columnInfo == null)
			return null;
		return columnInfo.getColumnLabels();
	}

	/**
	 * ??ì²? ì»¬ë?? Name?? ë¦¬í??
	 * 
	 * @return
	 */
	public String[] getColumnNames() {
		if (columnInfo == null)
			return null;
		return columnInfo.getColumnNames();
	}

	protected int findColumn(String name) {
		if (columnInfo == null)
			return -1;
		return columnInfo.findColumn(name);
	}

	protected void finalize() {
		try {
			close();
		} catch (Exception e) {
		}
	}

	public void close() {
		try {
			rs.close();
		} catch (Exception e) {
		}
		try {
			pstmt.close();
		} catch (Exception e) {
		}
		// try{ con.close();}catch(Exception e){}
	}

	public String getQuery() {
		return query;
	}

	static java.io.PrintStream logger = System.out;
	// static java.io.PrintStream logger = null;
	int level = 3;
	java.io.PrintStream back_logger = logger;

	public void setLogEnabled(boolean flag) {
		if (flag) {
			logger = back_logger;
		} else {
			back_logger = logger;
			logger = null;
		}
	}

	static boolean isDoc = false;

	public static void setDocumentMode(boolean doc) {
		isDoc = doc;
	}

	public ResultSet getResultSet() {
		return rs;
	}

	public boolean next() {
		try {
			return rs.next();
		} catch (Exception e) {
		}
		return false;
	}

	public String get(int column) {
		try {
			String s = rs.getString(column);
			if (s == null)
				return "";
			return s;
		} catch (Exception e) {
		}
		return "";
	}

	public String get(String name) {
		String v = get(findColumn(name));
		DBSecure sec = DBSecure.getInstance();
		if(!sec.isSecure()) return v;

		if(!sec.isSecureColumn(name)) return v;
		
		return sec.decrypt(v);
	}

}
