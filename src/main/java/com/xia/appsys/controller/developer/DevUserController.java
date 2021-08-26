package com.xia.appsys.controller.developer;

import com.xia.appsys.entity.DevUser;
import com.xia.appsys.service.developer.DevUserService;
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
 * @date 2021年08月23日 17:15
 */
@Controller
@RequestMapping("/dev")
public class DevUserController {
    @Resource
    private DevUserService service;

    private final Logger logger = Logger.getLogger(DevUserController.class);

    @RequestMapping("/login")
    public String login() {
        logger.debug("LoginCOntroller welcome AppinfoSystem develper====");
        return "devlogin";
    }

    @RequestMapping("/dologin")
    public String dologin(@RequestParam String devCode,
                          @RequestParam String devPassword,
                          HttpSession session,
                          HttpServletRequest request) {
        logger.debug("dologin===");

        DevUser user = service.login(devCode, devPassword);
        if (null != user) {
            session.setAttribute(Constants.DEV_USER_SESSION, user);

            //页面跳转
            return "redirect:/dev/flatform/main";
        } else {
            request.setAttribute("error", "用户名或密码不正确");
            return "devlogin";
        }
    }

    @RequestMapping("/flatform/main")
    public String main(HttpSession session) {
        if (session.getAttribute(Constants.DEV_USER_SESSION) == null) {
            return "redirect:/dev/login";
        }
        return "developer/main";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.DEV_USER_SESSION);
        return "devlogin";
    }
}
