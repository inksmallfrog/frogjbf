package com.inksmallfrog.frogjbf.config;

import com.inksmallfrog.frogjbf.util.IOUtil;
import com.inksmallfrog.frogjbf.util.JBFControllerClass;
import org.json.JSONObject;

import java.io.IOException;
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
    private static JBFConfig ourInstance = new JBFConfig();

    public static JBFConfig getInstance() {
        return ourInstance;
    }

    private JBFConfig() {
        loadConfig();
    }

    private String staticRouterPrefix;
    private String dynamicRouterPrefix;
    private int maxDownloadBuffer;
    private Map<String, JBFControllerClass> urlMethod2ControllerClassMap = new HashMap<>();
    private Map<String, Method> urlMethod2HandlerMap = new HashMap<>();

    private String[] urlMethods = {"get", "post", "delete", "put"};

    private void loadConfig(){
        String configFilePath = JBFConfig.class.getClassLoader()
                .getResource("/").getPath().replace("classes", "jbf.config.json");
        String jsonText = "";
        try {
            jsonText = IOUtil.getStringFromFile(configFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(jsonText);
        staticRouterPrefix = jsonObject.has("staticPrefix") ? jsonObject.getString("staticPrefix") : null;
        dynamicRouterPrefix = jsonObject.has("dynamicRouterPrefix") ? jsonObject.getString("dynamicRouterPrefix") : null;
        maxDownloadBuffer = jsonObject.has("maxDownloadBuffer") ? jsonObject.getInt("maxDownloadBuffer") : 8 * 1024 * 1024;
        JSONObject rulesObject = jsonObject.getJSONObject("rules");
        Iterator it = rulesObject.keys();
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
