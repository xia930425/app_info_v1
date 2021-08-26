package com.xia.appsys.service.developer;

import com.xia.appsys.entity.DevUser;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.xia.appsys.mapper.DevUserMapper;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class DevUserServiceImpl implements DevUserService{

    @Resource
    private DevUserMapper devUserMapper;

    @Override
    public DevUser login(String devCode, String password) {
        DevUser user =null;
        user = devUserMapper.getLoginUser(devCode);
        if (null != user) {
            if (!user.getDevPassword().equals(password)) {
                user =null;
            }
        }
        return user;
    }
}
