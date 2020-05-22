package org.sdf.rgb;

import java.io.Reader;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.sdf.log.ILoggerWrapper;
import org.sdf.log.Log;

public class CSRecordSet extends RecordSet {
	private boolean rownumMode = false;
	private boolean pageMode = false;
	private boolean executed = false;
	// ?´ì????ë²??? ë¡?ê·¸ë?? ????ê¸? ???? ë³?ê²? 2007.07.09
	private boolean log = true;

	public CSRecordSet() {
		super();

	}

	/**
	 * ???±ì??
	 * 
	 * @param query
	 *            Query SQL
	 */
	public CSRecordSet(String query) {
		super(query);
	}

	/**
	 * ???±ì??
	 * 
	 * @param query
	 *            Query SQL
	 * @param curpage
	 *            ???? ???´ì?
	 * @param pagesize
	 *            ???´ì??? Row Count
	 */
	public CSRecordSet(String query, int curpage, int pagesize) {
		super(query, curpage, pagesize);
	}

	/*
	 * ë¯¸ì?¬ì??
	 */
	public CSRecordSet(String query, int curpage, int pagesize, boolean rownum) {
		super(query, curpage, pagesize, rownum);
	}

	public CSRecordSet(String query, boolean log) {
		super(query, log);
		// ?´ì????ë²??? ë¡?ê·¸ë?? ????ê¸? ???? ë³?ê²? 2007.07.09
		// this.log = log;
	}

	/**
	 * ???±ì??
	 * 
	 * @param rs
	 *            RecordSet
	 * @throws Exception
	 */
	public CSRecordSet(ResultSet rs) throws Exception {
		super(rs);
	}

	/**
	 * ì§????? ì»¬ë?? index?? ?´ë?¹í???? ??ì²? Row ?°ì?´í?°ë?? ë¦¬í??
	 * 
	 * @param index
	 * @return
	 */
	public String[] getValues(int index) {
		if (rows == null)
			return null;
		int count = getRowCount();
		String[] vals = new String[count];
		for (int i = 0; i < count; i++)
			vals[i] = get(i, index);

		return vals;
	}

	/**
	 * ì§????? ì»¬ë??name?? ?´ë?¹í???? ??ì²? Row ?°ì?´í?°ë?? ë¦¬í??
	 * 
	 * @param name
	 * @return
	 */
	public String[] getValues(String name) {
		if (rows == null)
			return null;
		int count = getRowCount();
		String[] vals = new String[count];
		for (int i = 0; i < count; i++)
			vals[i] = get(i, name);

		return vals;
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

	private String getString(ResultSet rs, Column column, int index)
			throws Exception {

		if (column.type == Types.CLOB) {
			Reader r = rs.getCharacterStream(index);
			if (r == null)
				return "";
			StringWriter w = new StringWriter();
			int read;
			while ((read = r.read()) != -1) {
				w.write(read);
			}

			w.flush();

			return w.toString();
		} else if (column.type == Types.DATE) {
			java.sql.Date d = rs.getDate(index);
			return df_dt.format(d);
		}

		return rs.getString(index);
	}

	public static SimpleDateFormat df_dt = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 * @param values
	 *            Binding ë³???
	 * @throws Exception
	 */
	public void executeQuery(Connection con, String[] values) throws Exception {
		StringBuffer log = new StringBuffer();
		org.sdf.util.StopWatch sw = new org.sdf.util.StopWatch();
		try {

			if (executed)
				return;
			MethodInfo m = new MethodInfo(level);
			String info = m.getInfo();
			if (isLogging()) {
				if (isDoc) {
					log.append("[RecordSet]").append(info).append(" ");
				} else {
					log.append("[RecordSet]").append(info).append(" ");
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

			if (!pageMode)
				_executeQueryAll(con, values);
			else if (!rownumMode)
				_executeQuery(con, values);
			// else
			// _executeQueryRownum( con, values);

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

				if (isLogging() && logwrap != null)
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
					String tmp = (log != null) ? log.toString() : "";
					SQLError ex = new SQLError(tmp, e);
					throw ex;
				} else {
					logger.println(log.toString());
				}
			} else {
				SQLError ex = new SQLError(query, e);
				throw ex;
			}
		} catch (Exception e) {
			if (isLogging())
				log.append("[LOG_FAIL:elapsed=").append(sw.getElapsed())
						.append("] -> " + e);

			throw e;
		}
	}

	protected void _executeQueryAll(Connection con, String[] values)
			throws Exception {
		CallableStatement pstmt = null;
		ResultSet rs = null;
		try {

			pstmt = con.prepareCall(query);

			int i = 1;
			for (; values != null && i <= values.length; i++) {

				pstmt.setString(i, values[i - 1]);

			}

			rs = pstmt.executeQuery();

			int count = makeColumns(rs);
			rows = new Vector();

			String[] record;
			int num = 0;
			while (rs.next()) {
				num++;
				record = new String[count];
				for (i = 1; i <= count; i++) {
					record[i - 1] = getString(rs, columnInfo.getColumn(i - 1),
							i);
				}
				rows.add(record);
			}

			total = rows.size();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	protected void _executeQuery(Connection con, String[] values)
			throws Exception {

		CallableStatement pstmt = null;
		ResultSet rs = null;
		try {
			total = 0;

			pstmt = con.prepareCall(query);

			int i = 1;
			for (; values != null && i <= values.length; i++) {
				pstmt.setString(i, values[i - 1]);
			}

			rs = pstmt.executeQuery();

			int count = makeColumns(rs);

			rows = new Vector();

			String[] record;
			int num = 0;
			while (rs.next()) {
				total++;
				num++;
				if (curpage != 0 && (num < (curpage - 1) * pagesize + 1))
					continue;
				if (pagesize != 0 && (num > curpage * pagesize))
					break;

				record = new String[count];
				for (i = 1; i <= count; i++) {

					record[i - 1] = getString(rs, columnInfo.getColumn(i - 1),
							i);
				}
				rows.add(record);
			}
			while (rs.next()) {
				total++;
			}
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	public static void setLogger(java.io.PrintStream out) {
		logger = out;
	}

	static ILoggerWrapper logwrap;
	static boolean logWrapperMode = false;

	public static void setLoggerWrapper(ILoggerWrapper l) {
		logwrap = l;
		logWrapperMode = true;
	}

	static java.io.PrintStream logger = System.out;
	// static java.io.PrintStream logger = null;
	protected int level = 4;
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

}
