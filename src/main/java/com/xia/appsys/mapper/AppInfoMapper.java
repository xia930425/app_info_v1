package com.xia.appsys.mapper;

import com.xia.appsys.entity.AppInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface AppInfoMapper {

    int add(AppInfo appInfo);

    int modify(AppInfo appInfo);

    int deleteAppInfoById(@Param("id") Integer delId);

    List<AppInfo> getAppInfoList(@Param(value="softwareName")String querySoftwareName,
                                        @Param(value="status")Integer queryStatus,
                                        @Param(value="categoryLevel1")Integer queryCategoryLevel1,
                                        @Param(value="categoryLevel2")Integer queryCategoryLevel2,
                                        @Param(value="categoryLevel3")Integer queryCategoryLevel3,
                                        @Param(value="flatformId")Integer queryFlatformId,
                                        @Param(value="devId")Integer devId,
                                        @Param(value="from")Integer currentPageNo,
                                        @Param(value="pageSize")Integer pageSize);

    int getAppInfoCount(@Param(value="softwareName")String querySoftwareName,
                               @Param(value="status")Integer queryStatus,
                               @Param(value="categoryLevel1")Integer queryCategoryLevel1,
                               @Param(value="categoryLevel2")Integer queryCategoryLevel2,
                               @Param(value="categoryLevel3")Integer queryCategoryLevel3,
                               @Param(value="flatformId")Integer queryFlatformId,
                               @Param(value="devId")Integer devId);

    AppInfo getAppInfo (@Param("id") Integer id,
                        @Param("APKName")String APKName);

    int deleteApplogo(@Param("id")Integer id);

    int updateVersionId(@Param("versionId")Integer versionId,
                        @Param("id")Integer appId);

    int updateSaleStatusByAppId(@Param("id") Integer appId);

    int updateSatus(@Param("status") Integer status,
                    @Param("id") Integer id);
}