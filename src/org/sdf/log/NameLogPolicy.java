package org.sdf.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;

/**
 * NameLogPolicy - 날짜등에 관계없이 특정 이름으로 로그를 수집
 *
 */
public class NameLogPolicy implements ILogPolicy {
	String name;
	String dir;
	String date = "";
	String mon = "";
	PrintWriter out;

	public NameLogPolicy(String dir, String name) {
		this.dir = dir;
		this.name = name;

		out = getPrintWriter();
	}

	public PrintWriter getPrintWriter() {
		boolean b = isNew();
		try {
			if (b || out == null || out.checkError()) {
				String dname = dir;
				String fname = name + ".log";
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
		return false;
	}

	public void backup() {
		
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
		String fname = name + ".log";
		return super.toString() + "[" + name + "] : NameLogPolicy : "
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
