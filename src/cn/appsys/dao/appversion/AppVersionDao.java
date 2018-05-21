package cn.appsys.dao.appversion;

import java.util.List;

import cn.appsys.pojo.AppVersion;

public interface AppVersionDao {
	public int add(AppVersion appVersion);
	
	public List<AppVersion> getVer(int appId);
	
	public AppVersion getAppVersionById(int id);
	
	public int delVer(int appId);
	
	public int deleteApkFile(int id);
	
	public int update(AppVersion appVersion);
}
