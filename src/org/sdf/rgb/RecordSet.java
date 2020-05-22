/*****************************************************************
 * Class : RecordSet.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park 
 *****************************************************************/

package org.sdf.rgb;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.sdf.log.ILoggerWrapper;
import org.sdf.log.Log;
import org.sdf.secure.DBSecure;
import org.sdf.slim.config.GlobalResource;

public class RecordSet extends Fetch {
	ColumnInfo columnInfo;
	List rows;
	String query;
	String orgQuery;

	private boolean rownumMode = false;
	private boolean pageMode = false;
	private boolean executed = false;
	// ?´ì????ë²??? ë¡?ê·¸ë?? ????ê¸? ???? ë³?ê²? 2007.07.09
	private boolean log = true;

	// private boolean log =false;
	public RecordSet() {
		executed = true;

	}

	/**
	 * ???±ì??
	 * 
	 * @param query
	 *            Query SQL
	 */
	public RecordSet(String query) {
		setQuery(query);
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
	public RecordSet(String query, int curpage, int pagesize) {
		this(query);
		setPage(curpage, pagesize);
	}

	/*
	 * ë¯¸ì?¬ì??
	 */
	public RecordSet(String query, int curpage, int pagesize, boolean rownum) {
		setPage(curpage, pagesize);
		if (pageMode && rownum)
			setRownumQuery(query);
		else
			setQuery(query);
	}

	public RecordSet(String query, boolean log) {
		this(query);
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
	public RecordSet(ResultSet rs) throws Exception {
		int count = makeColumns(rs);

		rows = new ArrayList();

		String[] record;
		int num = 0;
		while (rs.next()) {
			num++;
			record = new String[count];
			for (int i = 1; i <= count; i++) {
				record[i - 1] = rs.getString(i);
			}
			rows.add(record);
		}

		total = rows.size();
		executed = true;
	}
	
	public String get(String col){
		String v = super.get(col);

		if(!isSecure()) return v;
		if(!isSecureColumn(col)) return v;
		
		DBSecure sec = DBSecure.getInstance();
		return sec.decrypt(v);
	}
	
	public boolean isSecure(){
		if(columnInfo == null) return false;
		return columnInfo.isSecure;
	}
	
	public boolean isSecureColumn(String col){
		if(columnInfo == null) return false;
		return columnInfo.isSecureColumn(col);  
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
		if (executed)
			return;

		// level = 4;

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
		if (executed)
			return;

		// level = 4;

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

	public void executeQuery(Connection con, String[] values, Condition[] conds)
			throws Exception {
		if (executed)
			return;

		// level = 4;
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
		return (logger != null || logWrapperMode) && log;
	}

	/**
	 * Queryë¥? ????
	 * 
	 * @param con
	 * @param l
	 *            Binding ë³???
	 * @throws Exception
	 */
	public void executeQuery(Connection con, List l) throws Exception {
		String[] vals = null;
		if (l == null || l.size() == 0)
			executeQuery(con);
		vals = new String[l.size()];
		vals = (String[]) l.toArray(vals);
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
			else
				_executeQueryRownum(con, values);
			this.executed = true;
			if (isLogging()) {
				if (!isDoc) {
					log.append("[Success:elapsed=").append(sw.getElapsed())
							.append(",cnt:").append(getTotalCount()).append(
									"]\n");
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
			try {
				Clob sClob = rs.getClob(index);
				Reader r = sClob.getCharacterStream();
				// Reader r = rs.getCharacterStream(index);
				if (r == null)
					return "";
				StringWriter w = new StringWriter();
				int read;
				while ((read = r.read()) != -1) {
					w.write(read);
				}
				w.flush();
				return w.toString();
			} catch (Exception e) {
				return rs.getString(index);
			}
		}
		if (column.type == Types.BLOB) {
			InputStream r = rs.getBinaryStream(index);
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
			if (d == null)
				return "";
			return df_dt.format(d);
		}

		return rs.getString(index);
	}

	public static SimpleDateFormat df_dt = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private void _executeQueryAll(Connection con, String[] values)
			throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(query);

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

	private void _executeQuery(Connection con, String[] values)
			throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			total = getTotalCount(con, values);

			pstmt = con.prepareStatement(query);

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

	private void _executeQueryRownum(Connection con, String[] values)
			throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			total = getTotalCount(con, values);

			pstmt = con.prepareStatement(query);

			int i = 1;
			for (; values != null && i <= values.length; i++) {
				pstmt.setString(i, values[i - 1]);
			}

			pstmt.setInt(i++, (curpage - 1) * pagesize + 1);
			pstmt.setInt(i++, curpage * pagesize);

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
		if(logwrap !=null) logwrap.close();
		logwrap = l;
		logWrapperMode = true;
	}

	protected void setQuery(String query, boolean rownum) {
		if (pageMode && rownum)
			setRownumQuery(query);
		else
			setQuery(query);
	}

	protected void setQuery(String query) {
		query = org.sdf.util.StringUtil.replace(query, "#{db_owner}", GlobalResource.getInstance().getDbOwner());
		this.orgQuery = new String(query);

		this.query = query;
	}

	protected void setPage(int curpage, int pagesize) {
		if (curpage > 0 && pagesize > 0) {
			this.curpage = curpage;
			this.pagesize = pagesize;
			pageMode = true;
		}
	}

	protected void setRownumQuery(String query) {
		query = org.sdf.util.StringUtil.replace(query, "#{db_owner}", GlobalResource.getInstance().getDbOwner());
		this.orgQuery = new String(query);

		this.query = getRownumQuery(query);
		rownumMode = true;
	}

	protected String getRownumQuery(String query) {
		StringBuffer buf = new StringBuffer(query);
		int fromIndex = query.toLowerCase().indexOf("from");

		buf.insert(fromIndex, ", rownum rnum ");
		buf.insert(0, "select * from ( ");
		String tmp = buf.toString();
		int orderby = tmp.toLowerCase().indexOf("order by");
		if (orderby < 0)
			buf.append(" ) where rnum >= ? and rnum <= ? \norder by rnum desc");
		else
			buf.insert(orderby, " ) where rnum >= ? and rnum <= ? ");

		return buf.toString();
	}

	protected int makeColumns(ResultSet rs) {
		if (columnInfo == null)
			columnInfo = new ColumnInfo();

		return columnInfo.make(rs);
	}

	public int getCurIndex() {
		return currow;
	}

	public void setCurIndex(int row) {
		this.currow = row;
	}

	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}

	public String getCountSql(String sql) {
		/*
		String nsql = sql.toLowerCase();
		int off = nsql.lastIndexOf("order by ");
		if (off > 0){
			
			return sql.substring(0, off);
		}
		*/
		return sql;
	}

	public String getMSCountSql(String sql) {
		String nsql = sql.toLowerCase();
		int off = nsql.lastIndexOf("order by ");
		if (off > 0){
			
			return sql.substring(0, off);
		}
		return sql;
	}	
	
	public String getWithSql(String sql) {

		String tsql = sql.toLowerCase();
		int off = tsql.lastIndexOf("order by");
		int asoff = tsql.indexOf(" as ");
		if (off > 0) {
			sql = sql.substring(0, off - 1);
		}

		int len = sql.length();

		int ocnt = 0;
		boolean flag = false;
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (i = 0; i < len; i++) {
			char c = sql.charAt(i);
			if (c == '(') {
				ocnt++;
				if (i > asoff)
					flag = true;
			}
			if (c == ')') {
				ocnt--;
			}

			if (flag && ocnt == 0) {
				buf.append(c);
				buf.append("\nselect count(1) cnt from ( \n");
				buf.append(sql.substring(i + 1));
				break;
			} else {
				buf.append(c);
			}

		}
		buf.append("\n) tcnt");

		return buf.toString();
	}

	private int getTotalCount(Connection con, String[] values) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String countQuery = null;
		try {
			if (orgQuery == null)
				return 0;

			String tmp = orgQuery.toLowerCase().trim();
			if (tmp.indexOf(";with ") == 0) {
				countQuery = getWithSql(orgQuery);
			} else {
				//MSSQL
				if(con.getMetaData().getDatabaseProductName().contains("Microsoft SQL Server")) {
					String nsql = getMSCountSql(orgQuery);
					countQuery = new StringBuffer("select count(1) from ( ")
							.append(nsql).append(") t1 ").toString();
					
				}else {
					String nsql = getCountSql(orgQuery);
					countQuery = new StringBuffer("select count(1) from ( ")
							.append(nsql).append(") t1 ").toString();
				}					
			}

			/*
			 * countQuery = "select count(1) from ( "; String tmp =
			 * orgQuery.toLowerCase(); int from = tmp.indexOf( "from");
			 * 
			 * int orderby = tmp.lastIndexOf( "order by");
			 * 
			 * if( orderby == -1) countQuery += orgQuery; else{ countQuery +=
			 * orgQuery.substring(0, orderby -1); } countQuery += orgQuery;
			 * countQuery += " ) ";
			 */
			pstmt = con.prepareStatement(countQuery);
			int i = 1;
			for (; values != null && i <= values.length; i++) {
				pstmt.setString(i, values[i - 1]);
			}

			rs = pstmt.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			Log.sql.err("Count Sql Error", e);
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
		return 0;
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
	 * <pre>
	 * ì¡°í???? ê²°ê³¼?? Row Countë¥? ë¦¬í??
	 *     Page ê¸°ë?¥ì?? ?¬ì?©í?? ê²½ì?? ??ì²? ê²°ê³¼ê°? ???? ???? ?´ê????? Row countë¥? ë¦¬í??
	 * </pre>
	 * 
	 * @return int
	 */
	public int getRowCount() {
		if (rows == null)
			return 0;
		return rows.size();
	}

	/**
	 * <pre>
	 * ì¡°í???? ê²°ê³¼?? ??ì²? Row countë¥? ë¦¬í??
	 *    Page ê¸°ë?? ?¬ì?©í?? ê²½ì?°ì???? ??ì²´ê²°ê³¼ì?? ???? Row?? Countë¥? ë¦¬í??
	 * </pre>
	 */
	public int getTotalCount() {
		return total;
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

	public boolean valid(String name) {
		String value = get(name);

		if (value == null || value.trim().equals(""))
			return false;
		return true;
	}

	public String get(int row, int column) {
		try {
			String[] record = (String[]) rows.get(row);
			if (record[column - 1] == null)
				return "";
			return record[column - 1];
		} catch (Exception e) {
		}
		return "";
	}

	public Object getObject(int row, int column) {
		try {
			Object[] record = (String[]) rows.get(row);
			if (record[column - 1] == null)
				return null;
			return record[column - 1];
		} catch (Exception e) {
		}
		return null;
	}

	public void put(int row, int column, String v) {
		try {
			String[] record = (String[]) rows.get(row);
			record[column - 1] = v;
		} catch (Exception e) {
		}
	}

	public void put(int row, int column, Object v) {
		try {
			Object[] record = (Object[]) rows.get(row);
			record[column - 1] = v;
		} catch (Exception e) {
		}
	}

	/**
	 * ???? ì»¤ì???? ?´ë?¹í???? Rowë¥? String[] ë¡? ë¦¬í??
	 * 
	 * @return
	 */
	public String[] getCurrentRow() {
		try {
			return (String[]) rows.get(currow);
		} catch (Exception e) {
		}
		return null;
	}

	protected int findColumn(String name) {
		if (columnInfo == null)
			return -1;
		return columnInfo.findColumn(name);
	}

	public Object[] getObjects(Object entity) {
		return getObjects(entity, null);
	}

	public Object[] getObjects(Object entity, String acronym) {
		if (entity == null)
			return null;
		try {
			Class c = entity.getClass();
			List list = new ArrayList();
			while (next()) {
				Object obj = c.newInstance();
				copyToEntity(obj, acronym);
				list.add(obj);
			}
			return list.toArray();
		} catch (Exception e) {
		}
		return null;
	}

	protected void finalize() {
		try {
			clear();
		} catch (Exception e) {
		}
	}

	public void clear() {
		try {
			rows.clear();
			rows = null;
		} catch (Exception e) {
		}
		try {
			columnInfo.clear();
			columnInfo = null;
		} catch (Exception e) {
		}
		query = null;
		orgQuery = null;
	}

	public void close() {
		finalize();
	}

	public List getList() {
		return rows;
	}

	public String getQuery() {
		return query;
	}

	public String getValue(String keyval, String keyname, String valname) {
		if (keyval == null || keyval.equals(""))
			return "";
		int kcol = findColumn(keyname);
		int vcol = findColumn(valname);
		int cnt = getRowCount();
		for (int i = 0; i < cnt; i++) {
			String val = get(i, kcol);
			if (keyval.equals(val))
				return get(i, vcol);
		}

		return "";
	}

	public int moveCursor(String keyval, String keyname) {
		return moveCursor(keyval, keyname, 0);
	}

	public int moveCursor(String keyval, String keyname, int row) {
		if (keyval == null || keyval.equals(""))
			return -1;
		int kcol = findColumn(keyname);
		int cnt = getRowCount();
		for (int i = row; i < cnt; i++) {
			String val = get(i, kcol);
			if (keyval.equals(val)) {
				setCurRow(i);
				return i;
			}
		}

		return -1;
	}

	public boolean find(String keyval, String keyname) {
		return find(keyval, keyname, 0);
	}

	public boolean find(String keyval, String keyname, int row) {
		int cur = moveCursor(keyval, keyname, row);
		if (cur == -1)
			return false;
		setCurIndex(cur);
		return true;
	}

	static java.io.PrintStream logger = System.out;
	// static java.io.PrintStream logger = null;
	protected int level = 4;
	java.io.PrintStream back_logger = logger;

	public void setLogEnabled(boolean flag) {
		log = flag;
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

	/*
	 * public RecordSet filter( String[] cols){ RecordSet rs = new RecordSet();
	 * rs.columnInfo = columnInfo.filter( cols ); rs.pageMode = pageMode;
	 * 
	 * int[] colindex = new int[cols.length]; for( int i=0; i< cols.length; i++)
	 * 
	 * return rs; }
	 */

	public void addColumns(Column[] cols) {
		for (int i = 0; i < cols.length; i++) {
			columnInfo.addColumn(cols[i]);
		}
		if (rows == null)
			return;
		int size = rows.size();
		for (int i = 0; i < size; i++) {
			String[] row = (String[]) rows.get(i);
			String[] newrow = new String[row.length + cols.length];
			for (int j = 0; j < row.length; j++) {
				newrow[j] = row[j];
			}
			rows.set(i, newrow);
		}
	}

	public String[] getKeys() {
		return getColumnNames();
	}

	public RecordSet filter(String col, String val) {
		RecordSet rs = new RecordSet();
		rs.columnInfo = this.getColumnInfo();

		rs.rows = new ArrayList();
		String[] record = new String[rs.columnInfo.getColumnCount()];
		List l = this.getList();
		int idx = rs.findColumn(col) - 1;
		int cnt = 0;
		for (int i = 0; i < l.size(); i++) {
			String[] d = (String[]) l.get(i);
			if (!d[idx].equals(val))
				continue;
			rs.rows.add(d);
			cnt++;
		}
		rs.total = cnt;
		return rs;
	}
}
