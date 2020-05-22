package org.sdf.rgb;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sdf.lang.AList;
import org.sdf.lang.Data;
import org.sdf.log.Log;

public class SqlCfg {
	public String sql;
	public String strArgs;
	public String[] args;

	public AList conds;

	public SqlCfg() {
		conds = new AList();
	}

	public SqlCfg(String sql, String conf) throws Exception {
		this();
		setSql(sql);
		setConf(conf);
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setConf(String conf) throws Exception {
		try {
			JSONObject o = (JSONObject) JSONValue.parse(conf);
			String t = o.getString("args");
			setArgs(t);

			JSONArray arr = o.getArray("conds");

			setConds(arr);
		} catch (Exception e) {
			Log.cfg.err(e);
			// throw e;
		}
	}

	public int size() {
		if (args == null)
			return 0;
		return args.length;
	}

	public Query getQuery(Data data) {
		Query query = new Query(this.sql);

		for (int i = 0; i < size(); i++) {
			String id = args[i];
			// System.out.println( ">"+i + ":" + id);
			if (id.equals(""))
				continue;
			char c = id.charAt(0);
			if (c != '#') {
				String v = data.get(id);
				// System.out.println(">>"+i + ":" + v);
				query.addValue(v);

				continue;
			}

			id = id.substring(1);
			Cond cond = getCond(id);
			if (cond == null)
				continue;
			query.parseCond(cond, data);
		}

		return query;
	}

	protected Cond getCond(String id) {
		return (Cond) conds.get(id);
	}

	protected void setArgs(String str) {
		this.strArgs = str;

		args = org.sdf.util.StringUtil.getArray(str, ",");
		// System.out.println(str + ":" + args.length);
	}

	protected void setConds(JSONArray arr) {
		if (arr == null || arr.size() == 0)
			return;
		for (int i = 0; i < arr.size(); i++) {
			JSONObject o = (JSONObject) arr.get(i);
			Cond cond = new Cond(o);
			conds.add(cond.id, cond);
		}
	}

	public class Query {
		String sql;
		List vals;

		public Query(String sql) {
			this.sql = sql;
			vals = new ArrayList();
		}

		public void addValue(String v) {
			vals.add(v);
		}

		public void parseCond(Cond cond, Data data) {
			StringBuffer buf = new StringBuffer();

			int idx = 0;
			for (int i = 0; i < cond.size(); i++) {
				Arg arg = (Arg) cond.get(i);
				String tmp = arg.parse(cond, data, vals);
				// System.out.println(">>" +i + ":"+ arg.id + " --" + tmp);
				if (tmp.equals(""))
					continue;

				if (!cond.where && idx == 0)
					tmp = " where " + tmp;
				else
					tmp = " and " + tmp;
				buf.append(tmp);
				idx++;
			}

			String key = "#{" + cond.id + "}";
			sql = org.sdf.util.StringUtil.replace(sql, key, buf.toString());

		}
	}

	public class Cond extends AList {
		boolean where;
		String id;

		public Cond(String id) {
			super();
			this.id = id;

		}

		public Cond(JSONObject o) {
			this(o.getString("id"));
			this.where = o.getBoolean("where");
			setArgs(o.getArray("args"));
		}

		public void setArgs(JSONArray arr) {
			if (arr == null || arr.size() == 0)
				return;
			for (int i = 0; i < arr.size(); i++) {
				JSONObject o = (JSONObject) arr.get(i);
				Arg arg = new Arg(o);
				add(arg);
			}
		}

		public void addArg(Arg arg) {
			add(arg.id, arg);
		}

		public Arg getArg(String id) {
			return (Arg) get(id);
		}
	}

	public class Arg {
		public String id;
		public String vname;
		public String oper;
		public boolean require;

		public Arg(String id, String vname, String oper) {
			this.id = id;
			this.vname = vname;
			if (this.vname == null || this.vname.equals(""))
				this.vname = id;
			this.oper = ((oper == null || oper.trim().equals("")) ? "=" : oper
					.trim());
		}

		public Arg(String id, String vname, String oper, boolean require) {
			this(id, vname, oper);
			this.require = require;
		}

		public Arg(JSONObject o) {
			this(o.getString("id"), o.getString("vname"), o.getString("oper"),
					o.getBoolean("require"));
		}

		public String parse(Cond cond, Data data, List list) {

			// oper == '=' ?手化??
			if (oper.equals("=")) {
				if (!data.valid(vname)) {
					if (!require) {
						return "";
					}
					list.add("");
					return id + " = ?";
				}

				String v = data.get(vname);
				list.add(v);
				return id + " = ?";

			}
			// oper == 'in' ?手化??
			else if (oper.equals("in")) {
				String[] vals = data.getArray(vname);
				if (vals == null || vals.length == 0) {
					if (!require) {
						return "";
					}
					list.add("");
					return id + " in (?)";
				}

				String tmp = id + " in (";
				for (int i = 0; i < vals.length; i++) {
					if (i > 0)
						tmp += ",";
					tmp += "?";
					list.add(vals[i]);
				}
				tmp += ")";

				return tmp;
			}

			int len = oper.length();
			int cnt = 0;
			for (int i = 0; i < len; i++) {
				char c = oper.charAt(i);
				if (c == '?')
					cnt++;
			}
			String[] vnames = org.sdf.util.StringUtil.getArray(vname, ',');
			if (vnames.length != cnt) {
				return "";
			}

			boolean exists = true;
			for (int i = 0; i < vnames.length; i++) {
				exists = exists && data.valid(vnames[i]);
			}

			if (!exists) {
				if (!require) {
					return "";
				}

				for (int i = 0; i < vnames.length; i++) {
					list.add(data.get(vnames[i]));
				}
				return id + " " + oper;
			}

			for (int i = 0; i < vnames.length; i++) {
				list.add(data.get(vnames[i]));
			}
			return id + " " + oper;

		}
	}

	public static void main(String[] args) {
		String sql = new StringBuffer().append(
				"select * from tab where col = ? ").toString();
		/*
		 * .append("\n select * ") .append("\n from tab1  t, ")
		 * .append("\n        (select * from tab2 #{@cond1}) t2 ")
		 * .append("\n where t.col = ? ") .append("\n   and t.id = t2.id ")
		 * .append("\n #{@cond} ") .append("\n order by id ").toString();
		 */

		String conf = new StringBuffer().append("{ 'args':'col_1'} ")
				.toString();
		/*
		 * .append("\n { ") .append("\n 	'args':'#cond1,srm_id,#cond' ")
		 * .append("\n 	'conds':[ ") .append("\n 		{  ")
		 * .append("\n 			'id':'cond', ") .append("\n 			'where':true, ")
		 * .append("\n 			'args':[ ").append(
		 * "\n 				,{ 'id':'col1', 'vname':'col_1','oper':'','require':false} ")
		 * .append(
		 * "\n 				,{ 'id':'col2', 'vname':'a1,a2','oper':' in ( select * from aaa where a1 = ? and a2 = ?)','require':false} "
		 * ) .append("\n 			] ") .append("\n 		}, ") .append("\n 		{ ")
		 * .append("\n 			'id':'cond1', ") .append("\n 			'where':false, ")
		 * .append("\n 			'args':[ ").append(
		 * "\n 				,{ 'id':'col3', 'vname':'col_1','oper':'in','require':false} "
		 * ).append(
		 * "\n 				,{ 'id':'col4', 'vname':'a1,a2','oper':' in ( select * from aaa where a1 = ? and a2 = ?)','require':false} "
		 * ) .append("\n 			] ") .append("\n 		} ") .append("\n 	] ")
		 * .append("\n } ").toString();
		 */
		conf = conf.replace('\'', '"');

		Data data = new Data();
		// data.put("col_1","1");
		data.put("a1", "1");
		data.put("a2", "1");
		try {
			SqlCfg cfg = new SqlCfg(sql, conf);
			Query query = cfg.getQuery(data);
			System.out.println(query.vals.size() + "\n" + query.sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
