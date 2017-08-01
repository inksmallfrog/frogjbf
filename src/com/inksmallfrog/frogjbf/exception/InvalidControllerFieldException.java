package com.inksmallfrog.frogjbf.exception;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * This exception should be thrown
 * when the field with an annotation has a wrong type
 */
public class InvalidControllerFieldException extends Exception{
    public InvalidControllerFieldException(String className, String fieldName,
                                           String annotationName, Class fieldType){
        super("Not a validate field with the annotation @" + annotationName + "\n" +
                "Class: " + className + "\n" +
                "Field: " + fieldName + "\n" +
                "ExpectedType: " + getExpectedType(annotationName) + "\n" +
                "GotType: " + fieldType);
    }

    /**
     * get the expected return type based on annotationName
     * @param annotationName <String>
     * @return <String>
     */
    private static String getExpectedType(String annotationName){
        String expectedType = "";
        if(expectedType.equals("autoInject")){
            expectedType = "HttpServletRequest | HttpServletResponse";
        }
        else if(expectedType.equals("param")){
            expectedType = "String | String[] | InputStream";
        }
        return expectedType;
    }
}
