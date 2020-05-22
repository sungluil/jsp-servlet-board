package org.sdf.rgb;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sdf.lang.AList;
import org.sdf.log.Log;

public class DmlCfg {

	List inserts;
	List updates;
	List deletes;
	AList etc;

	public DmlCfg() {
		inserts = new ArrayList();
		updates = new ArrayList();
		deletes = new ArrayList();
		etc = new AList();
	}

	public List getInserts() {
		return inserts;
	}

	public List getUpdates() {
		return updates;
	}

	public List getDeletes() {
		return deletes;
	}

	public void setInsert(String s) {
		try {
			JSONArray arr = (JSONArray) JSONValue.parse(s);
			for (int i = 0; arr != null && i < arr.size(); i++) {
				JSONObject o = (JSONObject) arr.get(i);
				Cfg cfg = new Cfg(o);
				cfg.setType("insert");
				inserts.add(cfg);
			}
		} catch (Exception e) {
			Log.cfg.err("DmlCfg.Inserts", e);
		}
	}

	public void setUpdate(String s) {
		try {
			JSONArray arr = (JSONArray) JSONValue.parse(s);
			for (int i = 0; arr != null && i < arr.size(); i++) {
				JSONObject o = (JSONObject) arr.get(i);
				Cfg cfg = new Cfg(o);
				cfg.setType("update");
				updates.add(cfg);
			}
		} catch (Exception e) {
			Log.cfg.err("DmlCfg.Updates", e);
		}
	}

	public void setDelete(String s) {
		try {
			JSONArray arr = (JSONArray) JSONValue.parse(s);
			for (int i = 0; arr != null && i < arr.size(); i++) {
				JSONObject o = (JSONObject) arr.get(i);
				Cfg cfg = new Cfg(o);
				cfg.setType("delete");
				deletes.add(cfg);
			}
		} catch (Exception e) {
			Log.cfg.err("DmlCfg.Deletes", e);
		}
	}

	public void setEtc(String s) {
		try {
			JSONArray arr = (JSONArray) JSONValue.parse(s);
			for (int i = 0; arr != null && i < arr.size(); i++) {
				JSONObject o = (JSONObject) arr.get(i);
				Cfg cfg = new Cfg(o);
				etc.add(cfg.id, cfg);
			}
		} catch (Exception e) {
			Log.cfg.err("DmlCfg.Etc", e);
		}
	}

	public class Cfg {
		public String type;
		public String id;
		public String ent_id;
		public String table;
		public String strCols;
		public String strVals;
		public String strVnames;
		public String strVars;

		public String keyCol;
		public String keyVname;
		public String[] cols;
		public String[] vals;
		public String[] vnames;

		public Cfg(JSONObject o) {
			type = o.getString("type");
			id = o.getString("id");
			ent_id = o.getString("ent_id");
			table = o.getString("table");
			strCols = o.getString("cols");
			strVals = o.getString("vals");
			strVnames = o.getString("vnames");
			strVars = o.getString("vars");

			keyCol = o.getString("keycol");
			keyVname = o.getString("keyvname");

			cols = org.sdf.util.StringUtil.getArray(strCols, ',');

			if (strVals == null || strVals.equals("")) {
				vals = new String[cols.length];
				for (int i = 0; i < vals.length; i++) {
					vals[i] = "?";
				}
			} else {
				vals = org.sdf.util.StringUtil.getArray(strVals, ';');
			}

			if (strVnames == null || strVnames.equals("")) {
				vnames = cols;
			} else {
				vnames = org.sdf.util.StringUtil.getArray(strVnames, ',');
			}

			if (keyVname == null || keyVname.equals("")) {
				keyVname = "key";
			}
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSql() throws Exception {
			if (type.equals("insert")) {
				return getInsertSql();
			} else if (type.equals("update")) {
				return getUpdateSql();
			} else if (type.equals("delete")) {
				return getDeleteSql();
			}
			return "";
		}

		public String getUpdateSql() throws Exception {
			StringBuffer buf = new StringBuffer();
			buf.append("update ").append(table).append("\nset ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(", ");
				buf.append(cols[j]).append(" = ").append(vals[j]);
			}
			buf.append("\nwhere ").append(keyCol).append(" = ? ");

			String sql = buf.toString();

			return sql;
		}

		public String getInsertSql() throws Exception {

			StringBuffer buf = new StringBuffer();
			buf.append("insert into ").append(table).append("\n( ");
			for (int j = 0; j < cols.length; j++) {
				if (j > 0)
					buf.append(", ");
				buf.append(cols[j]);
			}
			buf.append(") ").append("\nvalues(");
			for (int j = 0; j < vals.length; j++) {
				if (j > 0)
					buf.append(", ");
				buf.append(vals[j]);
			}
			buf.append(") ");
			String sql = buf.toString();

			return sql;
		}

		public String getDeleteSql() throws Exception {

			StringBuffer buf = new StringBuffer();
			buf.append("delete from ").append(table);
			buf.append("\nwhere ").append(keyCol).append("=").append("?");
			String sql = buf.toString();

			return sql;
		}

		public String[] getCols() {
			return cols;
		}

		public String[] getVals() {

			return vals;
		}

		public String[] getVnames() {
			return vnames;
		}

	}

}
