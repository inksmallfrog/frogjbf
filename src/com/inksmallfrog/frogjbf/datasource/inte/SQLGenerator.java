package com.inksmallfrog.frogjbf.datasource.inte;

/**
 * Created by inksmallfrog on 17-8-1.
 */
public interface SQLGenerator {
    String getSlicePageSQL(String querySql, int minRow, int pageSize);
}
