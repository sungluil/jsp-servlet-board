package org.sdf.rgb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.sdf.log.Log;
import org.sdf.slim.config.GlobalResource;
import org.sdf.util.StringUtil;

public class DBSource {
	String fname;
	HashMap prop;

	static boolean otherWas;

	public DBSource() {
	}

	static int idx = 0;

	public DBSource(String fname) {
		prop = org.sdf.util.FileUtil.loadProperties(fname);
	}

	static String sfname;

	public static void setPropFile(String fname) {
		DBSource.sfname = fname;
	}

	public static int dbType;

	public static final int ORACLE = 1;
	public static final int MSSQL = 2;
	public static final int DB2 = 3;
	public static final int MYSQL = 4;	// MySQL ì¶?ê°?.

	public static boolean observe;
	public static int m_from;
	public static int m_amount;
	static {
		GlobalResource gr = GlobalResource.getInstance();
		String was = gr.get("was.type");
		if (was != null && was.toLowerCase().equals("other")) {
			otherWas = true;
		}
		Log.cfg.info("### Was Type : " + (otherWas ? "Non Tomcat" : "Tomcat"));

		String db = gr.get("db.type");
		if (db != null && db.toLowerCase().equals("mssql")) {
			dbType = DBSource.MSSQL;
		} else if (db != null && db.toLowerCase().equals("db2")) {
			dbType = DBSource.DB2;
		} else if (db != null && db.toLowerCase().equals("mysql")) {
			dbType = DBSource.MYSQL;
		} else if (db != null && db.toLowerCase().equals("mariadb")) {
			dbType = DBSource.MYSQL;
		} else {
			dbType = DBSource.ORACLE;
		}
		String sObserve = gr.get("db.observe");
		String sMethod = gr.get("db.observe_method");
		observe = (sObserve != null && sObserve.equals("true") );
		if(observe){
			try{
				String[] arr = StringUtil.getArray(sMethod,',');
				m_from = Integer.parseInt(arr[0]);
				m_amount = Integer.parseInt(arr[1]);
			}catch(Exception e){}
			
			if(m_from == 0) m_from = 3;
			if(m_amount == 0) m_amount = 6;
		}
		
		Log.cfg.info("### DB Type : " + DBSource.getDbTypeString());
		Log.cfg.info("### DB Observe : " + observe);
	}

	public static int getDbType() {
		return DBSource.dbType;
	}

	public static String getDbTypeString() {

		switch (dbType) {
			case DBSource.MSSQL:
				return "MS-SQL";
			case DBSource.DB2:
				return "DB2";
			case DBSource.MYSQL:
				return "MySQL";
			default:
				return "Oracle";
		}
	}

	public Connection getConnection(String name) throws Exception {
		try {
			if (prop == null) {

				Context ctx = null;
				DataSource source = null;

				if (otherWas) {
					ctx = new InitialContext();
				} else {
					ctx = (Context) new InitialContext()
							.lookup("java:comp/env");
				}
				source = (DataSource) ctx.lookup(name);
				//idx++;
				Connection con = source.getConnection();
				//System.out.println("-------------------------- DB Source :" + idx + "."+con);
				//cons.add(idx + ":"+con );
				if(observe){
					ConnectionChecker cc = new ConnectionChecker(con, getInfo());
					con = cc;
				}
				return con;
			}
			String driver = (String) prop.get(name + ".db.driver");
			String url = (String) prop.get(name + ".db.url");
			String user = (String) prop.get(name + ".db.user");
			String passwd = (String) prop.get(name + ".db.passwd");

			Class.forName(driver);
			Connection con = DriverManager.getConnection(url, user, passwd);
			return con;
		} catch (javax.naming.NoInitialContextException e) {
			if (sfname != null) {
				prop = org.sdf.util.FileUtil.loadProperties(sfname);
				String driver = (String) prop.get(name + ".db.driver");
				String url = (String) prop.get(name + ".db.url");
				String user = (String) prop.get(name + ".db.user");
				String passwd = (String) prop.get(name + ".db.passwd");

				Class.forName(driver);
				Connection con = DriverManager.getConnection(url, user, passwd);
				return con;
			}
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}
	}
	
	
	public Connection getConnection_ex(String name) throws Exception {
		try {
			if (prop == null) {

				Context ctx = null;
				DataSource source = null;

				ctx = new InitialContext();
				Log.act.info("WAS : "+otherWas);
				source = (DataSource) ctx.lookup(name);
				//idx++;
				Connection con = source.getConnection();
				//System.out.println("-------------------------- DB Source :" + idx + "."+con);
				//cons.add(idx + ":"+con );
				if(observe){
					ConnectionChecker cc = new ConnectionChecker(con, getInfo());
					con = cc;
				}
				return con;
			}
			String driver = (String) prop.get(name + ".db.driver");
			String url = (String) prop.get(name + ".db.url");
			String user = (String) prop.get(name + ".db.user");
			String passwd = (String) prop.get(name + ".db.passwd");

			Class.forName(driver);
			Connection con = DriverManager.getConnection(url, user, passwd);
			return con;
		} catch (javax.naming.NoInitialContextException e) {
			if (sfname != null) {
				prop = org.sdf.util.FileUtil.loadProperties(sfname);
				String driver = (String) prop.get(name + ".db.driver");
				String url = (String) prop.get(name + ".db.url");
				String user = (String) prop.get(name + ".db.user");
				String passwd = (String) prop.get(name + ".db.passwd");

				Class.forName(driver);
				Connection con = DriverManager.getConnection(url, user, passwd);
				return con;
			}
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}
	}	
	
	String getInfo() {
		Exception ex = new Exception();
		String info = "";
		java.io.PrintWriter w = null;
		java.io.BufferedReader r = null;
		java.io.StringWriter s = null;
		try {
			s = new java.io.StringWriter();
			w = new java.io.PrintWriter(s);
			ex.printStackTrace(w);
			r = new java.io.BufferedReader(new java.io.StringReader(s
					.toString()));
			StringBuffer buf = new StringBuffer();

			if(m_from == 0) m_from = 3;
			if(m_amount == 0) m_amount = 6;
			
			for (int i = 0; i < m_from; i++){
				r.readLine();
			}
			
			for (int i = 0; i < m_amount; i++){
				buf.append(r.readLine()).append("\n");
			}
			/*
			String str = r.readLine().trim();
			info = str.substring(3);
			*/
			info = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (Exception e) {
			}
			try {
				w.close();
			} catch (Exception e) {
			}
		}
		return info;
	}	

	public static void init() {
	}
	// public static List cons = new ArrayList();

}
