package org.sdf.rgb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sdf.lang.Data;
import org.sdf.lang.IData;
import org.sdf.log.Log;
import org.sdf.slim.config.GlobalResource;
import org.sdf.util.StringUtil;

public class SqlCond {
	public String id;
	public boolean where;
	List stmts = new ArrayList();

	boolean fail = false;

	final static int LIKE_BOTH = 11;
	final static int LIKE_LEFT = 12;
	final static int LIKE_RIGHT = 13;
	final static int BT_OF_DAY = 21;
	final static int ET_OF_DAY = 22;

	public SqlCond() {

	}

	public SqlCond(String s) {
		try {
			JSONObject o = (JSONObject) JSONValue.parse(s);
			init(o);
		} catch (Exception e) {
			fail = true;
			e.printStackTrace();
		}
	}

	public SqlCond(JSONObject o) {
		init(o);
	}

	public void init(JSONObject o) {
		id = o.getString("id");
		where = o.getBoolean("where");
		List l = o.getArray("stmts");

		for (int i = 0; l != null && i < l.size(); i++) {
			stmts.add(new Stmt((JSONObject) l.get(i)));
		}
	}

	public void setWhere(boolean w) {
		this.where = w;
	}

	public void addStmt(String stmt, List params) {
		addStmt(stmt, params, false, false);
	}

	public void addStmt(String stmt, List params, boolean require, boolean array) {
		stmts.add(new Stmt(stmt, params, require, array));
	}

	public boolean valid() {
		return !fail;
	}

	public boolean hasResult() {
		return (result != null && result.valid());
	}

	public String getSqlString() {
		if (hasResult())
			return result.getSql();
		return "";
	}

	public List getValues() {
		if (hasResult())
			return result.getVars();
		return new ArrayList();
	}

	StmtResult result;

	public StmtResult parse(IData data) {

		List vars = new ArrayList();
		StringBuffer buf = new StringBuffer();
		int idx = 0;
		for (int i = 0; i < stmts.size(); i++) {
			Stmt stmt = (Stmt) stmts.get(i);

			StmtResult r = stmt.parse(data);
			if (!r.valid())
				continue;
			if (idx == 0 && !where) {
				buf.append(" where ");
			} else {
				buf.append(" and ");
			}
			buf.append(r.getSql());
			vars.addAll(r.getVars());
			idx++;
		}

		if (idx == 0)
			result = new StmtResult();
		result = new StmtResult(buf.toString(), vars);
		return result;
	}

	public List getParams() {
		if (!valid())
			return new ArrayList();

		List l = new ArrayList();
		for (int i = 0; i < stmts.size(); i++) {
			Stmt stmt = (Stmt) stmts.get(i);
			l.addAll(stmt.params);
		}
		return l;
	}

	public String toParamString(Data d) {
		StringBuffer buf = new StringBuffer();
		List l = getParams();

		// System.out.println( "ToParamString:" +l.size());
		int idx = 0;
		for (int i = 0; i < l.size(); i++) {
			String key = (String) l.get(i);
			String[] vals = d.getArray(key);
			// System.out.println( "> " +key + ":" + vals.length);
			if (vals.length == 0) {
				if (!d.valid(key))
					continue;

				String v = d.get(key);
				if (idx++ > 0)
					buf.append("&");
				buf.append(key).append("=").append(v);
				continue;
			}
			for (int j = 0; j < vals.length; j++) {
				if (idx++ > 0)
					buf.append("&");
				buf.append(key).append("=").append(vals[j]);
			}
		}
		return buf.toString();
	}

	public class Stmt {
		public String stmt;
		public List params;
		public HashMap vals = new HashMap();
		public boolean require;
		public boolean array;
		public int mode;

		public Stmt(String stmt, List params) {

		    if(StringUtil.valid(stmt)) {
                stmt = StringUtil.replace(stmt, "#{db_owner}", GlobalResource.getInstance().getDbOwner());
            }
			this.stmt = stmt;
			this.params = params;
		}

		public Stmt(String stmt, List params, boolean require, boolean array) {
			this(stmt, params);
			this.require = require;
			this.array = array;
		}

		public Stmt(JSONObject o) {
			this(o.getString("stmt"), o.getArray("params"));
			this.require = o.getBoolean("require");
			this.array = o.getBoolean("array");

			setValues(o.getArray("valmap"));
		}

		public void setValues(List l) {
			if (l == null)
				return;
			for (int i = 0; i < l.size(); i++) {
				String s = (String) l.get(i);
				String[] arr = StringUtil.getArray(s, ':');
				if (arr == null || arr.length < 2)
					continue;
				vals.put(arr[0], arr);
			}
		}

		public String getValue(String key, String v) {
			String[] arr = (String[]) vals.get(key);
			if (arr == null || arr.length < 2)
				return v;
			if (!StringUtil.valid(v))
				return v;
			int mode = 0;
			try {
				mode = Integer.parseInt(arr[1]);
			} catch (Exception e) {
			}

			switch (mode) {
			case SqlCond.LIKE_BOTH:
				return "%" + v + "%";
			case SqlCond.LIKE_LEFT:
				return "%" + v;
			case SqlCond.LIKE_RIGHT:
				return v + "%";
			case SqlCond.BT_OF_DAY:
				return v + "000000";
			case SqlCond.ET_OF_DAY:
				return v + "235959";
			}
			return v;
		}

