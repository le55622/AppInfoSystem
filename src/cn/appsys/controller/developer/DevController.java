package cn.appsys.controller.developer;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.service.developer.DevService;
import cn.appsys.tools.Constants;

@Controller
@RequestMapping("/dev")
public class DevController {
	
	@Resource
	private DevService dvService;
	
	@Resource
	private AppInfoService appInfoService;
	
	@Resource
	private AppCategoryService appCategoryService;
	
	@Resource
	private DataDictionaryService dataDictionaryService;
	
	@Resource
	private AppVersionService appVersionService;
	
	@RequestMapping("/login")
	public String login() {
		return "devlogin";
	}
	
	@RequestMapping("/dologin")
	public String dologin(String devCode,String devPassword,HttpSession session) {
		DevUser duser = dvService.login(devCode, devPassword);
		if(duser!=null) {
			session.removeAttribute("error");
			session.setAttribute("devUserSession", duser);
			return "developer/main";
		}else {
			session.setAttribute("error", "账号密码不正确！");
			return "redirect:login";
		}
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("devUserSession");
		return "redirect:login";
	}
	
	@RequestMapping(value="/applist")
	public String getList(@RequestParam(value="pageIndex",required=false)String cutt,
			@RequestParam(value="querySoftwareName",required=false)String querySoftwareName,
			@RequestParam(value="queryStatus",required=false)String queryStatus,
			@RequestParam(value="queryFlatformId",required=false)String queryFlatformId,
			@RequestParam(value="queryCategoryLevel1",required=false)String queryCategoryLevel1,
			@RequestParam(value="queryCategoryLevel2",required=false)String queryCategoryLevel2,
			@RequestParam(value="queryCategoryLevel3",required=false)String queryCategoryLevel3,Model model) {
		int pageIndex = 1;
		int pageSize = 5;
		AppInfo appInfo = new AppInfo();
		appInfo.setAPKName(querySoftwareName);
		if(queryStatus!=null&&queryStatus!="") {
			appInfo.setStatus(Integer.parseInt(queryStatus));
		}
		if(queryFlatformId!=null&&queryFlatformId!="") {
			appInfo.setFlatformId(Integer.parseInt(queryFlatformId));
		}
		if(queryCategoryLevel1!=null&&queryCategoryLevel1!="") {
			appInfo.setCategoryLevel1(Integer.parseInt(queryCategoryLevel1));
		}
		if(queryCategoryLevel2!=null&&queryCategoryLevel2!="") {
			appInfo.setCategoryLevel2(Integer.parseInt(queryCategoryLevel2));
		}
		if(queryCategoryLevel3!=null&&queryCategoryLevel3!="") {
			appInfo.setCategoryLevel3(Integer.parseInt(queryCategoryLevel3));
		}
		int count = appInfoService.getCount(appInfo);
		int total = count%pageSize==0?count/pageSize:count/pageSize+1;
		if(cutt!=null) {
			pageIndex = Integer.parseInt(cutt);
		}
		List<AppInfo> listapp = appInfoService.getList(appInfo, pageIndex, pageSize);
		List<AppCategory> categoryLevel1List = appCategoryService.categoryLevelList(null);
		model.addAttribute("categoryLevel1List",categoryLevel1List);
		List<DataDictionary> statusList = dataDictionaryService.getDatalist("APP_STATUS");
		model.addAttribute("statusList",statusList);
		List<DataDictionary> flatFormList = dataDictionaryService.getDatalist("APP_FLATFORM");
		model.addAttribute("querySoftwareName",querySoftwareName);
		model.addAttribute("queryStatus",queryStatus);
		model.addAttribute("queryFlatformId",queryFlatformId);
		model.addAttribute("queryCategoryLevel1",queryCategoryLevel1);
		model.addAttribute("queryCategoryLevel2",queryCategoryLevel2);
		model.addAttribute("queryCategoryLevel3",queryCategoryLevel3);
		model.addAttribute("flatFormList",flatFormList);
		model.addAttribute("appInfoList",listapp);
		model.addAttribute("pageIndex",pageIndex);
		model.addAttribute("total",total);
		model.addAttribute("count",count);
		return "developer/appinfolist";
	}
	
