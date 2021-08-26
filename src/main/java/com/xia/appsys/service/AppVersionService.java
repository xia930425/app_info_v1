package com.xia.appsys.service;

import com.xia.appsys.entity.AppVersion;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface AppVersionService{

        /**
         * 根据appId查询相应的app版本列表
         * @param appId
         * @return
         */
        public List<AppVersion> getAppVersionList(Integer appId);
        /**
         * 新增app版本信息，并更新app_info表的versionId字段
         * @param appVersion
         * @return
         */
        public boolean appsysadd(AppVersion appVersion);
        /**
         * 根据id获取AppVersion
         * @param id
         * @return
         */
        public AppVersion getAppVersionById(Integer id);

        /**
         * 修改app版本信息
         * @param appVersion
         * @return
         */
        public int modify(AppVersion appVersion);

        /**
         * 删除apk文件
         * @param id
         * @return
         */
        public int deleteApkFile(Integer id);


}
