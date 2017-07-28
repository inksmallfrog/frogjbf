package com.inksmallfrog.frogjbf.util;

import com.inksmallfrog.frogjbf.annotation.AutoInject;
import com.inksmallfrog.frogjbf.annotation.Data;
import com.inksmallfrog.frogjbf.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by inksmallfrog on 17-7-27.
 */
public class JBFControllerClass {
    private Class<?> clazz;
    private Map<String, Method> methods = new HashMap<>();
    private Map<String, Field> autoInjectFields = new HashMap<>();
    private Map<String, Field> paramFields = new HashMap<>();
    private Map<String, Field> dataFields = new HashMap<>();

    public JBFControllerClass(String clazzName) throws Exception {
        try {
            this.clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for(Method method : clazz.getDeclaredMethods()){
            methods.put(method.getName(), method);
        }
        for(Field field : clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(AutoInject.class)){
                if(field.getType().equals(HttpServletRequest.class)){
                    if(!field.isAccessible()) field.setAccessible(true);
                    autoInjectFields.put("request", field);
                }else if(field.getType().equals(HttpServletResponse.class)){
                    if(!field.isAccessible()) field.setAccessible(true);
                    autoInjectFields.put("response", field);
                }else{
                    throw new Exception("Not a validate field with the annotation @autoinject\n" +
                            "only HttpServletRequest and HttpServletResponse available" +
                            "Class: " + clazzName + "\n" +
                            "Field: " + field.getName());
                }
            }
            if(field.isAnnotationPresent(Param.class)){
                Class<?> fieldClass = field.getType();
                if(fieldClass.equals(String.class) || fieldClass.equals(String[].class)){
                    if(!field.isAccessible()) field.setAccessible(true);
                    paramFields.put(field.getName(), field);
                }else{
                    throw new Exception("Not a validate field with the annotation @param\n" +
                            "only String and String[] available\n" +
                            "Class: " + clazzName + "\n" +
                            "Field: " + field.getName());
                }
            }
            if(field.isAnnotationPresent(Data.class)){
                if(!field.isAccessible()) field.setAccessible(true);
                dataFields.put(field.getName(), field);
            }
        }
    }

    public Object getInstance() throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    public Method getMethod(String methodName){
        return methods.get(methodName);
    }

    public void bindRequest(Object instance, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(instance == null){
            throw new Exception("");
        }
        Field requestField = autoInjectFields.get("request");
        if(requestField != null) requestField.set(instance, request);
        Field responseField = autoInjectFields.get("response");
        if(responseField != null) responseField.set(instance, response);

        for(Map.Entry<String, Field> entry : paramFields.entrySet()){
            Field field = entry.getValue();
            if(field.getType().equals(String.class)){
                field.set(instance, request.getParameter(entry.getKey()));
            }else if(field.getType().equals(String[].class)){
                field.set(instance, request.getParameterValues(entry.getKey()));
            }

        }
    }
    public Object getData(Object instance, String fieldName) throws IllegalAccessException {
        Field field = dataFields.get(fieldName);
        return field == null ? null : field.get(instance);
    }
}
