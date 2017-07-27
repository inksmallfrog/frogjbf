package com.inksmallfrog.frogjbf.annotation;

import com.inksmallfrog.frogjbf.util.ResponseTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by inksmallfrog on 17-7-27.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseType {
    public ResponseTypeEnum type() default ResponseTypeEnum.VIEW;
}
