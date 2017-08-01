package com.inksmallfrog.frogjbf.datasource.impl;

import java.math.BigDecimal;

import com.inksmallfrog.frogjbf.datasource.inte.TypeMapper;

/**
 * Created by inksmallfrog on 17-7-29.
 */
public class OracleTypeMapper implements TypeMapper {
    @Override
    public <T> T sqlToJtype(Object sqlValue, Class<T> jType) {
        if(sqlValue instanceof oracle.sql.CHAR){
            if(jType.equals(String.class)){
                return (T) sqlValue.toString();
            }else if(jType.equals(int.class) || jType.equals(Integer.class)){
                return (T) (Integer)Integer.parseInt(sqlValue.toString());
            }else if(jType.equals(short.class) || jType.equals(Short.class)){
                return (T) (Short)Short.parseShort(sqlValue.toString());
            }else if(jType.equals(long.class) || jType.equals(Long.class)){
                return (T) (Long)Long.parseLong(sqlValue.toString());
            }else if(jType.equals(float.class) || jType.equals(Float.class)){
                return (T) (Float)Float.parseFloat(sqlValue.toString());
            }else if(jType.equals(double.class) || jType.equals(Double.class)){
                return (T) (Double)Double.parseDouble(sqlValue.toString());
            }else if(jType.equals(byte.class) || jType.equals(Byte.class)){
                return (T) (Byte)Byte.parseByte(sqlValue.toString());
            }else if(jType.equals(boolean.class) || jType.equals(Boolean.class)){
                return (T) (Boolean)Boolean.parseBoolean(sqlValue.toString());
            }
        }
        else if(sqlValue instanceof BigDecimal){
            if(jType.equals(String.class)){
                return (T) sqlValue.toString();
            }else if(jType.equals(int.class) || jType.equals(Integer.class)){
                return (T) (Integer)((BigDecimal)sqlValue).intValue();
            }else if(jType.equals(short.class) || jType.equals(Short.class)){
                return (T) (Short)((BigDecimal)sqlValue).shortValue();
            }else if(jType.equals(long.class) || jType.equals(Long.class)){
                return (T) (Long)((BigDecimal)sqlValue).longValue();
            }else if(jType.equals(float.class) || jType.equals(Float.class)){
                return (T) (Float)((BigDecimal)sqlValue).floatValue();
            }else if(jType.equals(double.class) || jType.equals(Double.class)){
                return (T) (Double)((BigDecimal)sqlValue).doubleValue();
            }else if(jType.equals(byte.class) || jType.equals(Byte.class)){
                return (T) (Byte)((BigDecimal)sqlValue).byteValue();
            }
        }
        else if(sqlValue instanceof oracle.sql.DATE){
            if(jType.equals(String.class)){
                return (T) sqlValue.toString();
            }else if(jType.equals(long.class) || jType.equals(Long.class)){

            }
        }
        return null;
    }

    @Override
    public <T> T jToSql(Object jValue, String sqlType) {
        return null;
    }
}
