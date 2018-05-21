package cn.appsys.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.appcategory.AppCategoryDao;
import cn.appsys.pojo.AppCategory;

@Service("appCategoryService")
public class AppCategoryServiceImpl implements AppCategoryService {
	
	@Resource
	private AppCategoryDao appCategoryDao;

	public List<AppCategory> categoryLevelList(Integer parentId) {
		return appCategoryDao.categoryLevelList(parentId);
	}

}
