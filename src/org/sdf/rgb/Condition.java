package org.sdf.rgb;

import java.util.Hashtable;

/*
 * 
 * <PRE>
 * ??ë¡????¸ë?    : Help Desk ???? êµ¬ì?
 * ??ë¡?ê·¸ë?¨ê??? : 
 * ê´??? ???´ë?   : 
 * ?´ë???¤ë? 	    : Condition
 * ???±ì?? 	        : ë°??±í??
 * ???±ì?? 	        : 2005. 7. 8.
 * ë¹?ê³? 		        : 
 * ê°????´ë??  	    : 2005. 7. 8. ë°??±í??, v1.0, ìµ?ì´?????
 *                     
 * ***********************************************
 * ?¥í?? is null, is not null?? ???? val_hash ì¶?ê°?                       
 * </PRE>
 */

public class Condition {
	boolean where = false;
	// where ë°? and ì¡°ê±´ ë¯¸í????
	boolean nolink = false;
	Hashtable hash;
	Hashtable val_hash;
	String[] vals;
	String sql;
	String sort;

	public final static int EQUAL = 1;
	public final static int LIKE = 2;
	public final static int BETWEEN = 4;
	public final static int GREATER = 8;
	public final static int LESS = 16;
	public final static int IN = 32;
	public final static int NOT_EQUAL = 64;
	public final static int EQUAL_NULL = 128;
	public final static int NOT_EQUAL_NULL = 256;
	public final static int STATEMENT = 1024;
	public final static int MULTI_STATEMENT = 2048;
	public final static int REF_IN = 512;
	public final static int NOT = 1000;

	/**
	 * 
	 * @param where
	 *            Where ?? ì¡´ì?? ?¬ë?
	 */
	public Condition(boolean where) {
		this.where = where;
	}

	public Condition() {
		this.nolink = true;
	}

	/**
	 * operator '=' ë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void add(String key, String value) {
		add(key, value, Condition.EQUAL, false);
	}

	public void add(String key, String value, boolean require) {
		add(key, value, Condition.EQUAL, require);
	}

	/**
	 * operator '=' ë¡? ì»¨ë???? ì¶?ê°? ë§??? value ê°? null ?? ê²½ì?? [ is null ]ë¡? ë³?ê²?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void addNull(String key, String value) {
		add(key, value, Condition.EQUAL_NULL, false);
	}

	/**
	 * operator '=' ë¡? ì»¨ë???? ì¶?ê°? ë§??? value ê°? null ?? ê²½ì?? [ is not null ]ë¡? ë³?ê²?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void addNotNull(String key, String value) {
		add(key, value, Condition.NOT_EQUAL_NULL, false);
	}

	/**
	 * operator 'like' ë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void addLike(String key, String value) {
		add(key, value, Condition.LIKE, false);
	}

	/**
	 * operator 'in' ë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param values
	 *            Binding ë³?????
	 */
	public void addIn(String key, String[] values) {
		if (key == null || values == null || values.length == 0)
			return;
		boolean flag = false;
		for (int i = 0; i < values.length; i++) {
			flag = flag || org.sdf.util.StringUtil.valid(values[i]);
		}
		if (!flag)
			return;

		MultiItem item = new MultiItem(Condition.IN);
		item.addValues(values);

		addItem(key, item);
	}

	/**
	 * Statementë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void addStatement(String key, String statement, String value) {
		if (key == null || value == null || value.length() == 0)
			return;
		StatementItem item = new StatementItem(statement, value, 1);
		addItem(key, item);
	}

	/**
	 * Statementë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void addStatements(String key, String statement, String[] values) {

		if (key == null)
			return;
		if (statement == null || statement.trim().equals(""))
			return;
		int len = statement.length();
		int cnt = 0;
		for (int i = 0; i < len; i++) {
			if (statement.charAt(i) == '?')
				cnt++;
		}
		if (cnt == 0) {
			if (values != null && values.length != cnt)
				return;
		} else {
			if (values != null && values.length != cnt)
				return;
		}

		StatementMultiItem item = new StatementMultiItem(statement, values,
				Condition.MULTI_STATEMENT);
		addItem(key, item);
	}

	/**
	 * operator 'like' ë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Binding ë³???
	 */
	public void addRefIn(String key, String ref, String value) {
		if (key == null || value == null || value.equals(""))
			return;

		RefInItem item = new RefInItem(ref, value);

		addItem(key, item);
	}

