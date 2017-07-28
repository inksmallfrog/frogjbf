package com.inksmallfrog.frogjbf.annotation;

import com.inksmallfrog.frogjbf.util.ResponseTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * This Annotation marks the action method
 * and give the information of the responseType the action will return
 *
 * support type: VIEW(return String that discribe the redirect url or dispatch url)
 *                      eg. redirect:www.frogJBF.com will redirect to the www.frogJBF.com
 *                          www.frogJBF.com will dispatch the request to the www.frogJBF.com
 *               JSON(return String that contain the JSON text)
 *               TEXT(return String)
 *               STREAM(return byte[] or InputStream[] or StreamResponse)
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseType {
    ResponseTypeEnum type() default ResponseTypeEnum.VIEW;
}
