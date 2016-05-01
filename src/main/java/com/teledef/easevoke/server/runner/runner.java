package com.teledef.easevoke.server.runner;

import com.teledef.easevoke.examples.HelloWorldService;
import com.teledef.easevoke.server.annotation.EasevokeService;
import com.teledef.easevoke.server.annotation.EasevokeServiceHandler;
import com.teledef.easevoke.server.nio.ServerNIO;
import com.teledef.easevoke.server.nio.ServerNIOImpl;

public class runner {
	
	public static void main(String[] args) {
		
		new EasevokeServiceHandler("com.teledef.easevoke.examples.HelloWorldService");
		ServerNIO serverNIO = new ServerNIOImpl();
		serverNIO.start(6666);
	}
}

