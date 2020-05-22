package org.sdf.log;

import java.io.*;
import java.util.Calendar;

public class LogPolicy implements ILogPolicy {
	protected String name;
	protected String dir;
	String date = "";
	String mon = "";
	String logfile = "sdf";
	PrintWriter out;

	public LogPolicy(String dir, String name) {
		this.name = name;
		this.dir = dir;

		out = getPrintWriter();

	}

	public PrintWriter getPrintWriter() {
		boolean b = isNew();
		try {
			if (b || out == null || out.checkError()) {
				String dname = dir;
				String fname = logfile + "." + mon + ".log";
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

	public void backup() {
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
			}
			String dname = dir;
			String fname = logfile + "." + mon + ".log";
			File file = new File(dname, fname);
			if (file.exists()) {
				for (int i = 0;; i++) {
					File bfile = new File(dname, logfile + "." + mon + "." + i
							+ ".log");
					if (!bfile.exists()) {
						file.renameTo(bfile);
						Log.cfg.info(fname + " --> " + logfile + "." + mon
								+ "." + i + ".log " + " backup");
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
		return null;
	}

	public String prefix() {
		Calendar cal = Calendar.getInstance();
		String dt = ldf_dt.format(cal.getTime());
		StringBuffer buf = new StringBuffer().append("#[").append(dt).append(
				"]").append("[").append(getName()).append("] ");
		return buf.toString();
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

	public String toString() {

		return "[" + name + "] : LogPolicy : " + dir + "," + logfile;
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
