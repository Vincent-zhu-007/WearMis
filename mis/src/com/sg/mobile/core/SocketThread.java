package com.sg.mobile.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sg.mobile.util.XmlUtil;

public class SocketThread extends Thread {
	private ServerSocket serverSocket = null;  
    
    public SocketThread(ServerSocket serverScoket){  
    	try {  
    		if(serverSocket == null){
    			int port = getMobileServerSocketConfigPort();
    			
    			this.serverSocket = new ServerSocket(port);
    			System.out.println("socket start"); 
            }
        }catch (Exception e) {  
        	System.out.println("SocketThread创建socket服务出错");  
            e.printStackTrace();
        }
    }  
      
    public void run(){
    	if(serverSocket != null){
    		while(!this.isInterrupted()){  
    			try {  
    				Socket socket = serverSocket.accept();
    				
    				if(socket != null && !socket.isClosed()){     
    					/*处理接受的数据*/
    					socket.setSoTimeout(30000);
    					new SocketOperate(socket).run();
                    }  
                }catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }
    	}     
    }
    
    public void closeSocketServer(){  
    	try {  
    	   if(null!=serverSocket && !serverSocket.isClosed()){  
    		   serverSocket.close();  
    	   }  
    	} catch (IOException e) {  
    	   /*TODO Auto-generated catch block*/  
    	   e.printStackTrace();
    	}  
    }
    
	@SuppressWarnings("rawtypes")
	private int getMobileServerSocketConfigPort(){
		int port = 0;

		Document doc = XmlUtil.getXmlDoc("SocketConfig.xml");
		Element rootElement = doc.getRootElement();
		Iterator rootIter = rootElement.elementIterator();
		
		while (rootIter.hasNext()) {
			Element socketConfigElement = (Element) rootIter.next();
			
			String id = socketConfigElement.attribute("id").getValue();
			
			if(id != null && !id.equals("") && id.equals("MobileServer")){
				String strPort = socketConfigElement.attribute("port").getValue();
				
				if(strPort != null && !strPort.equals("")){
					port = Integer.parseInt(socketConfigElement.attribute("port").getValue());
				}
			}
		}
		
		return port;
	}
}
