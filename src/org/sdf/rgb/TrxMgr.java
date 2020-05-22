package org.sdf.rgb;

import java.sql.Connection;

import org.sdf.lang.Data;
import org.sdf.log.Log;
import org.sdf.slim.config.GlobalResource;
import org.sdf.util.IKeys;
import org.sdf.util.UniqueKey;

public class TrxMgr {
	String dbname;
	TrxContext ctx = null;

	public TrxMgr() {
		JPreparedStatement.setLogEnabled(true);
	}

	boolean logging = true;

	public void setLogging(boolean b) {
		this.logging = b;
	}

	public TrxMgr(String name) {
		this.dbname = name;
		try {
			ctx = new TrxContext(dbname);
		} catch (Exception e) {
			Log.cfg.err("TrxMgr init failed.", e);
		}
	}

	public int insertWithNewkey(Connection con, String table, String colstr,
			Data data) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		return insertWithNewkey(con, table, cols, data);
	}

	public int insertWithNewkey(Connection con, String table, String[] cols,
			Data data) throws Exception {
		return insert(con, table, cols, cols, data, true);
	}

	public int insert(Connection con, String table, String colstr, Data data)
			throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		return insert(con, table, cols, data);
	}

	public int insert(Connection con, String table, String colstr,
			String valstr, Data data) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		String[] vals = org.sdf.util.StringUtil.getArray(valstr, ',');
		return insert(con, table, cols, vals, data, false);
	}

	IKeys ikeys = null;

	public void setKeys(IKeys ikeys) {
		this.ikeys = ikeys;
	}

	protected String getKey() {
		if (ikeys == null) {
			UniqueKey key = UniqueKey.getInstance();
			String oid = key.getKey();
			return oid;
		}
		return ikeys.getKey();
	}

	public int insert(Connection con, String table, String[] cols, Data data)
			throws Exception {
		return insert(con, table, cols, cols, data, false);
	}

	public int insert(Connection con, String table, String[] cols,
			String[] valnames, Data data, boolean newKey) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {

			String oid = getKey();

			StringBuffer buf = new StringBuffer();
			buf.append("insert into "+GlobalResource.getInstance().getDbOwner()+"").append(table).append('(');
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]);
			}
			buf.append(") values ( ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append('?');
			}
			buf.append(")");

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);

			for (int j = 0; j < cols.length; j++) {
				String col = valnames[j].trim();

				String v = data.get(col);

				if (newKey && j == 0)
					pstmt.setString((j + 1), oid);
				else
					pstmt.setString((j + 1), v);
			}

			count = pstmt.executeUpdate();

		}

		finally {
			pstmt.close();
		}

		return count;
	}

	public int insert(Connection con, String table, String[] cols, String[] vals)
			throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {

			StringBuffer buf = new StringBuffer();
			buf.append("insert into "+GlobalResource.getInstance().getDbOwner()+"").append(table).append('(');
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]);
			}
			buf.append(") values ( ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append('?');
			}
			buf.append(")");

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);

			for (int j = 0; j < cols.length; j++) {

				pstmt.setString((j + 1), vals[j]);
			}

			count = pstmt.executeUpdate();

		} finally {
			pstmt.close();
		}
		return count;
	}

	/**
	 * 
	 * @param con
	 * @param table
	 * @param cols
	 * @param bCol
	 *            ë°°ì?ì»¬ë??
	 * @param vals
	 * @param bVals
	 * @return
	 * @throws Exception
	 */
	public int insertBatch(Connection con, String table, String[] cols,
			String bCol, String[] vals, String[] bVals) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			String oid = getKey();

			StringBuffer buf = new StringBuffer();
			buf.append("insert into "+GlobalResource.getInstance().getDbOwner()+"").append(table).append('(');
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]);
			}
			if (cols.length > 0)
				buf.append(',');
			buf.append(bCol);

			buf.append(") values ( ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append('?');
			}
			if (cols.length > 0)
				buf.append(',');
			buf.append('?');
			buf.append(")");

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);

			for (int i = 0; i < bVals.length; i++) {
				for (int j = 0; j < cols.length; j++) {
					pstmt.setString((j + 1), vals[j]);
				}
				pstmt.setString(cols.length + 1, bVals[i]);
				count += pstmt.executeUpdate();
			}
		} finally {
			pstmt.close();
		}
		return count;
	}

	/**
	 * 
	 * @param con
	 * @param table
	 * @param cols
	 * @param bCol
	 *            ë°°ì?ì»¬ë??
	 * @param vals
	 * @param bVals
	 * @return
	 * @throws Exception
	 */
	public int insertBatch(Connection con, String table, String[] cols,
			String[] bCol, String[] vals, String[][] bVals) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			String oid = getKey();

			StringBuffer buf = new StringBuffer();
			buf.append("insert into "+GlobalResource.getInstance().getDbOwner()+"").append(table).append('(');
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]);
			}
			if (cols.length > 0)
				buf.append(',');
			for (int i = 0; i < bCol.length; i++) {
				if (i > 0)
					buf.append(",");
				buf.append(bCol[i]);
			}

			buf.append(") values ( ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append('?');
			}
			if (cols.length > 0)
				buf.append(',');
			for (int i = 0; i < bCol.length; i++) {
				if (i > 0)
					buf.append(",");
				buf.append('?');
			}
			buf.append(")");

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);

			for (int i = 0; i < bVals[0].length; i++) {
				for (int j = 0; j < cols.length; j++) {
					pstmt.setString((j + 1), vals[j]);
				}
				for (int j = 0; j < bCol.length; j++) {
					pstmt.setString(cols.length + (j + 1), bVals[j][i]);
				}
				count += pstmt.executeUpdate();
			}
		} finally {
			pstmt.close();
		}
		return count;
	}

	public int update(Connection con, String table, String colstr,
			String keystr, Data data) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		String[] keys = org.sdf.util.StringUtil.getArray(keystr, ',');
		return update(con, table, cols, cols, keys, data);
	}

	public int update(Connection con, String table, String colstr,
			String valstr, String keystr, Data data) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		String[] valnames = org.sdf.util.StringUtil.getArray(valstr, ',');
		String[] keys = org.sdf.util.StringUtil.getArray(keystr, ',');
		return update(con, table, cols, valnames, keys, data);
	}

	public int update(Connection con, String table, String[] cols,
			String[] valnames, String[] keys, Data data) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {

			StringBuffer buf = new StringBuffer();
			buf.append("update "+GlobalResource.getInstance().getDbOwner()+"").append(table).append(" set ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]).append("= ?");
			}
			buf.append(" where ");
			for (int j = 0; j < keys.length; j++) {
				if (j > 0)
					buf.append(" and ");
				buf.append(keys[j]).append(" = ?");
			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			for (int j = 0; j < cols.length; j++) {
				String col = valnames[j].trim();
				String v = data.get(col);

				pstmt.setString((j + 1), v);
			}

			for (int j = 0; j < keys.length; j++) {
				String col = keys[j].trim();
				String v = data.get(col);
				pstmt.setString((j + 1) + cols.length, v);
			}

			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;
	}

	public int update(Connection con, String table, String[] cols,
			String[] vals, String[] keys, String[] kvals) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {

			StringBuffer buf = new StringBuffer();
			buf.append("update "+GlobalResource.getInstance().getDbOwner()+"").append(table).append(" set ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]).append("= ?");
			}
			buf.append(" where ");
			for (int j = 0; j < keys.length; j++) {
				if (j > 0)
					buf.append(" and ");
				buf.append(keys[j]).append(" = ?");
			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			for (int j = 0; j < cols.length; j++) {
				pstmt.setString((j + 1), vals[j]);
			}

			for (int j = 0; j < keys.length; j++) {
				pstmt.setString((j + 1) + cols.length, kvals[j]);
			}

			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;
	}

	public int updateBatch(Connection con, String table, String colstr,
			String keystr, Data data) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		String[] keys = org.sdf.util.StringUtil.getArray(keystr, ',');
		return updateBatch(con, table, cols, cols, keys, data);
	}

	public int updateBatch(Connection con, String table, String colstr,
			String valstr, String keystr, Data data) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		String[] valnames = org.sdf.util.StringUtil.getArray(valstr, ',');
		String[] keys = org.sdf.util.StringUtil.getArray(keystr, ',');
		return updateBatch(con, table, cols, valnames, keys, data);
	}

	public int updateBatch(Connection con, String table, String[] cols,
			String[] valnames, String[] keys, Data data) throws Exception {
		return updateBatch(con, table, cols, valnames, keys, keys, data);
	}

	public int updateBatch(Connection con, String table, String[] cols,
			String[] valnames, String[] keys, String[] reqCols, Data data)
			throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			int cnt = data.getInt("batch_cnt");

			StringBuffer buf = new StringBuffer();
			buf.append("update "+GlobalResource.getInstance().getDbOwner()+"").append(table).append(" set ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(',');
				buf.append(cols[j]).append("= ?");
			}
			buf.append(" where ");
			for (int j = 0; j < keys.length; j++) {
				if (j > 0)
					buf.append(" and ");
				buf.append(keys[j]).append(" = ?");
			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			for (int i = 0; i < cnt; i++) {
				boolean skip = false;
				for (int j = 0; j < reqCols.length; j++) {
					if (!data.valid(reqCols[j] + i)) {
						skip = true;
						break;
					}
				}

				if (skip)
					continue;
				for (int j = 0; j < cols.length; j++) {
					String col = valnames[j].trim();
					String v = data.get(col + i);

					pstmt.setString((j + 1), v);
				}

				for (int j = 0; j < keys.length; j++) {
					String col = keys[j].trim();
					String v = data.get(col + i);
					pstmt.setString((j + 1) + cols.length, v);
				}

				count += pstmt.executeUpdate();
			}
		} finally {
			pstmt.close();
		}
		return count;
	}

	/*
	 * String _get(String col,Data data){ String v = data.get(col); //v =
	 * org.sdf.util.StringUtil.han2eng(v);KSC5601 //try{ v = new
	 * String(v.getBytes("8859_1"), "utf-8" ); }catch(Exception e){} try{ v =
	 * new String(v.getBytes("KSC5601"), "utf-8" ); }catch(Exception e){} return
	 * v; }
	 */

	public int delete(Connection con, String table, String[] cols, String[] vals)
			throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			int idx = 0;
			StringBuffer buf = new StringBuffer();
			buf.append("delete from "+GlobalResource.getInstance().getDbOwner()+"" + table + " where ");
			for (int i = 0; i < cols.length; i++) {
				String key = cols[i];
				if (idx++ > 0)
					buf.append(" and ");
				buf.append(key);
			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			idx = 1;
			for (int i = 0; i < cols.length; i++) {
				String v = vals[i];
				pstmt.setString(idx++, v);
			}
			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;

	}

	public int delete(Connection con, String table, String colstr,
			String[] vals, boolean fit) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		boolean[] bstmtcols = new boolean[cols.length];

		return delete(con, table, cols, vals, bstmtcols, fit);
	}

	public int delete(Connection con, String table, String[] cols,
			String[] vals, boolean fit) throws Exception {
		boolean[] bstmtcols = new boolean[cols.length];
		return delete(con, table, cols, vals, bstmtcols, fit);
	}

	public int delete(Connection con, String table, String[] cols,
			String[] vals, boolean[] bstmtcols, boolean fit) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			int idx = 0;
			StringBuffer buf = new StringBuffer();
			buf.append("delete from "+GlobalResource.getInstance().getDbOwner()+"" + table + " where ");
			for (int i = 0; i < cols.length; i++) {
				String key = cols[i];
				String v = vals[i];
				if (!fit) {
					if (v.equals(""))
						continue;
				}
				if (idx++ > 0)
					buf.append(" and ");
				buf.append(key);
				if (!bstmtcols[i])
					buf.append(" = ? ");

			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			idx = 1;
			for (int i = 0; i < cols.length; i++) {
				String key = cols[i];
				String v = vals[i];
				if (!fit) {
					if (v.equals(""))
						continue;
				}
				pstmt.setString(idx++, v);
			}
			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;

	}

	public int delete(Connection con, String table, String colstr,
			String[] vals, boolean[] fits) throws Exception {
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		boolean[] bstmtcols = new boolean[cols.length];

		return delete(con, table, cols, vals, bstmtcols, fits);
	}

	public int delete(Connection con, String table, String[] cols,
			String[] vals, boolean[] fits) throws Exception {
		boolean[] bstmtcols = new boolean[cols.length];
		return delete(con, table, cols, vals, bstmtcols, fits);
	}

	public int delete(Connection con, String table, String[] cols,
			String[] vals, boolean[] bstmtcols, boolean[] fits)
			throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			int idx = 0;
			StringBuffer buf = new StringBuffer();
			buf.append("delete from "+GlobalResource.getInstance().getDbOwner()+"" + table + " where ");
			for (int i = 0; i < cols.length; i++) {
				String key = cols[i];
				String v = vals[i];
				if (!fits[i]) {
					if (v.equals(""))
						continue;
				}
				if (idx++ > 0)
					buf.append(" and ");
				buf.append(key);
				if (!bstmtcols[i])
					buf.append(" = ? ");

			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			idx = 1;
			for (int i = 0; i < cols.length; i++) {
				String key = cols[i];
				String v = vals[i];
				if (!fits[i]) {
					if (v.equals(""))
						continue;
				}
				pstmt.setString(idx++, v);
			}
			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;

	}

	public int deleteFlex(Connection con, String table, String[] cols,
			String[] vals, boolean[] fits, int[] cnts) throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {
			int idx = 0;
			StringBuffer buf = new StringBuffer();
			buf.append("delete from "+GlobalResource.getInstance().getDbOwner()+"" + table + " where ");
			for (int i = 0; i < cols.length; i++) {
				String key = cols[i];
				String v = vals[i];
				if (!fits[i]) {
					if (v.equals(""))
						continue;
				}
				if (idx++ > 0)
					buf.append(" and ");
				buf.append(key);
			}

			String sql = buf.toString();

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(logging);
			idx = 1;
			int idx1 = 0;
			for (int i = 0; i < cols.length; i++) {

				if (!fits[i]) {
					String v = vals[i];
					if (v.equals(""))
						continue;
				}
				for (int j = 0; j < cnts[i]; j++) {
					if (j > 0)
						idx1++;
					int k = i + (idx1);

					String v = vals[k];

					pstmt.setString(idx++, v);

				}
			}
			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;

	}

	public int execute(Connection con, String sql, String colstr, Data data)
			throws Exception {
		JPreparedStatement pstmt = null;
		int count = 0;
		try {

			String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');

			pstmt = new JPreparedStatement(con, sql);
			pstmt.setLogging(true);
			for (int j = 0; j < cols.length; j++) {
				String col = cols[j].trim();
				String v = data.get(col);

				pstmt.setString((j + 1), v);
			}

			count = pstmt.executeUpdate();
		} finally {
			pstmt.close();
		}
		return count;
	}

	public int execute(JPreparedStatement pstmt, String colstr, Data data)
			throws Exception {

		int count = 0;
		String[] cols = org.sdf.util.StringUtil.getArray(colstr, ',');
		for (int j = 0; j < cols.length; j++) {
			String col = cols[j].trim();
			String v = data.get(col);

			pstmt.setString((j + 1), v);
		}
		count = pstmt.executeUpdate();

		return count;
	}

	public int execute(JPreparedStatement pstmt, String[] cols, Data data)
			throws Exception {

		int count = 0;
		for (int j = 0; j < cols.length; j++) {
			String col = cols[j].trim();
			String v = data.get(col);

			pstmt.setString((j + 1), v);
		}
		count = pstmt.executeUpdate();

		return count;
	}
}
