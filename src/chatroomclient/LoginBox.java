package chatroomclient;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chatroomsend.Send;
import chatroomutil.ChatRoomUtil;

public class LoginBox extends JFrame implements ActionListener, MouseListener {
    private static final long serialVersionUID = -329711894663212488L;
    ClientServer myServer;
    String serverIP;
    int serverPort;
    JLabel l_account = new JLabel();
    JLabel l_password = new JLabel();
    JLabel l_regist = new JLabel();
    JTextField j_account = new JTextField();
    JPasswordField j_password = new JPasswordField();
    JButton submit = new JButton();
    JButton cancel = new JButton();
    JLabel clearAccount = new JLabel();
    JLabel clearPassword = new JLabel();
    public LoginBox(String ip, int port) {
        super("登录");
        try {
            myServer = new ClientServer(serverIP, serverPort);
            Thread myServerThread = new Thread(myServer);
            myServerThread.start();
        } catch (IOException e) {
            ChatRoomUtil.showErrorBox("服务器进程初始化失败，无法绑定端口。");
            System.exit(-1);
        }
        serverIP = ip;
        serverPort = port;
        setLayout(null);
        l_account.setVisible(true);
        l_account.setBounds(80, 40, 50, 30);
        l_account.setText("账号:");
        l_password.setVisible(true);
        l_password.setBounds(80, 80, 50, 30);
        l_password.setText("密码:");
        j_account.setBounds(130, 40, 150, 30);
        j_password.setBounds(130, 80, 150, 30);
        clearAccount.setBounds(280, 42, 26, 26);
        clearPassword.setBounds(280, 82, 26, 26);
        submit.setBounds(100, 130, 80, 30);
        submit.setText("登录");
        cancel.setText("取消");
        clearAccount.setText("X");
        clearPassword.setText("X");
        clearAccount.setOpaque(true);
        clearPassword.setOpaque(true);
        clearAccount.setBackground(Color.LIGHT_GRAY);
        clearPassword.setBackground(Color.LIGHT_GRAY);
        clearAccount.setHorizontalAlignment(JLabel.CENTER);
        clearPassword.setHorizontalAlignment(JLabel.CENTER);
        submit.setBackground(Color.LIGHT_GRAY);
        cancel.setBackground(Color.LIGHT_GRAY);
        submit.addActionListener(this);
        cancel.addActionListener(this);
        cancel.setBounds(190, 130, 80, 30);
        l_regist.setBounds(270, 200, 70, 30);
        l_regist.setText("没有账号?");
        this.add(l_account);
        this.add(l_password);
        this.add(j_account);
        this.add(j_password);
        this.add(submit);
        this.add(cancel);
        this.add(l_regist);
        this.add(clearAccount);
        this.add(clearPassword);
        setBounds(480, 240, 370, 270);
        setVisible(true);
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        validate();
        l_regist.addMouseListener(this);
        clearAccount.addMouseListener(this);
        clearPassword.addMouseListener(this);
    }
    String checkPwd(String id, String pwd) {
        Send sendClient = new Send();
        sendClient.connect(serverIP, serverPort, 1000);
        String ret= sendClient
                .send("logi" + id + "," + pwd + "," + myServer.getMyServerPort());
        if (ret.compareTo("yes") == 0) {
            myServer.chatRoom = new ChatRoom(id, serverIP, serverPort);
            dispose();
        } else if (ret.compareTo("wrong") == 0) {
            ChatRoomUtil.showErrorBox("登录失败，密码有误。");
        } else if (ret.compareTo("alreadyonline") == 0) {
            ChatRoomUtil.showErrorBox("登录失败，该账号已在线");
        } else if (ret.compareTo("nothisid") == 0) {
            ChatRoomUtil.showErrorBox("登录失败，账号不存在。");
            new RegisBox(serverIP, serverPort,
                    myServer.getMyServerPort());
        }
        return ret;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object t = e.getSource();
        if (e.getSource().getClass() == JButton.class) {
            JButton button = (JButton) (t);
            if (button.getText().compareTo("登录") == 0) {
                String id = j_account.getText();
                String pwd = String.valueOf(j_password.getPassword());
                if (id.compareTo("") == 0 || pwd.compareTo("") == 0) {
                    ChatRoomUtil.showErrorBox("账号与密码均不能为空");
                    return;
                }
                checkPwd(id, pwd);
            }
            if (button.getText().compareTo("取消") == 0) {
                dispose();
                System.exit(0);
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(l_regist)) {
            new RegisBox(serverIP, serverPort,
                    myServer.getMyServerPort());
        }
        if (e.getSource().equals(clearAccount)) {
            j_account.setText("");
        }
        if (e.getSource().equals(clearPassword)) {
            j_password.setText("");
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Object x = e.getSource();
        if (x.equals(l_regist)) {
            JLabel l = (JLabel) x;
            l.setForeground(Color.blue);
        }
        if (x.equals(clearAccount) || x.equals(clearPassword)) {
            JLabel l = (JLabel) x;
            l.setBackground(Color.GRAY);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Object x = e.getSource();
        if (x.equals(l_regist)) {
            JLabel l = (JLabel) x;
            l.setForeground(Color.BLACK);
        }
        if (x.equals(clearAccount) || x.equals(clearPassword)) {
            JLabel l = (JLabel) x;
            l.setBackground(Color.LIGHT_GRAY);
        }
    }
    public static void main(String[] args) {
        String serverIP = "127.0.0.1";
        int serverPort = 65142;
        new LoginBox(serverIP, serverPort);
    }
}