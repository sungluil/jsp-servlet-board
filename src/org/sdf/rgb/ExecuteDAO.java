package org.sdf.rgb;

import java.util.*;
import java.sql.*;

public class ExecuteDAO {
	String table;
	Hashtable cols = new Hashtable();
	Hashtable keycols = new Hashtable();
	List cbox = new ArrayList();

	Connection connection = null;
	public final static String ROW_COUNT = "rowcount";
	public final static int BATCH_COUNT = 100;

	public ExecuteDAO(Connection con) {
		this.connection = con;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void addKey(String keyname) {
		addKey(keyname, keyname);
	}

	public void addKey(String keyname, String valname) {
		cols.put(keyname, valname);
	}

	public void addKey(String[] keyname) {
		for (int i = 0; i < keyname.length; i++)
			addKey(keyname[i]);
	}

	public void addKey(String[] keyname, String[] valname) {
		for (int i = 0; i < keyname.length; i++)
			addKey(keyname[i], valname[i]);
	}

	public void addColumn(String colname) {
		addColumn(colname, colname);
	}

	public void addColumn(String colname, String valname) {
		cols.put(colname, valname);
	}

	public void addColumn(String[] colname) {
		for (int i = 0; i < colname.length; i++)
			addColumn(colname[i]);
	}

	public void addColumn(String[] colname, String[] valname) {
		for (int i = 0; i < colname.length; i++)
			addColumn(colname[i], valname[i]);
	}

	public void setCheckbox(String colname) {
		cbox.add(colname);
	}

	public String get(String col, org.sdf.servlet.Box box) {
		if (cbox.contains(col)) {
			if (box.valid(col))
				return "Y";
			return "N";
		}

		return box.get(col);
	}

	private int _insert(org.sdf.servlet.Box box, boolean batch)
			throws Exception {
		Enumeration enums = cols.keys();
		String[] colnames = new String[cols.size()];
		int idx = 0;
		String colstr = "";
		String valstr = "";
		while (enums.hasMoreElements()) {
			colnames[idx] = (String) enums.nextElement();
			colstr += colnames[idx++];
			valstr += "?";
			if (!enums.hasMoreElements())
				break;

			colstr += ", ";
			valstr += ", ";
		}

		String sql = "insert into " + table + " (" + colstr + " ) values ( "
				+ valstr + " ) ";

		PreparedStatement pstmt = null;
		int count = 0;
		try {
			pstmt = connection.prepareStatement(sql);
			if (batch) {
				int rowcount = box.getInt(ROW_COUNT);
				for (int j = 0; j < rowcount; j++) {
					for (int i = 0; i < colnames.length; i++) {
						pstmt.setString(i + 1, get((String) cols
								.get(colnames[i] + "_" + j), box));
					}
					pstmt.addBatch();
					if (j % BATCH_COUNT == BATCH_COUNT - 1)
						pstmt.executeBatch();
					count++;
				}
				pstmt.executeBatch();
			} else {
				for (int i = 0; i < colnames.length; i++) {
					pstmt.setString(i + 1, get((String) cols.get(colnames[i]),
							box));
				}
				count = pstmt.executeUpdate();
			}
			return count;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	public int insert(org.sdf.servlet.Box box) throws Exception {
		return _insert(box, false);
	}

	public int insertBatch(org.sdf.servlet.Box box) throws Exception {
		return _insert(box, true);
	}

	/*
	 * private int _insert( BufferedReader r) throws Exception{ Enumeration enum
	 * = cols.keys(); String[] colnames = new String[cols.size()]; int idx=0;
	 * String colstr = ""; String valstr = ""; while( enum.hasMoreElements()){
	 * colnames[idx] = (String)enum.nextElement(); colstr += colnames[idx++];
	 * valstr += "?"; if( !enum.hasMoreElements() ) break;
	 *
	 * colstr += ", "; valstr += ", "; }
	 *
	 * String sql = "insert into "+table+ " (" + colstr + " ) values ( " +
	 * valstr + " ) ";
	 *
	 * PreparedStatement pstmt = null; int count = 0; try{ pstmt =
	 * connection.prepareStatement(sql); if( batch){ int rowcount = box.getInt(
	 * ROW_COUNT); for( int j=0; j < rowcount;j++){ for( int i=0; i <
	 * colnames.length; i++){ pstmt.setString( i+1,get(
	 * (String)cols.get(colnames[i] + "_" + j), box)); } pstmt.addBatch(); if( j
	 * % BATCH_COUNT == BATCH_COUNT - 1 ) pstmt.executeBatch(); count++; }
	 * pstmt.executeBatch(); } else{ for( int i=0; i < colnames.length; i++){
	 * pstmt.setString( i+1,get( (String)cols.get(colnames[i]), box)); } count =
	 * pstmt.executeUpdate(); } return count; } finally{ try{ pstmt.close();
	 * }catch( Exception e){} } }
	 */
	private int _update(org.sdf.servlet.Box box, boolean batch)
			throws Exception {
		Enumeration enums = cols.keys();
		String[] colnames = new String[cols.size()];
		int idx = 0;
		String str = "";
		while (enums.hasMoreElements()) {
			colnames[idx] = (String) enums.nextElement();
			str += colnames[idx++] + " = ?";
			if (!enums.hasMoreElements())
				break;
			str += ", ";
		}

		String[] keynames = new String[keycols.size()];
		String keystr = "";
		enums = keycols.keys();

		while (enums.hasMoreElements()) {
			keynames[idx] = (String) enums.nextElement();
			keystr += keynames[idx++] + " = ? ";
			if (!enums.hasMoreElements())
				break;
			keystr += " and ";
		}

		String sql = "update " + table + " set " + str + " where  " + keystr;

		PreparedStatement pstmt = null;
		int count = 0;
		try {
			pstmt = connection.prepareStatement(sql);
			if (batch) {
				int rowcount = box.getInt(ROW_COUNT);
				for (int j = 0; j < rowcount; j++) {
					for (int i = 0; i < colnames.length; i++) {
						pstmt.setString(i + 1, get((String) cols
								.get(keynames[i] + "_" + j), box));

					}
					for (int i = 0; i < keynames.length; i++) {
						pstmt.setString(i + colnames.length + 1, box
								.get((String) cols.get(keynames[i] + "_" + j)));
					}
					pstmt.addBatch();
					if (j % BATCH_COUNT == BATCH_COUNT - 1)
						pstmt.executeBatch();
					count++;
				}
				pstmt.executeBatch();
			} else {
				for (int i = 0; i < colnames.length; i++) {
					pstmt.setString(i + 1, box.get((String) cols
							.get(colnames[i])));
				}
				for (int i = 0; i < keynames.length; i++) {
					pstmt.setString(i + colnames.length + 1, get((String) cols
							.get(keynames[i]), box));
				}
				count = pstmt.executeUpdate();
			}
			return count;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}

	}

	public int update(org.sdf.servlet.Box box) throws Exception {
		return _update(box, false);
	}

	public int updateBatch(org.sdf.servlet.Box box) throws Exception {
		return _update(box, true);
	}

	private int _delete(org.sdf.servlet.Box box, boolean batch)
			throws Exception {
		Enumeration enums = cols.keys();
		String[] colnames = new String[cols.size()];
		int idx = 0;
		String colstr = "";
		String valstr = "";
		while (enums.hasMoreElements()) {
			colnames[idx] = (String) enums.nextElement();
			colstr += colnames[idx++] = " = ? ";

			if (!enums.hasMoreElements())
				break;
			colstr += "and ";
		}

		String sql = "delete " + table + " where " + colstr;

		PreparedStatement pstmt = null;
		int count = 0;
		try {
			pstmt = connection.prepareStatement(sql);
			if (batch) {
				int rowcount = box.getInt(ROW_COUNT);
				for (int j = 0; j < rowcount; j++) {
					for (int i = 0; i < colnames.length; i++) {
						pstmt.setString(i + 1, get((String) cols
								.get(colnames[i] + "_" + j), box));
					}
					pstmt.addBatch();
					if (j % BATCH_COUNT == BATCH_COUNT - 1)
						pstmt.executeBatch();
					count++;
				}
				pstmt.executeBatch();
			} else {
				for (int i = 0; i < colnames.length; i++) {
					pstmt.setString(i + 1, get((String) cols.get(colnames[i]),
							box));
				}
				count = pstmt.executeUpdate();
			}
			return count;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}

	}

	public int delete(org.sdf.servlet.Box box) throws Exception {
		return _delete(box, false);
	}

	public int deleteBatch(org.sdf.servlet.Box box) throws Exception {
		return _delete(box, true);
	}
}