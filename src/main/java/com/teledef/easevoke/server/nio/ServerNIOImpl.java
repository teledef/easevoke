package com.teledef.easevoke.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.google.protobuf.InvalidProtocolBufferException;
import com.teledef.easevoke.examples.Person;
import com.teledef.easevoke.examples.Person.Person_example;

public class ServerNIOImpl implements ServerNIO {

	public void start(int port)   {
		
		try {		
		    Selector selector = Selector.open();
		    
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking( false );			
			ServerSocket ss = ssc.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			ss.bind(address);
		
			SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT );
	
			while (true) {
				
				int num = selector.select();
	
				Set selectedKeys = selector.selectedKeys();
				Iterator it = selectedKeys.iterator();
	
				while (it.hasNext()) {
					
					SelectionKey selectionkey = (SelectionKey)it.next();
					System.out.println(selectionkey.readyOps());
					if ((selectionkey.readyOps() & SelectionKey.OP_ACCEPT)
							== SelectionKey.OP_ACCEPT) {
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionkey.channel();
						SocketChannel socketchannel = serverSocketChannel.accept();
				     
						socketchannel.configureBlocking( false );
						SelectionKey newKey = socketchannel.register( selector, SelectionKey.OP_READ );

						it.remove();
										     
					} else if ((selectionkey.readyOps() & SelectionKey.OP_READ)
							== SelectionKey.OP_READ) {
						
						SocketChannel socketchannel = (SocketChannel)selectionkey.channel();
						if(socketchannel.isConnected()) {
	
							String response = (String)nioRequest(socketchannel);
							nioResponse(socketchannel,response);
							
							it.remove();
							//一定要取消对该连接通道的监听(selectionkey)并关闭该连接通道(socketchannel),不然会进入死循环
							selectionkey.cancel();
							socketchannel.close();
					    }		         
					}   
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object nioRequest(SocketChannel socketchannel) {
		try {
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);			 		
			while (true) {
				readBuffer.clear();
			 	int r = socketchannel.read(readBuffer);		
			 	Charset latin1 = Charset.forName( "utf-8" );
		     	CharsetDecoder decoder = latin1.newDecoder();
		     	CharBuffer cb = decoder.decode( readBuffer );
		     	System.out.println(r+cb.toString());
		     	if (r <= 0) {
		     		break;		     		
		     	}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "helloworld() eof";
	}
	
	public void nioResponse(SocketChannel socketchannel,String response) {
		if (response != null && response.trim().length() > 0) {
			
			Person_example person = Person_example.newBuilder().setId(1)
									.setName("ymh")
									.setEmail("114010343@qq.com").build();
			byte[] bytes = person.toByteArray();
/*******************************************************************************************/
			
//		    byte[] bytes = response.getBytes();
			
		    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		    writeBuffer.put(bytes);
		    writeBuffer.flip();
		    try {
				socketchannel.write(writeBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
