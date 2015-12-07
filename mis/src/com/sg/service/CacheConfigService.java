package com.sg.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.springframework.stereotype.Service;
import com.sg.dao.CacheConfigDao;
import com.sg.entity.CacheConfig;
import com.sg.mobile.entity.MobileServerConfig;

@Service
public class CacheConfigService implements BaseEntityManager<CacheConfig> {
	@Resource
	private CacheConfigDao cacheConfigDao;

	@Override
	public void create(CacheConfig entity) {
		// TODO Auto-generated method stub
		cacheConfigDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		cacheConfigDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<CacheConfig> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return cacheConfigDao.find(params, pageParams);
	}

	@Override
	public CacheConfig get(String id) {
		// TODO Auto-generated method stub
		return cacheConfigDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<CacheConfig> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return cacheConfigDao.findForUnPage(params);
	}

	@Override
	public void update(CacheConfig entity) {
		// TODO Auto-generated method stub
		cacheConfigDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return cacheConfigDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<CacheConfig> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("缓存列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("缓存编码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("缓存描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("同步标记");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				CacheConfig model = (CacheConfig) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCodeName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getAsyncEtag());
				row.createCell((short) 3).setCellValue(model.getStatus());
				cell = row.createCell((short) 4);
				cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(model.getCreateTime()));
			}
			
			/*第六步，将文件存到指定位置*/
			try
			{
				String fileName = UUID.randomUUID() + ".xls";
				String tempFilePath = path + "\\" + fileName;
				
				FileOutputStream fout = new FileOutputStream(tempFilePath);
				wb.write(fout);
				fout.close();
				
				filePath = tempFilePath;
			}
			catch (Exception e)
			{
				System.out.print(e.getMessage());
			}
		}
		
		return filePath;
	}
	
	public String getCacheConfigByCodeName(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String codeName = "";
		if(jsonObject.containsKey("codeName")){
			codeName = jsonObject.opt("codeName").toString();
		}
		
		if(codeName != null && !codeName.equals("")){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("codeName", codeName);
			
			List<CacheConfig> cacheConfigs = findForUnPage(params);
			
			if(cacheConfigs != null && cacheConfigs.size() > 0){
				CacheConfig cacheConfig = cacheConfigs.get(0);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", cacheConfig.getAsyncEtag());
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "没有匹配缓存");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
}
