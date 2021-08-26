package com.xia.appsys.service.impl;

import com.xia.appsys.entity.DataDictionary;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.xia.appsys.mapper.DataDictionaryMapper;
import com.xia.appsys.service.DataDictionaryService;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class DataDictionaryServiceImpl implements DataDictionaryService{

    @Resource
    private DataDictionaryMapper dataDictionaryMapper;

    @Override
    public List<DataDictionary> getDataDictionaryList(String typeCode) {
        return dataDictionaryMapper.getDataDictionaryList(typeCode);
    }
}
