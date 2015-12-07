package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sg.dao.CodeDao;
import com.sg.mobile.dao.WebSiteRoleDao;
import com.sg.mobile.entity.WebSiteRole;
import com.sg.service.BaseEntityManager;

@Service
public class WebSiteRoleService implements BaseEntityManager<WebSiteRole> {
	@Resource
	private WebSiteRoleDao webSiteRoleDao;
	
	@Resource
	private CodeDao codeDao;
	
	@Transactional
	@Override
	public void create(WebSiteRole entity) {
		// TODO Auto-generated method stub
		codeDao.create(entity);
		webSiteRoleDao.create(entity);
	}
	
	@Transactional
	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		codeDao.delete(id);
		webSiteRoleDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<WebSiteRole> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return webSiteRoleDao.find(params, pageParams);
	}

	@Override
	public WebSiteRole get(String id) {
		// TODO Auto-generated method stub
		return webSiteRoleDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<WebSiteRole> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return webSiteRoleDao.findForUnPage(params);
	}
	
	@Transactional
	@Override
	public void update(WebSiteRole entity) {
		// TODO Auto-generated method stub
		codeDao.update(entity);
		webSiteRoleDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return webSiteRoleDao.getTotalCount(params);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> findPermissionByRoles(Map params){
		Map<String, Object> roleMap = new HashMap<String, Object>();
		Map<String, Object> permissionMap = new HashMap<String, Object>();
		
		List<WebSiteRole> list = webSiteRoleDao.findPermissionByRoles(params);
		
		if(list != null && list.size() > 0){
			for (WebSiteRole webSiteRole : list) {
				if(!roleMap.containsKey(webSiteRole.getCodeName())){
					roleMap.put(webSiteRole.getCodeName(), webSiteRole.getPermission());
					
					String[] array = webSiteRole.getPermission().split(",");
					for (String str : array) {
						if(!permissionMap.containsKey(str)){
							permissionMap.put(str, str);
						}
					}
				}
			}
		}
		
		return permissionMap;
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<WebSiteRole> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("平台角色列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("角色编码名称");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("角色描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("角色编码类型");
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
				WebSiteRole model = (WebSiteRole) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCodeName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getCategory());
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
	
	public WebSiteRole getWebSiteRoleByCodeName(String codeName){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<WebSiteRole> list = findForUnPage(params);
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;
	}
}
