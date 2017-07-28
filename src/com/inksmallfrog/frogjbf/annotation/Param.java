package com.inksmallfrog.frogjbf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * This Annotation marks the field
 * which will hold the request parameter before the action
 *
 * note: the field's name must be the same with the request parameter's name
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
}
