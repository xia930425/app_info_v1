package com.xia.appsys.controller.backend;


import com.xia.appsys.entity.BackendUser;
import com.xia.appsys.service.BackendUserService;
import com.xia.appsys.tools.Constants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 夏兵
 * @date 2021年08月28日 8:57
 */
@Controller
@RequestMapping("/mager")
public class UserLoginController {
    private final Logger logger = Logger.getLogger(UserLoginController.class);

    @Resource
    private BackendUserService backendUserService;

    /**
     * 跳转至登录页
     * @return
     */
    @RequestMapping("/login")
    public String login  (){
        logger.debug("LoginController welcome AppInfoSystem backend==================");
        return "backendlogin";
    }

    /**
     * 登录业务
     * @param userCode
     * @param userPassword
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("/dologin")
    public String doLogin(@RequestParam String userCode, @RequestParam String userPassword,
                          HttpServletRequest request, HttpSession session) {
        logger.debug("doLogin====================================");

        BackendUser user = backendUserService.login(userCode, userPassword);

        if (null != user){
            //登录成功 放入session
            session.setAttribute(Constants.USER_SESSION,user);
            //页面跳转
            return "redirect:/mager/backend/main";
        }else {
            //用户不存在 带出提示信息
            request.setAttribute("error", "用户名或密码不正确");
            return "backendlogin";
        }
    }

    /**
     * 二次跳转 session判断
     * @param session
     * @return
     */
    @RequestMapping("/backend/main")
    public String main(HttpSession session) {
        if (session.getAttribute(Constants.USER_SESSION) == null) {
            return "redirect:/mager/login";
        }
        return "backend/main";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.USER_SESSION);
        return "backendlogin";
    }

}
