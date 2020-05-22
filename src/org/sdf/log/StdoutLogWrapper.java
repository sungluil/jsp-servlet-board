package org.sdf.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class StdoutLogWrapper implements ILoggerWrapper {
	String name;
	ILogPolicy policy;

	public StdoutLogWrapper(String name) {
		this.name = name;
		setLogPolicy(new StdoutLogPolicy(name));
	}

	public String getName() {
		return name;
	}

	public void setLogPolicy(ILogPolicy policy) {
		this.policy = policy;
	}

	public void err(String msg) {
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("E:");
		out.println(msg);
		out.flush();
	}

	public void info(String msg) {
		if (!policy.isInfo())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("I:");
		out.println(msg);
		out.flush();
	}

	public void debug(String msg) {
		if (!policy.isDebug())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("D:");
		out.println(msg);
		out.flush();
	}

	public void warn(String msg) {
		if (!policy.isWarn())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("W:");
		out.println(msg);
		out.flush();
	}

	public void err(Throwable e) {
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("E:");
		e.printStackTrace(out);
		out.flush();
	}

	public void err(org.sdf.rdb.SQLError e) {
		PrintWriter out = policy.getPrintWriter();

		printPrefix(out);
		out.print("SQL-E:");
		e.printStackTrace(out);
		out.flush();
	}

	public void err(String msg, org.sdf.rdb.SQLError e) {
		PrintWriter out = policy.getPrintWriter();

		printPrefix(out);
		out.print("SQL-E:");
		out.println(msg);
		e.printStackTrace(out);
		out.flush();
	}

	public void err(String msg, Throwable e) {
		PrintWriter out = policy.getPrintWriter();

		printPrefix(out);
		out.print("E:");
		out.println(msg);
		e.printStackTrace(out);
		out.flush();
	}

	public void printPrefix(PrintWriter out) {
		// System.out.println(this);
		out.print(policy.prefix());
		out.flush();
	}

	public PrintWriter getPrintWriter() {
		return policy.getPrintWriter();
	}

	public String toString() {
		return this.getClass().getName() + "[" + name + "] " + policy;
	}

	public ILogPolicy getLogPolicy(){
		return policy;
	}
	
	public void close(){
		if(policy != null) policy.close();
	}
		
}
