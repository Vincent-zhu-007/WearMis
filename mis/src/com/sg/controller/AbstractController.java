package com.sg.controller;

import com.sg.util.Configuration;

public abstract class AbstractController {
	/**
	 * 返回码：200成功。
	 */
	protected final static String AJAX_SUCCESS_CODE = "200";
	
	/**
	 * 将外网文件路径，替换成内网文件路径
	 * 
	 * @param srcFilePath
	 *            外网转换成内网
	 * @return
	 */
	protected String changeFilePath(String srcFilePath)
	{
		if (srcFilePath != null && !"".equals(srcFilePath))
		{
			Configuration cf = new Configuration();
			String destFilePath = cf.getValue("FtpUrl");
			String netWwork = cf.getValue("HttpPath").substring(0,
					cf.getValue("HttpPath").lastIndexOf(":"));
			if (srcFilePath.lastIndexOf(netWwork) != -1)
			{
				srcFilePath = srcFilePath.replace(
						srcFilePath.substring(0, srcFilePath.lastIndexOf(":")),
						"http://" + destFilePath);
			}
			
		}
		return srcFilePath;
	}
	
	/**
	 * 内网地址ip转外网ip，不加端口号
	 * 
	 * @param srcFilePath
	 * @return
	 */
	protected String intranetTurnExtranetFilePath(String srcFilePath)
	{
		if (srcFilePath != null && !"".equals(srcFilePath))
		{
			Configuration cf = new Configuration();
			String destFilePath = "http://" + cf.getValue("FtpUrl");
			if (srcFilePath.indexOf(destFilePath) != -1)
			{
				String netWwork = cf.getValue("HttpPath").substring(0,
						cf.getValue("HttpPath").lastIndexOf(":"));
				srcFilePath = srcFilePath.replace(
						srcFilePath.substring(0, srcFilePath.lastIndexOf(":")),
						netWwork);
			}
		}
		return srcFilePath;
	}
}
