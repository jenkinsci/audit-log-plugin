package io.jenkins.plugins.audit.filter;

import hudson.Extension;
import hudson.init.Initializer;
import hudson.model.User;
import hudson.util.PluginServletFilter;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

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
        if (user != null) {
            ThreadContext.put("userId", user.getId());
        }
        ThreadContext.putIfNull("requestId", UUID.randomUUID().toString());
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            ThreadContext.put("requestMethod", httpRequest.getMethod());
            ThreadContext.put("requestUri", httpRequest.getRequestURI());
        }
        chain.doFilter(request, response);
        ThreadContext.clearMap();
    }
}
