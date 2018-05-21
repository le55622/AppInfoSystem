package cn.appsys.service.developer;

import java.util.List;

import cn.appsys.pojo.DataDictionary;

public interface DataDictionaryService {
	public List<DataDictionary> getDatalist(String typeCode);
}
