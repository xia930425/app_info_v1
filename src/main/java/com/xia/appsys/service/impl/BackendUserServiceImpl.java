package com.xia.appsys.service.impl;

import com.xia.appsys.entity.BackendUser;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.xia.appsys.mapper.BackendUserMapper;
import com.xia.appsys.service.BackendUserService;
/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class BackendUserServiceImpl implements BackendUserService{

    @Resource
    private BackendUserMapper backendUserMapper;

    @Override
    public BackendUser login(String userCode, String userPassword) {
        BackendUser user = backendUserMapper.getUserLogin(userCode);
        if (null != user) {
            if (!user.getUserPassword().equals(userPassword)) {
                user = null;
            }
        }
        return user;
    }
}
