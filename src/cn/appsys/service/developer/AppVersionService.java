package cn.appsys.service.developer;

import java.util.List;

import cn.appsys.pojo.AppVersion;

public interface AppVersionService {
	public boolean add(AppVersion appVersion) throws Exception;
	
	public List<AppVersion> getVer(int appId);
	
	public AppVersion getAppVersionById(int id);
	
	public int delVer(int appId);
	
	public int deleteApkFile(int id);
	
	public int update(AppVersion appVersion);
}
