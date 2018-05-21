package cn.appsys.dao.appcategory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.appsys.pojo.AppCategory;

public interface AppCategoryDao {
	public List<AppCategory> categoryLevelList(@Param("parentId")Integer parentId);
}
