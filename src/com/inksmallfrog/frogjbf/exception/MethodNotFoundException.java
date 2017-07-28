package com.inksmallfrog.frogjbf.exception;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * This exception should be thrown
 * when can't get the specified method from class
 */
public class MethodNotFoundException extends Exception {
    public MethodNotFoundException(String className, String methodName){
        super("Method not found\n" +
                "Class: " + className + "\n" +
                "Method: " + methodName + "");
    }
}
