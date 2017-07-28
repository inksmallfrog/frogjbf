package com.inksmallfrog.frogjbf.util.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import com.inksmallfrog.frogjbf.config.DataSourceConfig;

class ConnectionPool {
	private int maxConnection = 1;
	private List<Connection> connectionList = null;
	private Queue<Connection> freeConnectionQueue = null;
	private DataSourceConfig config = null;

	/**
	 * Constructor
	 * init the connectionPool with the config
	 * @param config <DataSourceConfig>
	 */
	ConnectionPool(DataSourceConfig config){
		//加载驱动
		try {
			Class.forName(config.getDriver());
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		this.maxConnection = Math.max(config.getMaxConnetionCount(), 1);
		this.connectionList = new ArrayList<>(maxConnection);
		this.freeConnectionQueue = new LinkedList<>();
		this.config = config;
	}
	
	/**
	 * get a connection from the connectionPool or a new connection
	 * @return <Connection>
	 */
	synchronized Connection getConnection(){
		Connection conn = null;
		while(conn == null){
			if(hasFreeConnection()){
				try {
					conn = getFreeConnection();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
	 * check if there any free connection
	 * @return <boolean>
	 */
	private boolean hasFreeConnection(){
		return !freeConnectionQueue.isEmpty();
	}
	
	/**
	 * Get connection from the freeConnectionQueue
	 * @return null | <Connection>
	 */
	private Connection getFreeConnection() throws SQLException {
		Connection conn;
		//avoid the case that database shutdown the connection
		do{
			conn = freeConnectionQueue.isEmpty() ? null : freeConnectionQueue.poll();
		}while(conn != null && conn.isClosed());
		return conn;
	}
	/**
	 * make the connection go to the freeConnectionQueue
	 * @param conn <Connection>
	 */
	synchronized void freeConnection(Connection conn){
		try {
			if(conn != null && !conn.isClosed()){
				freeConnectionQueue.offer(conn);
				notify();	//notify the guy who is waiting free connection

				//remove the connection when timeout
				new Timer().schedule(new TimerTask(){
					@Override
					public void run() {
						try {
							freeConnectionQueue.remove(conn);
							connectionList.remove(conn);
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}, config.getTimeout());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 销毁连接池
	 *
	public void destroyPool(){
		try {
			for(Connection conn : connectionList){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
