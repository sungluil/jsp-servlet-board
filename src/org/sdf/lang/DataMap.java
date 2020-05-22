package org.sdf.lang;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdf.rdb.RecordSet;
import org.sdf.rdb.Result;
import org.sdf.util.StringUtil;
/**
 *
 * @author admin
 *
 *

 {
    "id1":{
    	"error":false,
    	"errorMessage":"",
    	"total":2,
    	"count":2,
    	"curpage":0,
    	"pagesize":0,
    	"page":false,
    	"rows":[
    		{ "col1":"A1", "col2":"A2", "col3":"A3"},
    		{ "col1":"B1", "col2":"B2", "col3":"B3"}

    	]
    },
    "id2":{
    	"error":false,
    	"errorMessage":"",
    	"total":1,
    	"count":1,
    	"curpage":0,
    	"pagesize":0,
    	"page":false,
    	"rows":[
    		{ "col1":"A1", "col2":"A2", "col3":"A3"},
    		{ "col1":"B1", "col2":"B2", "col3":"B3"}

    	]
    }
 }
 */

public class DataMap extends HashMap implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5753579695433992848L;
	private JSONObject jo ;
	private HashMap configs;

	public DataMap(){
		super();
	}

	public Result getResult(String key){
		return (Result) get(key);
	}

	public void setResult(String key, Result r){
		put(key,r);
	}

	public void setConfig(String key, JSONObject o){
		if(this.configs == null) this.configs = new HashMap();
		this.configs.put(key,o);
	}

	public JSONObject getConfig(String key){
		if(this.configs == null) return null;
		return (JSONObject)this.configs.get(key) ;
	}

	public RecordSet rs(String key){
		Result r = (Result) get(key);
		if(r== null) return new RecordSet();
		return r.getRecordSet();
	}

	public void set(String key, JSONObject o){
		jo.put(key,o);
	}

	public JSONObject data(){
		if(jo == null) jo = this.toJSON();
		return jo;
	}

	public void setData(JSONObject o){
		this.jo = o;
	}

	public JSONObject getData(){
		return this.data();
	}

	/*
	public JSONObject rows(String key){
		Result r = getResult(key);


	}
	*/

	public JSONObject toJSON(){

		if(jo == null) jo = new JSONObject();

		Set set = this.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String)entry.getKey();
			Result r = (Result) entry.getValue();

			RecordSet rs = r.getRecordSet();

			JSONObject o = new JSONObject();

			Exception e = r.getException();

			o.put("error", r.isError);
			o.put("errorMessage", e != null ? e.getMessage() : "" );
			o.put("total", rs.getTotalCount());
			o.put("count", rs.getRowCount());
			o.put("curpage", rs.curpage);
			o.put("pagesize", rs.pagesize);
			o.put("page", rs.curpage > 0 && rs.pagesize > 0);


			JSONArray arr = new JSONArray();
			o.put("rows", arr);

			if(r.isError) continue;

			JSONObject cfg = getConfig(key);

			this.toJSON(cfg,rs,arr);

			jo.put(key, o);
		}

		return jo;

	}

	public JSONArray rows(String key){
		JSONArray arr = new JSONArray()
		;
		JSONObject cfg = getConfig(key);
		Result r = this.getResult(key);
		RecordSet rs = r.getRecordSet();

		return toJSON(cfg,rs,arr);
	}

	public JSONArray toJSON(JSONObject cfg, RecordSet rs, JSONArray arr){

		String[] cols = null;
		String[] names = null;
		String[] types = null;
		boolean bType = false;


		if(cfg!=null){
			JSONArray carr = cfg.getArray("cols");
			if(carr!=null && carr.size()>0){
				int size = carr.size();
				cols = new String[size];
				names = new String[size];
				types = new String[size];
				for(int i=0;  i<size;i++){
					JSONObject co = (JSONObject) carr.get(i);
					cols[i] = co.getString("id");
					names[i] = co.getString("name");
					if(!StringUtil.valid(cols[i])) cols[i] = names[i];
					if(!StringUtil.valid(names[i])) names[i] = cols[i];
					types[i] = co.getString("type");

				}

				bType = true;

			}
		}else{
			cols = rs.getColumnLabels().clone();

		}

		for(int i=0; i<cols.length; i++){
			cols[i] = cols[i].toLowerCase();
			//names[i] = names[i].toLowerCase();
		}

		while(rs.next()){
			JSONObject row = new JSONObject();
			for(int i=0; i<cols.length;i++){
				String col = names[i];
				String id = cols[i];
				String s = rs.get(id);

				if(bType){
					String type = types[i];
					if(type !=null && type.equals("int")){
						long n = 0;
						try{
							n = Integer.parseInt(s);
							row.put(col,n);
						}catch(Exception ex){
							row.put(col,null);
						}

					}else if(type !=null && type.equals("double")){
						double n = 0;
						try{
							n = Double.parseDouble(s);
							row.put(col,n);
						}catch(Exception ex){
							row.put(col,null);
						}
					}else if(type !=null && type.equals("boolean")){
						row.put(col, rs.getBoolean(id));
					}else if(type !=null && type.startsWith("array")){
						String[] varr = StringUtil.getArray(s,',');
						JSONArray jarr = new JSONArray();
						for(int j=0; j<varr.length; j++){
							String v = varr[j];
							if(type.equals("array-int")){
								long n = 0;
								try{
									n = Integer.parseInt(varr[j]);
									jarr.add(n);
								}catch(NumberFormatException  ex){
									jarr.add(null);
								}catch(ArrayIndexOutOfBoundsException  ex){
									jarr.add(null);
								}

							}else if(type.equals("array-double")){
								double n = 0;
								try{
									n = Double.parseDouble(varr[j]);
									jarr.add(n);
								}catch(NumberFormatException  ex){
									jarr.add(null);
								}catch(ArrayIndexOutOfBoundsException  ex){
									jarr.add(null);
								}

							}else {
								jarr.add(v);
							}
						}
						row.put(col, jarr);
					}else{
						row.put(col, s);
					}
				}else{
					row.put(col, s);
				}
			}
			arr.add(row);
		}

		return arr;

	}

	public String toJSONString(){
		return this.data().toString();
	}
}
