package com.teledef.easevoke.client.nio;
import java.io.FileInputStream;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.teledef.easevoke.examples.Person;
import com.teledef.easevoke.examples.Person.Person_example;

public class ClientNIOImpl implements ClientNIO {

	int RECONNECT_TIME = 10;
	int SELECT_EXPIRED_TIME = 2000;
	
	public static void main(String args[]) {
		ClientNIO clientnio = new ClientNIOImpl();
		clientnio.invoke("localhost",6666,null);
	}

public String invoke(String host,int port,Map<String,Object> invokeInfo) {
		
		try {
			
		    SocketChannel socketChannel = SocketChannel.open();
		    socketChannel.configureBlocking(false);
		    //If this channel is in non-blocking mode then an invocation of this method initiates a non-blocking connection operation.
		    //If the connection is established immediately, as can happen with a local connection, 
		    //then this method returns true. 
		    //Otherwise this method returns false and the connection operation must later be completed by invoking the finishConnect method.
		    // 如果SocketChannel在非阻塞模式下,会立刻返回,一般情况下都是返回false,
		    // 所以后面一定要使用finishConnect()方法去完成连接,否则无法正确完成通道的连接去进行读写操作
		    socketChannel.connect(new InetSocketAddress("localhost", port));  
			
		    Selector selector = Selector.open();		    		
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_CONNECT);
						
			int reconnectTime = 0;
			while (reconnectTime < RECONNECT_TIME ) {

				reconnectTime++;
				int num = selector.select(SELECT_EXPIRED_TIME);

	
				Set selectedKeys = selector.selectedKeys();
				Iterator it = selectedKeys.iterator();
	
				while (it.hasNext()) {
										
					SelectionKey selectionkey = (SelectionKey)it.next();

					if ((selectionkey.readyOps() & SelectionKey.OP_CONNECT)
							== SelectionKey.OP_CONNECT && selectionkey.isConnectable()) { 
						
						socketChannel = (SocketChannel)selectionkey.channel();

						if(socketChannel.finishConnect()) {
							//覆盖掉上一次的SelectionKey.OP_CONNECT 不然而且会重复进入OP_CONNECT事件
							SelectionKey selectionKey = socketChannel.register( selector, SelectionKey.OP_WRITE );
							it.remove();
						}
						
										     
					} else if ((selectionkey.readyOps() & SelectionKey.OP_WRITE)
							== SelectionKey.OP_WRITE && selectionkey.isWritable()) {
						
						socketChannel = (SocketChannel)selectionkey.channel();
							
						nioRequest(socketChannel,"THIS IS NIO REQUEST FROM CLIENT");
						//覆盖掉上一次的SelectionKey.OP_WRITE 不然而且会重复进入OP_WRITE事件
						SelectionKey selectionKey = socketChannel.register( selector, SelectionKey.OP_READ );
							
						it.remove();
					    
						
					} else if ((selectionkey.readyOps() & SelectionKey.OP_READ)
							== SelectionKey.OP_READ && selectionkey.isReadable()) {
						
						socketChannel = (SocketChannel)selectionkey.channel();
						if(socketChannel.isConnected()) {
							
							nioResponse(socketChannel);
							
							it.remove();
							//一定要取消对该连接通道的监听(selectionkey)并关闭该连接通道(socketchannel),不然会进入死循环
							selectionkey.cancel();
							socketChannel.close();
					    }						
					}   
				}
			}
			socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Hello This message from NIO client!";
	}
	
	public Object nioRequest(SocketChannel socketchannel,String requestInfo) {
		
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);	
			byte[] bytes = requestInfo.getBytes();
			    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			    writeBuffer.put(bytes);
			    writeBuffer.flip();
			    try {
			    	while(writeBuffer.hasRemaining()) {
			    		socketchannel.write(writeBuffer);
			    	}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		return "helloworld() eof";
	}
	
	public void nioResponse(SocketChannel socketchannel) {
		
		try {
			
			while (true) {
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				readBuffer.clear();
			 	int r = socketchannel.read(readBuffer);
			 	
/*******************************************************************************************/			 	
		     	if (r <= 0) {	     		
		     		break;		     		
	     	    }
		     	byte[] bytes = new byte[r];
		     	readBuffer.flip();
			 	readBuffer.get(bytes,0,r);
			 	Person_example person = Person_example.parseFrom(bytes);
			 	System.out.println( person.getId()+person.getName()+person.getEmail());
/*******************************************************************************************/			 	
//			 	Charset latin1 = Charset.forName( "utf-8" );
//		     	CharsetDecoder decoder = latin1.newDecoder();
//		     	CharBuffer cb = decoder.decode( readBuffer );
//		     	if (r <= 0) {
//		     		System.out.println("THIS IS THE CLIENT GETTING INFO FROM SERVER"+cb);
//		     		break;		     		
//		     	}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
