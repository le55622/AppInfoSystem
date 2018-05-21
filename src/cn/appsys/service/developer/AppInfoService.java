package cn.appsys.service.developer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.appsys.pojo.AppInfo;

public interface AppInfoService {
	public List<AppInfo> getList(AppInfo appInfo,int pageIndex,int pageSize);
	
	public int getCount(AppInfo appInfo);
	
	public AppInfo getApp(int id);
	
	public int delApp(int id);
	
	public int add(AppInfo appInfo);
	
	public AppInfo getAppInfo(Integer id,String APKName) throws Exception;
	
	public int deleteAppLogo(int id);
	
	public int updateVersionId(Integer versionId,Integer appId)throws Exception;
	
	public int update(AppInfo appInfo);
	
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo) throws Exception;
	
	public int updateSatus(Integer status,Integer id)throws Exception;
}
