package org.sdf.log;

import java.io.PrintWriter;

public class NoLoggingWrapper implements ILoggerWrapper {
	String name;

	public String getName() {
		return name;
	}

	public NoLoggingWrapper(String name) {
		this.name = name;
	}

	public final void setLogPolicy(ILogPolicy policy) {

	}

	public final void err(String msg) {

	}

	public final void info(String msg) {

	}

	public final void debug(String msg) {

	}

	public final void warn(String msg) {

	}

	public final void err(Throwable e) {

	}

	public final void err(org.sdf.rdb.SQLError e) {

	}

	public final void err(String msg, org.sdf.rdb.SQLError e) {

	}

	public final void err(String msg, Throwable e) {

	}

	public PrintWriter getPrintWriter() {
		return null;
	}

	public String toString() {

		return "[" + name + "] : NoLoggingWrapper ";
	}
	
	public ILogPolicy getLogPolicy(){
		return null;
	}
		
	public void close(){
	
	}
}
