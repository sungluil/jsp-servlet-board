/**
 * CondItem
 */
package org.sdf.rgb;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.sdf.lang.Data;

/**
 * column, operator, statement, args ex. [t.id] [=] [] [#{box.id}] [t.id] [like]
 * [] [%#{box.id}%] [t.dt] [between] [] [#{box.sdate};#{box.edate}] [t.id]
 * [statement] [in (select id from table where type = ?] [#{box.type}] [t.id]
 * [mstatement] [in (select id from table where type = ? and emp_id = ?]
 * [#{box.type},#{session.emp_id}]
 * 
 * @author shpark
 * 
 */
public class CondItem {
	public String col;
	public int oper;
	public String stmt;
	public String vstmt;
	Args[] args;

	public CondItem(String col, int oper, String v) {
		this.col = col;
		this.oper = oper;
		this.vstmt = v;
		this.args = parse(v);
	}

	public CondItem(String col, int oper, String stmt, String v) {
		this.col = col;
		this.oper = oper;
		this.stmt = stmt;
		this.vstmt = v;
		this.args = parse(v);
	}

	public String value(HttpSession session, Data data) {
		if (args.length > 0) {

			String v = args[0].value(session, data);

			return v;
		}
		return "";
	}

	public String[] values(HttpSession session, Data data) {
		if (args.length > 0) {
			String[] vals = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				vals[i] = args[i].value(session, data);

			}
			return vals;
		}
		return new String[0];
	}

	public String[] valueArray(HttpSession session, Data data) {
		if (args.length > 0) {
			String[] vals = args[0].values(session, data);

			return vals;
		}
		return new String[0];
	}

	public Condition apply(Condition cond, HttpSession session, Data data) {
		switch (this.oper) {
		case Condition.EQUAL: {

			String v = this.value(session, data);

			cond.add(this.col, v);
		}
			break;
		case Condition.LIKE:
		case Condition.GREATER:
		case Condition.LESS:
			cond.add(this.col, this.value(session, data), this.oper);
			break;
		case Condition.BETWEEN:
			String[] vals = this.values(session, data);

			if (vals.length == 2)
				cond.addBetween(this.col, vals[0], vals[1]);
			break;
		case Condition.IN:
			cond.addIn(this.col, this.valueArray(session, data));
			break;
		case Condition.STATEMENT:
			cond.addStatement(this.col, this.stmt, this.value(session, data));
			break;
		case Condition.MULTI_STATEMENT:

			vals = this.values(session, data);

			cond.addStatements(this.col, this.stmt, vals);
			break;
		}
		return cond;
	}

	/**
	 * @param v
	 * @return
	 */
	/**
	 * @param v
	 * @return
	 */
	/**
	 * @param v
	 * @return
	 */
	/**
	 * @param v
	 * @return
	 */
	Args[] parse(String v) {
		List l = new ArrayList();
		String[] arr = org.sdf.util.StringUtil.getArray(v, ';');

		for (int i = 0; i < arr.length; i++) {
			int idx = arr[i].indexOf("#{box.");
			if (idx != -1) {
				int idx1 = arr[i].indexOf("}");
				if (idx1 == -1)
					continue;
				Args a = new Args(arr[i].substring(idx + 6, idx1), arr[i],
						CondItem.BOX);
				l.add(a);

			} else {
				idx = arr[i].indexOf("#{session.");
				if (idx != -1) {
					int idx1 = arr[i].indexOf("}");
					if (idx1 == -1)
						continue;
					Args a = new Args(arr[i].substring(idx + 10, idx1), arr[i],
							CondItem.SESSION);
					l.add(a);

				}
			}
		}

		if (l.size() == 0)
			return new Args[0];
		Args[] args = new Args[l.size()];
		return (Args[]) l.toArray(args);
	}

	class Args {

		String key;
		String templ;
		int type;

		public Args(String key, String templ, int type) {
			this.key = key;
			this.templ = templ;
			this.type = type;
		}

		String value(HttpSession session, Data data) {
			if (type == CondItem.BOX) {
				return org.sdf.util.StringUtil.replace(templ, "#{box." + key
						+ '}', data.get(key));
			} else if (type == CondItem.SESSION) {
				if (session == null)
					return "";
				String v = (String) session.getAttribute(key);
				return org.sdf.util.StringUtil.replace(templ, "#{session."
						+ key + '}', data.get(key));
			}
			return "";
		}

		String[] values(HttpSession session, Data data) {
			if (type == CondItem.BOX) {
				return data.getArray(key);
			} else if (type == CondItem.SESSION) {
				try {
					if (session == null)
						return new String[0];
					return (String[]) session.getAttribute(key);
				} catch (Exception e) {

				}
			}
			return new String[0];
		}

		public String toString() {
			return key + ":" + templ + ":" + type;

		}
	}

	public final static int BOX = 1;
	public final static int SESSION = 2;

	public static void main(String[] args) {
		Data d = new Data();
		Condition cond = new Condition(true);
		// d.put("arg1", "Test");
		d.put("sdate", "20090701");
		d.put("edate", "20090731");
		String[] s = { "A01", "A02", "A03" };
		// d.put("cd", s);

		CondItem item = new CondItem("t.col", Condition.EQUAL, "",
				"ABC#{box.arg1}DEF");
		cond = item.apply(cond, null, d);

		item = new CondItem("t.col1", Condition.LIKE, "", "AAA#{box.arg1}AAA");
		cond = item.apply(cond, null, d);

		item = new CondItem("t.col2", Condition.BETWEEN, "",
				"#{box.sdate};#{box.edate}");
		cond = item.apply(cond, null, d);

		item = new CondItem("t.col3", Condition.STATEMENT,
				" in (select id from tab where dt = ?)", "#{box.sdate}");
		cond = item.apply(cond, null, d);

		item = new CondItem("t.col4", Condition.MULTI_STATEMENT,
				" in (select id from tab where dt between ? and ?)",
				"#{box.sdate};#{box.edate}");
		cond = item.apply(cond, null, d);

		item = new CondItem("t.col5", Condition.IN, "", "#{box.cd}");
		cond = item.apply(cond, null, d);

		System.out.println("> SQL:" + cond.getSQLString());
		String[] vals = cond.getValues();
		for (int i = 0; i < vals.length; i++) {
			System.out.println("  [" + i + "] " + vals[i]);
		}

	}
}
