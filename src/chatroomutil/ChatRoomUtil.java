package chatroomutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ChatRoomUtil {
	public static String showErrorBox(String message) {//显示错误信息对话框
        JOptionPane.showMessageDialog(null, message, "错误",
                JOptionPane.ERROR_MESSAGE);
        return message;
    }
    //获取消息
    public static BufferedReader getMsgFromSocket(Socket socket,StringBuffer buffer){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("获取Socket输入流错误");
            System.exit(-1);
        }
        try {
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            buffer.deleteCharAt(buffer.length()-1);//去掉最后一个回车符
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("读取Socket输入流错误");
            System.exit(-1);
        }
        try {
            socket.shutdownInput();
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("关闭Socket输入时发生错误");
            System.exit(-1);
        }
        return reader; 
    }
    //发送消息
    public static PrintWriter putMesgToSocket(Socket socket,String message){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("获取Socket输出流错误");
            System.exit(-1);
        }
        writer.println(message);
        writer.flush();
        try {
            socket.shutdownOutput();
        } catch (IOException e1) {
            ChatRoomUtil.showErrorBox("关闭Socket输出时发生错误");
            System.exit(-1);
        }
        return writer;
    }
    //关闭socket、输入流、输出流
    public static void closeSocket(Socket socket,BufferedReader reader,PrintWriter writer ){
        try {
            reader.close();
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("关闭Scoket输入流时发生错误");
            System.exit(-1);
        }
        writer.close();
        try {
            socket.close();
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("关闭Scoket输出流时发生错误");
            System.exit(-1);
        }
    }
}
