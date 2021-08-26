package com.xia.appsys.mapper;

import com.xia.appsys.entity.AppVersion;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface AppVersionMapper {

    /**
     * 根据id获取app版本信息
     * @param appId
     * @return
     */
    List<AppVersion> getAppVersionList(@Param("appId") Integer appId);

    /**
     * 添加app版本并更新app_info表的versionId字段
     * @param appVersion
     * @return
     */
    int add(AppVersion appVersion);

    /**
     * 查询APP版本数
     * @param appId
     * @return
     */
    int getVersionCountByAppId(@Param("appId")Integer appId);

    /**
     * 删除APP版本信息
     * @param appId
     * @return
     */
    int deleteVersionByAppId(@Param("appId")Integer appId);

    /**
     * 获取AppVersion对象
     * @param id
     * @return
     */
    AppVersion getAppVersionById(@Param("id")Integer id);


    int modify(AppVersion appVersion);


    int deleteApkFile(@Param("id")Integer id);

}