package cn.appsys.controller.backend;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.BackendUser;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.service.backend.BackendUserService;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.tools.Constants;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	
	@Resource
	private BackendUserService backendUserService;
	
	@Resource
	private AppInfoService appInfoService;
	
	@Resource
	private AppCategoryService appCategoryService;
	
	@Resource
	private DataDictionaryService dataDictionaryService;
	
	@Resource
	private AppVersionService appVersionService;
	
	@RequestMapping("/login")
	public String login(){
		return "backendlogin";
	}
	
	@RequestMapping("/dologin")
	public String dologin(@RequestParam String userCode,@RequestParam String userPassword,Model model,HttpSession session) {
		BackendUser backendUser = new BackendUser();
		backendUser.setUserCode(userCode);
		backendUser.setUserPassword(userPassword);
		BackendUser b = backendUserService.login(backendUser);
		if(b!=null) {
			session.setAttribute(Constants.USER_SESSION, b);
			return "backend/main";
		}else {
			model.addAttribute("error", "用户名或密码不正确");
			return "backendlogin";
		}
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute(Constants.USER_SESSION);
		return "backendlogin";
	}
	
	@RequestMapping("/applist")
	public String applist(@RequestParam(value="pageIndex",required=false)String cutt,
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
		return "backend/applist";
	}
	
	@RequestMapping("categorylevellist")
	@ResponseBody
	public Object categorylevellist(@RequestParam("pid")Integer parentId) {
		List<AppCategory> list = appCategoryService.categoryLevelList(parentId);
		return JSONArray.toJSONString(list);
	}
	
	@RequestMapping(value="/check")
	public String check(@RequestParam(value="aid",required=false) String appId,
					   @RequestParam(value="vid",required=false) String versionId,
					   Model model){
		AppInfo appInfo = null;
		AppVersion appVersion = null;
		try {
			appInfo = appInfoService.getApp(Integer.parseInt(appId));
			appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute(appVersion);
		model.addAttribute(appInfo);
		return "backend/appcheck";
	}
	
	@RequestMapping(value="/checksave")
	public String checkSave(AppInfo appInfo){
		try {
			if(appInfoService.updateSatus(appInfo.getStatus(),appInfo.getId())>0){
				return "redirect:/manager/applist";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "backend/appcheck";
	}
}
