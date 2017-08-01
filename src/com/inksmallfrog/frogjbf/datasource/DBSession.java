package com.inksmallfrog.frogjbf.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.inksmallfrog.frogjbf.global.JBFContext;
import com.inksmallfrog.frogjbf.util.BeanClassWrapper;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * This class is using for maintaining DBSession
 * each request thread will get a DBSessionMap when servlet need it
 * and each DBSession is responsible for a type of data-source
 */
public class DBSession {
	private DataSourceConfig config = null;
	private ConnectionPool connectionPool = null;	//A reference to the global connectionPool
	private Connection transactionConn = null; 

	public DBSession() {
	}

	public DBSession(DataSourceConfig config, ConnectionPool connectionPool) {
		this.config = config;
		this.connectionPool = connectionPool;
	}

	//Getters && Setters
	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	public DataSourceConfig getConfig() {
		return config;
	}

	public void setConfig(DataSourceConfig config) {
		this.config = config;
	}

	//Sql operations
	public void beginTransaction() throws SQLException{
		if(null == transactionConn){
			transactionConn = connectionPool.getConnection();
		}
		transactionConn.setAutoCommit(false);
	}
	
	public void commit() throws SQLException{
		if(null != transactionConn){
			transactionConn.commit();
		}
	}
	
	public void endTransaction() throws SQLException{
		if(null != transactionConn){
			transactionConn.setAutoCommit(true);
		}
		connectionPool.freeConnection(transactionConn);
		transactionConn = null;
	}

	public Object querySingleItem(String sql, Object...args){
		Object res = new Object();
		Connection connection = connectionPool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if(null != args){
				for(int i = 0; i < args.length; ++i){
					ps.setObject(i + 1, args[i]);
				}
			}
			rs = ps.executeQuery();
			if(rs.next()){
				res = rs.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(connection, ps, rs);
		}
		return res;
	}

	/**
	 *
	 * @param beanClazz
	 * @param sql
	 * @param args
	 * @param <T>
	 * @return
	 */
	public <T> List<T> queryRows(Class beanClazz, String sql, Object...args){
		BeanClassWrapper classWrapper = JBFContext.getAppContext()
				.getBeanClass(beanClazz.getName());
		List<T> res = new ArrayList<T>();
		Connection connection = connectionPool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> columnNames = new ArrayList<>();
		try {
			ps = connection.prepareStatement(sql);
			if(null != args){
				for(int i = 0; i < args.length; ++i){
					ps.setObject(i + 1, args[i]);
				}
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i = 0; i < columnCount; ++i){
				columnNames.add(rsmd.getColumnName(i + 1));
			}
			while(rs.next()){
				T bean = (T) classWrapper.newInstance();
				for(int i = 0; i < columnCount; ++i){
					classWrapper.setBeanFromDataSource(bean, columnNames.get(i),
							rs.getObject(i + 1), config);
				}
				res.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(connection, ps, rs);
		}
		return res;
	}
	

	/**
	 * call insertRow(String sql, String[] primaryCols, Object...args)
	 *
	 * @param sql <String> sql will be compiled by the prepareStatement
	 * @param primaryCol <String> the col will be returned as the primary col
	 * @param args <Object> arguments should be bound to the sql
	 * @return <Object> the col value defined by primaryCol | <null> insert err
	 */
	public Object insertRow(String sql, String primaryCol, Object...args){
		String [] primaryCols = {primaryCol};
		return insertRow(sql, primaryCols, args);
	}

	/**
	 * insert item
	 *
	 * @param sql <String> sql will be compiled by the prepareStatement
	 * @param primaryCols <String[]> the cols will be returned as the primary col
	 * @param args <Object> arguments should be bound to the sql
	 * @return <Object> the col value defined by primaryCols | <null> insert err
	 */
	public Object insertRow(String sql, String[] primaryCols, Object...args){
		Object res = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = connectionPool.getConnection();
		int rowAffected = 0;
		try {
			ps = connection.prepareStatement(sql, primaryCols);
			if(null != args){
				for(int i = 0; i < args.length; ++i){
					ps.setObject(i + 1, args[i]);
				}
			}
			rowAffected = ps.executeUpdate();
			if(rowAffected > 0){
				rs = ps.getGeneratedKeys();
				if(rs.next()){
					res = rs.getObject(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(connection, ps, rs);
		}
		return res;
	}

	/**
	 * update or delete item
	 *
	 * @param sql <String> sql will be compiled by the prepareStatement
	 * @param args <Object> arguments should be bound to the sql
	 * @return <int> the number of rows has been effected
	 */
	public int updateRow(String sql, Object...args){
		Connection connection = connectionPool.getConnection();
		PreparedStatement ps = null;
		int rowAffected = 0;
		try {
			ps = connection.prepareStatement(sql);
			if(null != args){
				for(int i = 0; i < args.length; ++i){
					ps.setObject(i + 1, args[i]);
				}
			}
			rowAffected = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(connection, ps, null);
		}
		return rowAffected;
	}

	private long getTotalRow(String querySql, Object[] args){
		String sql = "SELECT COUNT(1) FROM( " + querySql + ")";
		Object sqlValue = querySingleItem(sql, args);
		return config.getTypeMapper().sqlToJtype(sqlValue, Long.class);
	}

	public <T> List<T> queryPage(Class beanClazz, PageInfo pageInfo, String querySql, Object...args){
		List<T> ret = null;
		long totalRow = getTotalRow(querySql, args);
		int pageSize = pageInfo.getPageSize();
		int totalPage = (int) Math.ceil((totalRow * 1.0f) / pageSize);
		int targetPage = pageInfo.getCurPage();
		int minRow = (targetPage - 1) * pageSize + 1;
		int maxRow = minRow + pageSize;
		pageInfo.setTotalPage(totalPage);
		pageInfo.setRowCount(0);
		if (totalPage > 0) {
			String sql = config.getSQLGenerator().getSlicePageSQL(querySql, minRow, maxRow);
			ret = queryRows(beanClazz, sql, args);
			pageInfo.setRowCount(ret.size());
		}
		return ret;
	}
	
	/**
	 * clase any thing about the connection
	 *
	 * note: just put connection back to the freeConnectionQueue
	 * 		 not really close it
	 *
	 * @param c <Connection>
	 * @param s <Statement>
	 * @param r <ResultSet>
	 */
	private void close(Connection c, Statement s, ResultSet r){
		if(null != r){
			try{
				r.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(null != s){
			try{
				s.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(null != c){
			connectionPool.freeConnection(c);
		}
	}
	private void close(Connection c, Statement s){
		close(c, s, null);
	}

	private void close(Connection c){
		close(c, null, null);
	}
}
