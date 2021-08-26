package com.xia.appsys.service.impl;

import com.xia.appsys.entity.AppVersion;
import com.xia.appsys.mapper.AppInfoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.xia.appsys.mapper.AppVersionMapper;
import com.xia.appsys.service.AppVersionService;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class AppVersionServiceImpl implements AppVersionService{

    @Resource
    private AppVersionMapper appVersionMapper;

    private AppInfoMapper appInfoMapper;

    @Override
    public List<AppVersion> getAppVersionList(Integer appId) {
        return appVersionMapper.getAppVersionList(appId);
    }

    @Override
    public boolean appsysadd(AppVersion appVersion) {
        boolean flag = false;
        Integer versionId = null;
        if (appVersionMapper.add(appVersion) >0) {
            versionId = appVersion.getId();
            flag = true;
        }
        if (appInfoMapper.updateVersionId(versionId, appVersion.getAppId()) > 0 && flag) {
            flag = true;
        }
        return flag;
    }

    @Override
    public AppVersion getAppVersionById(Integer id) {

        return appVersionMapper.getAppVersionById(id);
    }

    @Override
    public int modify(AppVersion appVersion) {
        return appVersionMapper.modify(appVersion);
    }

    @Override
    public int deleteApkFile(Integer id) {
        return appVersionMapper.deleteApkFile(id);
    }
}
