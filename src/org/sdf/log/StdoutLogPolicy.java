package org.sdf.log;

import java.io.*;
import java.util.Calendar;

public class StdoutLogPolicy implements ILogPolicy {
	protected String name;

	PrintWriter out;

	public StdoutLogPolicy() {

	}

	public StdoutLogPolicy(String name) {
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public PrintWriter getPrintWriter() {
		if (out == null || out.checkError()) {
			out = new PrintWriter(System.out, true);
		}
		return out;
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

	public void backup() {

	}

	public String toString() {

		return "[" + name + "] : StdoutLogPolicy ";
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
		/*
  		try {
 
			out.close();
		} catch (Exception e) {
		}
		 */
	}
}
