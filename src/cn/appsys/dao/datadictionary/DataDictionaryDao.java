package cn.appsys.dao.datadictionary;

import java.util.List;

import cn.appsys.pojo.DataDictionary;

public interface DataDictionaryDao {
	public List<DataDictionary> getDatalist(String typeCode);
}