	/**
	 * operator 'between sval and eval' ë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param sval
	 *            ???? Binding ë³???
	 * @param eval
	 *            ?? Binding ë³???
	 */
	public void addBetween(String key, String sval, String eval) {
		addBetween(key, sval, eval, null, null);
	}

	/**
	 * <pre>
	 * operator 'between sval and eval' ë¡? ì»¨ë???? ì¶?ê°?
	 * 	ex. addBetween( "t.dt", data.get("sdate"), date.get("edate"), "yyyymmdd", "-(9/24)");
	 *            --> dt between to_date( ?, 'yyyymmdd' ) - (9/24) and to_date( ?, 'yyyymmdd' ) - (9/24)
	 * </pre>
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param sval
	 *            ???? Binding ë³???
	 * @param eval
	 *            ?? Binding ë³???
	 * @param dt_format
	 *            ?°ì?´í?°í?¬ë§· ì§???
	 * @param etc
	 *            ê¸°í??
	 */
	public void addBetween(String key, String sval, String eval,
			String dt_format, String etc) {
		// System.out.println("addB--" +key + ":" +sval + "," + eval);
		if (key == null || sval == null || eval == null)
			return;
		boolean flag = false;
		flag = flag || org.sdf.util.StringUtil.valid(sval);
		flag = flag || org.sdf.util.StringUtil.valid(eval);

		if (!flag)
			return;

		// System.out.println(".addB--" +key + ":" +sval + "," + eval);
		BetweenItem item = new BetweenItem(Condition.BETWEEN, dt_format, etc);
		String[] values = new String[2];
		values[0] = sval;
		values[1] = eval;
		item.addValues(values);
		addItem(key, item);
	}

	void addItem(String key, Item item) {
		if (hash == null) {
			hash = new Hashtable();
			// val_hash = new Hashtable();
		}

		hash.put(key, item);
		// if( item.type != Condition.NOT_EQUAL_NULL && item.type ==
		// Condition.EQUAL_NULL)
		// val_hash.put( key, item);
	}

	public void add(String key, String value, int type) {
		if (key == null)
			return;
		if (value == null || value.equals("")) {
			return;
		}

		Item item = new Item(value, type);
		addItem(key, item);
	}

	/**
	 * ì§????? operator ë¡? ì»¨ë???? ì¶?ê°?
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Bingding ë³???
	 * @param type
	 *            operation ????
	 * @param require
	 *            ???????? ?¬ë? type?? Equal?? ê²½ì?°ì??ë§? ì²´í??
	 */
	public void add(String key, String value, int type, boolean require) {
		if (key == null)
			return;
		if (type == Condition.EQUAL && require) {

		} else if (value == null || value.equals("")) {
			return;
		}

		Item item = null;
		switch (type) {
		case Condition.EQUAL_NULL:
			item = new NullItem(value, type);
		case Condition.NOT_EQUAL_NULL:
			item = new NullItem(value, type);
		default:
			item = new Item(value, type, require);
		}

		addItem(key, item);
	}

	/**
	 * 
	 * @param key
	 *            SQL?? ì»¬ë?¼ë?
	 * @param value
	 *            Bingding ë³???
	 * @param type
	 *            operation ????
	 * @param dt_format
	 *            ??ì§??¬ë§·
	 * @param etc
	 *            ê¸°í??
	 */
	public void addDate(String key, String value, int type, String dt_format,
			String etc) {
		if (key == null || value == null || value.equals(""))
			return;

		DateItem item = new DateItem(value, type, dt_format, etc);

		addItem(key, item);
	}

	/**
	 * <pre>
	 * SQL String?? ë¦¬í??
	 * Binding ë³??? ê°??? ???¨í??ì§? ????ê²½ì?? ?´ë?? ì¡°ê±´?? ???¸í??ê³? ë¦¬í??
	 * </pre>
	 * 
	 * @return
	 */
	public String getSQLString() {
		if (hash == null)
			return "";
		if (sql != null)
			return sql;

		java.util.Enumeration keys = hash.keys();

		StringBuffer buf = new StringBuffer();
		if (!nolink) {
			if (where)
				buf.append("and ");
			else
				buf.append("where ");
		}
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			buf.append(key);
			Item item = (Item) hash.get(key);
			buf.append(item.getOperator());

			if (!keys.hasMoreElements())
				break;
			buf.append("\n\tand ");
		}

