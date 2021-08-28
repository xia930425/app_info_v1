package com.xia.appsys.service.impl;

import com.xia.appsys.entity.AppInfo;
import com.xia.appsys.mapper.AppInfoMapper;
import com.xia.appsys.service.AppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月28日 9:38
 */
@Service
public class AppServiceImpl implements AppService {
    @Resource
    private AppInfoMapper mapper;


    @Override
    public List<AppInfo> getAppinfoList(String querySoftwareName,
                                        Integer queryCategoryLevel1,
                                        Integer queryCategoryLevel2,
                                        Integer queryCategoryLevel3,
                                        Integer queryFlatformId,
                                        Integer currentPageNo,
                                        Integer pageSize) {

        return mapper.getAppInfoList(querySoftwareName,1,queryCategoryLevel1,queryCategoryLevel2,queryCategoryLevel3,
                                     queryFlatformId,null,(currentPageNo-1)*pageSize,pageSize);
    }

    @Override
    public int getAppInfoCount(String softwareName, Integer queryCategoryLevel1, Integer queryCategoryLevel2, Integer queryCategoryLevel3, Integer queryFlatformId) {
        return mapper.getAppInfoCount(softwareName,1,queryCategoryLevel1,queryCategoryLevel2,queryCategoryLevel3,
                                       queryFlatformId,null);
    }

    @Override
    public AppInfo getAppInfo(Integer id) {
        return mapper.getAppInfo(id,null);
    }

    @Override
    public boolean updateSatus(Integer status, Integer id) {
        boolean flag = false;
        if (mapper.updateSatus(status, id) > 0) {
            flag = true;
        }
        return flag;
    }
}
