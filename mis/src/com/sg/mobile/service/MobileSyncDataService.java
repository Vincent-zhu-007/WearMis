package com.sg.mobile.service;

import java.util.List;

import org.dom4j.Document;
import org.springframework.stereotype.Service;

import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileRpcParamenter;
import com.sg.mobile.util.XmlUtil;
import com.sg.util.SendHttpRequestUtil;

@Service
public class MobileSyncDataService {
	public void SyncData(String methodName, List<MobileRpcParamenter> paras, MobileCompany mobileCompany){
		Document doc = XmlUtil.createRpcDoc(methodName, paras);
		
		String rpchost = mobileCompany.getRpcHost();
		String rpcport = mobileCompany.getRpcPort();
		String rpcappName = mobileCompany.getRpcAppName();
		
		String strUrl = rpchost + ":" + rpcport + "/" + rpcappName;
		String xml = doc.asXML();
		
		try {
			SendHttpRequestUtil.sendPost(strUrl, xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
