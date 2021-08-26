package com.xia.appsys.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.xia.appsys.mapper.AdPromotionMapper;
import com.xia.appsys.service.AdPromotionService;
/**
 * @author 夏兵
 * @date 2021年08月23日 16:59
 */
@Service
public class AdPromotionServiceImpl implements AdPromotionService{

    @Resource
    private AdPromotionMapper adPromotionMapper;

}
