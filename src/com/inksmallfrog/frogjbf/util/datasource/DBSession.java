package com.inksmallfrog.frogjbf.util.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBSession {	
	private ConnectionPool connectionPool = null;
	private String dataSourceName = "";
	
	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	public List<Map<String, Object>> queryRows(String sql, Object...args){
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = connectionPool.getConnection();
		try {
			ps = connection.prepareStatement(sql);
			if(args != null){
				for(int i = 0; i < args.length; ++i){
					ps.setObject(i + 1, args[i]);
				}
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				int columnCount = rsmd.getColumnCount();
				Map<String, Object> row = new HashMap<String, Object>();
				for(int i = 0; i < columnCount; ++i){
					row.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1));
				}
				res.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close(connection, ps, rs);
		}
		
		return res;
	}
	/**
	 * 执行插入操作
	 * 返回新插入数据的主键(sql结果的第一列)
	 * null 表示插入失败
	 * @param sql
	 * @param obj
	 * @return
	 */
	public Object insertRow(String sql, String primaryRow, Object...args){
		Object res = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = connectionPool.getConnection();
		int rowAffected = 0;
		try {
			String generatedColumns[] = {primaryRow};
			ps = connection.prepareStatement(sql, generatedColumns);
			if(args != null){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close(connection, ps, rs);
		}
		return res;
	}
	/**
	 * 执行更新/删除操作
	 * 返回影响的行数
	 * @param sql
	 * @param args
	 * @return
	 */
	public int updateRow(String sql, Object...args){
		PreparedStatement ps = null;
		int rowAffected = 0;
		Connection connection = connectionPool.getConnection();
		try {
			ps = connection.prepareStatement(sql);
			if(args != null){
				for(int i = 0; i < args.length; ++i){
					ps.setObject(i + 1, args[i]);
				}
			}
			rowAffected = ps.executeUpdate();
		} catch (SQLException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close(connection, ps, null);
		}
		return rowAffected;
	}
	
	public void close(Connection c){
		close(c, null, null);
	}
	public void close(Connection c, Statement s){
		close(c, s, null);
	}
	public void close(Connection c, Statement s, ResultSet r){
		if(r != null){
			try{
				r.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(s != null){
			try{
				s.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(c != null){
			connectionPool.freeConnection(c);
		}
	}
}
