package chatroomsend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import chatroomutil.ChatRoomUtil;

public class Send {
	Socket socket;
    public Send() {
        super();
        socket = new Socket();
    }
    public int connect(String IP, int port, int timeout) {//连接
        try {
            socket.connect(new InetSocketAddress(IP, port), timeout);
        } catch (IOException e) {
            return -1;
        }
        return 0;
    }
    public String send(String message) {
        PrintWriter writer=ChatRoomUtil.putMesgToSocket(socket, message);
        StringBuffer buffer=new StringBuffer();
        BufferedReader reader=ChatRoomUtil.getMsgFromSocket(socket,buffer);
        String rMessage = buffer.toString();
        ChatRoomUtil.closeSocket(socket,reader,writer);
        return rMessage;
    }
}
