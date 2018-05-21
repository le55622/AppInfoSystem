package cn.appsys.service.backend;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.backenduser.BackendUserDao;
import cn.appsys.pojo.BackendUser;

@Service("backendUserService")
public class BackendUserServiceImpl implements BackendUserService{
	
	@Resource
	private BackendUserDao backendUserDao;
	
	public BackendUser login(BackendUser backendUser) {
		return backendUserDao.login(backendUser);
	}
}
