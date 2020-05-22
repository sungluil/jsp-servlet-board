package org.sdf.log;

import java.io.File;
import java.util.HashMap;

import org.sdf.util.FileUtil;
import org.sdf.util.StringUtil;

public class Log {

	public static ILoggerWrapper cfg = new NoLoggingWrapper("cfg");
	public static ILoggerWrapper sys = new NoLoggingWrapper("sys");
	public static ILoggerWrapper sql = new NoLoggingWrapper("sql");
	public static ILoggerWrapper dev = new NoLoggingWrapper("dev");
	public static ILoggerWrapper biz = new NoLoggingWrapper("biz");
	public static ILoggerWrapper info = new NoLoggingWrapper("info");
	public static ILoggerWrapper bat = new NoLoggingWrapper("bat");
	public static ILoggerWrapper dbg = new NoLoggingWrapper("dbg");
	public static ILoggerWrapper act = new NoLoggingWrapper("act");
	public static ILoggerWrapper box = new NoLoggingWrapper("box");

	public static HashMap loggers = new HashMap();

	public static String logconf = "/WEB-INF/config/log.cfg";

	protected static String basedir = "";
	protected static String logdir = "";
	protected static FileUtil futil;

	public static String getBaseDir(){
		return basedir;
	}

	public static String getLogDir(){
		return logdir;
	}

	public static void setConfigLogger(ILoggerWrapper logger) {
		cfg = logger;
	}

	public static void setSystemLogger(ILoggerWrapper logger) {
		sys = logger;
	}

	public static void setSQLLogger(ILoggerWrapper logger) {
		sql = logger;
	}

	public static void setDevelopeLogger(ILoggerWrapper logger) {
		dev = logger;
	}

	public static void setBusinessLogger(ILoggerWrapper logger) {
		biz = logger;
	}

	public static void setBatchLogger(ILoggerWrapper logger) {
		bat = logger;
	}

	public static void setDebugLogger(ILoggerWrapper logger) {
		dbg = logger;
	}

	public static void setActLogger(ILoggerWrapper logger) {
		act = logger;
	}

	public static ILoggerWrapper get(String name) {
		if (futil.isModified()) {
			reset();
			HashMap prop = futil.load();
			initConfig(prop);
		}

		if (name.equals("cfg"))
			return cfg;
		if (name.equals("sys"))
			return sys;
		if (name.equals("sql"))
			return sql;
		if (name.equals("dev"))
			return dev;
		if (name.equals("biz"))
			return biz;
		if (name.equals("info"))
			return info;
		if (name.equals("bat"))
			return bat;
		if (name.equals("dbg"))
			return dbg;
		if (name.equals("act"))
			return act;
		if (name.equals("box"))
			return box;
		return (ILoggerWrapper) loggers.get(name);
	}

	public static void setLogger(String name, ILoggerWrapper logger) {
		if (name.equals("cfg"))
			cfg = logger;
		else if (name.equals("sys"))
			sys = logger;
		else if (name.equals("sql"))
			sql = logger;
		else if (name.equals("dev"))
			dev = logger;
		else if (name.equals("biz"))
			biz = logger;
		else if (name.equals("info"))
			info = logger;
		else if (name.equals("bat"))
			bat = logger;
		else if (name.equals("dbg"))
			dbg = logger;
		else if (name.equals("act"))
			act = logger;
		else if (name.equals("box"))
			box = logger;
		else
			loggers.put(name, logger);
	}

	public static void init(String bdir) {
		Log.basedir = bdir;
		// File dir = new File(bdir);
		// if(!dir.exists()) dir.mkdirs();

		String conffile =  Log.logconf;
		String ip_addr = "";
		try{
			java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
			ip_addr = addr.getHostAddress();
			if(StringUtil.valid(ip_addr)) ip_addr = "."+ip_addr;
		}catch(Exception e){}

		String fname = conffile + ip_addr;
		File f = new File(Log.basedir,fname);
		if(!f.exists()){
			fname = conffile;
			f = new File(Log.basedir, fname  );
		}

		if (!f.exists()) {
			cfg = new StdoutLogWrapper("cfg");
			Log.cfg.err("Log Conf-file not found. [" + conffile + "]");

			return;
		}

		System.out.println("Log Config File  : " + fname );
		futil = new FileUtil(f.getAbsolutePath());
		HashMap prop = futil.load();
		initConfig(prop);
	}

