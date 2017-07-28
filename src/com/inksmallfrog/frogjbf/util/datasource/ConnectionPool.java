package com.inksmallfrog.frogjbf.util.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.inksmallfrog.frogjbf.config.DataSourceConfig;

public class ConnectionPool {
	private int maxConnection = 1;
	private List<Connection> connectionList = null;
	private Queue<Connection> freeConnectionQueue = null;
	private DataSourceConfig config = null;
	
	public ConnectionPool(DataSourceConfig config){
		//加载驱动
		try {
			Class.forName(config.getDriver());
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		this.maxConnection = Math.max(config.getMaxConnetionCount(), 1);
		this.connectionList = new ArrayList<Connection>(maxConnection);
		this.freeConnectionQueue = new LinkedList<Connection>();
		this.config = config;
	}
	
	/**
	 * 获取一个数据库连接
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized Connection getConnection(){
		Connection conn = null;
		while(conn == null){
			if(hasFreeConnection()){
				conn = getFreeConnection();
			}else{
				if(connectionList.size() == config.getMaxConnetionCount()){
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
						connectionList.add(conn);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			}
		}
		return conn;
	}
	
	/**
	 * 判断是否存在空闲连接
	 * @return
	 */
	private boolean hasFreeConnection(){
		return !freeConnectionQueue.isEmpty();
	}
	
	/**
	 * 从空闲队列中获取连接
	 * @return
	 */
	private Connection getFreeConnection(){
		if(freeConnectionQueue.isEmpty()){
			return null;
		}else{
			return freeConnectionQueue.poll();
		}
	}
	/**
	 * 关闭数据库连接
	 * @param conn
	 */
	public synchronized void closeConnection(Connection conn){
		try {
			if(conn != null && !conn.isClosed()){
				freeConnectionQueue.offer(conn);
				notify();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 销毁连接池
	 */
	public void destroyPool(){
		try {
			for(Connection conn : connectionList){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
