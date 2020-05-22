package org.sdf.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HourlyLogPolicy extends DailyLogPolicy {

	public HourlyLogPolicy(String dir, String name) {
		super(dir,name);
	}

	public static SimpleDateFormat df_dh= new SimpleDateFormat("yyyyMMdd_HH");


	public PrintWriter getPrintWriter() {
		boolean b = isNew();
		try {
			if (b || out == null || out.checkError()) {

				String dname = dir + "/" + mon;
				String fname = name + "." + this.date + ".log";

				try {
					out.close();
				} catch (Exception e) {
				}

				File file = new File(dname, fname);
				FileOutputStream fos = new FileOutputStream(file, true);
				Writer writer = new OutputStreamWriter(fos, "euc-kr");

				out = new PrintWriter(writer, true);
			}

			return out;

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return new PrintWriter(System.out);
	}

	protected boolean isNew() {
		Calendar cal = Calendar.getInstance();
		String dt = df_dh.format(cal.getTime());
		
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


	public String toString() {
		String dname = dir + "/" + mon;
		String fname = name + "." + this.date + ".log";
		return "[" + name + "] : "+getClass().getName() +" : " + dname + "," + fname;
	}
}
