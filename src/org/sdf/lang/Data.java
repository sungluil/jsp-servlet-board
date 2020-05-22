package org.sdf.lang;

/**
 * Class : Data.class
 * 개요 : Data 저장 Class
 * 설명 : 데이터 입출력 및 저장된 데이터로부터 각종 Type으로 리턴하는 Utility class
 *  Author : 박성호
 *  Create Date : 2003.02.01
 **/
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.sdf.util.AES;

public class Data extends java.util.HashMap implements IData, Serializable {

	static final long serialVersionUID = -7019143455797488426L;

	public void set(java.util.Hashtable src) {
		java.util.Enumeration e = src.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = src.get(key);
			put(key, value);
		}
	}

	/**
	 *
	 * @param entity
	 *            java.lang.Object
	 */
	public void copyToEntity(Object entity) {
		if (entity == null)
			throw new NullPointerException(
					"trying to copy from Dat to null entity class");

		Class c = entity.getClass();
		java.lang.reflect.Field[] field = c.getFields();
		for (int i = 0; i < field.length; i++) {
			try {
				String fieldtype = field[i].getType().getName();
				String fieldname = field[i].getName();

				if (containsKey(fieldname)) {
					if (fieldtype.equals("java.lang.String")) {
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

	public boolean valid(String key) {
		return get(key).trim().length() > 0;
	}

	public String han(String key) {
		return org.sdf.util.StringUtil.eng2han(getString(key));
	}

	public String utf8(String key) {
		try {
			return new String(getString(key).getBytes("8859_1"), "utf-8");
		} catch (Exception e) {
		}
		return "";
	}

	public String get(String key) {
		return getString(key);
	}

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public boolean getBoolean(String key) {
		String v = getString(key).toLowerCase();
		return "1".equals(v) || "y".equals(v) || "on".equals(v)
				|| "true".equals(v);
	}

	public String getChecked(String key) {
		String v = getString(key).toLowerCase();
		if ("1".equals(v) || "y".equals(v) || "on".equals(v)
				|| "true".equals(v))
			return "checked";
		return "";
	}

	public String getBoolValue(String key) {
		if (getBoolean(key))
			return "1";
		return "0";
	}

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public double getDouble(String key) {
		String value = removeComma(getString(key));
		if (value.equals(""))
			return 0;
		double num = 0;
		try {
			num = Double.valueOf(value).doubleValue();
		} catch (Exception e) {
			num = 0;
		}
		return num;
	}

	/**
	 * @return float
	 * @param key
	 *            java.lang.String
	 */
	public float getFloat(String key) {
		return (float) getDouble(key);
	}

	/**
	 * @return int
	 * @param key
	 *            java.lang.String
	 */
	public int getInt(String key) {
		double value = getDouble(key);
		return (int) value;
	}

	/**
	 * @return long
	 * @param key
	 *            java.lang.String
	 */
	public long getLong(String key) {
		String value = removeComma(getString(key));
		if (value.equals(""))
			return 0L;

		return (long) getDouble(key);

	}

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public String getString(String key) {
		String value = null;
		try {
			Object o = (Object) super.get(key);
			Class c = o.getClass();
			if (o == null)
				value = "";
			else if (c.isArray()) {
				int length = Array.getLength(o);
				if (length == 0)
					value = "";
				else {
					Object item = Array.get(o, 0);
					if (item == null)
						value = "";
					else
						value = item.toString();
				}
			} else
				value = o.toString();
		} catch (Exception e) {
			value = "";
		}
		return value;
	}

	/**
	 *
	 * @return Vector
	 * @param key
	 *            java.lang.String
	 */
	public Vector getVector(String key) {
		Vector vector = new Vector();
		try {
			Object o = (Object) super.get(key);
			Class c = o.getClass();
			if (o != null) {
				if (c.isArray()) {
					int length = Array.getLength(o);
					if (length != 0) {
						for (int i = 0; i < length; i++) {
							Object tiem = Array.get(o, i);
							if (tiem == null)
								vector.addElement("");
							else
								vector.addElement(tiem.toString());
						}
					}
				} else
					vector.addElement(o.toString());
			}
		} catch (Exception e) {
		}
		return vector;
	}

	public String[] getArray(String key) {
		Vector v = getVector(key);
		if (v.size() == 0)
			return new String[0];
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}

	public String encrypt(String key) {
		try {
			return AES.encrypt(getString(key));
		} catch (Exception e) {
		}
		return "";
	}

	public String decrypt(String key) {
		try {
			return AES.decrypt(getString(key));
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * @param key
	 *            java.lang.String
	 * @param value
	 *            java.lang.String
	 */
	public void put(String key, String value) {
		super.put(key, value);
	}

	public void put(String key, boolean b) {
		super.put(key, b);
	}

	public void put(String key, int v) {
		super.put(key, String.valueOf(v));
	}

	public void put(String key, long v) {
		super.put(key, String.valueOf(v));
	}

	public void put(String key, double v) {
		super.put(key, String.valueOf(v));
	}

	public void put(String key, float v) {
		super.put(key, String.valueOf(v));
	}

	public Object getObject(String Key) {
		return super.get(Key);
	}

	/**
	 * remove "," in string.
	 *
	 * @return java.lang.String
	 * @param s
	 *            java.lang.String
	 */
	private static String removeComma(String s) {
		if (s == null)
			return null;
		if (s.indexOf(",") != -1) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c != ',')
					buf.append(c);
			}
			return buf.toString();
		}
		return s;
	}

	public void clear() {
		try {
			finalize();
		} catch (Throwable e) {
		}
	}

	protected void finalize() throws Throwable {
		super.clear();
	}

	public synchronized String toString() {
		int max = size() - 1;
		StringBuffer buf = new StringBuffer();

		Set set = entrySet();
		Iterator it = set.iterator();

		buf.append("{");

		for (int i = 0; i <= max; i++) {

			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			String value = null;
			Object o = entry.getValue();
			if (o == null)
				value = "";
			else {
				Class c = o.getClass();
				if (c.isArray()) {
					int length = Array.getLength(o);
					if (length == 0)
						value = "";
					else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null)
							value = "";
						else
							value = item.toString();
					} else {
						StringBuffer valueBuf = new StringBuffer();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null)
								valueBuf.append(item.toString());
							if (j < length - 1)
								valueBuf.append(",");
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else
					value = o.toString();
			}
			buf.append(key + "=" + value);
			if (i < max)
				buf.append(", ");
		}
		buf.append("}");

		return buf.toString();

	}

	public String fix(String name) {
		String v = get(name);
		return org.sdf.servlet.HttpUtility.translate(v);
	}

	public void copy(IData d) {
		String[] keys = d.getKeys();
		for (int i = 0; i < keys.length; i++) {
			put(keys[i], d.getObject(keys[i]));
		}
	}

	public String[] getKeys() {
		Set set = this.keySet();
		String[] keys = new String[set.size()];
		set.toArray(keys);
		return keys;
	}
}