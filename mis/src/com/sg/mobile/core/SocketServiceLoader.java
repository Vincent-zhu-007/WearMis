package com.sg.mobile.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SocketServiceLoader implements ServletContextListener {
	/*socket server 线程*/  
    private SocketThread socketThread;
      
    @Override  
    public void contextDestroyed(ServletContextEvent arg0) {  
    	if(socketThread != null && !socketThread.isInterrupted()){  
    		socketThread.closeSocketServer();  
    		socketThread.interrupt();  
    	}  
    }  
  
    @Override  
    public void contextInitialized(ServletContextEvent arg0) {  
    	/*TODO Auto-generated method stub*/  
    	if(socketThread == null){
    		/*新建线程类*/  
    		socketThread = new SocketThread(null);  
    		/*启动线程*/  
    		socketThread.start();  
        }  
    }
}
