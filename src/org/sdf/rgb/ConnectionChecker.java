package org.sdf.rgb;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConnectionChecker implements Connection{
	String id;
	String info;
	private Connection con;
	public ConnectionChecker(Connection con, String info){
		this.con = con;
		ConnectionObserver obs = ConnectionObserver.getInstance();
		this.info = info; 
		this.id = obs.add(info);
	}

	
	public Connection getConnection(){
		return con;
	}
	
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		con.clearWarnings();
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub
		ConnectionObserver obs = ConnectionObserver.getInstance();
		obs.remove(this.id);
		con.close();
		con = null;
	}

	public void commit() throws SQLException {
		// TODO Auto-generated method stub
		con.commit();
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.createArrayOf(typeName, elements);
	}

	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		
		return con.createBlob();
	}

	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return con.createClob();
	}

	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return con.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return con.createSQLXML();
	}

	public Statement createStatement() throws SQLException {
		// TODO Auto-generated method stub
		return con.createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.createStatement(resultSetType,resultSetConcurrency);
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.createStruct(typeName, attributes);
	}

	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		return con.getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		return con.getCatalog();
	}

	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return con.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return con.getClientInfo(name);
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return con.getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return con.getMetaData();
	}

	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		return con.getTransactionIsolation();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return con.getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return con.getWarnings();
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return con.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		return con.isReadOnly();
	}

	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return con.isValid(timeout);
	}

	public String nativeSQL(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return con.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareCall(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareStatement(sql, columnNames);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		con.releaseSavepoint(savepoint);
		
	}

	public void rollback() throws SQLException {
		// TODO Auto-generated method stub
		con.rollback();
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		con.rollback(savepoint);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		// TODO Auto-generated method stub
		con.setAutoCommit(autoCommit);
	}

	public void setCatalog(String catalog) throws SQLException {
		// TODO Auto-generated method stub
		con.setCatalog(catalog);
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		con.setClientInfo(properties);
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		con.setClientInfo(name, value);
	}

	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub
		con.setHoldability(holdability);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		// TODO Auto-generated method stub
		con.setReadOnly(readOnly);
	}

	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return con.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		return con.setSavepoint(name);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		// TODO Auto-generated method stub
		con.setTransactionIsolation(level);
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub
		con.setTypeMap(map);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return con.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return con.unwrap(iface);
	}


	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	
}