	@RequestMapping("categorylevellist")
	@ResponseBody
	public Object categorylevellist(@RequestParam("pid")Integer parentId) {
		List<AppCategory> list = appCategoryService.categoryLevelList(parentId);
		return JSONArray.toJSONString(list);
	}
	
	@RequestMapping("datadictionarylist")
	@ResponseBody
	public Object datadictionarylist(String tcode) {
		List<DataDictionary> flatFormList = dataDictionaryService.getDatalist(tcode);
		return JSONArray.toJSONString(flatFormList);
	}
	
	@RequestMapping("appversionadd")
	public String appversionadd(Model model,@RequestParam int id,@RequestParam(value="error",required= false)String fileUploadError,
			 AppVersion appVersion) {
		if(null != fileUploadError && fileUploadError.equals("error1")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		}else if(null != fileUploadError && fileUploadError.equals("error2")){
			fileUploadError	= Constants.FILEUPLOAD_ERROR_2;
		}else if(null != fileUploadError && fileUploadError.equals("error3")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		appVersion.setAppId(id);
		List<AppVersion> appVersionList = null;
		try {
			appVersionList = appVersionService.getVer(id);
			appVersion.setAppName((appInfoService.getAppInfo(id,null)).getSoftwareName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appVersionList", appVersionList);
		model.addAttribute(appVersion);
		model.addAttribute("fileUploadError",fileUploadError);
		return "developer/appversionadd";
	}
	
	@RequestMapping("appview/{id}")
	public String appview(@PathVariable int id,Model model) {
		AppInfo app = appInfoService.getApp(id);
		List<AppVersion> appVersionList = appVersionService.getVer(id);
		model.addAttribute("appInfo",app);
		model.addAttribute("appVersionList",appVersionList);
		return "developer/appinfoview";
	}
	
	@RequestMapping("appinfoadd")
	public String appinfoadd(Model model) {
		return "developer/appinfoadd";
	}
	
	@RequestMapping("appinfoaddsave")
	public String appinfoaddsave(AppInfo appInfo,HttpSession session,HttpServletRequest request,
			@RequestParam(value="a_logoPicPath",required= false) MultipartFile attach) {
		String logoPicPath =  null;
		String logoLocPath =  null;
		if(!attach.isEmpty()){
			String path = session.getServletContext().getRealPath("statics"+java.io.File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			int filesize = 500000;
			if(attach.getSize() > filesize){//上传大小不得超过 50k
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
            }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
			   ||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式
				 String fileName = appInfo.getAPKName() + ".jpg";//上传LOGO图片命名:apk名称.apk
				 File targetFile = new File(path,fileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				} 
				 logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				 logoLocPath = path+File.separator+fileName;
			}else{
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		appInfo.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setCreationDate(new Date());
		appInfo.setLogoPicPath(logoPicPath);
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setDevId(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setStatus(1);
		try {
			if(appInfoService.add(appInfo)>0){
				return "redirect:/dev/applist";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "developer/appinfoadd";
	}
	
	@RequestMapping("delapp")
	@ResponseBody
	public Object delapp(int id) throws Exception {
		appVersionService.delVer(id);
		int i = appInfoService.delApp(id);
		Map<String, String> resultMap = new HashMap<String, String>();
		if(i>0) {
			resultMap.put("delResult", "true");
		}else {
			resultMap.put("delResult", "true");
		}
		return JSONArray.toJSONString(resultMap);
	}
	
	@RequestMapping(value="/apkexist")
	@ResponseBody
	public Object apkNameIsExit(@RequestParam String APKName){
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(StringUtils.isNullOrEmpty(APKName)){
			resultMap.put("APKName", "empty");
		}else{
			AppInfo appInfo = null;
			try {
				appInfo = appInfoService.getAppInfo(null, APKName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(null != appInfo)
				resultMap.put("APKName", "exist");
			else
				resultMap.put("APKName", "noexist");
		}
		return JSONArray.toJSONString(resultMap);
	}
	
	@RequestMapping(value="/addversionsave")
	public String addVersionSave(AppVersion appVersion,HttpSession session,HttpServletRequest request,
						@RequestParam(value="a_downloadLink",required= false) MultipartFile attach ){		
		String downloadLink =  null;
		String apkLocPath = null;
		String apkFileName = null;
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
				 String apkName = null;
				 try {
					apkName = appInfoService.getAppInfo(appVersion.getAppId(),null).getAPKName();
				 } catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				 }
				 if(apkName == null || "".equals(apkName)){
					 return "redirect:/dev/appversionadd?id="+appVersion.getAppId()
							 +"&error=error1";
				 }
				 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
				 File targetFile = new File(path,apkFileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/appversionadd?id="+appVersion.getAppId()
							 +"&error=error2";
				} 
				downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
				apkLocPath = path+File.separator+apkFileName;
			}else{
				return "redirect:/dev/appversionadd?id="+appVersion.getAppId()
						 +"&error=error3";
			}
		}
		appVersion.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setCreationDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if(appVersionService.add(appVersion)){
				appInfoService.updateVersionId(appVersion.getId(), appVersion.getAppId());
				return "redirect:/dev/applist";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/dev/appversionadd?id="+appVersion.getAppId();
	}
	
	@RequestMapping(value="/appversionmodify")
	public String modifyAppVersion(@RequestParam("vid") String versionId,
									@RequestParam("aid") String appId,
									@RequestParam(value="error",required= false)String fileUploadError,
									Model model){
		AppVersion appVersion = null;
		List<AppVersion> appVersionList = null;
		if(null != fileUploadError && fileUploadError.equals("error1")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		}else if(null != fileUploadError && fileUploadError.equals("error2")){
			fileUploadError	= Constants.FILEUPLOAD_ERROR_2;
		}else if(null != fileUploadError && fileUploadError.equals("error3")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		try {
			appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
			appVersionList = appVersionService.getVer(Integer.parseInt(appId));
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute(appVersion);
		model.addAttribute("appVersionList",appVersionList);
		model.addAttribute("fileUploadError",fileUploadError);
		return "developer/appversionmodify";
	}
	
	@RequestMapping(value = "/delfile",method=RequestMethod.GET)
	@ResponseBody
	public Object delFile(@RequestParam(value="flag",required=false) String flag,
						 @RequestParam(value="id",required=false) String id){
		HashMap<String, String> resultMap = new HashMap<String, String>();
		String fileLocPath = null;
		if(flag == null || flag.equals("") ||
			id == null || id.equals("")){
			resultMap.put("result", "failed");
		}else if(flag.equals("logo")){//删除logo图片（操作app_info）
			try {
				fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id), null)).getLogoLocPath();
				File file = new File(fileLocPath);
			    if(file.exists())
			     if(file.delete()){//删除服务器存储的物理文件
						if(appInfoService.deleteAppLogo(Integer.parseInt(id))>0){//更新表
							resultMap.put("result", "success");
						 }
			    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(flag.equals("apk")){//删除apk文件（操作app_version）
			try {
				fileLocPath = (appVersionService.getAppVersionById(Integer.parseInt(id))).getApkLocPath();
				File file = new File(fileLocPath);
			    if(file.exists())
			     if(file.delete()){//删除服务器存储的物理文件
						if(appVersionService.deleteApkFile(Integer.parseInt(id))>0){//更新表
							resultMap.put("result", "success");
						 }
			    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return JSONArray.toJSONString(resultMap);
	}
	
	@RequestMapping(value="/appversionmodifysave")
	public String modifyAppVersionSave(AppVersion appVersion,HttpSession session,HttpServletRequest request,
					@RequestParam(value="attach",required= false) MultipartFile attach){	
		String downloadLink =  null;
		String apkLocPath = null;
		String apkFileName = null;
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
				 String apkName = null;
				 try {
					apkName = appInfoService.getAppInfo(appVersion.getAppId(),null).getAPKName();
				 } catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				 }
				 if(apkName == null || "".equals(apkName)){
					 return "redirect:/dev/appversionmodify?vid="+appVersion.getId()
							 +"&aid="+appVersion.getAppId()
							 +"&error=error1";
				 }
				 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
				 File targetFile = new File(path,apkFileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/appversionmodify?vid="+appVersion.getId()
							 +"&aid="+appVersion.getAppId()
							 +"&error=error2";
				} 
				downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
				apkLocPath = path+File.separator+apkFileName;
			}else{
				return "redirect:/dev/appversionmodify?vid="+appVersion.getId()
						 +"&aid="+appVersion.getAppId()
						 +"&error=error3";
			}
		}
		appVersion.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setModifyDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if(appVersionService.update(appVersion)>0){
				return "redirect:/dev/applist";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "developer/appversionmodify";
	}
	
	@RequestMapping(value="/appinfomodify",method=RequestMethod.GET)
	public String modifyAppInfo(@RequestParam("id") String id,
								@RequestParam(value="error",required= false)String fileUploadError,
								Model model){
		AppInfo appInfo = null;
		if(null != fileUploadError && fileUploadError.equals("error1")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		}else if(null != fileUploadError && fileUploadError.equals("error2")){
			fileUploadError	= Constants.FILEUPLOAD_ERROR_2;
		}else if(null != fileUploadError && fileUploadError.equals("error3")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}else if(null != fileUploadError && fileUploadError.equals("error4")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_4;
		}
		try {
			appInfo = appInfoService.getAppInfo(Integer.parseInt(id),null);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute(appInfo);
		model.addAttribute("fileUploadError",fileUploadError);
		return "developer/appinfomodify";
	}
	
	@RequestMapping(value="/appinfomodifysave",method=RequestMethod.POST)
	public String modifySave(AppInfo appInfo,HttpSession session,HttpServletRequest request,
							@RequestParam(value="attach",required= false) MultipartFile attach){		
		String logoPicPath =  null;
		String logoLocPath =  null;
		String APKName = appInfo.getAPKName();
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			int filesize = 500000;
			if(attach.getSize() > filesize){//上传大小不得超过 50k
            	 return "redirect:/dev/appinfomodify?id="+appInfo.getId()
						 +"&error=error4";
            }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
			   ||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式
				 String fileName = APKName + ".jpg";//上传LOGO图片命名:apk名称.apk
				 File targetFile = new File(path,fileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/appinfomodify?id="+appInfo.getId()
							+"&error=error2";
				} 
				 logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				 logoLocPath = path+File.separator+fileName;
            }else{
            	return "redirect:/dev/appinfomodify?id="+appInfo.getId()
						 +"&error=error3";
            }
		}
		appInfo.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setModifyDate(new Date());
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setLogoPicPath(logoPicPath);
		try {
			if(appInfoService.update(appInfo)>0){
				return "redirect:/dev/applist";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "developer/appinfomodify";
	}
	
	@RequestMapping(value="/{appid}/sale",method=RequestMethod.PUT)
	@ResponseBody
	public Object sale(@PathVariable String appid,HttpSession session){
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		Integer appIdInteger = 0;
		try{
			appIdInteger = Integer.parseInt(appid);
		}catch(Exception e){
			appIdInteger = 0;
		}
		resultMap.put("errorCode", "0");
		resultMap.put("appId", appid);
		if(appIdInteger>0){
			try {
				DevUser devUser = (DevUser)session.getAttribute(Constants.DEV_USER_SESSION);
				AppInfo appInfo = new AppInfo();
				appInfo.setId(appIdInteger);
				appInfo.setModifyBy(devUser.getId());
				if(appInfoService.appsysUpdateSaleStatusByAppId(appInfo)){
					resultMap.put("resultMsg", "success");
				}else{
					resultMap.put("resultMsg", "success");
				}		
			} catch (Exception e) {
				resultMap.put("errorCode", "exception000001");
			}
		}else{
			//errorCode:0为正常
			resultMap.put("errorCode", "param000001");
		}
		
		/*
		 * resultMsg:success/failed
		 * errorCode:exception000001
		 * appId:appId
		 * errorCode:param000001
		 */
		return JSONArray.toJSONString(resultMap);
	}
}
