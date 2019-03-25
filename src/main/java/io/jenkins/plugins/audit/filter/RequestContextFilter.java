package io.jenkins.plugins.audit.filter;

import hudson.Extension;
import hudson.init.Initializer;
import hudson.model.User;
import hudson.util.PluginServletFilter;
import io.jenkins.plugins.audit.RequestContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Extension
public class RequestContextFilter extends PluginServletFilter {

    /**
     * Registering the filter
     */
    @Initializer
    public static void init() throws ServletException {
        PluginServletFilter.addFilter(new RequestContextFilter());
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
        //RequestContext.setRequestUri(request.getRequestURI());
        chain.doFilter(request, response);
        RequestContext.clear();
    }
}
