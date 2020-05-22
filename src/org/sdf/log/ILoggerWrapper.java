package org.sdf.log;

import java.io.PrintWriter;

public interface ILoggerWrapper {
	public String getName();

	public abstract void setLogPolicy(ILogPolicy policy);

	public abstract void err(String msg);

	public abstract void info(String msg);

	public abstract void debug(String msg);

	public abstract void warn(String msg);

	public abstract void err(Throwable e);

	public void err(org.sdf.rdb.SQLError e);

	public void err(String msg, org.sdf.rdb.SQLError e);

	public void err(String msg, Throwable e);

	public PrintWriter getPrintWriter();
	
	public ILogPolicy getLogPolicy();	
	
	public void close();
}