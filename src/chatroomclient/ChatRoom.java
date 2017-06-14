package chatroomclient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import chatroomsend.Send;
import chatroomutil.ChatRoomUtil;

public class ChatRoom extends JFrame implements ActionListener, MouseListener {
    private static final long serialVersionUID = -9016166784223701159L;
    String myId;
    String serverIP;
    int serverPort;
    JLabel chatRoomLabel = new JLabel();
    JLabel onlineLabel = new JLabel();
    JTextArea chatArea = new JTextArea();
    JPanel onlineArea = new JPanel();
    JTextArea sendArea = new JTextArea(5, 20);
    JButton sendButton = new JButton();
    JScrollPane chatScroll = new JScrollPane(chatArea);
    JScrollPane onlineScroll = new JScrollPane(onlineArea);
    JScrollPane sendScroll = new JScrollPane(sendArea);
    JPanel chatPanel = new JPanel();
    JPanel onlinePanel = new JPanel();
    JPanel sendPanel = new JPanel();
    JPanel chatAndOnlinePanel = new JPanel();
    HashSet<String> friends = new HashSet<String>();
    Hashtable<String, ChatDialog> chatDialogs = new Hashtable<String, ChatDialog>();
    BorderLayout mainLayout = new BorderLayout();
    BorderLayout chatLayout = new BorderLayout();
    BorderLayout onlineLayout = new BorderLayout();
    BorderLayout sendLayout = new BorderLayout();
    BorderLayout chatAndOnlineLayout = new BorderLayout();
    FlowLayout onlineAreaLayout = new FlowLayout();
    public ChatRoom(String id, String serverIP, int port) {
        super("聊天室   " + id);
        this.myId = id;
        this.serverIP = serverIP;
        this.serverPort = port;
        mainLayout.setVgap(5);
        sendLayout.setHgap(10);
        chatAndOnlineLayout.setHgap(15);
        onlineAreaLayout.setVgap(1);
        this.setLayout(mainLayout);
        chatPanel.setLayout(chatLayout);
        onlinePanel.setLayout(onlineLayout);
        sendPanel.setLayout(sendLayout);
        chatAndOnlinePanel.setLayout(chatAndOnlineLayout);
        chatArea.setLineWrap(true);
        sendArea.setLineWrap(true);
        onlineArea.setLayout(onlineAreaLayout);
        onlineArea.setPreferredSize(new Dimension(130, 200));
        sendButton.setText("   发送    ");
        sendButton.setBackground(Color.LIGHT_GRAY);
        sendButton.addActionListener(this);
        onlineLabel.setText("在线好友(点击可开始聊天)");
        chatRoomLabel.setText("聊天");
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(230, 230, 230));
        chatPanel.add(chatRoomLabel, BorderLayout.NORTH);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        onlinePanel.add(onlineLabel, BorderLayout.NORTH);
        onlinePanel.add(onlineScroll, BorderLayout.CENTER);
        sendPanel.add(sendScroll, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);
        chatAndOnlinePanel.add(chatPanel, BorderLayout.CENTER);
        chatAndOnlinePanel.add(onlinePanel, BorderLayout.EAST);
        this.add(sendPanel, BorderLayout.SOUTH);
        this.add(chatAndOnlinePanel, BorderLayout.CENTER);
        friends.add(myId);
        this.setBounds(200, 200, 600, 650);
        setVisible(true);
        reFreshOnlineArea();
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {//点击关闭按钮时发送下线消息
                sendOffLine();
                System.exit(0);
            }
        });
        sendArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                String str = sendArea.getText();
                if (str.length() > 5000) {
                    ChatRoomUtil.showErrorBox("字数过多，超出限制。");
                }
            }
        });
        new java.util.Timer().schedule(new TimerTask() {//定时刷新界面
            public void run() {
                repaint();
            }
        }, 400, 400);
    }
    int reFreshOnlineArea() {//刷新当前在线用户
        int size = friends.size();
        onlineArea.removeAll();
        Iterator<String> it = friends.iterator();
        while (it.hasNext()) {
            String str = it.next();
            String id = str;
            JLabel label = new JLabel(id);
            label.addMouseListener(this);
            label.setPreferredSize(new Dimension(130, 20));
            label.setBackground(Color.GRAY);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setOpaque(true);
            onlineArea.add(label);
        }
        onlineArea.setPreferredSize(new Dimension(130, size * 22));
        this.repaint();
        this.validate();
        return 0;
    }
    int sendOffLine() {//发送下线消息
        Send sendClient = new Send();
        sendClient.connect(serverIP, serverPort, 1000);
        sendClient.send("offl" + myId);
        return 0;
    }
    public int addFriend(String[] fs) {//添加上线的用户
        for (int i = 0; i < fs.length; i++) {
            friends.add(fs[i]);
        }
        reFreshOnlineArea();
        return 0;
    }
    public int removeFriend(String friendID) {//移除一个用户（已下线）
        friends.remove(friendID);
        reFreshOnlineArea();
        return 0;
    }
    public int addRecord(String mesg) {//添加聊天记录
        chatArea.append(mesg + "\n");
        chatArea.setCaretPosition(chatArea.getText().length());
        this.validate();
        return 0;
    }
    public int dialogAddRecord(String id, String mesg) {//给一对一聊天增加聊天记录
        if (chatDialogs.containsKey(id) == false) {
            chatDialogs.put(id,
                    new ChatDialog(this, myId, id, serverIP, serverPort));
        }
        chatDialogs.get(id).addRecord(mesg);
        return 0;
    }
    int removeDialog(String id) {//移除一对一聊天
        chatDialogs.remove(id);
        return 0;
    }
    private String sendMessage(String mesg) {//发送群聊消息
        Send sendClient = new Send();
        sendClient.connect(serverIP, serverPort, 1000);
        String getm = sendClient.send("mesg" + myId + ":  \n" + mesg);
        return getm;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass() == JButton.class) {
            sendMessage(sendArea.getText());
            sendArea.setText("");
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().getClass() == JLabel.class) {
            JLabel l = (JLabel) e.getSource();
            if (l.getText().compareTo(myId) == 0) {
                return;
            }
            if (chatDialogs.containsKey(l.getText())) {
                return;
            }
            chatDialogs.put(l.getText(), new ChatDialog(this, myId, l.getText(),
                    serverIP, serverPort));
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource().getClass() == JLabel.class) {
            JLabel l = (JLabel) e.getSource();
            l.setBackground(Color.LIGHT_GRAY);
            l.setForeground(Color.BLACK);
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource().getClass() == JLabel.class) {
            JLabel l = (JLabel) e.getSource();
            l.setBackground(Color.GRAY);
            l.setForeground(Color.WHITE);
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
}