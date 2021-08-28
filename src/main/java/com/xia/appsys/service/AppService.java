package com.xia.appsys.service;


import com.xia.appsys.entity.AppInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月28日 9:29
 */
public interface AppService {
    /**
     * 根据条件查询待审核的App列表并分页显示
     * @param softwareName
     * @param queryCategoryLevel1
     * @param queryCategoryLevel2
     * @param queryCategoryLevel3
     * @param queryFlatformId
     * @param currentPageNo
     * @param pageSize
     * @return
     */

    List<AppInfo> getAppinfoList(@Param("softwareName") String softwareName,
                                 @Param("categoryLevel1") Integer queryCategoryLevel1,
                                 @Param("categoryLevel2") Integer queryCategoryLevel2,
                                 @Param("categoryLevel3") Integer queryCategoryLevel3,
                                 @Param("flatformId") Integer queryFlatformId,
                                 @Param("from") Integer currentPageNo,
                                 @Param("pageSize") Integer pageSize) ;


    /**
     * 查询出待审核（status=1）的APP数量
     * @param softwareName
     * @param queryCategoryLevel1
     * @param queryCategoryLevel2
     * @param queryCategoryLevel3
     * @param queryFlatformId
     * @return
     */
    int getAppInfoCount(@Param("querySoftwareName") String querySoftwareName,
                        @Param("categoryLevel1") Integer queryCategoryLevel1,
                        @Param("categoryLevel2") Integer queryCategoryLevel2,
                        @Param("categoryLevel3") Integer queryCategoryLevel3,
                        @Param("flatformId") Integer queryFlatformId);

    /**
     * 根据id获取app详细信息
     * @param id
     * @return
     */
    AppInfo getAppInfo(@Param("id")Integer id);

    /**
     * 根据id更新app的satus
     * @param status
     * @param id
     * @return
     */
    boolean updateSatus(@Param("status")Integer status,@Param("id")Integer id);
}