		sql = buf.toString();
		return sql;
	}

	public boolean hasCondition() {
		if (hash == null)
			return false;
		if (hash.size() == 0)
			return false;
		return true;
	}

	/**
	 * <pre>
	 * SQL Binding ë³???ë¥? ë¦¬í??
	 *  Binding ë³??? ê°??? ???¨í??ì§? ????ê²½ì?? ?´ë?? Bindingë³????? ???¸í??ê³? ë¦¬í??
	 * </pre>
	 * 
	 * @return String[]
	 */

	public String[] getValues() {
		// ------------------------------------------------------------------------------
		// ì¶??? hash --> val_hash ë¡? ????
		// ------------------------------------------------------------------------------
		if (hash == null) {
			String[] val = new String[0];
			return val;
		}

		if (vals != null)
			return vals;

		vals = new String[getValueCount()];

		java.util.Enumeration keys = hash.keys();

		int i = 0;
		while (keys.hasMoreElements()) {
			Item item = (Item) hash.get(keys.nextElement());
			if (item instanceof MultiItem) {
				String[] values = ((MultiItem) item).getValues();
				for (int j = 0; j < values.length; j++)
					vals[i++] = values[j];
			} else if (item instanceof BetweenItem) {

				String[] values = ((BetweenItem) item).getValues();
				for (int j = 0; j < values.length; j++)
					vals[i++] = values[j];
			} else
				vals[i++] = item.getValue();
		}

		return vals;
	}

	/**
	 * ???¨í?? ë³??? Countë¥? ë¦¬í??
	 * 
	 * @return int ë³??? Count
	 */
	public int getValueCount() {
		// ------------------------------------------------------------------------------
		// ì¶??? hash --> val_hash ë¡? ????
		// ------------------------------------------------------------------------------
		if (hash == null)
			return 0;

		java.util.Enumeration keys = hash.keys();

		int count = 0;
		while (keys.hasMoreElements()) {
			Item item = (Item) hash.get(keys.nextElement());
			count += item.getValueCount();
		}
		return count;
	}

	public void destroy() {
		if (hash != null) {
			hash.clear();
			hash = null;
			sql = null;
			vals = null;
		}
	}

	class Item {
		String value;
		int type = Condition.EQUAL;
		boolean require = false;

		Item(int type) {
			this.type = type;
		}

		Item(String value, int type) {
			this.value = value;
			this.type = type;
		}

		Item(String value, int type, boolean require) {
			this(value, type);
			this.require = require;
		}

		String getOperator() {

			return _getOperator() + "? ";
		}

		String _getOperator() {
			switch (type) {
			case Condition.LIKE:
				return " like ";
			case Condition.GREATER:
				return " > ";
			case Condition.LESS:
				return " < ";
			case Condition.GREATER + Condition.EQUAL:
				return " >= ";
			case Condition.LESS + Condition.EQUAL:
				return " <= ";
			case Condition.NOT_EQUAL:
				return " <> ";
			case Condition.IN:
				return " in	";
			case Condition.BETWEEN:
				return " between ";
			case Condition.EQUAL_NULL:
				return " is null ";
			case Condition.NOT_EQUAL_NULL:
				return " is not null ";
			}

			return "=";
		}

		int getValueCount() {

			return 1;
		}

		String getValue() {
			if (type == Condition.LIKE)
				return "%" + value + "%";
			return value;
		}
	}

	class NullItem extends Item {
		NullItem(int type) {
			super(type);
		}

		NullItem(String value, int type) {
			super(value, type);
		}

		String getOperator() {

			return _getOperator();
		}

		String _getOperator() {
			if (value == null || value.length() == 0)
				return "= ? ";

			switch (type) {
			case Condition.EQUAL_NULL:
				return " is null ";
			case Condition.NOT_EQUAL_NULL:
				return " is not null ";
			}
			return " = ? ";
		}

		int getValueCount() {
			if (value == null || value.length() == 0)
				return 0;
			return 1;
		}

		String getValue() {

			if (type == Condition.LIKE)
				return "%" + value + "%";
			return value;
		}
	}

	class DateItem extends Item {
		String dt_format;
		String etc;

		DateItem(int type) {
			super(type);
		}

		DateItem(String value, int type) {
			super(value, type);
		}

		DateItem(String value, int type, String dt_format) {
			this(value, type);
			this.dt_format = dt_format;
		}

		DateItem(String value, int type, String dt_format, String etc) {
			this(value, type, dt_format);
			this.etc = etc;
		}

		String getOperator() {
			switch (type) {
			case Condition.GREATER:
				return getDateOperator(" > ");
			case Condition.LESS:
				return getDateOperator(" < ");
			case Condition.GREATER + Condition.EQUAL:
				return getDateOperator(" >= ");
			case Condition.LESS + Condition.EQUAL:
				return getDateOperator(" <= ");
			case Condition.NOT_EQUAL:
				return getDateOperator(" <> ");
			}
			return super.getOperator();
		}

		String getDateOperator(String v) {
			if (dt_format != null)
				v += "to_date(?,'" + dt_format + "')";
			else
				v += "?";

			if (etc != null)
				v += etc;
			return v + " ";
		}
	}

	class MultiItem extends Item {
		String[] values;

		MultiItem(String value, int type) {
			super(value, type);
		}

		MultiItem(int type) {
			super(type);
		}

		public void addValues(String[] values) {
			this.values = values;
		}

		String getOperator() {
			switch (type) {
			case Condition.IN:
				String tmp = " in (";
				for (int i = 0; i < values.length; i++) {
					tmp += " ?";
					if (i >= values.length - 1)
						break;
					tmp += ",";
				}
				tmp += " ) ";
				return tmp;
			}

			return "=";
		}

		int getValueCount() {
			if (values == null)
				return 0;
			return values.length;
		}

		String[] getValues() {
			return values;
		}
	}

	class StatementItem extends Item {
		String statement;

		StatementItem(String statement, String value, int type) {
			super(value, type);
			this.statement = statement;
		}

		StatementItem(String value, int type) {
			super(value, type);
		}

		StatementItem(int type) {
			super(type);
		}

		String getOperator() {
			return " " + statement;
		}
	}

	class StatementMultiItem extends MultiItem {
		String statement;

		StatementMultiItem(String statement, String[] value, int type) {
			super(type);
			this.statement = statement;
			addValues(value);
		}

		StatementMultiItem(String[] value, int type) {
			super(type);
		}

		StatementMultiItem(int type) {
			super(type);
		}

		String getOperator() {
			return " " + statement;
		}
	}

	class RefInItem extends Item {
		String ref;

		RefInItem(String ref, String value) {
			this(ref, value, Condition.REF_IN);
		}

		RefInItem(String ref, String value, int type) {
			super(value, type);
			this.ref = ref;
		}

		RefInItem(String ref, int type) {
			super(type);
			this.ref = ref;
		}

		public void addValue(String values) {
			this.value = values;
		}

		String getOperator() {
			switch (type) {
			case Condition.REF_IN:
				String tmp = " in (";
				tmp += ref;
				tmp += " ) ";
				return tmp;
			}

			return "=";
		}

		int getValueCount() {
			return 1;
		}

		String getValue() {
			return value;
		}
	}

	class BetweenItem extends Item {
		String[] values;
		String dt_format;
		String etc;

		BetweenItem(int type, String dt_format, String etc) {
			this(type, dt_format);
			this.etc = etc;
		}

		BetweenItem(int type, String dt_format) {
			this(type);
			this.dt_format = dt_format;
		}

		BetweenItem(String value, int type) {
			super(value, type);
		}

		BetweenItem(int type) {
			super(type);
		}

		public void addValues(String[] values) {
			this.values = values;
		}

		String getOperator() {
			switch (type) {
			case Condition.BETWEEN:
				String v = "?";
				if (dt_format != null) {
					v = "to_date(?,'" + dt_format + "')";
				}
				if (etc != null)
					v += etc;
				return " between " + v + " and " + v + " ";
			}

			return "=";
		}

		int getValueCount() {
			if (values == null)
				return 0;
			return values.length;
		}

		String[] getValues() {
			return values;
		}
	}

	public void setSort(String sort) {
		if (sort == null || sort.trim().length() == 0)
			this.sort = "";
		else
			this.sort = "order by " + sort;
	}

	public String getSort() {
		if (sort == null)
			return "";
		return sort;

	}
}