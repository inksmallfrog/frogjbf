package com.inksmallfrog.frogjbf.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * This class is using for maintaining data-source connection
 */
public class ConnectionPool {
	private List<Connection> connectionList = null;			//all connections maintained
	private Queue<Connection> freeConnectionQueue = null;	//free connections
	private DataSourceConfig config = null;					//data-source config

	/**
	 * Constructor
	 * init the connectionPool with data-source config
	 * @param config <DataSourceConfig>
	 */
	public ConnectionPool(DataSourceConfig config){
		//加载驱动
		try {
			Class.forName(config.getDriver());
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		this.connectionList = new ArrayList<>(config.getMaxConnetionCount());
		this.freeConnectionQueue = new LinkedList<>();
		this.config = config;
	}
	
	/**
	 * Try to get a connection from the freeQueue
	 * or create a new connection
	 * @return <Connection>
	 */
	synchronized Connection getConnection(){
		Connection conn = null;
		try{
			while(null == conn){
				conn = getFreeConnection();
				if(null == conn){	//no free connection available
					if(connectionList.size() == config.getMaxConnetionCount()){
						wait();
					}else{
						conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
						connectionList.add(conn);
					}
				}
			}
		}catch (SQLException | InterruptedException e){
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * Get connection from the freeConnectionQueue
	 * @return null | <Connection>
	 */
	private Connection getFreeConnection() throws SQLException {
		Connection conn;
		do{	//avoid the case that database shutdown the connection
			conn = freeConnectionQueue.isEmpty() ? null : freeConnectionQueue.poll();
		}while(null != conn && conn.isClosed());
		return conn;
	}
	/**
	 * make the connection go to the freeConnectionQueue
	 * @param conn <Connection>
	 */
	synchronized void freeConnection(final Connection conn){
		try {
			if(null != conn && !conn.isClosed()){
				freeConnectionQueue.offer(conn);
				notify();	//notify the guy who is waiting free connection

				//remove the connection when timeout
				new Timer().schedule(new TimerTask(){
					@Override
					public void run() {
						try {
							if(conn != null){
								freeConnectionQueue.remove(conn);
								connectionList.remove(conn);
								conn.close();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}, config.getTimeout());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 销毁连接池
	 */
	public void destroyPool(){
		try {
			for(Connection conn : connectionList){
				connectionList.remove(conn);
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
