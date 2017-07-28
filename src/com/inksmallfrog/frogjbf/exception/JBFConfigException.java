package com.inksmallfrog.frogjbf.exception;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * This exception should be thrown
 * when there is anything wrong with the jbf.config.json
 */
public class JBFConfigException extends Exception {
    public JBFConfigException(String reason){
        super("There maybe something wrong in your jbf.config.json file \n" +
                "Reason: " + reason);
    }
}
