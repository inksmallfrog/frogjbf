package com.inksmallfrog.frogjbf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * This Annotation marks the field
 * which will be auto-injected
 * in the controller Class
 *
 * support type: HttpServletRequest  (request),
 *               HttpServletResponse (response)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInject {
}
