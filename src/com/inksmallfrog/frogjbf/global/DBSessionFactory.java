package com.inksmallfrog.frogjbf.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inksmallfrog.frogjbf.datasource.DataSourceConfig;
import com.inksmallfrog.frogjbf.datasource.ConnectionPool;
import com.inksmallfrog.frogjbf.datasource.DBSession;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * Singleton
 *
 * work as the class name
 */
public class DBSessionFactory {
	//singleton
	private static DBSessionFactory factory = new DBSessionFactory();
	static DBSessionFactory getInstance(){
		return factory;
	}
	private DBSessionFactory(){}

	//connectionPools is used for all threads
	private Map<String, ConnectionPool> connectionPools = new HashMap<String, ConnectionPool>();

	/**
	 * create default DBSession according to config
	 * @return <DBSession>
	 */
	private DBSession createDefaultDBSession(){
		return createDBSession(JBFConfig.getAppConfig().getDefaultDataSourceConfig());
	}
	/**
	 * create default DBSession base on data-source name
	 * @return <DBSession>
	 */
	private DBSession createDBSession(String dataSourceName){
		return createDBSession(JBFConfig.getAppConfig().getDataSourceConfig(dataSourceName));
	}
	/**
	 * create default DBSession base on data-source config
	 * @return <DBSession>
	 */
	private DBSession createDBSession(DataSourceConfig config){
		ConnectionPool pool = connectionPools.get(config);
		if(null == pool){
			pool = new ConnectionPool(config);
			connectionPools.put(config.getName(), pool);
		}
		DBSession dbSession = new DBSession(config, pool);
		return dbSession;
	}
	private void bindDBSessionToCurrentThread(DBSession dbSession, DataSourceConfig config){
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
			dbSession = createDBSession(config);
			bindDBSessionToCurrentThread(dbSession, config);
		}
		return dbSession;
	}
	public List<DBSession> getAllDBSession(){
		return JBFContext.getAppContext().getAllDBSession();
	}
	public void unbindDefaultDBSessionFromThread(){
		unbindDBSessionFromThread(JBFConfig.getAppConfig().getDefaultDataSourceConfig());
	}
	public void unbindDBSessionFromThread(DataSourceConfig defaultDataSourceConfig) {
		unbindDBSessionFromThread(defaultDataSourceConfig.getName());
	}
	public void unbindDBSessionFromThread(String name) {
		JBFContext.getAppContext().removeSession(name);
	}
	public void closeCurrentDBSession(){
		List<DBSession> dbSessions = getAllDBSession();
		for(DBSession dbSession : dbSessions){
			unbindDBSessionFromThread(dbSession.getConfig().getName());
		}
	}
	public void destroy(){
		Set<String> keys = connectionPools.keySet();
		for(String key : keys){
			connectionPools.get(key).destroyPool();
		}
	}
}
