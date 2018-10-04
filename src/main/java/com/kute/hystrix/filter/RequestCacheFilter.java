package com.kute.hystrix.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * created by bailong001 on 2018/10/03 17:35
 */
@WebFilter(urlPatterns = "/*")
public class RequestCacheFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCacheFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Hystrix Request Cache init .....");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            context.shutdown();
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("Hystrix Request Cache destroy.....");
    }
}
