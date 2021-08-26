package com.xia.appsys.service;

import com.xia.appsys.entity.DataDictionary;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface DataDictionaryService{

    public List<DataDictionary> getDataDictionaryList(String typeCode);


}
