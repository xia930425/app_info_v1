package com.xia.appsys.service;

import com.xia.appsys.entity.BackendUser;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface BackendUserService{

    BackendUser login(String userCode, String userPassword);


}
