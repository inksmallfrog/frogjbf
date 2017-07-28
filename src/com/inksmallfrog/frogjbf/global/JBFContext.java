package com.inksmallfrog.frogjbf.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inksmallfrog.frogjbf.config.DataSourceConfig;
import com.inksmallfrog.frogjbf.util.PackageLoader;
import com.inksmallfrog.frogjbf.util.WordMapper;
import com.inksmallfrog.frogjbf.util.datasource.DBSession;
import com.inksmallfrog.frogjbf.util.datasource.DBSessionFactory;


public class JBFContext {
	private static JBFContext context = new JBFContext();
	public static JBFContext getAppContext(){
		return context;
	}
	private JBFContext(){}
	
	public final ThreadLocal<Map<String, DBSession>> sessionTrdLocal = new ThreadLocal<Map<String, DBSession>>();
	public void putDBSession(DBSession dbSession, DataSourceConfig config){
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		if(sessionPool == null){
			sessionPool = new HashMap<String, DBSession>();
		}
		if(dbSession != null && config != null){
			sessionPool.put(config.getName(), dbSession);
		}
		sessionTrdLocal.set(sessionPool);
	}
	public DBSession getDBSession(String configName){
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		if(null == sessionPool){
			return null;
		}else{
			return sessionTrdLocal.get().get(configName);
		}
	}
	public List<DBSession> getAllDBSession(){
		List<DBSession> sessions = new ArrayList<DBSession>();
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		for(DBSession session : sessionPool.values()){
			sessions.add(session);
		}
		return sessions;
	}
	public void removeSession(String configName){
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		sessionPool.remove(configName);
		sessionTrdLocal.set(sessionPool);
	}
	
	private DBSessionFactory dbSessionFactory = DBSessionFactory.getInstance();
	public DBSessionFactory getDbSessionFactory(){
		return dbSessionFactory;
	}
	
	private Map<String, Object> daoInstances = new HashMap<String, Object>();
	private Map<String, Object> serviceInstances = new HashMap<String, Object>();
	public void initAllInstances(){
		JBFConfig config = JBFConfig.getAppConfig();
		loadPackageInstanceToMap(config.getDaoPackagePath(), daoInstances);
		loadPackageInstanceToMap(config.getServicePackagePath(), serviceInstances);
	}
	
	private void loadPackageInstanceToMap(String packagePath, Map<String, Object> map) {
		if(null != packagePath){
			List<String> daoClasses = PackageLoader.getAllClassNamesFromPackage(packagePath);
			for(String className : daoClasses){
				String beanId = WordMapper.classNameToCamelName(className);
				Class<?> clazz = null;
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Object instance = null;
				try {
					instance = clazz.newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				map.put(beanId, instance);
			}
		}
	}
	public Object getDao(String daoName){
		return daoInstances.get(daoName);
	}
	public Object getService(String daoName){
		return serviceInstances.get(daoName);
	}
}
