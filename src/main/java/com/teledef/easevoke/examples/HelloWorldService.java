package com.teledef.easevoke.examples;

import com.teledef.easevoke.server.annotation.EasevokeService;

@EasevokeService(servicePublisher="minghao yang",serviceDescription="hello world service",serviceDate="2016-04-30")
public class HelloWorldService {
	public HelloWorldService() {
		System.out.println("hello wolrd!");
	}
}
