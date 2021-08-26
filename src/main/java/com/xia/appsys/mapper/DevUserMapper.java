package com.xia.appsys.mapper;

import com.xia.appsys.entity.DevUser;
import org.apache.ibatis.annotations.Param;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface DevUserMapper {

    /**
     *
     * @param devCode
     * @return
     */
    DevUser getLoginUser(@Param("devCode")String devCode);
}