package com.inksmallfrog.frogjbf.servlet;

import com.inksmallfrog.frogjbf.annotation.ResponseType;
import com.inksmallfrog.frogjbf.config.JBFConfig;
import com.inksmallfrog.frogjbf.constant.URLConstant;
import com.inksmallfrog.frogjbf.util.JBFControllerClass;
import com.inksmallfrog.frogjbf.util.ResponseTypeEnum;
import com.inksmallfrog.frogjbf.util.StreamResponse;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * Created by inksmallfrog on 17-7-27.
 */
@WebServlet(name = "JBFServlet")
public class JBFServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JBFConfig config = JBFConfig.getInstance();
        JBFControllerClass jbfControllerClass = config.getHandlerClassByUrlAndMethod(request.getRequestURI(), request.getMethod());
        if(jbfControllerClass == null){
            response.setStatus(404);
        }
        Object instance = null;
        try {
            instance = jbfControllerClass.getInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        try {
            jbfControllerClass.bindRequest(instance, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Method method = config.getHandlerByUrlAndMethod(request.getRequestURI(), request.getMethod());
        ResponseType anno = method.getAnnotation(ResponseType.class);
        ResponseTypeEnum type = (anno == null ? ResponseTypeEnum.VIEW : anno.type());
        try {
            switch (type){
                case VIEW: {
                    response.setContentType("text/html;charset=utf-8");
                    String res = (String) method.invoke(instance);
                    if (res.startsWith(URLConstant.REDIRECT_PREFIX)) {
                        response.sendRedirect(res.substring(URLConstant.REDIRECT_PREFIX.length() + 1));
                    } else {
                        request.getRequestDispatcher(res).forward(request, response);
                    }
                    break;
                }
                case JSON: {
                    response.setContentType("application/json;charset=utf-8");
                    Object obj = method.invoke(instance);
                    if (obj instanceof String) {
                        PrintWriter w = response.getWriter();
                        w.write((String) obj);
                        w.flush();
                        w.close();
                    } else if (obj instanceof JSONTokener) {
                        PrintWriter w = response.getWriter();
                        w.write(obj.toString());
                        w.flush();
                        w.close();
                    }
                    break;
                }
                case TEXT: {
                    response.setContentType("text/plain;charset=utf-8");
                    String res = (String) method.invoke(instance);
                    PrintWriter w = response.getWriter();
                    w.write(res);
                    w.flush();
                    w.close();
                    break;
                }
                case STREAM: {
                    response.setContentType("application/octet-stream;charset=ISO-8859-1");
                    Object res = method.invoke(instance);
                    String filename = "unnamed";
                    byte[] buffer = null;
                    InputStream stream = null;
                    if(res instanceof StreamResponse){
                        StreamResponse sr = (StreamResponse) res;
                        filename = sr.getFilename();
                        buffer = sr.getBuffer();
                        stream = sr.getStream();
                    }else if(res instanceof byte[]){
                        filename = (String) jbfControllerClass.getData(instance, "filename");
                        buffer = (byte[]) res;
                    }else if(res instanceof InputStream){
                        filename = (String) jbfControllerClass.getData(instance, "filename");
                        stream = (InputStream) res;
                    }
                    filename = filename != null ? filename : "unnamed";
                    filename = new String(filename.getBytes(), "ISO-8859-1");
                    filename = URLEncoder.encode(filename, "ISO-8859-1");
                    response.setHeader("Content-Disposition", "attachment;filename=" + filename);
                    BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
                    if(buffer != null){
                        os.write(buffer);
                    }else{
                        buffer = new byte[config.getMaxDownloadBuffer()];
                        int size = -1;
                        while((size = stream.read(buffer)) > 0){
                            os.write(buffer, 0, size);
                        }
                    }
                    os.flush();
                    os.close();
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
