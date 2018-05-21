package cn.appsys.dao.appinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.appsys.pojo.AppInfo;

public interface AppInfoDao {
	public List<AppInfo> getList(@Param("appInfo")AppInfo appInfo,@Param("pageIndex") int pageIndex,@Param("pageSize")int pageSize);
	
	public int getCount(@Param("appInfo")AppInfo appInfo);
	
	public AppInfo getApp(int id);
	
	public int delApp(int id);
	
	public int add(AppInfo appInfo);
	
	public AppInfo getAppInfo(@Param(value="id")Integer id,@Param(value="APKName")String APKName)throws Exception;
	
	public int deleteAppLogo(int id);
	
	public int updateVersionId(@Param(value="versionId")Integer versionId,@Param(value="id")Integer appId)throws Exception;
	
	public int update(AppInfo appInfo);
	
	public int updateSatus(@Param(value="status")Integer status,@Param(value="id")Integer id)throws Exception;
}
