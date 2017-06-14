package chatroomutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {
	protected ServerSocket sSocket;
	public Server() {
		
	}
	
	protected int listen() {
		 while(true) {
			 Socket ots = null;
			 try {
				ots = sSocket.accept();
			} catch (IOException e) {
				ChatRoomUtil.showErrorBox("服务器Accept错误");
				System.exit(-1);
			}
			 Thread hThread = new Thread(new handleThread(ots));
			 hThread.start();
		 }
	}
	
	// 处理消息的进程 的内部类
	class handleThread implements Runnable {

		Socket ots;
		handleThread(Socket ots) {
			this.ots = ots;
		}
		
		@Override
		public void run() {
			handleMessage(ots);
		}
		
	}
	
	protected abstract String handle(Socket ots,String rMessage);//处理函数，输入收到的消息，返回要发送的消息
	
	private String handleMessage(Socket ots) {
		StringBuffer buffer =new StringBuffer();
		BufferedReader reader = ChatRoomUtil.getMsgFromSocket(ots,buffer);//获取消息
		String rMessage = buffer.toString();
		String message=handle(ots,rMessage);//处理消息
		PrintWriter writer=ChatRoomUtil.putMesgToSocket(ots, message);//发送消息
		ChatRoomUtil.closeSocket(ots,reader,writer);//关闭
		return null;
	}
}
