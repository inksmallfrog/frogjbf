package com.inksmallfrog.frogjbf.global;

import com.inksmallfrog.frogjbf.datasource.DataSourceConfig;
import com.inksmallfrog.frogjbf.exception.JBFConfigException;
import com.inksmallfrog.frogjbf.exception.MethodNotFoundException;
import com.inksmallfrog.frogjbf.exception.UnsupportDataSourceException;
import com.inksmallfrog.frogjbf.util.IOUtil;
import com.inksmallfrog.frogjbf.util.JBFControllerClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * Singleton
 *
 * The structure of the app config
 *
 */
public class JBFConfig {
    //hunger Singleton mode
    private static JBFConfig config = new JBFConfig();
    public static JBFConfig getAppConfig() {
        return config;
    }
    private JBFConfig() {
        loadConfig();
    }

    //DEFAULT CONSTANTS
    private final String CONFIG_PATH_RELATIVE_TO_CLASSLOADER = "../jbf.config.json";
    private final String DEFAULT_REDIRECT_PREFIX = "redirect:";
    private final int DEFAULT_PORT = -1;
    private final int DEFAULT_MAX_DOWNLOAD_BUFFER = 8 * 1024 * 1024; //8MB
    private final int DEFAULT_MAX_CONNECTION_COUNT = 100;
    private final long DEFAULT_CONNECTION_TIMEOUT = 30 * 60 * 1000; //30min

    //config items
    private String urlRedirectPrefix;
    private String beanPackagePath;
    private String daoPackagePath;
    private String servicePackagePath;

    private int maxDownloadBuffer;

    private Map<String, List<String>> prefixPattern = new HashMap();
    private Map<String, List<String>> postfixPattern = new HashMap();
    
    private Map<String, JBFControllerClass> urlMethod2ControllerClassMap = new HashMap<>();
    private Map<String, Method> urlMethod2HandlerMap = new HashMap<>();
    private Map<String, DataSourceConfig> dataSources = new HashMap<>();
    private DataSourceConfig defaultDataSource = null;

    private String[] requestMethodsAvailable = {"get", "post", "delete", "put"};
    private String[] routerPatternTypes = {"staticPattern", "dynamicPattern"};

