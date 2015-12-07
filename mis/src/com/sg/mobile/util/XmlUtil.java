package com.sg.mobile.util;

import java.io.File;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import com.sg.mobile.entity.MobileRpcParamenter;

public class XmlUtil {
	public static Document getXmlDoc(String fileName){
		Document doc = null;
		
		String filePath = Thread.currentThread().getContextClassLoader().getResource(fileName).getPath().substring(1);
		File file = new File(filePath.replaceAll("%20"," "));
		
		try {
			if(file.exists()){
				SAXReader sr = new SAXReader();
	            doc = sr.read(file);
			}
		} catch (Exception e) {
			// TODO: handle exception
				System.out.println(e.getMessage());
		}
		
		return doc;
	}
	
	public static Document createRpcDoc(String methodName, List<MobileRpcParamenter> paras){
		Document doc = getXmlDoc("/com/sg/mobile/xml/rpc.xml");
		Element rootElement = doc.getRootElement();
		
		Element methodNameElement = DocumentHelper.createElement(QName.get("methodName", rootElement.getNamespace()));
		rootElement.add(methodNameElement);
		methodNameElement.setText(methodName);
		
		Element paramsElement = DocumentHelper.createElement(QName.get("params", rootElement.getNamespace()));
		rootElement.add(paramsElement);
		
		if(paras != null && paras.size() > 0){
			
			for (MobileRpcParamenter mobileRpcParamenter : paras) {
				Element paramElement = DocumentHelper.createElement(QName.get("param", paramsElement.getNamespace()));
				paramsElement.add(paramElement);
				
				Element valueElement = DocumentHelper.createElement(QName.get("value", paramElement.getNamespace()));
				paramElement.add(valueElement);
				
				Element paraTypeElement = DocumentHelper.createElement(QName.get(mobileRpcParamenter.getParaType(), valueElement.getNamespace()));
				valueElement.add(paraTypeElement);
				paraTypeElement.setText(mobileRpcParamenter.getParaValue());
			}
		}
		
		return doc;
	}
}
