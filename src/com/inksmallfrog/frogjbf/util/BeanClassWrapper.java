package com.inksmallfrog.frogjbf.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.inksmallfrog.frogjbf.annotation.Column;
import com.inksmallfrog.frogjbf.annotation.TableName;
import com.inksmallfrog.frogjbf.datasource.DataSourceConfig;

public class BeanClassWrapper {
	private Class clazz;

	private Map<String, Field> fields = new HashMap();
	private String tableName = "";
	
	public BeanClassWrapper(Class clazz){
		this.clazz = clazz;
		TableName tableNameAnnotation = (TableName) clazz.getAnnotation(TableName.class);
		if(null != tableNameAnnotation){
			tableName = tableNameAnnotation.name();
		}
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			Column columnAnnotation = field.getAnnotation(Column.class);
			String colName = (null != columnAnnotation) ? columnAnnotation.name() : field.getName();
			this.fields.put(colName, field);
			if(!field.isAccessible()){
				field.setAccessible(true);
			}
		}
	}
	public Object newInstance(){
		Object ins = null;
		try {
			ins = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return ins;
	}
	public void setBeanFromDataSource(Object bean, String key,
									  Object value, DataSourceConfig config){
		Field field = fields.get(key.toLowerCase());
		if(null != field){
			Class fieldType = field.getType();
			Object typedValue = config.getTypeMapper().sqlToJtype(value, fieldType);
			try {
				field.set(bean, typedValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}
}
