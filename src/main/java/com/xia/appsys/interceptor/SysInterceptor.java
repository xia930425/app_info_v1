package com.xia.appsys.interceptor;


import com.xia.appsys.entity.BackendUser;
import com.xia.appsys.entity.DevUser;
import com.xia.appsys.tools.Constants;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @author 夏兵
 * @date 2021年08月28日 10:24
 */
public class SysInterceptor implements HandlerInterceptor {
    private final Logger logger = Logger.getLogger(SysInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("SysInterceptor preHandle ==========================");
        HttpSession session = request.getSession();

        BackendUser backendUser = (BackendUser) session.getAttribute(Constants.USER_SESSION);
        DevUser devUser = (DevUser)session.getAttribute(Constants.DEV_USER_SESSION);

        if (null != devUser) {
            return true;
        } else if (null != backendUser) {
            return true;
        }else {
            response.sendRedirect(request.getContextPath()+"/403.jsp");
            return false;

        }


    }
}
