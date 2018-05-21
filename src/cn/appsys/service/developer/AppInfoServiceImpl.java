package cn.appsys.service.developer;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.appsys.dao.appinfo.AppInfoDao;
import cn.appsys.dao.appversion.AppVersionDao;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;

@Transactional
@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {
	@Resource
	private AppInfoDao appInfoDao;
	
	@Resource
	private AppVersionDao appVersionDao;
	
	public List<AppInfo> getList(AppInfo appInfo,int pageIndex,int pageSize){
		return appInfoDao.getList(appInfo, pageIndex, pageSize);
	}
	
	public int getCount(AppInfo appInfo) {
		return appInfoDao.getCount(appInfo);
	}
	
	public AppInfo getApp(int id) {
		return appInfoDao.getApp(id);
	}
	
	public int delApp(int id) {
		return appInfoDao.delApp(id);
	}
	
	public int add(AppInfo appInfo) {
		return appInfoDao.add(appInfo);
	}
	
	public AppInfo getAppInfo(Integer id,String APKName) throws Exception {
		return appInfoDao.getAppInfo(id, APKName);
	}
	
	public int deleteAppLogo(int id) {
		return appInfoDao.deleteAppLogo(id);
	}
	
	public int updateVersionId(Integer versionId,Integer appId)throws Exception{
		return appInfoDao.updateVersionId(versionId, appId);
	}
	
	public int update(AppInfo appInfo) {
		return appInfoDao.update(appInfo);
	}
	
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfoObj) throws Exception {
		/*
		 * 上架： 
			1 更改status由【2 or 5】 to 4 ， 上架时间
			2 根据versionid 更新 publishStauts 为 2 
			
			下架：
			更改status 由4给为5
		 */
		
		Integer operator = appInfoObj.getModifyBy();
		if(operator < 0 || appInfoObj.getId() < 0 ){
			throw new Exception();
		}
		
		//get appinfo by appid
		AppInfo appInfo = appInfoDao.getAppInfo(appInfoObj.getId(), null);
		if(null == appInfo){
			return false;
		}else{
			switch (appInfo.getStatus()) {
				case 2: //当状态为审核通过时，可以进行上架操作
					onSale(appInfo,operator,4,2);
					break;
				case 5://当状态为下架时，可以进行上架操作
					onSale(appInfo,operator,4,2);
					break;
				case 4://当状态为上架时，可以进行下架操作
					offSale(appInfo,operator,5);
					break;

			default:
				return false;
			}
		}
		return true;
	}
	
	
	private void onSale(AppInfo appInfo,Integer operator,Integer appInfStatus,Integer versionStatus) throws Exception{
		offSale(appInfo,operator,appInfStatus);
		setSaleSwitchToAppVersion(appInfo,operator,versionStatus);
	}
	
	
	private boolean offSale(AppInfo appInfo,Integer operator,Integer appInfStatus) throws Exception{
		AppInfo _appInfo = new AppInfo();
		_appInfo.setId(appInfo.getId());
		_appInfo.setStatus(appInfStatus);
		_appInfo.setModifyBy(operator);
		_appInfo.setOffSaleDate(new Date(System.currentTimeMillis()));
		appInfoDao.update(_appInfo);
		return true;
	}
	
	private boolean setSaleSwitchToAppVersion(AppInfo appInfo,Integer operator,Integer saleStatus) throws Exception{
		AppVersion appVersion = new AppVersion();
		appVersion.setId(appInfo.getVersionId());
		appVersion.setPublishStatus(saleStatus);
		appVersion.setModifyBy(operator);
		appVersion.setModifyDate(new Date(System.currentTimeMillis()));
		appVersionDao.update(appVersion);
		return false;
	}
	
	public int updateSatus(Integer status,Integer id)throws Exception{
		return appInfoDao.updateSatus(status, id);
	}
}
