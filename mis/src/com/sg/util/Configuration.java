package com.sg.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 
 * @author zhe.wang
 *
 */
public class Configuration {
	private Properties propertie;
	private InputStream inputFile;
	private FileOutputStream outputFile;
	
	public Configuration()
	{
		try
		{
			inputFile = Configuration.class.getClassLoader()
					.getResourceAsStream("app.properties");
			propertie = new Properties();
			propertie.load(inputFile);
			inputFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getValue(String key)
	{
		if (propertie.containsKey(key))
		{
			String value = propertie.getProperty(key);// �õ�ĳһ���Ե�ֵ
			return value;
		}
		else
		{
			return "";
		}
	}
	
	public int getIntValue(String key)
	{
		int retValue = -1;
		String value = getValue(key);
		if (!"".equals(value))
		{
			retValue = Integer.parseInt(getValue(key));
		}
		return retValue;
	}
	
	public void setValue(String key, String value)
	{
		propertie.setProperty(key, value);
	}
	
	public void saveFile(String description)
	{
		try
		{
			String fileName = Configuration.class.getClassLoader()
					.getResource("app.properties").getFile();
			outputFile = new FileOutputStream(fileName);
			propertie.store(outputFile, description);
			outputFile.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		
		Configuration sysConfig = new Configuration();
		String rootPath = sysConfig.getValue("uploadPath");// ��ô浽�Ǹ��̵��ļ���
		System.out.println(rootPath);
		sysConfig.setValue("uploadPath", "d:newPath");
		sysConfig.saveFile("");
		
	}
}
