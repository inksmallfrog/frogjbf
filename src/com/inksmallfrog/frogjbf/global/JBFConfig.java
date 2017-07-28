package com.inksmallfrog.frogjbf.global;

import com.inksmallfrog.frogjbf.config.DataSourceConfig;
import com.inksmallfrog.frogjbf.util.IOUtil;
import com.inksmallfrog.frogjbf.util.JBFControllerClass;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by inksmallfrog on 17-7-27.
 * JBFConfig class
 * Singleton
 */
public class JBFConfig {
    private static JBFConfig config = new JBFConfig();
    public static JBFConfig getAppConfig() {
        return config;
    }
    private JBFConfig() {
        loadConfig();
    }

    private String daoPackagePath;
    private String servicePackagePath;
    private String serverRoot;
    private String staticRouterPrefix;
    private String dynamicRouterPrefix;
    private int maxDownloadBuffer;
    private Map<String, JBFControllerClass> urlMethod2ControllerClassMap = new HashMap<>();
    private Map<String, Method> urlMethod2HandlerMap = new HashMap<>();
    private Map<String, DataSourceConfig> dataSources = new HashMap<>();
    private DataSourceConfig defaultDataSource = null;

    private String[] urlMethods = {"get", "post", "delete", "put"};

    
    
    public String getServerRoot() {
		return serverRoot;
	}

    public DataSourceConfig getDefaultDataSourceConfig(){
    	if(null == defaultDataSource){
    		try {
				throw new Exception("No default dataSource found!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return defaultDataSource;
    }
    
    public DataSourceConfig getDataSourceConfig(String configName){
    	DataSourceConfig config = dataSources.get(configName);
    	if(null == config){
    		try {
				throw new Exception("No dataSource found! asked for: " + configName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return config;
    }
    
	public String getDaoPackagePath() {
		return daoPackagePath;
	}
	public void setDaoPackagePath(String daoPackagePath) {
		this.daoPackagePath = daoPackagePath;
	}
	public String getServicePackagePath() {
		return servicePackagePath;
	}
	public void setServicePackagePath(String servicePackagePath) {
		this.servicePackagePath = servicePackagePath;
	}
	private void loadConfig(){
        InputStream configFilePath = JBFConfig.class.getClassLoader().getResourceAsStream("../jbf.config.json");
        String jsonText = "";
        try {
            jsonText = IOUtil.getStringFromInputStream(configFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(jsonText);
        
        daoPackagePath = jsonObject.has("daoPackagePath") ? jsonObject.getString("daoPackagePath") : null;
        servicePackagePath = jsonObject.has("servicePackagePath") ? jsonObject.getString("servicePackagePath") : null;
        serverRoot = jsonObject.has("serverRoot") ? jsonObject.getString("serverRoot") : "";
        staticRouterPrefix = jsonObject.has("staticPrefix") ? jsonObject.getString("staticPrefix") : null;
        dynamicRouterPrefix = jsonObject.has("dynamicRouterPrefix") ? jsonObject.getString("dynamicRouterPrefix") : null;
        maxDownloadBuffer = jsonObject.has("maxDownloadBuffer") ? jsonObject.getInt("maxDownloadBuffer") : 8 * 1024 * 1024;
        
        JSONObject dataSourcesObject = jsonObject.getJSONObject("dataSources");
        Iterator<?> dataSourcesIt = dataSourcesObject.keys();
        while(dataSourcesIt.hasNext()){
        	String dataSourceName = (String) dataSourcesIt.next();
        	JSONObject dataSourceObject = dataSourcesObject.getJSONObject(dataSourceName);
        	String dataSourceDriver = dataSourceObject.getString("driver");
        	String dataSourceUrl = dataSourceObject.getString("url");
        	String dataSourceHost = dataSourceObject.getString("host");
        	String dataSourcePort = dataSourceObject.getString("port");
        	String dataSourceUser = dataSourceObject.getString("user");
        	String dataSourcePassword = dataSourceObject.getString("password");
        	int maxConnectionCount = 100;
        	if(dataSourceObject.has("maxConnectionCount")){
        		maxConnectionCount = dataSourceObject.getInt("maxConnectionCount");
        	}
        	DataSourceConfig dataSourceConfig = new DataSourceConfig(dataSourceName, dataSourceDriver, 
        											dataSourceUrl, dataSourceHost, 
        											dataSourcePort, dataSourceUser,
        											dataSourcePassword, maxConnectionCount);
        	dataSources.put(dataSourceName, dataSourceConfig);
        	if(dataSourceObject.has("default") && dataSourceObject.getString("default").equals("true")){
        		if(defaultDataSource != null){
        			try {
						throw new Exception("multi default dataSource error!");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		defaultDataSource = dataSourceConfig;
        	}
        }
        
        JSONObject rulesObject = jsonObject.getJSONObject("rules");
        Iterator<?> it = rulesObject.keys();
        while(it.hasNext()){
            String url = (String)it.next();
            JSONObject ruleObject = rulesObject.getJSONObject(url);
            String clazzName = "";
            String action = "";
            for(String method : urlMethods){
                if(ruleObject.has(method)){
                    String handlerName = ruleObject.getString(method);
                    int endPosOfClassName = handlerName.lastIndexOf('.');
                    clazzName = handlerName.substring(0, endPosOfClassName);
                    JBFControllerClass controllerClass = null;
                    try {
                        controllerClass = new JBFControllerClass(clazzName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    urlMethod2ControllerClassMap.put(url + ":" + method, controllerClass);
                    action = handlerName.substring(endPosOfClassName + 1, handlerName.length());
                    Method handler = controllerClass.getMethod(action);
                    urlMethod2HandlerMap.put(url + ":" + method, handler);
                }
            }
        }
        JBFContext.getAppContext().initAllInstances();
    }

    public String getDynamicRouterPrefix() {
        return dynamicRouterPrefix;
    }

    public String getStaticRouterPrefix() {
        return staticRouterPrefix;
    }

    public JBFControllerClass getHandlerClassByUrlAndMethod(String url, String method){
        return urlMethod2ControllerClassMap.get(url + ":" + method.toLowerCase());
    }
    public Method getHandlerByUrlAndMethod(String url, String method){
        return urlMethod2HandlerMap.get(url + ":" + method.toLowerCase());
    }

    public int getMaxDownloadBuffer(){
        return maxDownloadBuffer;
    }
}
