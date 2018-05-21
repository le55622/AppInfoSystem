package cn.appsys.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.datadictionary.DataDictionaryDao;
import cn.appsys.pojo.DataDictionary;

@Service("dataDictionaryService")
public class DataDictionaryServiceImpl implements DataDictionaryService {
	@Resource
	private DataDictionaryDao dataDictionaryDao;
	
	public List<DataDictionary> getDatalist(String typeCode){
		return dataDictionaryDao.getDatalist(typeCode);
	}
}