	private void loadConfig(){
        JSONObject jsonObject = loadJsonFromConfig();
        loadSingleData(jsonObject);
        loadDataSources(jsonObject);
        loadRouterPattern(jsonObject);
        loadRouterRules(jsonObject);
    }
    private JSONObject loadJsonFromConfig(){
        InputStream configFilePath = JBFConfig.class.getClassLoader().getResourceAsStream(CONFIG_PATH_RELATIVE_TO_CLASSLOADER);
        String jsonText = "";
        try {
            jsonText = IOUtil.getStringFromInputStream(configFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(jsonText);
    }
    private void loadSingleData(JSONObject jsonObject){
        urlRedirectPrefix = (String) requireJsonItemWithDefaultValue(jsonObject,
                "urlRedirectPrefix", DEFAULT_REDIRECT_PREFIX);
        beanPackagePath = (String) requireJsonItemWithDefaultValue(jsonObject,
                "beanPackagePath", null);
        daoPackagePath = (String) requireJsonItemWithDefaultValue(jsonObject,
                "daoPackagePath", null);
        servicePackagePath = (String) requireJsonItemWithDefaultValue(jsonObject,
                "servicePackagePath", null);
        maxDownloadBuffer = (int) requireJsonItemWithDefaultValue(jsonObject,
                "maxDownloadBuffer", DEFAULT_MAX_DOWNLOAD_BUFFER);
    }
    private void loadRouterPattern(JSONObject jsonObject){
    	for(String patternType : routerPatternTypes){
    		if(jsonObject.has(patternType)){
    			List<String> prefixPatterns = new ArrayList();
    			List<String> postfixPatterns = new ArrayList();
        		JSONObject patternTypeObject = jsonObject.getJSONObject(patternType);
        		
        		JSONArray prefix = patternTypeObject.getJSONArray("prefix");
        		JSONArray postfix = patternTypeObject.getJSONArray("postfix");
        		for(int i = 0; i < prefix.length(); ++i){
        			prefixPatterns.add(prefix.getString(i));
        		}
        		for(int i = 0; i < postfix.length(); ++i){
        			postfixPatterns.add(postfix.getString(i));
        		}
        		
        		prefixPattern.put(patternType, prefixPatterns);
        		postfixPattern.put(patternType, postfixPatterns);
        	}
    	}
    	
    }
   
    private void loadDataSources(JSONObject jsonObject){
        if(jsonObject.has("dataSources")){
            JSONObject dataSourcesObject = jsonObject.getJSONObject("dataSources");
            Iterator<?> dataSourcesIt = dataSourcesObject.keys();
            while(dataSourcesIt.hasNext()){
                String dataSourceName = (String) dataSourcesIt.next();
                JSONObject dataSourceObject = dataSourcesObject.getJSONObject(dataSourceName);
                String dataSourceDB = (String) requireJsonItem(dataSourceObject, "db");
                String dataSourceDBName = (String) requireJsonItem(dataSourceObject, "dbName");
                String dataSourceHost = (String) requireJsonItemWithDefaultValue(dataSourceObject,
                        "host", "localhost");
                int dataSourcePort = (int) requireJsonItemWithDefaultValue(dataSourceObject,
                        "port", DEFAULT_PORT);
                String dataSourceUser = (String) requireJsonItem(dataSourceObject, "user");
                String dataSourcePassword = (String) requireJsonItem(dataSourceObject, "password");
                int maxConnectionCount = (int) requireJsonItemWithDefaultValue(dataSourceObject,
                        "maxConnectionCount", DEFAULT_MAX_CONNECTION_COUNT);
                long timeout = (long) requireJsonItemWithDefaultValue(dataSourceObject,
                        "timeout", DEFAULT_CONNECTION_TIMEOUT);
                DataSourceConfig dataSourceConfig = null;
                try {
                    dataSourceConfig = new DataSourceConfig(
                            dataSourceName, dataSourceDBName,
                            dataSourceDB, dataSourceHost,
                            dataSourcePort, dataSourceUser,
                            dataSourcePassword, maxConnectionCount, timeout);
                } catch (UnsupportDataSourceException e) {
                    e.printStackTrace();
                }
                dataSources.put(dataSourceName, dataSourceConfig);
                /*
                 * if there are many data-sources defined as "default": true
                 * the last one will be treated as the default
                 */
                if(dataSourceObject.has("default") && dataSourceObject.getBoolean("default")){
                    defaultDataSource = dataSourceConfig;
                }
                /*
                 * if there is no explicit default dataSource
                 * make the first one as the default dataSource
                 */
                if(null == defaultDataSource){
                    defaultDataSource = dataSourceConfig;
                }
            }
        }else{
            System.out.println("WARNING: NO dataSources defined in jbf.config.json!");
        }
    }
    private Object requireJsonItem(JSONObject jsonObje, String key){
        Object ret = null;
        if(jsonObje.has(key)){
            ret = jsonObje.get(key);
        }else{
            try {
                throw new JBFConfigException("Can't found " + key + " under " + jsonObje.toString());
            } catch (JBFConfigException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
    private Object requireJsonItemWithDefaultValue(JSONObject jsonObj, String key, Object defaultVal){
        return jsonObj.has(key) ? jsonObj.get(key) : defaultVal;
    }
    private void loadRouterRules(JSONObject jsonObject){
        if(jsonObject.has("rules")){
            JSONObject rulesObject = jsonObject.getJSONObject("rules");
            Iterator<?> it = rulesObject.keys();
            while(it.hasNext()){
                String url = (String)it.next();
                JSONObject ruleObject = rulesObject.getJSONObject(url);
                String clazzName;
                String action;
                for(String requestMethod : requestMethodsAvailable){
                    if(ruleObject.has(requestMethod)){
                        String handlerName = ruleObject.getString(requestMethod);
                        int endPosOfClassName = handlerName.lastIndexOf('.');
                        if(endPosOfClassName <= 0){
                            try {
                                throw new JBFConfigException("Not a validate action found at: " + url + "<" + requestMethod + ">\n" +
                                        "Expected: classPath.method\n" +
                                        "Found: " + handlerName);
                            } catch (JBFConfigException e) {
                                e.printStackTrace();
                            }
                        }
                        clazzName = handlerName.substring(0, endPosOfClassName);
                        JBFControllerClass controllerClass = null;
                        try {
                            controllerClass = new JBFControllerClass(clazzName);
                            urlMethod2ControllerClassMap.put(url + ":" + requestMethod, controllerClass);
                            action = handlerName.substring(endPosOfClassName + 1, handlerName.length());
                            Method handler = controllerClass.getMethod(action);
                            urlMethod2HandlerMap.put(url + ":" + requestMethod, handler);
                        } catch (ClassNotFoundException | MethodNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else{
            System.out.println("WARNING: NO routing rules defined in jbf.config.json!");
        }
    }


    //Getters && Setters

    /*public String getServerRoot() {
		return serverRoot;
	}*/
    public DataSourceConfig getDefaultDataSourceConfig(){
        if(null == defaultDataSource){
            try {
                throw new JBFConfigException("No default dataSource found!\n" +
                        "Have you defined any dataSource in dataSources{} ?");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultDataSource;
    }

    public DataSourceConfig getDataSourceConfig(String configName){
        DataSourceConfig config = dataSources.get(configName);
        if(null == config){
            try {
                throw new JBFConfigException("No dataSource found! \n" +
                        "asked for: " + configName + "\n" +
                        "Have you defined this dataSource in dataSources{} ?");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    String getDaoPackagePath() {
        return daoPackagePath;
    }
    
    public String getBeanPackagePath() {
		return beanPackagePath;
	}
	public void setBeanPackagePath(String beanPackagePath) {
		this.beanPackagePath = beanPackagePath;
	}
	public void setDaoPackagePath(String daoPackagePath) {
        this.daoPackagePath = daoPackagePath;
    }
    String getServicePackagePath() {
        return servicePackagePath;
    }
    public void setServicePackagePath(String servicePackagePath) {
        this.servicePackagePath = servicePackagePath;
    }
    public List<String> getPrefixPatterns(String patternType) {
        return prefixPattern.get(patternType);
    }

    public List<String> getPostfixPatterns(String patternType) {
        return postfixPattern.get(patternType);
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

    public String getUrlRedirectPrefix() {
        return urlRedirectPrefix;
    }
    public void setUrlRedirectPrefix(String urlRedirectPrefix) {
        this.urlRedirectPrefix = urlRedirectPrefix;
    }
}
