package com.sg.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

public class FileUtil {
	public static String getSaveUrlByStream(String fileName, String dirName, String savePath, InputStream is){
		JSONObject josnObject = new JSONObject();
		
		String saveUrl = "/mis/attached/";
		
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
		
		String message = checkFile(fileExt, dirName, savePath);
		
		if(message.equals("success")){
			/*创建文件夹*/
			savePath += dirName + "/";
			saveUrl += dirName + "/";
			
			File saveDirFile = new File(savePath);
			if (!saveDirFile.exists()) {
				saveDirFile.mkdirs();
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String ymd = sdf.format(new Date());
			savePath += ymd + "/";
			saveUrl += ymd + "/";
			
			File dirFile = new File(savePath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			
			File file = new File(savePath, newFileName);
			OutputStream os = null;
			try{
				os = new FileOutputStream(file);
				
				byte buffer[]=new byte[4*1024];
				
				int len = 0;
				while((len = is.read(buffer)) != -1){
					os.write(buffer,0,len);
				}
				
				os.flush();
				
				saveUrl = saveUrl + newFileName;
				
				josnObject.put("result", "1");
				josnObject.put("data", saveUrl);
			}
			catch(Exception e){
				e.printStackTrace();
				josnObject.put("result", "0");
				josnObject.put("message", e.getMessage());
			}
			finally{
				try{
					os.close();
				}
				catch(Exception e){
					e.printStackTrace();
					josnObject.put("result", "0");
					josnObject.put("message", e.getMessage());
				}
			}
		}else {
			josnObject.put("result", "0");
			josnObject.put("message", message);
		}
		
		return josnObject.toString();
	}
	
	public static String checkFile(String fileExt, String dirName, String savePath){
		String message = "success";
		
		/*检查目录*/
		File uploadDir = new File(savePath);
		
		if(!uploadDir.isDirectory()){
			message = "上传目录不存在";
			return message;
		}
		
		/*检查目录写权限*/
		if(!uploadDir.canWrite()){
			message = "上传目录没有写权限";
			return message;
		}
		
		/*定义允许上传的文件扩展名*/
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,apk,gif,jpg,jpeg,swf,flv,mp3,mp4,3gp,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb,amr,3gp");
		
		if(!extMap.containsKey(dirName)){
			message = "目录名不正确";
			return message;
		}
		
		/*检查扩展名*/
		if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
			message = "上传文件扩展名是不允许的扩展名,只允许"+extMap.get(dirName)+"格式";
			return message;
		}
		
		return message;
	}
	
	public static InputStream byteTOInputStream(byte[] in){
        ByteArrayInputStream is = new ByteArrayInputStream(in);  
        return is;  
    }
	
	/*base64字符串转化成inputstream*/  
    public static InputStream strImageToInputStream(String strImage) {
    	/*图像数据为空*/
        if (strImage == null)   
            return null;  
        BASE64Decoder decoder = new BASE64Decoder();  
        try {  
        	/*Base64解码*/  
            byte[] b = decoder.decodeBuffer(strImage);  
            for(int i=0;i<b.length;++i) {
            	/*调整异常数据*/
                if(b[i]<0) {  
                    b[i]+=256;  
                }  
            }
            
            InputStream is = FileUtil.byteTOInputStream(b);
            
            return is;
        } catch (Exception e) {  
            return null;  
        }  
    }
    
    public static String readTxtFile(String filePath) {
    	String result = "";
    	
        try {
        	String encoding="GBK";
        	File file = new File(filePath);
        	//判断文件是否存在
        	if(file.isFile() && file.exists()){ 
        		InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
        		BufferedReader bufferedReader = new BufferedReader(read);
        		String lineTxt = null;
        		while((lineTxt = bufferedReader.readLine()) != null){
        			result += lineTxt + ",";
        		}
        		read.close();
        } else {
        	System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        
        return result;
    }
}
