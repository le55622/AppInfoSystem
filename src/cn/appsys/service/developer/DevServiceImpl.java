package cn.appsys.service.developer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.devuser.DevUserDao;
import cn.appsys.pojo.DevUser;

@Service("devService")
public class DevServiceImpl implements DevService {
	@Resource
	private DevUserDao devUserDao;

	public DevUser login(String devCode, String devPassword) {
		return devUserDao.login(devCode, devPassword);
	}
	
}
