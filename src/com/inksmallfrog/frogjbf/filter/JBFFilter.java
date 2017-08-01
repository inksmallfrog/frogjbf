package com.inksmallfrog.frogjbf.filter;

import com.inksmallfrog.frogjbf.global.JBFConfig;
import com.inksmallfrog.frogjbf.global.JBFContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by inksmallfrog on 17-7-27.
 *
 * This class is a god class to catch
 * all kinds of request.
 *
 * It will filter the static request
 * according to the config.
 */
@WebFilter(filterName = "JBFFilter")
public class JBFFilter implements Filter {
    public void destroy() {
    	JBFContext.getAppContext().destroy();
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        /*
         * get the servletPath to avoid
         * getting different route in different Java web container
         */
        String requestUri = request.getServletPath();

        RouteType routeType = isStaticRouteUri(requestUri);
        switch(routeType){
		case DYNAMIC:
			req.getServletContext().getRequestDispatcher(requestUri).forward(req, resp);
			break;
		case NOT_FOUND:
			((HttpServletResponse)resp).sendError(404);
			break;
		case STATIC:
			req.getServletContext().getNamedDispatcher("default").forward(req, resp);
			break;
		default:
			((HttpServletResponse)resp).sendError(404);
			break;
        }
    }

    public void init(FilterConfig config) throws ServletException {
		JBFConfig.getAppConfig();
		JBFContext.getAppContext();
    }
    
    private RouteType isStaticRouteUri(String uri){
        JBFConfig config = JBFConfig.getAppConfig();
        
    	List<String> staticPrefixPatterns = config.getPrefixPatterns("staticPattern");
        List<String> staticPostfixPatterns = config.getPostfixPatterns("staticPattern");
        
        List<String> dynamicPrefixPatterns = config.getPrefixPatterns("dynamicPattern");
        List<String> dynamicPostfixPatterns = config.getPostfixPatterns("dynamicPattern");
        
        if(null != staticPrefixPatterns){
        	for(String prefix : staticPrefixPatterns){
        		if(uri.startsWith(prefix)){
        			return RouteType.STATIC;
        		}
        	}
        }
        if(null != staticPostfixPatterns){
        	for(String postfix : staticPostfixPatterns){
        		if(uri.endsWith(postfix)){
        			return RouteType.STATIC;
        		}
        	}
        }
        if(null == dynamicPrefixPatterns && null == dynamicPostfixPatterns){
        	return RouteType.DYNAMIC;
        }
        if(null != dynamicPrefixPatterns){
        	for(String prefix : staticPrefixPatterns){
        		if(uri.startsWith(prefix)){
        			return RouteType.DYNAMIC;
        		}
        	}
        }
        if(null != dynamicPostfixPatterns){
        	for(String postfix : staticPrefixPatterns){
        		if(uri.endsWith(postfix)){
        			return RouteType.STATIC;
        		}
        	}
        }
        return RouteType.NOT_FOUND;
    }
    
    enum RouteType{
    	NOT_FOUND, STATIC, DYNAMIC
    }
}
