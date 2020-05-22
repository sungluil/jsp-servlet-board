package org.sdf.lang;

import java.util.*;

public class AList extends ArrayList {

	HashMap ids;
	List keys;
	int capacity;

	public AList() {
		super();
	}

	public AList(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}

	public boolean add(String key, Object o) {
		if (key != null) {
			if (ids == null) {
				if (capacity == 0) {
					ids = new HashMap();
				} else {
					ids = new HashMap(capacity);
				}

			}
			if (keys == null)
				keys = new ArrayList();
			ids.put(key, o);
			keys.add(key);
		}
		return super.add(o);
	}

	public List keys() {
		return keys;
	}

	public String key(int idx) {
		if (keys == null)
			return "";
		return (String) keys.get(idx);
	}

	public Object get(String key) {
		if (ids == null)
			return null;
		return (Object) ids.get(key);
	}
	
	public void put(String key, Object o) {
		if (ids == null){
			if (capacity == 0) {
				ids = new HashMap();
			} else {
				ids = new HashMap(capacity);
			}			
		}
		ids.put(key, o);
	}	

	public Object current() {
		return (Object) get(currow);
	}

	public Object remove(int i) {
		Object o = super.remove(i);

		Object key = keys.get(i);
		ids.remove(key);
		return o;
	}

	public boolean remove(Object o) {
		// ids.remove(o);
		return super.remove(o);
	}

	protected int currow = -1;

	public int getCurRow() {
		return currow;
	}

	public void setCurRow(int row) {
		this.currow = row;
	}

	public void reset() {
		currow = -1;
	}

	public boolean first() {
		if (size() <= 0)
			return false;
		currow = 0;
		return true;
	}

	public boolean last() {
		if (size() <= 0)
			return false;
		currow = size() - 1;
		return true;
	}

	public boolean next() {
		currow++;
		int size = size();
		if (currow < 0 || size <= 0 || currow >= size)
			return false;
		return true;
	}

	public boolean has() {
		int size = size();
		if (currow < 0 || size <= 0 || (currow + 1) >= size)
			return false;
		return true;
	}

	public boolean pre() {
		currow--;
		int size = size();
		if (currow < 0 || size <= 0 || currow >= size)
			return false;
		return true;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getName()).append("[");

		/*
		 * for(int i=0; i< this.size(); i++){ if(i>0) buf.append(",");
		 * buf.append(get(i)); }
		 */

		buf.append("]");
		return buf.toString();
	}

	public void clear() {
		super.clear();
		if (this.ids != null)
			this.ids.clear();
		if (this.keys != null)
			this.keys.clear();
		this.ids = null;
		this.keys = null;
	}
}
