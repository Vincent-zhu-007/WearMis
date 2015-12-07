package com.sg.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
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
import org.dom4j.Document;
import org.springframework.stereotype.Service;
import com.sg.dao.ProvinceDao;
import com.sg.entity.City;
import com.sg.entity.Province;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.util.MemCached;

@Service
public class ProvinceService implements BaseEntityManager<Province> {
	@Resource
	private ProvinceDao provinceDao;
	
	@Resource
	private CityService cityService;

	@Override
	public void create(Province entity) {
		// TODO Auto-generated method stub
		provinceDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		provinceDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Province> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return provinceDao.find(params, pageParams);
	}

	@Override
	public Province get(String id) {
		// TODO Auto-generated method stub
		return provinceDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Province> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return provinceDao.findForUnPage(params);
	}

	@Override
	public void update(Province entity) {
		// TODO Auto-generated method stub
		provinceDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return provinceDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Province> getProvinceCache() {
		List<Province> list = new ArrayList<Province>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("Province");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("Province").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((Province)JSONObject.toBean(jsonObject, Province.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("Province", jsonObject);
			}
		}
		
		return list;
	}
	
	public void clearProvinceCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("Province");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("Province", "");
			}
		}
	}
	
	public List<Map<String, Object>> getProvinceCacheListMap() {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		List<Province> list = getProvinceCache();
		for (Province entity : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", entity.getCodeName());
			map.put("text", entity.getDescription());
			maps.add(map);
		}
		return maps;
	}
	
	public Map<String, Object> getCityCacheMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Province> list = getProvinceCache();
		for (Province entity : list) {
			map.put(entity.getCodeName(), entity.getDescription());
		}
		return map;
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<Province> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("省列表");
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
			cell.setCellValue("顺序");
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
				Province model = (Province) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCodeName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getDisplaySort());
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
	
	public String getPocAreaJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		try {
			JSONArray provinceJsonArray = new JSONArray();
			
			JSONObject jsonObjectData = new JSONObject();
			
			List<Province> provinces = getProvinceCache();
			
			if(provinces != null && provinces.size() > 0){
				for (Province province : provinces) {
					JSONObject provinceJsonObject = new JSONObject();
					
					provinceJsonObject.put("codeName", province.getCodeName());
					provinceJsonObject.put("description", province.getDescription());
					provinceJsonObject.put("displaySort", province.getDisplaySort());
					
					provinceJsonArray.add(provinceJsonObject);
				}
				
				jsonObjectData.put("provinces", provinceJsonArray);
				
				JSONArray cityJsonArray = new JSONArray();
				List<City> citys = cityService.getCityCache();
				
				if(citys != null && citys.size() > 0){
					for (City city : citys) {
						JSONObject cityJsonObject = new JSONObject();
						
						cityJsonObject.put("codeName", city.getCodeName());
						cityJsonObject.put("description", city.getDescription());
						cityJsonObject.put("displaySort", city.getDisplaySort());
						cityJsonObject.put("provinceCodeName", city.getProvinceCodeName());
						
						cityJsonArray.add(cityJsonObject);
					}
				}
				
				jsonObjectData.put("citys", cityJsonArray);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", jsonObjectData);
				
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "没有省级信息");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", e.getMessage());
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
}
