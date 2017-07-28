package com.inksmallfrog.frogjbf.util.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inksmallfrog.frogjbf.config.DataSourceConfig;
import com.inksmallfrog.frogjbf.global.JBFConfig;
import com.inksmallfrog.frogjbf.global.JBFContext;

public class DBSessionFactory {
	private static DBSessionFactory factory = new DBSessionFactory();
	public static DBSessionFactory getInstance(){
		return factory;
	}
	private DBSessionFactory(){}
	
	private Map<String, ConnectionPool> connectionPools = new HashMap<String, ConnectionPool>();
	
	private DBSession createDefaultDBSession(){
		DataSourceConfig defaultDataSourceConfig = JBFConfig.getAppConfig().getDefaultDataSourceConfig();
		return createDBSession(defaultDataSourceConfig);
	}
	private DBSession createDBSession(String dataSourceName){
		DataSourceConfig config = JBFConfig.getAppConfig().getDataSourceConfig(dataSourceName);
		return createDBSession(config);
	}
	private DBSession createDBSession(DataSourceConfig config){
		ConnectionPool pool = connectionPools.computeIfAbsent(config.getName(), k -> new ConnectionPool(config));
		DBSession dbSession;
		dbSession = new DBSession();
		dbSession.setDataSourceName(config.getName());
		dbSession.setConnectionPool(pool);
		return dbSession;
	}
	
	private void bindDBSessionToThread(DBSession dbSession, DataSourceConfig config){
		JBFContext.getAppContext().putDBSession(dbSession, config);
	}
	
	public DBSession getDefaultDBSession(){
		return getDBSession(JBFConfig.getAppConfig().getDefaultDataSourceConfig());
	}
	public DBSession getDBSession(String configName){
		return getDBSession(JBFConfig.getAppConfig().getDataSourceConfig(configName));
	}
	public DBSession getDBSession(DataSourceConfig config){
		DBSession dbSession = JBFContext.getAppContext().getDBSession(config.getName());
		if(dbSession == null){
			dbSession = createDefaultDBSession();
			bindDBSessionToThread(dbSession, config);
		}
		return dbSession;
	}
	public List<DBSession> getAllDBSession(){
		return JBFContext.getAppContext().getAllDBSession();
	}
	public void unbindDefaultDBSessionFromThread(){
		unbindDBSessionFromThread(JBFConfig.getAppConfig().getDefaultDataSourceConfig());
	}
	public void unbindDBSessionFromThread(
			DataSourceConfig defaultDataSourceConfig) {
		unbindDBSessionFromThread(defaultDataSourceConfig.getName());
	}
	public void unbindDBSessionFromThread(String name) {
		JBFContext.getAppContext().removeSession(name);
	}
	
	
	public void closeCurrentDBSession(){
		List<DBSession> dbSessions = getAllDBSession();
		for(DBSession dbSession : dbSessions){
			unbindDBSessionFromThread(dbSession.getDataSourceName());
		}
	}
}
