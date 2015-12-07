package com.sg.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;

public class SendHttpRequestUtil {
	@SuppressWarnings("unused")
	public static void sendPost(String strUrl, String xml) throws Exception {
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
        Async async = Async.newInstance().use(threadpool);
        
        Request request = Request.Post(strUrl)
				.addHeader("Content-Type", "text/xml")
		        .useExpectContinue()
		        .version(HttpVersion.HTTP_1_1)
		        .bodyString(xml, ContentType.TEXT_XML);
        
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void completed(Content content) {
				// TODO Auto-generated method stub
				System.out.println(content);
			}

			@Override
			public void failed(Exception e) {
				// TODO Auto-generated method stub
				System.out.println(e.getMessage());
			}
        });
        
        try {
        	/*future.get();*/
            /*System.out.println(future.get());*/
        } finally {
        	threadpool.shutdown();
        }
        
        System.out.println("Done");
    }
	
	@SuppressWarnings("unused")
	public static void sendPostJson(String strUrl, String json) throws Exception {
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
        Async async = Async.newInstance().use(threadpool);
        
        Request request = Request.Post(strUrl)
				.addHeader("Content-Type", "text/json")
		        .useExpectContinue()
		        .version(HttpVersion.HTTP_1_1)
		        .bodyString(json, ContentType.APPLICATION_JSON);
        
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void completed(Content content) {
				// TODO Auto-generated method stub
				System.out.println(content);
			}

			@Override
			public void failed(Exception e) {
				// TODO Auto-generated method stub
				System.out.println(e.getMessage());
			}
        });
        
        try {
        	/*future.get();*/
            /*System.out.println(future.get());*/
        } finally {
        	threadpool.shutdown();
        }
        
        System.out.println("Done");
    }
	
	@SuppressWarnings("unused")
	public static void sendPutXml(String strUrl, String xml, String contentType, String companyCode) throws Exception {
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
        Async async = Async.newInstance().use(threadpool);
        
        Request request = Request.Put(strUrl)
				.addHeader("Content-Type", contentType)
				.addHeader("CompanyCode", companyCode)
		        .useExpectContinue()
		        .version(HttpVersion.HTTP_1_1)
		        .bodyString(xml, ContentType.TEXT_XML);
        
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void completed(Content content) {
				// TODO Auto-generated method stub
				System.out.println(content);
			}

			@Override
			public void failed(Exception e) {
				// TODO Auto-generated method stub
				System.out.println(e.getMessage());
			}
        });
        
        try {
        	/*future.get();*/
            /*System.out.println(future.get());*/
        } finally {
        	threadpool.shutdown();
        }
        
        System.out.println("Done");
    }
	
	@SuppressWarnings("unused")
	public static void sendDeleteXml(String strUrl, String xml, String contentType, String companyCode) throws Exception {
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
        Async async = Async.newInstance().use(threadpool);
        
        Request request = Request.Delete(strUrl)
				.addHeader("Content-Type", contentType)
				.addHeader("CompanyCode", companyCode)
		        .useExpectContinue()
		        .version(HttpVersion.HTTP_1_1)
		        .bodyString(xml, ContentType.TEXT_XML);
        
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void completed(Content content) {
				// TODO Auto-generated method stub
				System.out.println(content);
			}

			@Override
			public void failed(Exception e) {
				// TODO Auto-generated method stub
				System.out.println(e.getMessage());
			}
        });
        
        try {
        	/*future.get();*/
            /*System.out.println(future.get());*/
        } finally {
        	threadpool.shutdown();
        }
        
        System.out.println("Done");
    }
}
