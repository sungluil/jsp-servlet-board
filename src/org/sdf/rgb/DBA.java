package org.sdf.rgb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;

public class DBA {
	private String sqldir;
	long lasttime = -1;
	java.util.Hashtable sqls = new java.util.Hashtable();
	java.util.Hashtable dbtypes = new java.util.Hashtable();

	/*
	 * private DBA(){ init(); }
	 * 
	 * public static DBA getInstance(){ if( dba == null ) dba = new DBA();
	 * return dba; }
	 */
	public void markingUpdate() {
		File dir = new File(sqldir);
		if (!dir.exists())
			return;

		File cf = new File(dir, "dba.dat");
		cf
				.setLastModified(java.util.Calendar.getInstance().getTime()
						.getTime());
	}

	public void init(String sqldir) {
		// System.out.println( sqldir);
		this.sqldir = sqldir;
		File dir = new File(sqldir);
		if (!dir.exists())
			return;

		File cf = new File(dir, "dba.dat");

		try {
			if (!cf.exists())
				cf.createNewFile();
		} catch (Exception e) {
		}

		if (cf.lastModified() == lasttime)
			return;

		dbtypes.clear();
		lasttime = cf.lastModified();
		String[] dbs = dir.list();
		if (dbs == null)
			return;

		for (int i = 0; i < dbs.length; i++) {
			File d = new File(dir, dbs[i]);
			if (!d.isDirectory())
				continue;

			try {
				int idx = dbs[i].indexOf('.');
				String dbtype = dbs[i].substring(0, idx);
				dbtypes.put(dbtype, dbs[i]);
				addSQL(dbtype, d);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void addSQL(String dbtype, File dir) throws Exception {
		String[] sqls = dir.list();
		if (sqls == null || sqls.length == 0)
			return;

		for (int i = 0; i < sqls.length; i++) {
			// System.out.println( sqls[i]);
			if (!sqls[i].endsWith(".sql"))
				continue;
			parse(dbtype, dir, sqls[i]);
		}
	}

	private void parse(String dbtype, File dir, String fname) throws Exception {
		File f = new File(dir, fname);

		BufferedReader reader = new BufferedReader(new FileReader(f));

		String key = org.sdf.util.StringUtil.getLine(reader);
		String title = org.sdf.util.StringUtil.getLine(reader);

		String line = null;
		StringBuffer sqlstr = new StringBuffer();
		while (true) {
			line = org.sdf.util.StringUtil.getLine(reader);
			if (line == null)
				break;
			sqlstr.append(line).append("\n");
		}
		if (sqlstr.length() == 0)
			return;
		SQL sql = new SQL();
		sql.key = key;
		sql.dbtype = dbtype;
		sql.title = title;
		sql.fname = fname;
		sql.sql = sqlstr.toString();

		sqls.put(dbtype + "-" + key, sql);
	}

	protected String getQuery(String dbtype, String key) {

		SQL sql = (SQL) sqls.get(dbtype + "-" + key);
		if (sql == null)
			return null;

		return sql.getSQL();
	}

	public RecordSet getRecordSet(Connection connection, String dbtype,
			String key) throws Exception {
		RecordSet rset = null;
		String query = getQuery(dbtype, key);

		if (query == null)
			return null;
		// System.out.println( query);
		rset = new RecordSet(query);

		rset.executeQuery(connection);
		return rset;
	}

	public String getDBType(String type) {
		return (String) dbtypes.get(type);
	}

	public java.util.Enumeration getSQLs() {
		return sqls.elements();
	}

	public void update(String orgkey, String key, String dbtype, String title,
			String s) {
		SQL sql = (SQL) sqls.get(dbtype + "-" + orgkey);
		StringBuffer buf = new StringBuffer();

		sql.key = key;
		sql.title = title;
		sql.sql = s;

		buf.append(key).append("\n").append(title).append("\n\n").append(s)
				.append("\n");

		File f = new File(new File(sqldir, getDBType(dbtype)), sql
				.getFileName());

		try {
			FileWriter writer = new FileWriter(f);
			writer.write(buf.toString());
			writer.close();
		} catch (Exception e) {
		}
		// markingUpdate();
	}

	public void insert(String key, String dbtype, String title, String s) {
		StringBuffer buf = new StringBuffer();

		SQL sql = new SQL();
		sql.key = key;
		sql.dbtype = dbtype;
		sql.title = title;
		sql.fname = key + ".sql";
		sql.sql = s;

		sqls.put(dbtype + "-" + key, sql);

		buf.append(key).append("\n").append(title).append("\n\n").append(s)
				.append("\n");

		File f = new File(new File(sqldir, getDBType(dbtype)), sql
				.getFileName());

		try {
			FileWriter writer = new FileWriter(f);
			writer.write(buf.toString());
			writer.close();
		} catch (Exception e) {
		}
		// markingUpdate();
	}

	public void delete(String key, String dbtype) {
		SQL sql = (SQL) sqls.get(dbtype + "-" + key);

		File f = new File(new File(sqldir, getDBType(dbtype)), sql
				.getFileName());
		f.delete();
		sqls.remove(dbtype + "-" + key);
		sql = null;
		// markingUpdate();
	}
}