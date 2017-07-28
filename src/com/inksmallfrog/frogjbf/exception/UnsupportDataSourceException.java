package com.inksmallfrog.frogjbf.exception;

/**
 * Created by inksmallfrog on 17-7-28.
 *
 * This exception should be thrown
 * when user has defined an unsupported db in the dataSources
 */
public class UnsupportDataSourceException extends Exception {
    public UnsupportDataSourceException(String db){
        super("Sorry! " + db + " dataSource has not been supported yet \n" +
                "it is welcomed to ask the author to add the support for it!");
    }
}
