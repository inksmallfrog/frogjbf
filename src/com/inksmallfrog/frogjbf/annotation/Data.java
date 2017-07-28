package com.inksmallfrog.frogjbf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * This Annotation marks the field
 * which will hold some information after the action
 *
 * support: ResponseType(type=ResponseTypeEnum.STREAM) -> filename
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {
}
