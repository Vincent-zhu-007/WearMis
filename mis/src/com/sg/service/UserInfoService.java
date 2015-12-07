package com.sg.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
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
import com.sg.dao.UserInRoleDao;
import com.sg.dao.UserInfoDao;
import com.sg.entity.UserInfo;

@Service
public class UserInfoService implements BaseEntityManager<UserInfo> {
	@Resource
	private UserInfoDao userInfoDao;
	
	@Resource
	private UserInRoleDao userInRoleDao;
	
	@Transactional
	@Override
	public void create(UserInfo entity) {
		// TODO Auto-generated method stub
		userInfoDao.create(entity);
		userInRoleDao.create(entity);
	}
	
	@Transactional
	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		userInfoDao.delete(id);
		userInRoleDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserInfo> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return userInfoDao.find(params, pageParams);
	}

	@Override
	public UserInfo get(String id) {
		// TODO Auto-generated method stub
		return userInfoDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserInfo> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return userInfoDao.findForUnPage(params);
	}
	
	@Transactional
	@Override
	public void update(UserInfo entity) {
		// TODO Auto-generated method stub
		userInfoDao.update(entity);
		userInRoleDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return userInfoDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<UserInfo> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("用户列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				UserInfo model = (UserInfo) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getUserName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getStatus());
				cell = row.createCell((short) 3);
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
}