	public static void initConfig(HashMap prop) {

		try {

			String dir = (String) prop.get("log.dir");
			File dirs = new File(dir);
			if (!dirs.exists())
				dirs.mkdirs();


			String[] logs = StringUtil.getArray((String) prop
					.get("log.enable"), ";");

			Class cla = null;
			String[] tmp = new String[2];
			tmp[0] = dir;

			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < logs.length; i++) {

				String policyClass = (String) prop.get("log.policy." + logs[i]);
				String policyLevel = (String) prop.get("log.level." + logs[i]);
				try {
					buf.append("\n[" + logs[i] + ":" + policyClass).append("] ");
					if (policyClass == null) {
						ILoggerWrapper logger = new StdoutLogWrapper(logs[i]);
						Log.setLogger(logs[i], logger);
						continue;
					}
					tmp[1] = logs[i];
					ILoggerWrapper logger = new LoggerWrapper(dir, logs[i]);
					Log.setLogger(logs[i], logger);
					cla = Class.forName(policyClass);
					Class[] classes = new Class[2];
					classes[0] = tmp[0].getClass();
					classes[1] = tmp[1].getClass();
					java.lang.reflect.Constructor cons = cla
							.getConstructor(classes);

					ILogPolicy policy = (ILogPolicy) cons.newInstance(tmp);
					logger.setLogPolicy(policy);
					setLogLevel(policy, policyLevel);

				} catch (ClassNotFoundException e) {
					e.printStackTrace(System.out);
					Log.cfg.err("logger[" + logs[i] + "] : [" + policyClass
							+ "] not found.", e);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					Log.cfg.err("logger[" + logs[i] + "] : [" + policyClass
							+ "] error.", e);
				}
			}

			org.sdf.rdb.RecordSet.setLoggerWrapper(Log.sql);
			org.sdf.rdb.CSRecordSet.setLoggerWrapper(Log.sql);
			org.sdf.rdb.ResultSetWrapper.setLoggerWrapper(Log.sql);
			org.sdf.rdb.JPreparedStatement.setStaticLoggerWrapper(Log.sql);

			logdir = dir;
			System.out.println("Log Directory : " + dir + " [ "+dirs.getAbsolutePath()+" ]");
			Log.cfg.info("---------------------------------------------------------------------");
			Log.cfg.info("Log Directory : " + dir + " [ "+dirs.getAbsolutePath()+" ]");
			Log.cfg.info(buf.toString());
			Log.cfg.info("---------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

	protected static void setLogLevel(ILogPolicy policy, String level) {
		if (!StringUtil.valid(level)) {
			policy.setWarnEnable(true);
			policy.setInfoEnable(true);
			policy.setDebugEnable(true);
			return;
		}

		if (level.equals("W")) {
			policy.setWarnEnable(true);
			policy.setInfoEnable(false);
			policy.setDebugEnable(false);

		} else if (level.equals("I")) {
			policy.setWarnEnable(true);
			policy.setInfoEnable(true);
			policy.setDebugEnable(false);

		} else if (level.equals("D")) {
			policy.setWarnEnable(true);
			policy.setInfoEnable(true);
			policy.setDebugEnable(true);

		} else {
			policy.setWarnEnable(true);
			policy.setInfoEnable(true);
			policy.setDebugEnable(true);
		}

	}

	protected static void reset() {
		cfg = new NoLoggingWrapper("cfg");
		sys = new NoLoggingWrapper("sys");
		sql = new NoLoggingWrapper("sql");
		dev = new NoLoggingWrapper("dev");
		biz = new NoLoggingWrapper("biz");
		info = new NoLoggingWrapper("info");
		bat = new NoLoggingWrapper("bat");
		dbg = new NoLoggingWrapper("dbg");
		act = new NoLoggingWrapper("act");
		box = new NoLoggingWrapper("box");
		loggers = new HashMap();

	}



}
