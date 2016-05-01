package com.teledef.easevoke.client.nio;

import java.util.Map;

public interface ClientNIO {
	
	public String invoke(String host,int port,Map<String,Object> invokeInfo);
}
