package com.example.dinner.filter;

import com.alibaba.fastjson.JSON;
import com.example.dinner.common.BaseContext;
import com.example.dinner.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String requestURI = req.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        boolean check = check(urls, requestURI);
        if (check) {
            chain.doFilter(req, resp);
            return;
        }
        if (req.getSession().getAttribute("employee") != null) {
            Long id = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            chain.doFilter(req, resp);
            return;
        }

        if (req.getSession().getAttribute("user") != null) {
            Long userId = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            chain.doFilter(req, resp);
            return;
        }

        resp.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }
}
