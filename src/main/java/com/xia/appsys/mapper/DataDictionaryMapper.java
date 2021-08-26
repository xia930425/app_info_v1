package com.xia.appsys.mapper;

import com.xia.appsys.entity.DataDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
public interface DataDictionaryMapper {

    public List<DataDictionary> getDataDictionaryList(@Param("typeCode") String typeCode);

}