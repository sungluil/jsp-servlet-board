/*****************************************************************
 * Class : TableManager.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.rgb;

//import java.io.*;
import java.sql.*;

//import java.util.Calendar;

public class TableManager {
	Connection connection = null;

	public TableManager(Connection conn) {
		connection = conn;
	}

	/**
	 * tableA��i truncate
	 */
	public void truncate(String table) throws Exception {
		PreparedStatement pstmt = null;
		try {

			String query = "truncate table " + table;

			pstmt = connection.prepareStatement(query);

			pstmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	public RecordSet desc(String table) throws Exception {
		RecordSet rset = null;
		String sql = "desc " + table;

		rset = new RecordSet(sql);

		rset.executeQuery(connection);

		return rset;
	}

}
