package chatroomserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import chatroomsend.Send;
import chatroomutil.ChatRoomUtil;
import chatroomutil.Server;

public class ServerServer extends Server{

	Hashtable<String, User> onlineTable = new Hashtable<String, User>();// 在线用户集合
	Hashtable<String, User> offlineTable = new Hashtable<String, User>();// 离线用户集合
	
	// 用户内部类
	class User {
	      String id;
	      String pwd;
	      String IP;
	      int port;
	      User(String id, String pwd) {
	    	  this.id = id;
	    	  this.pwd = pwd;
         }
	 }
	// 发送进程内部类
	    class sendThread implements Runnable {
	        String mesg;
	        String IP;
	        int port;
	        sendThread(String IP, int port, String mesg) {
	            this.IP = IP;
	            this.port = port;
	            this.mesg = mesg;
	        }
	        @Override
	        public void run() {
	            Send sendClient = new Send();
	            sendClient.connect(IP, port, 1000);
	            sendClient.send(mesg);
	        }
	    };
	    public ServerServer(int port) throws IOException {
	        init(port);
	        initUsers();
	        listen();
	    }
	    private int init(int port) throws IOException {
	        try {
	            sSocket = new ServerSocket(port);//绑定指定端口
	        } catch (IOException e) {
	            ChatRoomUtil.showErrorBox("服务器初始化失败，无法绑定端口。");
	            System.exit(-1);
	        }
	        return port;
	    }
	    private int initUsers() {//读取Users文件，初始化offlineTable
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader("Users.txt"));
	        } catch (FileNotFoundException e1) {
	            ChatRoomUtil.showErrorBox("无法找到User文件");
	            System.exit(-1);
	        }
	        String line;
	        String[] fa;
	        try {
	            while ((line = reader.readLine()) != null) {
	                fa = line.split(":", 2);
	                User f = new User(fa[0], fa[1]);
	                offlineTable.put(fa[0], f);
	            }
	            reader.close();
	        } catch (IOException e) {
	            ChatRoomUtil.showErrorBox("读取User文件错误");
	            System.exit(-1);
	        }
	        return 0;
	    }
	    private int saveNewUser(String id, String pwd) {//将新用户写入Users文件最后
	        try {
	            RandomAccessFile file = new RandomAccessFile("Users.txt", "rw");
	            file.seek(file.length());
	            file.writeBytes(id + ":" + pwd + "\r\n");
	            file.close();
	        } catch (FileNotFoundException e) {
	            ChatRoomUtil.showErrorBox("写入User文件错误");
	            System.exit(-1);
	        } catch (IOException e) {
	            ChatRoomUtil.showErrorBox("写入User文件错误");
	            System.exit(-1);
	        }
	        return 0;
	    }
	    String loginCheck(String id, String pwd, String IP, int port) {//检查登录用户的合法性
	        System.out.println("logi check");
	        if (onlineTable.containsKey(id))
	            return "alreadyonline";//该用户已在线
	        User f = offlineTable.get(id);
	        if (f == null)
	            return "nothisid";//无此用户
	        if (f.pwd.compareTo(pwd) == 0) {
	            oneUserOnline(id, IP, port);
	            sendOnlinesToNewOnlineUser(id, IP, port);
	            sendNewOnlineUserToOnlines(id);
	            return "yes";//合法
	        } else {
	            return "wrong";//密码错误
	        }
	    }
	    int oneUserOnline(String id, String IP, int port) {//一个新用户上线
	        User f = offlineTable.get(id);
	        offlineTable.remove(id);
	        onlineTable.put(id, f);
	        f.IP = IP;
	        f.port = port;
	        return 0;
	    }
	    int sendNewOnlineUserToOnlines(String id) {//给所有在线用户发送新上线的用户的id
	        Enumeration<User> fs = onlineTable.elements();
	        while (fs.hasMoreElements()) {
	            User f = fs.nextElement();
	            if (f.id.compareTo(id) != 0) {
	                Thread hThread = new Thread(
	                        new sendThread(f.IP, f.port, "newf" + id));
	                hThread.start();
	            }
	        }
	        return 0;
	    }
	    int sendMesg(String mesg) {//向所有在线用户转发一条消息
	        Enumeration<User> fs = onlineTable.elements();
	        while (fs.hasMoreElements()) {
	            User f = fs.nextElement();
	            Thread hThread = new Thread(
	                    new sendThread(f.IP, f.port, "mesg" + mesg));
	            hThread.start();
	        }
	        return 0;
	    }
	    int sendChat(String id, String mesg) {//向一个用户发送一条一对一聊天的消息
	        User f = onlineTable.get(id);
	        Thread hThread = new Thread(
	                new sendThread(f.IP, f.port, "chat" + mesg));
	        hThread.start();
	        return 0;
	    }
	    String newRegisUser(String id, String pwd) {//有新注册的用户
	        if (onlineTable.containsKey(id) || offlineTable.containsKey(id)) {
	            return "no";
	        }
	        offlineTable.put(id, new User(id, pwd));
	        saveNewUser(id, pwd);
	        return "yes";
	    }
	    int sendOnlinesToNewOnlineUser(String id, String IP, int port) {//给新上线的用户发送所有已在线用户的id
	        if (onlineTable.isEmpty() || onlineTable.size() == 1) {
	            return 0;
	        }
	        StringBuffer strBuf = new StringBuffer();
	        Enumeration<User> fs = onlineTable.elements();
	        while (fs.hasMoreElements()) {
	            User f = fs.nextElement();
	            if (f.id.compareTo(id) != 0) {
	                strBuf.append(f.id);
	                strBuf.append(";");
	            }
	        }
	        String str = strBuf.toString();
	        Thread hThread = new Thread(new sendThread(IP, port, "newf" + str));
	        hThread.start();
	        return 0;
	    }
	    int oneUserOffline(String id) {//有一个用户下线，将其下线消息发送给所有在线用户
	        Enumeration<User> fs = onlineTable.elements();
	        while (fs.hasMoreElements()) {
	            User f = fs.nextElement();
	            if (f.id.compareTo(id) == 0) {
	                onlineTable.remove(id);
	                offlineTable.put(id, f);
	            } else {
	                Thread hThread = new Thread(
	                        new sendThread(f.IP, f.port, "offl" + id));
	                hThread.start();
	            }
	        }
	        return 0;
	    }
	    protected  String handle(Socket ots,String rMessage){
	        System.out.println("handle");
	        if (rMessage.startsWith("regi")) {//注册
	            rMessage = rMessage.substring("regi".length());
	            String id = rMessage.substring(0, rMessage.indexOf(','));
	            String pwd = rMessage.substring(rMessage.indexOf(',') + 1);
	            return newRegisUser(id, pwd);
	        }
	        if (rMessage.startsWith("logi")) {//登录
	            System.out.println("logi");
	            rMessage = rMessage.substring("logi".length());
	            String id = rMessage.substring(0, rMessage.indexOf(','));
	            String pwd = rMessage.substring(rMessage.indexOf(',') + 1,
	                    rMessage.lastIndexOf(','));
	            String portstr = rMessage.substring(rMessage.lastIndexOf(',') + 1);
	            int port = new Integer(portstr);
	            String IP = ots.getInetAddress().getHostAddress();
	            return loginCheck(id, pwd, IP, port);
	        }
	        if (rMessage.startsWith("mesg")) {//聊天室消息
	            String mesg = rMessage.substring(("mesg").length());
	            sendMesg(mesg);
	            return "getm";
	        }
	        if (rMessage.startsWith("chat")) {//一对一消息
	            String chat = rMessage.substring(("chat").length());
	            String id = chat.substring(0, chat.indexOf(':'));
	            String mesg = chat.substring(chat.indexOf(':') + 1);
	            sendChat(id, mesg);
	            return "getm";
	        }
	        if (rMessage.startsWith("offl")) {//下线
	            String id = rMessage.substring(("offl").length());
	            oneUserOffline(id);
	            return "getm";
	        }
	        return "getm";
	    }

	    public static void main(String[] args) {
	        try {
	            new ServerServer(65142);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
