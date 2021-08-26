package com.xia.appsys.mapper;

import com.xia.appsys.entity.AppCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface AppCategoryMapper {

    List<AppCategory> getAppCategoryListByParentId(@Param("parentId") Integer parentId);
}