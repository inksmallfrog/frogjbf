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

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * Singleton
 *
 * The context of the app
 *
 */
public class JBFContext {
	//Singleton structure
	private static JBFContext context = new JBFContext();
	public static JBFContext getAppContext(){
		return context;
	}
	private JBFContext(){}

	//bind DBSession to thread
	private final ThreadLocal<Map<String, DBSession>> sessionTrdLocal = new ThreadLocal<Map<String, DBSession>>();
	public void putDBSession(DBSession dbSession, DataSourceConfig config){
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		if(sessionPool == null){ sessionPool = new HashMap<>(); }
		if(dbSession != null && config != null){ sessionPool.put(config.getName(), dbSession); }
		sessionTrdLocal.set(sessionPool);
	}
	public DBSession getDBSession(String configName){
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		return (null == sessionPool) ? null : sessionTrdLocal.get().get(configName);
	}
	public List<DBSession> getAllDBSession(){
		List<DBSession> sessions = new ArrayList<>();
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		if(null != sessionPool){
			for(DBSession session : sessionPool.values()){ sessions.add(session); }
		}
		return sessions;
	}
	public void removeSession(String configName){
		Map<String, DBSession> sessionPool = sessionTrdLocal.get();
		if(null != sessionPool){ sessionPool.remove(configName); }
		sessionTrdLocal.set(sessionPool);
	}
	
	private DBSessionFactory dbSessionFactory = DBSessionFactory.getInstance();
	public DBSessionFactory getDbSessionFactory(){
		return dbSessionFactory;
	}
	
	private Map<String, Object> daoInstances = new HashMap<>();
	private Map<String, Object> serviceInstances = new HashMap<>();
	void initAllInstances(){
		JBFConfig config = JBFConfig.getAppConfig();
		loadPackageClassesInstanceToMap(config.getDaoPackagePath(), daoInstances);
		loadPackageClassesInstanceToMap(config.getServicePackagePath(), serviceInstances);
	}

	/**
	 * load all classes' instance under the package defined in config
	 * to the target map
	 * key: 	camel-spelling of the class name
	 * value: 	classes' instance
	 *
	 * @param packagePath <String> the name load from config
	 * @param map <Map<String, Object>>
	 */
	private void loadPackageClassesInstanceToMap(String packagePath, Map<String, Object> map) {
		if(null != packagePath){
			List<String> classes = PackageLoader.getAllClassNamesFromPackage(packagePath);
			for(String className : classes){
				String beanId = WordMapper.classNameToCamelName(className);
				Class<?> clazz = null;
				Object instance = null;
				try {
					clazz = Class.forName(className);
					instance = clazz.newInstance();
					map.put(beanId, instance);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
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
