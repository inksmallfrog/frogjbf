package com.inksmallfrog.frogjbf.exception;

import com.inksmallfrog.frogjbf.util.ResponseTypeEnum;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * This exception should be thrown
 * when the action return type not match the ResponseType that user defined
 */
public class InvalidResponseTypeException extends Exception {
	public InvalidResponseTypeException(String clazzName, String methodName,
								 ResponseTypeEnum responseTypeEnum, Class returnType){
		super("Invalid ResponseType user defined\n" +
				"Class: " + clazzName + "\n" +
				"Method: " + methodName + "\n" +
				"ResponseType: " + responseTypeEnum.toString() + "\n" +
				"ExpectedType: " + getExpectedType(responseTypeEnum) + "\n" +
				"GotType: " + returnType.toString());
	}

	/**
	 * get the expected return type based on ResponseType
	 * @param responseTypeEnum <ResponseType>
	 * @return <String>
	 */
	private static String getExpectedType(ResponseTypeEnum responseTypeEnum){
		String expectedType = "";
		switch (responseTypeEnum){
			case VIEW:
				expectedType = "String";
				break;
			case JSON:
				expectedType = "String | JSONTokener";
				break;
			case TEXT:
				expectedType = "String";
				break;
			case STREAM:
				expectedType = "byte[] | InputStream | StreamResponse";
				break;
		}
		return expectedType;
	}
}
