package com.teledef.easevoke.server.annotation;

public class EasevokeServiceHandler {

	public EasevokeServiceHandler(String className) {
		
		Class<?> requestedClass = null;
		try {
			requestedClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//判断是不是被注解的类
		requestedClass.isAnnotationPresent(EasevokeService.class);
		
	}
}
