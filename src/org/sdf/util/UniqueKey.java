package org.sdf.util;

import java.util.Calendar;

import org.sdf.lang.Time;
import org.sdf.slim.config.GlobalResource;

public class UniqueKey implements IKeys {
	long time;
	int index;
	long chgtime;
	static UniqueKey key;
	Time t = new Time();
	String suffix;
	private UniqueKey() {
		chgtime = Calendar.getInstance().getTime().getTime() / 1000 / 60;
		time = ((long) (Calendar.getInstance().getTime().getTime() / 1000)) * 100000;
		index = 0;
		GlobalResource gr = GlobalResource.getInstance();
		suffix = gr.get("ukey.suffix");
		if(suffix!=null) suffix= suffix.trim();
	}
	
	public String suffix(){
		return suffix;
	}

	public static synchronized UniqueKey getInstance() {
		if (key == null)
			key = new UniqueKey();

		if (key.index >= 100000)
			key = new UniqueKey();

		long ntime = Calendar.getInstance().getTime().getTime() / 1000 / 60;
		if (ntime != key.chgtime || key.index >= 100000)
			key = new UniqueKey();
		
		return key;
	}

	public final synchronized String getKey() {
		index++;
		return String.valueOf((time + index)  )+ (suffix != null ? suffix : "");
	}

	public final synchronized String getHexKey() {
		index++;
		return Long.toHexString((time + index))+ (suffix != null ? suffix : "");
	}

	public final synchronized String curHexKey() {
		return Long.toHexString((time + index))+ (suffix != null ? suffix : "");
	}

	public final synchronized String curKey() {
		return String.valueOf((time + index)) + (suffix != null ? suffix : "");
	}

}