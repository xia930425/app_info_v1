package com.xia.appsys.service.impl;

import com.xia.appsys.entity.AppInfo;
import com.xia.appsys.entity.AppVersion;
import com.xia.appsys.mapper.AppVersionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.xia.appsys.mapper.AppInfoMapper;
import com.xia.appsys.service.AppInfoService;

import java.io.File;
import java.nio.MappedByteBuffer;
import java.util.Date;
import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Resource
    private AppInfoMapper appInfoMapper;

    @Resource
    private AppVersionMapper appVersionMapper;


    @Override
    public int add(AppInfo appInfo) {
        return appInfoMapper.add(appInfo);
    }

    @Override
    public int modify(AppInfo appInfo) {
        return appInfoMapper.modify(appInfo);
    }

    @Override
    public int deleteAppInfoById(Integer delId) {
        return appInfoMapper.deleteAppInfoById(delId);
    }

    @Override
    public List<AppInfo> getAppInfoList(String querySoftwareName,
                                        Integer queryStatus,
                                        Integer queryCategoryLevel1,
                                        Integer queryCategoryLevel2,
                                        Integer queryCategoryLevel3,
                                        Integer queryFlatformId,
                                        Integer devId,
                                        Integer currentPageNo,
                                        Integer pageSize) {
        return appInfoMapper.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, (currentPageNo - 1) * pageSize, pageSize);
    }

    @Override
    public int getAppInfoCount(String querySoftwareName,
                               Integer queryStatus,
                               Integer queryCategoryLevel1,
                               Integer queryCategoryLevel2,
                               Integer queryCategoryLevel3,
                               Integer queryFlatformId,
                               Integer devId) {
        return appInfoMapper.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
    }

    @Override
    public AppInfo getAppInfo(Integer id, String APKName) {
        return appInfoMapper.getAppInfo(id, APKName);
    }

    @Override
    public int deleteAppLogo(Integer id) {
        return appInfoMapper.deleteApplogo(id);
    }


    @Override
    /**
     * 业务：根据appId删除APP信息
     * 1、通过appId，查询app_verion表中是否有数据
     * 2、若版本表中有该app应用对应的版本信息，则进行级联删除，先删版本信息（app_version），后删app基本信息（app_info）
     * 3、若版本表中无该app应用对应的版本信息，则直接删除app基本信息（app_info）。
     * 注意：事务控制，上传文件的删除
     */
    public boolean appsysdeleteAppById(Integer id) throws Exception {

            boolean flag = false;
            int versionCount = appVersionMapper.getVersionCountByAppId(id);
            List<AppVersion> appVersionList = null;

            if (versionCount > 0) {
                //删除上传的apk文件
                appVersionList = appVersionMapper.getAppVersionList(id);
                for (AppVersion appVersion : appVersionList) {
                    if (appVersion.getApkLocPath() != null && !appVersion.getApkLocPath().equals("")) {
                        File file = new File(appVersion.getApkLocPath());
                        if (file.exists()) {
                            if (!file.delete()) {
                                throw new Exception();
                            }
                        }
                    }
                }
                //<2> 删除app_version表数据
                appVersionMapper.deleteVersionByAppId(id);
            }
            //删除app_version表数据
            AppInfo appInfo = appInfoMapper.getAppInfo(id, null);
            if (appInfo.getLogoLocPath() != null && !appInfo.getLogoLocPath().equals("")) {
                File file = new File(appInfo.getLogoLocPath());
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new Exception();
                    }
                }

            }
            if(appInfoMapper.deleteAppInfoById(id) > 0){
                flag = true;
            }

            return flag;
        }





    @Override
    public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo) throws Exception {
        /*
         * 上架
         * 更改status 2or5 to 4  上架时间
         * 根据versionid 更新publishstauts 2
         * */
        Integer operator = appInfo.getModifyBy();
        if (operator < 0 || appInfo.getId() < 0) {
            throw new Exception();
        }
        AppInfo aInfo = appInfoMapper.getAppInfo(appInfo.getId(), null);
        if (null == aInfo) {
            return false;
        } else {
            switch (aInfo.getStatus()) {
                case 2://当状态为审核通过时，可以进行上架操作
                    onSale(aInfo, operator, 4, 2);
                    break;
                case 5:
                    onSale(aInfo, operator, 4, 2);
                    break;
                case 4://当状态为上架时，可以进行下架操作
                    offSale(aInfo, operator, 5);
                    break;
                default:
                    return false;
            }
        }

        return true;
    }

    private void onSale(AppInfo appInfo, Integer operator,
                        Integer appInfoStatus, Integer versionStatus) throws Exception {
        offSale(appInfo, operator, appInfoStatus);
        setSaleSwitchToAppVersion(appInfo, operator, versionStatus);


    }

    private boolean offSale(AppInfo appInfo, Integer operator, Integer appInfStatus) throws Exception {
        AppInfo _appInfo = new AppInfo();
        _appInfo.setId(appInfo.getId());
        _appInfo.setStatus(appInfStatus);
        _appInfo.setModifyBy(operator);
        _appInfo.setOffSaleDate(new Date(System.currentTimeMillis()));
        appInfoMapper.modify(_appInfo);
        return true;
    }

    private boolean setSaleSwitchToAppVersion(AppInfo appInfo, Integer operator, Integer saleStatus) {
        AppVersion appVersion = new AppVersion();
        appVersion.setId(appInfo.getVersionId());
        appVersion.setPublishStatus(saleStatus);
        appVersion.setModifyBy(operator);
        appVersion.setModifyDate(new Date(System.currentTimeMillis()));
        appVersionMapper.modify(appVersion);
        return false;
    }
}
