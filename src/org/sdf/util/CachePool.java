package org.sdf.util;

import java.util.*;

public class CachePool {
	protected HashMap maps = new HashMap();

	static CachePool pool = new CachePool();

	private CachePool() {
	}

	public static CachePool getInstance() {
		return pool;
	}

	public HashMap get(String code) {
		return (HashMap) maps.get(code);
	}

	public Object get(String code, String key) {
		HashMap codes = (HashMap) maps.get(code);
		if (codes == null) {
			return null;
		}
		CacheObj cobj = (CacheObj) codes.get(key);
		if (cobj == null)
			return null;
		return cobj.obj;
	}

	public void put(String code, String key, Object obj) {
		HashMap codes = (HashMap) maps.get(code);
		if (codes == null) {
			codes = new HashMap();
			maps.put(code, codes);

		}
		CacheObj cobj = new CacheObj();
		cobj.cal = Calendar.getInstance();
		cobj.obj = obj;
		codes.put(key, cobj);

	}

	public void clear() {
		if (maps != null) {
			maps.clear();
		}
		maps = new HashMap();
	}

	public void clear(String code) {
		maps.remove(code);
		maps.put(code, new HashMap());
	}

	public HashMap maps() {
		return maps;
	}

	public class CacheObj {
		public Calendar cal;
		public Object obj;
	}
}
