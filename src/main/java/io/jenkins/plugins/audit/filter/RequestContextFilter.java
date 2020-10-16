package io.jenkins.plugins.audit.filter;

import hudson.Extension;
import hudson.init.Initializer;
import hudson.model.User;
import hudson.util.PluginServletFilter;
import io.jenkins.plugins.audit.RequestContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Extension
public class RequestContextFilter implements Filter {

    /**
     * Registering the filter
     */
    @Initializer
    public static void init() throws ServletException {
        PluginServletFilter.addFilter(new RequestContextFilter());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    /**
     * The filter through which the flow passes is used to set the context level attributes
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        User user = User.current();
        if(user != null){
            RequestContext.setUserId(user.getId());
        }
        RequestContext.setIpAddress(request.getRemoteAddr());
        RequestContext.setNodeName(request.getLocalName());
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            RequestContext.setRequestUri(httpRequest.getRequestURI());
        }
        chain.doFilter(request, response);
        RequestContext.clear();
    }
}
