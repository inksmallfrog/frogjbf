package com.inksmallfrog.frogjbf.exception;

public class InvalidParamBindToFieldException extends Exception {
	public InvalidParamBindToFieldException(String className, String fieldName,
			String paramType, String fieldType) {
		super("Invalid parameter bind to field \n" 
				+ "\n" + "Class: " + className 
				+ "\n" + "Field: " + fieldName
				+ "\n" + "ParamType: " + paramType
				+ "\n" + "FieldType: " + fieldType);
	}

}
