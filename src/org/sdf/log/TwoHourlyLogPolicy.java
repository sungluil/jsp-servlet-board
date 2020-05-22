package org.sdf.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TwoHourlyLogPolicy extends HourlyLogPolicy {

	public TwoHourlyLogPolicy(String dir, String name) {
		super(dir,name);
	}


	protected boolean isNew() {
		Calendar cal = Calendar.getInstance();
		String dt = df_d.format(cal.getTime());
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if(h%2==1){
			h=h-1;
		}
		String hh = h <10 ? "0" + h : String.valueOf(h);
		dt += '_' + hh;

		if (!this.date.equals(dt)) {
			this.date = dt;
			String m = this.date.substring(0, 6);
			if (!mon.equals(m)) {
				mon = m;
				new File(dir, mon).mkdirs();
			}
			return true;
		}
		return false;
	}
	
	

}