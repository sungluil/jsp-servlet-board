/*****************************************************************
 * Class : Fetch.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.rgb;

import java.util.Vector;
import java.sql.*;

import org.sdf.lang.*;

abstract public class Fetch implements IData {
	public int curpage;
	public int pagesize;
	public int total;

	protected int currow = -1;

	public int getCurRow() {
		return currow;
	}

	public void setCurRow(int row) {
		this.currow = row;
	}

	public int getTotalPage() {
		if (pagesize <= 0)
			return 1;

		return (int) Math.ceil((double) total / (double) pagesize);
	}

	public int getCurPage() {
		return curpage;
	}

	public int getPageSize() {
		return pagesize;
	}

	abstract public int getColumnCount();

	abstract public int getRowCount();

	abstract public int getTotalCount();

	public void reset() {
		currow = -1;
	}

	public boolean first() {
		if (getRowCount() <= 0)
			return false;
		currow = 0;
		return true;
	}

	public boolean last() {
		if (getRowCount() <= 0)
			return false;
		currow = getRowCount() - 1;
		return true;
	}

	public boolean next() {
		currow++;

		if (currow < 0 || getRowCount() <= 0 || currow >= getRowCount())
			return false;
		return true;
	}

	public boolean has() {
		if (currow < 0 || getRowCount() <= 0 || (currow + 1) >= getRowCount())
			return false;
		return true;
	}

	public boolean pre() {
		currow--;

		if (currow < 0 || getRowCount() <= 0 || currow >= getRowCount())
			return false;
		return true;
	}

	abstract public String get(int row, int column);

	abstract public Object getObject(int row, int column);

	abstract public void put(int row, int column, String v);

	abstract public void put(int row, int column, Object v);

	public String get(int row, String name) {
		return get(row, findColumn(name));
	}

	public String get(int column) {
		return get(currow, column);
	}

	public Object getObject(String name) {
		return getObject(findColumn(name));
	}

	public Object getObject(int column) {
		return getObject(currow, column);
	}

	public String get(String name) {
		return get(currow, findColumn(name));
	}

	public String getString(String name) {
		return get(name);
	}

	public String fix(String name) {
		String v = get(name);
		return org.sdf.servlet.HttpUtility.translate(v);
	}

	public String fix(int idx) {
		String v = get(idx);
		return org.sdf.servlet.HttpUtility.translate(v);
	}

	public void put(String name, String v) {
		put(currow, findColumn(name), v);
	}

	public void put(int column, String v) {
		put(currow, column, v);
	}

	public void put(int column, Object v) {
		put(currow, column, v);
	}

	public String[] getArray(String key) {
		String[] s = new String[1];
		s[0] = getString(key);
		return s;
	}

	public int getInt(int col) {
		try {
			return (int) getDouble(col);
		} catch (Exception e) {
		}
		return 0;
	}

	public long getLong(int col) {
		try {
			return (long) getDouble(col);
		} catch (Exception e) {
		}
		return 0;
	}

	public double getDouble(int col) {
		try {
			return Double.valueOf(get(col)).doubleValue();
		} catch (Exception e) {
		}
		return 0;
	}

	public float getFloat(int col) {
		try {
			return (float) Double.valueOf(get(col)).doubleValue();
		} catch (Exception e) {
		}
		return 0;
	}

	public boolean getBoolean(int col) {
		try {
			String v = get(col);
			return "1".equals(v) || "y".equals(v) || "on".equals(v)
					|| "true".equals(v);
		} catch (Exception e) {
		}
		return false;
	}

	public int getInt(String name) {
		try {
			return (int) getDouble(name);
		} catch (Exception e) {
		}
		return 0;
	}

	public long getLong(String name) {
		try {
			return (long) getDouble(name);
		} catch (Exception e) {
		}
		return 0;
	}

	public double getDouble(String name) {
		try {
			return Double.valueOf(get(name)).doubleValue();
		} catch (Exception e) {
		}
		return 0;
	}

	public float getFloat(String name) {
		try {
			return (float) Double.valueOf(get(name)).doubleValue();
		} catch (Exception e) {
		}
		return 0;
	}

	public boolean getBoolean(String name) {
		try {
			String v = get(name);
			return "1".equals(v) || "y".equals(v) || "on".equals(v)
					|| "true".equals(v);
		} catch (Exception e) {
		}
		return false;
	}

	public String getChecked(String name) {
		try {
			String v = get(name).toLowerCase();
			if ("1".equals(v) || "y".equals(v) || "on".equals(v)
					|| "true".equals(v))
				return "checked";
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 
	 * @param entity
	 *            java.lang.Object
	 */

	public void copyToEntity(Object entity) {
		copyToEntity(entity, null);
	}

	public void copyToEntity(Object entity, String acronym) {

		if (entity == null)
			throw new NullPointerException(
					"trying to copy from Record to null entity class");
		if (acronym == null)
			acronym = "";
		else
			acronym += "_";

		Class c = entity.getClass();
		java.lang.reflect.Field[] field = c.getFields();
		for (int i = 0; i < field.length; i++) {
			try {
				String fieldtype = field[i].getType().getName();
				String fieldname = acronym + field[i].getName();

				if (containsKey(fieldname)) {
					if (fieldtype.equals("java.lang.String")) {
						String val = getString(fieldname);
						// System.out.println(fieldname + ":" + val);
						field[i].set(entity, getString(fieldname));
					} else if (fieldtype.equals("int")) {
						field[i].setInt(entity, getInt(fieldname));
					} else if (fieldtype.equals("double")) {
						field[i].setDouble(entity, getDouble(fieldname));
					} else if (fieldtype.equals("long")) {
						field[i].setLong(entity, getLong(fieldname));
					} else if (fieldtype.equals("float")) {
						field[i].set(entity, new Float(getDouble(fieldname)));
					} else if (fieldtype.equals("boolean")) {
						field[i].setBoolean(entity, getBoolean(fieldname));
					}
				}
			} catch (Exception e) {
			}
		}
	}

	abstract protected int findColumn(String name);

	public int getColumnIndex(String name) {
		return findColumn(name);
	}

	public boolean containsKey(String name) {
		return findColumn(name) != -1;
	}

	abstract public void clear();

	protected void finalize() throws Throwable {
		clear();
		super.finalize();
	}
}