package org.sdf.rgb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sdf.lang.Data;

public class DbMgr {
	String dbname;

	public DbMgr() {

	}

	public void setLogging(boolean b) {
		this.logging = b;
	}

	public DbMgr(String name) {
		this.dbname = name;
	}

	public Result query(String sql) {

		return query(sql, null);
	}

	public Result query(SqlCfg conf, Data data) {
		return query(6, conf, data, -1, -1);
	}

	public Result query(SqlCfg conf, Data data, int curpage, int pagesize) {
		return query(6, conf, data, curpage, pagesize);
	}

	public Result query(int level, SqlCfg conf, Data data, int curpage,
			int pagesize) {

		Result result = new Result();
		Connection con = null;
		String sql = null;
		try {

			RecordSet rs = null;
			sql = conf.sql;
			SqlCfg.Query query = conf.getQuery(data);
			sql = query.sql;

			con = new DBSource().getConnection(dbname);
			rs = new RecordSet(sql, curpage, pagesize);
			rs.setLogEnabled(logging);
			rs.level = level;
			rs.executeQuery(con, query.vals);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	public Result query(String sql, String[] val) {
		return query(6, sql, val);
	}

	public Result query(int level, String sql, String[] val) {

		Result result = new Result();
		Connection con = null;
		try {

			RecordSet rs = null;
			String query = sql;

			con = new DBSource().getConnection(dbname);
			rs = new RecordSet(query);
			rs.setLogEnabled(logging);
			rs.level = level;
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	public Result query(String sql, String[] val, int curpage, int pagesize) {
		return query(6, sql, val, curpage, pagesize);
	}

	public Result query(int level, String sql, List list, int curpage,
			int pagesize) {
		String[] vals = null;
		if (list == null)
			vals = new String[0];
		else
			vals = new String[list.size()];
		list.toArray(vals);
		return query(level, sql, vals, curpage, pagesize);
	}

	public Result query(int level, String sql, String[] val, int curpage,
			int pagesize) {

		Result result = new Result();
		Connection con = null;
		try {
			RecordSet rs = null;
			String query = sql;

			con = new DBSource().getConnection(dbname);
			rs = new RecordSet(query, curpage, pagesize);
			rs.setLogEnabled(logging);
			rs.level = level;
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	public Result query(Connection con, String sql) {
		return query(con, sql, null);
	}

	public Result query(Connection con, String sql, String[] val) {

		Result result = new Result();
		try {

			RecordSet rs = null;
			String query = sql;
			rs = new RecordSet(query);
			rs.setLogEnabled(logging);
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		}

		return result;
	}

	public Result query(int depth, Connection con, String sql, String[] val) {
		return query(depth, con, sql, val, 0, 0);
	}

	public Result query(int depth, Connection con, String sql, String[] val,
			int curpage, int pagesize) {

		Result result = new Result();
		try {

			RecordSet rs = null;
			String query = sql;
			rs = new RecordSet(query, curpage, pagesize);
			rs.level = depth;
			rs.setLogEnabled(logging);
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		}

		return result;
	}

	public Result callQuery(int level, String sql, List list, int curpage,
			int pagesize) {
		String[] vals = null;
		if (list == null)
			vals = new String[0];
		else
			vals = new String[list.size()];
		list.toArray(vals);
		return callQuery(level, sql, vals, curpage, pagesize);
	}

	public Result callQuery(int level, String sql, String[] val, int curpage,
			int pagesize) {

		Result result = new Result();
		Connection con = null;
		try {
			CSRecordSet rs = null;
			String query = sql;

			con = new DBSource().getConnection(dbname);
			rs = new CSRecordSet(query, curpage, pagesize);
			rs.setLogEnabled(logging);
			rs.level = level;
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	public Result callQuery(Connection con, String sql, String[] val) {
		return callQuery(6, con, sql, val);

	}

	public Result callQuery(int depth, Connection con, String sql, String[] val) {

		Result result = new Result();
		try {

			CSRecordSet rs = null;
			String query = sql;
			rs = new CSRecordSet(query);
			rs.level = depth;
			rs.setLogEnabled(logging);
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		}

		return result;
	}

	public Result queryWrapper(Connection con, String sql, String[] val) {

		Result result = new Result();
		try {

			RecordSet rs = null;
			String query = sql;
			rs = new ResultSetWrapper(query);

			rs.setLogEnabled(logging);
			rs.executeQuery(con, val);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		}
		return result;
	}

	public Result queryWrapper(Connection con, String sql) {

		Result result = new Result();
		try {

			RecordSet rs = null;
			String query = sql;
			rs = new ResultSetWrapper(query);
			rs.setLogEnabled(logging);
			rs.executeQuery(con);
			result.setRecordSet(rs);
		} catch (SQLException e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		} catch (Exception e) {
			org.sdf.log.Log.sql.err(sql, e);
			result.setException(e);
		}
		return result;
	}

	boolean logging = true;
}