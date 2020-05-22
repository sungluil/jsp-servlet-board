package org.sdf.log;

import java.io.PrintWriter;

public class LoggerWrapper implements ILoggerWrapper {
	String name;
	ILogPolicy policy;

	public LoggerWrapper(String dir, String name) {
		this.name = name;
		setLogPolicy(new LogPolicy(dir, name));
	}

	public String getName() {
		return name;
	}

	public void setLogPolicy(ILogPolicy policy) {
		this.policy = policy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gdf.slim.ILoggerWrapper#err(java.lang.String)
	 */
	public void err(String msg) {
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("E:");
		out.println(msg);
		out.flush();
	}

	public void err(Object msg) {
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("E:");
		out.println(msg);
		out.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gdf.slim.ILoggerWrapper#info(java.lang.String)
	 */
	public void info(String msg) {
		if (!policy.isInfo())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("I:");
		out.println(msg);
		out.flush();
	}

	public void info(Object msg) {
		if (!policy.isInfo())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("I:");
		out.println(msg);
		out.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gdf.slim.ILoggerWrapper#debug(java.lang.String)
	 */
	public void debug(String msg) {
		if (!policy.isDebug())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("D:");
		out.println(msg);
		out.flush();
	}

	public void debug(Object msg) {
		if (policy.isDebug())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("D:");
		out.println(msg);
		out.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gdf.slim.ILoggerWrapper#warn(java.lang.String)
	 */
	public void warn(String msg) {
		if (!policy.isWarn())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("W:");
		out.println(msg);
		out.flush();
	}

	public void warn(Object msg) {
		if (!policy.isWarn())
			return;
		PrintWriter out = policy.getPrintWriter();
		printPrefix(out);
		out.print("W:");
		out.println(msg);
		out.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gdf.slim.ILoggerWrapper#err(java.lang.Throwable)
	 */
	public void err(Throwable e) {
		PrintWriter out = policy.getPrintWriter();
		// System.out.println("P------->" +policy);
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
		// System.out.println(this + " --> "+ out);
		out.print(policy.prefix());
		out.flush();
	}

	public PrintWriter getPrintWriter() {
		return policy.getPrintWriter();
	}

	public ILogPolicy getLogPolicy(){
		return policy;
	}
	
	public String toString() {
		return this.getClass().getName() + "[" + name + "] " + policy;
	}

	public void close(){
		if(policy != null) policy.close();
	}
}
