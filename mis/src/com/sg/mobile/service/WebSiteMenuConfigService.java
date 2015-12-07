package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.WebSiteMenuConfigDao;
import com.sg.mobile.entity.WebSiteMenuConfig;
import com.sg.service.BaseEntityManager;
import com.sg.util.MemCached;

@Service
public class WebSiteMenuConfigService implements BaseEntityManager<WebSiteMenuConfig> {
	@Resource
	private WebSiteMenuConfigDao webSiteMenuConfigDao;

	@Override
	public void create(WebSiteMenuConfig entity) {
		// TODO Auto-generated method stub
		webSiteMenuConfigDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		webSiteMenuConfigDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<WebSiteMenuConfig> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return webSiteMenuConfigDao.find(params, pageParams);
	}

	@Override
	public WebSiteMenuConfig get(String id) {
		// TODO Auto-generated method stub
		return webSiteMenuConfigDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<WebSiteMenuConfig> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return webSiteMenuConfigDao.findForUnPage(params);
	}

	@Override
	public void update(WebSiteMenuConfig entity) {
		// TODO Auto-generated method stub
		webSiteMenuConfigDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return webSiteMenuConfigDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<WebSiteMenuConfig> GetWebSiteMenuConfigCache() {
		List<WebSiteMenuConfig> list = new ArrayList<WebSiteMenuConfig>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("WebSiteMenuConfig");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("WebSiteMenuConfig").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((WebSiteMenuConfig)JSONObject.toBean(jsonObject, WebSiteMenuConfig.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
		    	params.put("status", "Y");
				list = findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("WebSiteMenuConfig", jsonObject);
			}
		}
		
		return list;
	}
	
	public void ClearWebSiteMenuConfigCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("WebSiteMenuConfig");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("WebSiteMenuConfig", "");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<WebSiteMenuConfig> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("平台权限列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("编码名称");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("是否包含子菜单");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("顺序");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("类型");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("父菜单编码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 6);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				WebSiteMenuConfig model = (WebSiteMenuConfig) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCode());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getHasChildren());
				row.createCell((short) 3).setCellValue(model.getDisplaySort());
				row.createCell((short) 4).setCellValue(model.getLevelCode());
				row.createCell((short) 5).setCellValue(model.getParentCode());
				row.createCell((short) 6).setCellValue(model.getStatus());
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
	
	public Map<String, Object> getLevel1WebSiteMenuConfigCacheMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<WebSiteMenuConfig> list = GetWebSiteMenuConfigCache();
		for (WebSiteMenuConfig entity : list) {
			if(entity.getLevelCode().equals("Level1")) {
				map.put(entity.getCode(), entity.getDescription());
			}
		}
		return map;
	}
	
	public List<Map<String, Object>> getLevel1WebSiteMenuConfig() {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		List<WebSiteMenuConfig> list = GetWebSiteMenuConfigCache();
		for (WebSiteMenuConfig entity : list) {
			if(entity.getLevelCode().equals("Level1")) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", entity.getCode());
				map.put("text", entity.getDescription());
				maps.add(map);
			}
		}
		return maps;
	}
}
