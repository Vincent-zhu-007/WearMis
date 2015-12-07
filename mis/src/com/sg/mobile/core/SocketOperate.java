package com.sg.mobile.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SocketOperate {
	private Socket socket;  
    
    public SocketOperate(Socket socket) {  
       this.socket = socket;  
    }
    
    public void run() {
        try{
        	InputStream in = socket.getInputStream();
        	 
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            while(true){    
                /*读取客户端发送的信息*/  
                String sendData = "";
                
                byte b[];
				try {
					b = readStream(in);
					
					sendData = new String(b,0,b.length);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					/*e.printStackTrace();*/
					System.out.println("准备关闭socket");  
                    break;
				}
                
                if(sendData.equals("end") || sendData.equals("")){     
                    System.out.println("准备关闭socket");  
                    break;
                }

                if(!sendData.equals("")){
                	System.out.println("客户端发送的数据：" + sendData);
                	
                	byte[] message = messageToBytes("success");
                	
                	String result = new String(message, "GB2312");
                	
                    out.print(result);
                    out.flush();   
                    out.close();  
                }                    
            }
            
            socket.close();
            System.out.println("socket stop.....");
            
        }catch(IOException ex){     
 
        }finally{     
               
        }     
    }
    
	private byte[] readStream(InputStream inStream) throws Exception {  
        int count = 0;  
        while (count == 0) {  
            count = inStream.available();  
        }  
        byte[] b = new byte[count];
        inStream.read(b);  
        return b;  
    }
	
	ByteBuffer _intShifter = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE)
            .order(ByteOrder.LITTLE_ENDIAN);

	public byte[] intToByte(int value) {
		_intShifter.clear();
		_intShifter.putInt(value);      
		return _intShifter.array();
	}

	public int byteToInt(byte[] data) {
		_intShifter.clear();
		_intShifter.put(data, 0, Integer.SIZE / Byte.SIZE);
		_intShifter.flip();
		return _intShifter.getInt();
	}
	
	public byte[] messageToBytes(String message){
		
		byte command = 1;
		byte mainVersion = 1;
		byte secondVersion = 1;
		
    	int messageLength = message.getBytes().length;
    	
    	int totalLength = 7 + messageLength; 
    	
    	byte[] byteLength = intToByte(totalLength);
    	
    	byte[] buffer = new byte[totalLength];
    	
    	//先将长度的4个字节写入到数组中。
    	for (int i = 0; i < byteLength.length; i++) {
    		buffer[i] = byteLength[i];
		}
    	
    	//将CommandHeader写入到数组中
    	buffer[4] = command;
        //将主版本号写入到数组中
        buffer[5] = mainVersion;
        //将次版本号写入到数组中
        buffer[6] = secondVersion;
        
        byte[] byteMessage = message.getBytes();
        for (int i = 0; i < byteMessage.length; i++) {
        	int j = i + 7;
        	
        	buffer[j] = byteMessage[i];
		}
    	
    	return buffer;
	}
}
