package com.inksmallfrog.frogjbf.test.controller;

import com.inksmallfrog.frogjbf.annotation.AutoInject;
import com.inksmallfrog.frogjbf.annotation.Param;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by inksmallfrog on 17-7-27.
 */
public class Hello {
    @AutoInject
    private HttpServletRequest request;

    @Param
    private String name;
    @Param
    private String[] favorite;

    public String sayHello(){
        return "hello.jsp";
    }

    public String postHello(){
        request.setAttribute("name", name);
        request.setAttribute("favorite", favorite);
        return "hello.jsp";
    }
}
