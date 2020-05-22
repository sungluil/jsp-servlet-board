package org.sdf.rgb;

import java.sql.*;

/**
 * <PRE>
 * ??ë¡????¸ë?    : Help Desk ???? êµ¬ì?
 * ??ë¡?ê·¸ë?¨ê??? : 
 * ê´??? ???´ë?   : 
 * ?´ë???¤ë? 	    : TrxContext
 * ???±ì?? 	        : ë°??±í??
 * ???±ì?? 	        : 2005. 5. 18.
 * ë¹?ê³? 		        : 
 * ê°????´ë??  	    : 2005. 5. 18. ë°??±í??, v1.0, ìµ?ì´?????
 * 
 * 
 * </PRE>
 */

public class TrxContext {
	Connection con;

	public TrxContext(String name) throws Exception {
		con = new DBSource().getConnection(name);
	}

	public TrxContext(String name, boolean b) throws Exception {
		con = new DBSource().getConnection_ex(name);
	}	
	
	public TrxContext(String name, String fname) throws Exception {

		con = new DBSource(fname).getConnection(name);
	}

	public Connection getConnection() {
		return con;
	}

	public void begin() throws Exception {
		con.setAutoCommit(false);
	}

	public void commit() throws Exception {
		con.commit();
	}

	public void rollback() {
		try {
			con.rollback();
		} catch (Exception e) {
		}
	}

	public void close() {
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
		}
		try {
			con.close();
		} catch (Exception e) {
		}
		con = null;
	}
}
