package com.inksmallfrog.frogjbf.util;

import com.inksmallfrog.frogjbf.annotation.AutoInject;
import com.inksmallfrog.frogjbf.annotation.Data;
import com.inksmallfrog.frogjbf.annotation.Param;
import com.inksmallfrog.frogjbf.annotation.ResponseType;
import com.inksmallfrog.frogjbf.exception.InvalidControllerFieldException;
import com.inksmallfrog.frogjbf.exception.InvalidResponseTypeException;
import com.inksmallfrog.frogjbf.exception.MethodNotFoundException;
import org.json.JSONTokener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

    public JBFControllerClass(String clazzName) throws ClassNotFoundException {
        this.clazz = Class.forName(clazzName);
        loadMethods(clazz);
        loadFields(clazz);
    }

    private void loadMethods(Class clazz){
        String clazzName = clazz.getName();
        for(Method method : clazz.getDeclaredMethods()){
            ResponseType anno = method.getAnnotation(ResponseType.class);
            Class returnType = method.getReturnType();
            ResponseTypeEnum responseTypeEnum = (null == anno) ? ResponseTypeEnum.VIEW : anno.type();
            try{
                switch(responseTypeEnum){
                    case VIEW:
                        if(!returnType.equals(String.class)){
                            throw new InvalidResponseTypeException(clazzName, method.getName(), ResponseTypeEnum.VIEW, returnType);
                        }
                        break;
                    case JSON:
                        if(!returnType.equals(String.class)
                                && !returnType.equals(JSONTokener.class)){
                            throw new InvalidResponseTypeException(clazzName, method.getName(), ResponseTypeEnum.JSON, returnType);
                        }
                        break;
                    case TEXT:
                        if(!returnType.equals(String.class)){
                            throw new InvalidResponseTypeException(clazzName, method.getName(), ResponseTypeEnum.TEXT, returnType);
                        }
                        break;
                    case STREAM:
                        if(!returnType.equals(byte[].class)
                                && !returnType.equals(InputStream.class)
                                && !returnType.equals(StreamResponse.class)){
                            throw new InvalidResponseTypeException(clazzName, method.getName(), ResponseTypeEnum.STREAM, returnType);
                        }
                        break;
                }
            }catch (InvalidResponseTypeException e){
                e.printStackTrace();
            }
            methods.put(method.getName(), method);
        }
    }

    private void loadFields(Class clazz){
        try{
            for(Field field : clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(AutoInject.class)){
                    if(field.getType().equals(HttpServletRequest.class)){
                        if(!field.isAccessible()) field.setAccessible(true);
                        autoInjectFields.put("request", field);
                    }else if(field.getType().equals(HttpServletResponse.class)){
                        if(!field.isAccessible()) field.setAccessible(true);
                        autoInjectFields.put("response", field);
                    }else{
                        throw new InvalidControllerFieldException(clazz.getName(), field.getName(),
                                "autoInject", field.getType());
                    }
                }
                if(field.isAnnotationPresent(Param.class)){
                    Class<?> fieldClass = field.getType();
                    if(fieldClass.equals(String.class) || fieldClass.equals(String[].class)){
                        if(!field.isAccessible()) field.setAccessible(true);
                        paramFields.put(field.getName(), field);
                    }else{
                        throw new InvalidControllerFieldException(clazz.getName(), field.getName(),
                                "param", field.getType());
                    }
                }
                if(field.isAnnotationPresent(Data.class)){
                    if(!field.isAccessible()) field.setAccessible(true);
                    dataFields.put(field.getName(), field);
                }
            }
        }catch (InvalidControllerFieldException e){
            e.printStackTrace();
        }

    }

    public Object newInstance() throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    public Method getMethod(String methodName) throws MethodNotFoundException {
        Method method = methods.get(methodName);
        if(null == method){
            throw new MethodNotFoundException(clazz.getName(), methodName);
        }
        return method;
    }

    public void bindRequest(Object instance, HttpServletRequest request, HttpServletResponse response){
        try{
            if(instance == null){
                throw new NullPointerException("Controller classes' instance is null!");
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
        }catch(NullPointerException | IllegalAccessException e){
            e.printStackTrace();
        }
    }
    public Object getData(Object instance, String fieldName) throws IllegalAccessException {
        Field field = dataFields.get(fieldName);
        return field == null ? null : field.get(instance);
    }
}
