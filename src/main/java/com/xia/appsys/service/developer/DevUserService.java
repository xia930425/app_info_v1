package com.xia.appsys.service.developer;

import com.xia.appsys.entity.DevUser;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface DevUserService{

    public DevUser login(String devCode, String password);


}
