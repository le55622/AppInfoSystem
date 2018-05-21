package cn.appsys.service.developer;

import cn.appsys.pojo.DevUser;

public interface DevService {
	public DevUser login(String devCode,String devPassword);
}
