package com.xia.appsys.controller.developer;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.xia.appsys.entity.AppCategory;
import com.xia.appsys.entity.AppInfo;
import com.xia.appsys.entity.AppVersion;
import com.xia.appsys.entity.DataDictionary;
import com.xia.appsys.entity.DevUser;
import com.xia.appsys.service.AppCategoryService;
import com.xia.appsys.service.AppInfoService;
import com.xia.appsys.service.AppVersionService;
import com.xia.appsys.service.DataDictionaryService;
import com.xia.appsys.tools.Constants;
import com.xia.appsys.tools.PageSupport;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.type.IntegerTypeHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 夏兵
 * @date 2021年08月24日 14:31
 */
@Controller
@RequestMapping("/dev/flatform/app")
public class AppInfoController {

    private final Logger logger = Logger.getLogger(AppInfoController.class);
    @Resource
    private AppInfoService appInfoService;

    @Resource
    private DataDictionaryService dataDictionaryService;

    @Resource
    private AppCategoryService appCategoryService;

    @Resource
    private AppVersionService appVersionService;

    @RequestMapping("/list")
    public String getAppInfoList(Model model,
                                 HttpSession session,
                                 @RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
                                 @RequestParam(value = "queryStatus", required = false) String _queryStatus,
                                 @RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
                                 @RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
                                 @RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
                                 @RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
                                 @RequestParam(value = "pageIndex", required = false) String pageIndex
    ) {
        logger.debug("getAppInfoList--> querySoftwareName" + querySoftwareName);
        logger.debug("getAppInfoList--> queryStatus" + _queryStatus);
        logger.debug("getAppInfoList--> queryCategoryLevel1" + _queryCategoryLevel1);
        logger.debug("getAppInfoList--> queryCategoryLevel2" + _queryCategoryLevel2);
        logger.debug("getAppInfoList--> queryCategoryLevel3" + _queryCategoryLevel3);
        logger.debug("getAppInfoList--> queryFlatformId" + _queryFlatformId);
        logger.debug("getAppInfoList--> pageIndex" + pageIndex);

        Integer devId = ((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId();
        List<AppInfo> appInfoList = null;
        List<DataDictionary> statusList = null;
        List<DataDictionary> flatFormList = null;
        //列出一级分类列表
        List<AppCategory> categoryLevel1List = null;
        List<AppCategory> categoryLevel2List = null;
        List<AppCategory> categoryLevel3List = null;

        //页面容量
        int pageSize = 5;
        //当前页码
        int currentPageNo = 1;
        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }
        Integer queryStatus = null;
        if (null != _queryStatus && !"".equals(_queryStatus)) {
            queryStatus = Integer.parseInt(_queryStatus);
        }
        Integer queryCategoryLevel1 = null;
        if (null != _queryCategoryLevel1 && !"".equals(_queryCategoryLevel1)) {
            queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
        }
        Integer queryCategoryLevel2 = null;
        if (null != _queryCategoryLevel2 && !"".equals(_queryCategoryLevel2)) {
            queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
        }
        Integer queryCategoryLevel3 = null;
        if (null != _queryCategoryLevel3 && !"".equals(_queryCategoryLevel3)) {
            queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
        }
        Integer queryFlatformId = null;
        if (null != _queryFlatformId && !"".equals(_queryFlatformId)) {
            queryFlatformId = Integer.parseInt(_queryFlatformId);
        }

        int totalCount = appInfoService.getAppInfoCount(querySoftwareName,
                queryStatus, queryCategoryLevel1, queryCategoryLevel2,
                queryCategoryLevel3, queryFlatformId, devId);

        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }


        appInfoList = appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);

        statusList = this.getDataDictionaryList("APP_STATUS");

        flatFormList = this.getDataDictionaryList("APP_FLATFORM");

        categoryLevel1List = appCategoryService.getAppCategoryListByParentId(null);

