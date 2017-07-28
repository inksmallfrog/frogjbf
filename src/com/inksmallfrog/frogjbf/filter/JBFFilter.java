package com.inksmallfrog.frogjbf.filter;

import com.inksmallfrog.frogjbf.global.JBFConfig;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by inksmallfrog on 17-7-27.
 */
@WebFilter(filterName = "JBFFilter")
public class JBFFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        JBFConfig config = JBFConfig.getAppConfig();
        String staticRouterPrefix = config.getStaticRouterPrefix();
        String dynamicRouterPrefix = config.getDynamicRouterPrefix();

        HttpServletRequest request = (HttpServletRequest)req;
        String requestUri = request.getServletPath();
        if(staticRouterPrefix != null && requestUri.startsWith(staticRouterPrefix)){
            chain.doFilter(req, resp);
        }else if(dynamicRouterPrefix == null || requestUri.startsWith(dynamicRouterPrefix)){
            req.getServletContext().getRequestDispatcher(requestUri).forward(req, resp);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
