package org.sdf.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;

public class MonthlyLogPolicy implements ILogPolicy {
	String name;
	String dir;
	String date = "";
	String mon = "";
	PrintWriter out;

	public MonthlyLogPolicy(String dir, String name) {
		this.dir = dir;
		this.name = name;

		out = getPrintWriter();
	}

	public PrintWriter getPrintWriter() {
		boolean b = isNew();
		try {
			if (b || out == null || out.checkError()) {
				String dname = dir;
				String fname = name + "." + mon + ".log";
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

	private boolean isNew() {
		Calendar cal = Calendar.getInstance();
		String m = df_m.format(cal.getTime());
		if (!mon.equals(m)) {
			mon = m;
			return true;
		}
		return false;
	}

	public void backup() {
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
			}
			String dname = dir;
			String fname = name + "." + mon + ".log";
			File file = new File(dname, fname);
			if (file.exists()) {
				for (int i = 0;; i++) {
					File bfile = new File(dname, name + "." + mon + "." + i
							+ ".log");
					if (!bfile.exists()) {
						file.renameTo(bfile);
						Log.cfg.info(fname + " --> " + name + "." + mon + "."
								+ i + ".log " + " backup");
						break;
					}
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public String suffix() {
		return "";
	}

	public String prefix() {
		Calendar cal = Calendar.getInstance();
		String dt = ldf_dt.format(cal.getTime());
		StringBuffer buf = new StringBuffer().append("#[").append(dt).append(
				"]");
		return buf.toString();
	}

	public String toString() {
		String dname = dir;
		String fname = name + "." + mon + ".log";
		return super.toString() + "[" + name + "] : MonthlyLogPolicy : "
				+ dname + "," + fname;
	}

	public boolean info = false;
	public boolean warn = false;
	public boolean debug = false;

	public boolean isInfo() {
		return info;
	}

	public boolean isWarn() {
		return warn;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setInfoEnable(boolean b) {
		this.info = b;
	}

	public void setWarnEnable(boolean b) {
		this.warn = b;
	}

	public void setDebugEnable(boolean b) {
		this.debug = b;
	}
	
	public void close(){
		try {
			out.close();
		} catch (Exception e) {
		}
	}	
}
