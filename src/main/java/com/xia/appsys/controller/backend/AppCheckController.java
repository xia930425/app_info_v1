package com.xia.appsys.controller.backend;


import com.xia.appsys.entity.AppCategory;
import com.xia.appsys.entity.AppInfo;
import com.xia.appsys.entity.AppVersion;
import com.xia.appsys.entity.DataDictionary;
import com.xia.appsys.service.AppCategoryService;
import com.xia.appsys.service.AppService;
import com.xia.appsys.service.AppVersionService;
import com.xia.appsys.service.DataDictionaryService;
import com.xia.appsys.tools.Constants;
import com.xia.appsys.tools.PageSupport;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 夏兵
 * @date 2021年08月28日 9:28
 */
@Controller
@RequestMapping("/mager/backend/app")
public class AppCheckController {
    private final Logger logger = Logger.getLogger(AppCheckController.class);

    @Resource
    private AppService appService;
    @Resource
    private AppVersionService appVersionService;
    @Resource
    private DataDictionaryService dataDictionaryService;
    @Resource
    private AppCategoryService appCategoryService;

    @RequestMapping(value="/list")
    public String getAppInfoList(Model model, HttpSession session,
                                 @RequestParam(value = "querySoftwareName",required = false)String querySoftwareName,
                                 @RequestParam(value="queryCategoryLevel1",required=false) String _queryCategoryLevel1,
                                 @RequestParam(value="queryCategoryLevel2",required=false) String _queryCategoryLevel2,
                                 @RequestParam(value="queryCategoryLevel3",required=false) String _queryCategoryLevel3,
                                 @RequestParam(value="queryFlatformId",required=false) String _queryFlatformId,
                                 @RequestParam(value="pageIndex",required=false) String pageIndex){
        logger.info("getAppInfoList -- > querySoftwareName: " + querySoftwareName);
        logger.info("getAppInfoList -- > queryCategoryLevel1: " + _queryCategoryLevel1);
        logger.info("getAppInfoList -- > queryCategoryLevel2: " + _queryCategoryLevel2);
        logger.info("getAppInfoList -- > queryCategoryLevel3: " + _queryCategoryLevel3);
        logger.info("getAppInfoList -- > queryFlatformId: " + _queryFlatformId);
        logger.info("getAppInfoList -- > pageIndex: " + pageIndex);

        List<AppInfo> appInfoList = null;
        List<DataDictionary> flatFormList = null;
        List<AppCategory> categoryLevel1List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        List<AppCategory> categoryLevel2List = null;
        List<AppCategory> categoryLevel3List = null;

        int pageSize = Constants.pageSize;

        int currentPageNo = 1;

        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        Integer queryCategoryLevel1 = null;
        if(_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")){
            queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
        }
        Integer queryCategoryLevel2 = null;
        if(_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")){
            queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
        }
        Integer queryCategoryLevel3 = null;
        if(_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")){
            queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
        }
        Integer queryFlatformId = null;
        if(_queryFlatformId != null && !_queryFlatformId.equals("")){
            queryFlatformId = Integer.parseInt(_queryFlatformId);
        }

        //总数量
        int totalCount = 0;
        totalCount = appService.getAppInfoCount(querySoftwareName, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId);

        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }
        appInfoList = appService.getAppinfoList(querySoftwareName, queryCategoryLevel1, queryCategoryLevel2,
                queryCategoryLevel3, queryFlatformId, currentPageNo, pageSize);
        flatFormList = this.getDataDictionaryList("APP_FLATFORM");
        categoryLevel1List = appCategoryService.getAppCategoryListByParentId(null);

        model.addAttribute("appInfoList", appInfoList);
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("pages", pages);
        model.addAttribute("querySoftwareName", querySoftwareName);
        model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
        model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
        model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
        model.addAttribute("queryFlatformId", queryFlatformId);

        //二级分类列表和三级分类列表---回显
        if (queryCategoryLevel2 != null) {
            categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
            model.addAttribute("categoryLevel2List", categoryLevel2List);
        }
        if (queryCategoryLevel3 != null) {
            categoryLevel3List = getCategoryList(queryCategoryLevel1.toString());
            model.addAttribute("categoryLevel3List", categoryLevel3List);
        }

        return "backend/applist";
    }



    /**
     * 查询数据字典
     *
     * @param typeCode
     * @return
     */
    public List<DataDictionary> getDataDictionaryList(String typeCode) {
        return dataDictionaryService.getDataDictionaryList(typeCode);
    }

    /**
     * 根据parentId查询出相应的分类级别列表
     *
     * @param pid
     * @return
     */
    @RequestMapping(value = "/categorylevellist.json", method = RequestMethod.GET)
    @ResponseBody
    public List<AppCategory> getAppCategoryList(@RequestParam String pid) {
        logger.debug("getAppCategoryList pid ============ " + pid);
        if (pid.equals("")) {
            pid = null;
        }
        return getCategoryList(pid);
    }


    /**
     * 根据typeCode 查询出相应的数据字典
     *
     * @param tcode
     * @return
     */
    @RequestMapping(value = "/datadictionarylist.json", method = RequestMethod.GET)
    @ResponseBody
    public List<DataDictionary> getDataDicList(@RequestParam String tcode) {
        logger.debug("getDataDicList tcode ============ " + tcode);
        return this.getDataDictionaryList(tcode);
    }

    /**
     * 查询分类
     *
     * @param pid
     * @return
     */
    public List<AppCategory> getCategoryList(String pid) {
        return appCategoryService.getAppCategoryListByParentId(pid == null ? null : Integer.parseInt(pid));
    }

    /**
     *  跳转到APP信息审核页面
     * @param appId
     * @param versionId
     * @param model
     * @return
     */
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public String check(@RequestParam("aid") String appId,
                        @RequestParam("vid") String versionId,
                        Model model) {
        AppInfo appInfo = appService.getAppInfo(Integer.parseInt(appId));
        AppVersion appVersion= appVersionService.getAppVersionById(Integer.parseInt(versionId));

        model.addAttribute(appVersion);
        model.addAttribute(appInfo);
        return "backend/appcheck";
    }

    /**
     * 保存信息审核
     * @param appInfo
     * @return
     */
    @RequestMapping(value = "/checksave",method = RequestMethod.POST)
    public String checkSave(AppInfo appInfo) {
        logger.debug("appInfo =========== > " + appInfo.getStatusName());
        boolean flag = appService.updateSatus(appInfo.getStatus(), appInfo.getId());
        if (flag) {
            return "redirect:/mager/backend/app/list";
        }

        return "backend/appcheck";
    }
}
