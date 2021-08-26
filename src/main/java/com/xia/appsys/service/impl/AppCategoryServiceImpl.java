package com.xia.appsys.service.impl;

import com.xia.appsys.entity.AppCategory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.xia.appsys.mapper.AppCategoryMapper;
import com.xia.appsys.service.AppCategoryService;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class AppCategoryServiceImpl implements AppCategoryService{

    @Resource
    private AppCategoryMapper appCategoryMapper;

    @Override
    public List<AppCategory> getAppCategoryListByParentId(Integer parentId) {
        return appCategoryMapper.getAppCategoryListByParentId(parentId);
    }
}
