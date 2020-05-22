package org.sdf.rgb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sdf.lang.Data;
import org.sdf.lang.Sorter;
import org.sdf.lang.Sorting;
import org.sdf.log.Log;

public class SqlParams {
	List params = new ArrayList();
	boolean fail = false;

	public SqlParams() {

	}

	public SqlParams(String s) {
		try {

			JSONArray arr = (JSONArray) JSONValue.parse(s);
			// Log.sql.info(s + ":" + arr);
			init(arr);
		} catch (Exception e) {
			fail = true;
			Log.sql.err("SqlParams error", e);
		}
	}

	public SqlParams(JSONArray arr) {
		init(arr);
	}

	public void init(JSONArray arr) {
		// Log.sql.info(arr + ":" + (arr!=null? arr.size():"0") );
		for (int i = 0; arr != null && i < arr.size(); i++) {
			params.add(new Param((JSONObject) arr.get(i)));
		}
		// Collections.sort(params,new Sorter());
	}

	public List getValues(Data d) {
		List l = new ArrayList();
		for (int i = 0; params != null && i < params.size(); i++) {
			Param param = (Param) params.get(i);
			l.add(d.get(param.id));
		}
		return l;
	}

	public class Param {
		public String id;
		public String inout;
		public String type;

		Param(JSONObject o) {

			id = o.getString("id");
			inout = o.getString("inout");
			type = o.getString("type");
		}

	}
}
