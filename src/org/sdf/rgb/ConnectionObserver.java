package org.sdf.rgb;

import java.util.Calendar;
import java.util.HashMap;

import org.sdf.log.Log;

public class ConnectionObserver extends HashMap{
	private static ConnectionObserver monitor = new ConnectionObserver();
	int idx = 0;
	int len = 5;
	long cdt =0;
	
	long total = 0;
	long count = 0;
	
	public static ConnectionObserver getInstance(){
		return monitor;
	}
	
	public String add( String info){
		inc();
		String tm = org.sdf.lang.Time.cur_dttm();
		String id = getId();

		super.put(id, info);
		//Log.biz.info(id + "\n"+info);
		return id;
	}
	
	public String add( ConnectionChecker con){
		inc();
		String tm = org.sdf.lang.Time.cur_dttm();
		String id = getId();

		super.put(id, con);
		//Log.biz.info(id + "\n"+con.info);
		return id;
	}
	
	public void remove(String id){

		super.remove(id);
		//Log.biz.info(id + " remove." + size() );		
	}
	
	public void inc(){
		count++;
		if(count >= 100000){
			total++;
			count = 0;
		}
		
	}
	
	public String toString(){
		return "UC:" + size() + ",T:" + total + ",C:" + count;
	}
	
	private String getId(){
		Calendar cal = Calendar.getInstance();
	
		int yy = cal.get(Calendar.YEAR)-2000;
		int mm = cal.get(Calendar.MONTH) + 1;
		int dd = cal.get(Calendar.DAY_OF_MONTH);
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mi = cal.get(Calendar.MINUTE);
		
		long dt =yy * 100000000 + mm * 1000000 + dd* 10000 + hh * 100 + mi  ;
		
		if(cdt != dt){
			idx=0;
			cdt = dt;
		}else idx++;
		
		
		String id = dt + "_" + org.sdf.util.StringUtil.fillZero(idx, len);
		return  id;
	}
	
	
}