		public StmtResult parse(IData data) {
			List vars = new ArrayList();
			if (array) {
				for (int i = 0; params != null && i < params.size(); i++) {
					String param = (String) params.get(i);
					if (!data.valid(param))
						continue;
					String[] arr = data.getArray(param);
					for (int j = 0; arr != null && j < arr.length; j++)
						vars.add(arr[j]);
				}
				int size = vars.size();
				if (size == 0) {
					if (require) {
						String str = new String(stmt);
						str = StringUtil.replace(str, "#{array}",
								"?");
						vars.add("");
						return new StmtResult(str, vars);
					}
					return new StmtResult();
				}
				String tmp = "";
				for (int i = 0; i < size; i++) {
					if (i > 0)
						tmp += ",";
					tmp += "?";
				}

				String str = new String(stmt);
				str = StringUtil.replace(str, "#{array}", tmp);
				return new StmtResult(str, vars);
			}

			// if require, null
			if (require) {
				for (int i = 0; params != null && i < params.size(); i++) {
					String param = (String) params.get(i);
					String v = data.get(param);
					v = getValue(param, v);
					vars.add(v);
				}

				return new StmtResult(stmt, vars);
			}

			List l = new ArrayList();

			//
			for (int i = 0; params != null && i < params.size(); i++) {
				String param = (String) params.get(i);
				if (!data.valid(param))
					continue;

				String v = data.get(param);
				v = getValue(param, v);
				l.add(v);
			}

			if (params.size() != l.size()) {
				return new StmtResult();
			}

			for (int i = 0; i < l.size(); i++) {
				vars.add(l.get(i));
			}

			return new StmtResult(stmt, vars);
		}
	}

	public class StmtResult {
		String sql;
		List vars;

		public StmtResult() {
			vars = new ArrayList();
		}

		public StmtResult(String sql, List vars) {
			this.sql = sql;
			this.vars = vars;
		}

		public String getSql() {
			return sql;
		}

		public List getVars() {
			return vars;
		}

		public boolean valid() {
			return sql != null && !sql.equals("");
		}
	}

	public static SqlCond[] parseConds(String s) {
		try {
			Object o = JSONValue.parse(s);
			return SqlCond.parseConds(o);
		} catch (Exception e) {
			Log.cfg.err("SqlCond:" + s, e);
		}
		return new SqlCond[0];
	}

	public static SqlCond[] parseConds(Object o) {
		try {
			if (o instanceof JSONArray) {
				JSONArray arr = (JSONArray) o;
				SqlCond[] conds = new SqlCond[arr.size()];
				for (int i = 0; i < conds.length; i++) {
					conds[i] = new SqlCond();
					conds[i].init((JSONObject) arr.get(i));
				}
				return conds;
			} else if (o instanceof JSONObject) {
				SqlCond[] conds = new SqlCond[1];
				conds[0] = new SqlCond();
				conds[0].init((JSONObject) o);
				return conds;
			}
		} catch (Exception e) {
			Log.cfg.err("SqlCond:" + o, e);
		}
		return new SqlCond[0];
	}

	public static void main(String[] args) {
		String cfg = "{"
				+ "	 'id' : 'cond'"
				+ "	,'where' : true"
				+ "	,'stmts':["
				+ "		 { 'stmt':'srm_req_dttm1 between ? and ? ', 'params': [ 'sdate','edate'], 'require':true}"
				+ "		,{ 'stmt':'( srm_id = ? or srm_id = 1234) ', 'params': [ 'key'], 'require':true,'valmap':['key:11']}"
				+ "		,{ 'stmt':'( srm1_id = ? or srm1_id = 1234) ', 'params': [ 'key1'], 'require':true,'valmap':['key1:11']}"
				+ "		,{ 'stmt':'srm_cat_cd in (#{array})','params':['cat_cd','cat_cd1'], 'array':true, 'require':false}	"
				+ "	]        " + "}	";

		cfg = cfg.replace('\'', '"');

		SqlCond cond = new SqlCond(cfg);
		Data d = new Data();
		d.put("sdate", "20091001");
		d.put("edate", "20091031");

		String[] arr = { "1", "2", "3" };
		d.put("cat_cd", arr);

		String[] arr1 = { "a", "b", "c", "d" };
		d.put("cat_cd1", arr1);
		d.put("key", "01");
		StmtResult result = cond.parse(d);

		System.out.println("==============================");
		System.out.println(result.getSql());
		System.out.println("==============================");
		List l = result.getVars();
		for (int i = 0; i < l.size(); i++) {
			System.out.println(i + ":[" + l.get(i) + "]");
		}
	}

	public String[] getStmtParams() {
		HashMap params = new HashMap();

		for (int i = 0; i < stmts.size(); i++) {
			Stmt stmt = (Stmt) stmts.get(i);
			for (int j = 0; j < stmt.params.size(); j++) {
				String id = (String) stmt.params.get(i);
				params.put(id, id);
			}
		}

		Set set = params.keySet();
		String[] arr = new String[set.size()];
		Iterator itr = set.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			String s = (String) itr.next();
			arr[i] = s;
		}

		return arr;
	}

	public String getStmtParamString(Data d) {
		HashMap params = new HashMap();

		for (int i = 0; i < stmts.size(); i++) {
			Stmt stmt = (Stmt) stmts.get(i);
			for (int j = 0; j < stmt.params.size(); j++) {
				String id = (String) stmt.params.get(j);
				params.put(id, d.get(id));
			}
		}

		Set set = params.keySet();
		StringBuffer buf = new StringBuffer();
		Iterator itr = set.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			String s = (String) itr.next();
			if (i > 0)
				buf.append("&");
			buf.append(s).append("=").append(params.get(s));
		}
		return buf.toString();
	}
}
