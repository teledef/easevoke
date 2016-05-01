package com.teledef.easevoke.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

import javax.swing.text.Element;

/**
 * 发布为easevoke服务的注解
 * 
 * @author minghao yang
 *
 * 
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasevokeService {
	
	//服务发布者的姓名
	String servicePublisher() default "unkown_publisher";
	
	//服务描述
	String serviceDescription() default "unkown_description";
	
	//服务创建日期
	String serviceDate() default "0000-00-00";
	
}