        model.addAttribute("appInfoList", appInfoList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("pages", pages);
        model.addAttribute("queryStatus", queryStatus);
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

        return "developer/appinfolist";
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
     * 查询分类
     *
     * @param pid
     * @return
     */
    public List<AppCategory> getCategoryList(String pid) {
        return appCategoryService.getAppCategoryListByParentId(pid == null ? null : Integer.parseInt(pid));
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
     * 跳转到新增页面
     *
     * @param appInfo
     * @return
     */
    @RequestMapping(value = "/appinfoadd", method = RequestMethod.GET)
    public String toAdd(@ModelAttribute("appInfo") AppInfo appInfo) {
        return "developer/appinfoadd";
    }

    /**
     * 查询应用名称是否唯一 并返回前台
     *
     * @param APKName
     * @return
     */
    @RequestMapping("/apkexist.json")
    @ResponseBody
    public Object apkNameIsExit(@RequestParam String APKName) {
        Map<String, String> resulMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(APKName)) {
            resulMap.put("APKName", "empty");
        } else {
            AppInfo appInfo = appInfoService.getAppInfo(null, APKName);
            if (null != appInfo) {
                resulMap.put("APKName", "exist");
            } else {
                resulMap.put("APKName", "noexist");
            }
        }
        return JSONArray.toJSONString(resulMap);
    }

    /**
     * 查看app信息，包括app基本信息和版本信息列表（跳转到查看页面）
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/appview/{id}", method = RequestMethod.GET)
    public String view(@PathVariable String id, Model model) {
        AppInfo appInfo = null;
        List<AppVersion> appVersionList = null;

        appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
        appVersionList = appVersionService.getAppVersionList(Integer.parseInt(id));

        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute(appInfo);

        return "developer/appinfoview";
    }

    /**
     * 保存app新增的数据
     *
     * @param appInfo
     * @param session
     * @param request
     * @param attach
     * @return
     */
    @RequestMapping("/appinfoaddsave")
    public String addSave(AppInfo appInfo, HttpSession session, HttpServletRequest request,
                          @RequestParam(value = "attach", required = false) MultipartFile attach) {
        String logoPicPath = null;
        String logoLocPath = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            logger.debug("uploadFile path" + path);
            String oldFileName = attach.getOriginalFilename();  //获取原声文件名
            String prefix = FilenameUtils.getExtension(oldFileName); // 原始文件后缀名
            int filesize = 500000; //上传大小
            if (attach.getSize() > filesize) {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
                return "developer/appinfoadd";
                //equslsIgnoreCase 忽略大小写  比较后缀名
            } else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                    || prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")) {
                String fileName = appInfo.getAPKName() + ".jpg";
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();  //创建文件夹
                }
                try {
                    attach.transferTo(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
                    return "developer/appinfoadd";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;

                logger.debug("logoPicPath" + logoPicPath);
                logger.debug("logLocPath" + logoLocPath);
            } else {//格式不对
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
                return "developer/appinfoadd";
            }
        }
        appInfo.setCreatedBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());//获取上传者信息
        appInfo.setCreationDate(new Date());
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setLogoPicPath(logoPicPath);
        appInfo.setDevId(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setStatus(1);
        try {
            if (appInfoService.add(appInfo) > 0) {
                return "redirect:/dev/flatform/app/list";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appinfoadd";

    }

    /**
     * 增加appversion信息（跳转到新增app版本页面）
     *
     * @param appId
     * @param fileUploadError
     * @param appVersion
     * @param model
     * @return
     */
    @RequestMapping("/appversionadd")
    public String addVersion(@RequestParam("id") String appId,
                             @RequestParam(value = "error", required = false) String fileUploadError,
                             AppVersion appVersion, Model model) {
        logger.debug("fileUploadError===============    " + fileUploadError);

        if ("error1".equals(fileUploadError)) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_1;
        } else if ("error2".equals(fileUploadError)) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && "error3".equals(fileUploadError)) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_3;
        }
        appVersion.setAppId(Integer.parseInt(appId));
        List<AppVersion> appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
        appVersion.setAppName((appInfoService.getAppInfo(Integer.parseInt(appId), null)).getSoftwareName());

        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute(appVersion);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appversionadd";

    }


    /**
     * 保存新增appversion数据（子表）-上传该版本的apk包
     *
     * @param appVersion
     * @param session
     * @param request
     * @param a_downloadLink
     * @return
     */
    @RequestMapping(value = "/addversionsave", method = RequestMethod.POST)
    public String addVersionSave(AppVersion appVersion, HttpSession session, HttpServletRequest request,
                                 @RequestParam(value = "a_downloadLink", required = false) MultipartFile a_downloadLink) {
        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;

        if (!a_downloadLink.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            logger.debug("uploadFile path" + path);

            String oldFileName = a_downloadLink.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//文件后缀名
            assert prefix != null;
            if (prefix.equalsIgnoreCase("apk")) {//apk文件命名：apk名称+版本号+.apk
                String apkName = null;

                apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();

                if (apkName == null || "".equals(apkName)) {
                    return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId() + "&error=error1";
                }
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path, apkFileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();   //创建文件夹
                }
                try {
                    a_downloadLink.transferTo(targetFile);  //文件上传至创建的路径
                } catch (IOException e) {
                    e.printStackTrace();
                    return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId() + "&error=error2";
                }

                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;

                apkLocPath = path + File.separator + apkFileName;

            } else {
                return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId()
                        + "&error=error3";
            }
        }

        appVersion.setCreatedBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appVersion.setCreationDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);

        if (appVersionService.appsysadd(appVersion)) {
            return "redirect:/dev/flatform/app/list";
        }

        return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId();
    }

    /**
     * @param appid
     * @param session
     * @return
     */
    @RequestMapping(value = "/{appid}/sale.json", method = RequestMethod.PUT)
    @ResponseBody
    public Object sale(@PathVariable String appid, HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer appIdInteger = 0;
        try {

            appIdInteger = Integer.parseInt(appid);
        } catch (Exception e) {
            appIdInteger = 0;
        }
        resultMap.put("errorCode", "0");
        resultMap.put("appId", appid);

        if (appIdInteger > 0) {
            try {
                DevUser devUser = (DevUser) session.getAttribute(Constants.DEV_USER_SESSION);
                AppInfo appInfo = new AppInfo();
                appInfo.setId(appIdInteger);
                appInfo.setModifyBy(devUser.getId());

                if (appInfoService.appsysUpdateSaleStatusByAppId(appInfo)) {
                    resultMap.put("resultMsg", "success");
                } else {
                    resultMap.put("resultMsg", "success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                resultMap.put("errorCode", "exception000001");
            }
        } else {
            resultMap.put("errorCode", "exception000001");
        }
        return resultMap;
    }

    /**
     * 修改appInfo信息（跳转到修改appInfo页面）
     *
     * @param id
     * @param fileUploadError
     * @param model
     * @return
     */
    @RequestMapping(value = "/appinfomodify", method = RequestMethod.GET)
    public String modifyAppInfo(@RequestParam("id") String id,
                                @RequestParam(value = "error", required = false) String fileUploadError,
                                Model model) {
        AppInfo appInfo = null;
        logger.debug("modifyAppInfo --------- id: " + id);
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_3;
        } else if (null != fileUploadError && fileUploadError.equals("error4")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_4;
        }

        try {
            appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute(appInfo);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appinfomodify";

    }

    /**
     * 修改最新的appVersion信息（跳转到修改appVersion页面）
     *
     * @param versionId
     * @param appId
     * @param fileUploadError
     * @param model
     * @return
     */
    @RequestMapping(value = "/appversionmodify", method = RequestMethod.GET)
    public String modifyAppVersion(@RequestParam("vid") String versionId,
                                   @RequestParam("aid") String appId,
                                   @RequestParam(value = "error", required = false) String fileUploadError,
                                   Model model) {
        AppVersion appVersion = null;
        List<AppVersion> appVersionList = null;
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_3;
        }
        try {
            appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute(appVersion);
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute("fileUploadError", fileUploadError);

        return "developer/appversionmodify";
    }

    /**
     * 保存修改后的appInfo
     *
     * @param appInfo
     * @param session
     * @param request
     * @param attach
     * @return
     */
    @RequestMapping(value = "/appinfomodifysave", method = RequestMethod.POST)
    public String modifySave(AppInfo appInfo, HttpSession session, HttpServletRequest request,
                             @RequestParam(value = "attach", required = false) MultipartFile attach) {
        String logoPicPath = null;
        String logoLocPath = null;
        String APKName = appInfo.getAPKName();
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            logger.info("uploadFile path: " + path);
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            int filesize = 500000;

            if (attach.getSize() > filesize) {//上传大小不得超过 50k

                return "redirect:/dev/flatform/app/appinfomodify?id=" + appInfo.getId()
                        + "&error=error4";
            } else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                    || prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")) {//上传图片格式
                String fileName = APKName + ".jpg";//上传LOGO图片命名:apk名称.apk
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "redirect:/dev/flatform/app/appinfomodify?id=" + appInfo.getId()
                            + "&error=error2";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                return "redirect:/dev/flatform/app/appinfomodify?id=" + appInfo.getId()
                        + "&error=error3";
            }
        }

        appInfo.setModifyBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setModifyDate(new Date());
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setLogoPicPath(logoPicPath);

        try {
            if (appInfoService.modify(appInfo) > 0) {

                return "redirect:/dev/flatform/app/list";
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "developer/appinfomodify";
    }

    /**
     * 保存修改后的appVersion
     *
     * @param appVersion
     * @param session
     * @param request
     * @param attach
     * @return
     */
    @RequestMapping(value = "/appversionmodifysave", method = RequestMethod.POST)
    public String modifyAppVersionSave(AppVersion appVersion, HttpSession session, HttpServletRequest request,
                                       @RequestParam(value = "attach", required = false) MultipartFile attach) {

        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            logger.info("uploadFile path: " + path);
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            if (prefix.equalsIgnoreCase("apk")) {//apk文件命名：apk名称+版本号+.apk
                String apkName = null;
                try {
                    apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (apkName == null || "".equals(apkName)) {
                    return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId()
                            + "&aid=" + appVersion.getAppId()
                            + "&error=error1";
                }
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path, apkFileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId()
                            + "&aid=" + appVersion.getAppId()
                            + "&error=error2";
                }
                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
                apkLocPath = path + File.separator + apkFileName;
            } else {

                return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId()
                        + "&aid=" + appVersion.getAppId()
                        + "&error=error3";
            }
        }
        appVersion.setModifyBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appVersion.setModifyDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        try {
            if (appVersionService.modify(appVersion) > 0) {
                return "redirect:/dev/flatform/app/list";
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "developer/appversionmodify";
    }

    /**
     * @param flag
     * @param id
     * @return
     */
    @RequestMapping(value = "/delfile.json", method = RequestMethod.GET)
    @ResponseBody
    public Object delFile(@RequestParam("flag") String flag,
                          @RequestParam("id") String id) {
        Map<String, String> resultMap = new HashMap<>();
        String fileLocPath = null;
        if (flag == null || flag.equals("") || id == null || id.equals("")) {
            resultMap.put("result", "failed");
        } else if (flag.equals("logo")) {//删除logo图片（操作app_info）
            try {
                fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id), null)).getLogoLocPath();
                File file = new File(fileLocPath);

                if (file.exists()) {
                    if (file.delete()) {//删除服务器存储的物理文件
                        if (appInfoService.deleteAppLogo(Integer.parseInt(id)) > 0) {
                            resultMap.put("result", "success");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (flag.equals("apk")) {
            try {
                fileLocPath = (appVersionService.getAppVersionById(Integer.parseInt(id))).getApkLocPath();
                File file = new File(fileLocPath);

                if (file.exists()) {
                    if (file.delete()) {//删除服务器存储的物理文件
                        if (appVersionService.deleteApkFile(Integer.parseInt(id)) > 0) {
                            resultMap.put("result", "success");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return JSONArray.toJSONString(resultMap);
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping(value = "/delapp.json")
    @ResponseBody
    public Object delApp(@RequestParam String id) {
        logger.debug("delApp appId===================== " + id);
        Map<String, String> resultMap = new HashMap<>();
        if (StringUtils.isNullOrEmpty(id)) {
            resultMap.put("delResult", "notexist");
        } else {
            try {
                if (appInfoService.appsysdeleteAppById(Integer.parseInt(id))) {
                    resultMap.put("delResult", "true");
                } else {
                    resultMap.put("delResult", "false");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JSONArray.toJSONString(resultMap);
    }


}
