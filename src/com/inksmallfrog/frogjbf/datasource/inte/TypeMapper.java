package com.inksmallfrog.frogjbf.datasource.inte;

/**
 * Created by inksmallfrog on 17-7-29.
 */
public interface TypeMapper {
    public <T> T sqlToJtype(Object sqlValue, Class<T> jType);
    public <T> T jToSql(Object jValue, String sqlType);
}
