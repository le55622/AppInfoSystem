package cn.appsys.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.appsys.dao.appinfo.AppInfoDao;
import cn.appsys.dao.appversion.AppVersionDao;
import cn.appsys.pojo.AppVersion;

@Transactional
@Service("appVersionService")
public class AppVersionServiceImpl implements AppVersionService {
	@Resource
	private AppVersionDao appVersionDao;
	
	@Resource
	private AppInfoDao appInfoDao;
	
	public boolean add(AppVersion appVersion) throws Exception {
		boolean flag = false;
		Integer versionId = null;
		if(appVersionDao.add(appVersion) > 0){
			versionId = appVersion.getId();
			flag = true;
		}
		if(appInfoDao.updateVersionId(versionId, appVersion.getAppId()) > 0 && flag){
			flag = true;
		}
		return flag;
	}
	
	public List<AppVersion> getVer(int appId){
		return appVersionDao.getVer(appId);
	}
	
	public AppVersion getAppVersionById(int id) {
		return appVersionDao.getAppVersionById(id);
	}
	
	public int delVer(int appId) {
		return appVersionDao.delVer(appId);
	}
	
	public int deleteApkFile(int id) {
		return appVersionDao.deleteApkFile(id);
	}
	
	public int update(AppVersion appVersion) {
		return appVersionDao.update(appVersion);
	}
}
