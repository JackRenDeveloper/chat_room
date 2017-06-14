package chatroomclient;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chatroomsend.Send;
import chatroomutil.ChatRoomUtil;
public class RegisBox extends JFrame implements ActionListener {
    private static final long serialVersionUID = 5795626909275119718L;
    String serverIP;
    int serverPort;
    int myServerPort;
    JLabel id_label = new JLabel();
    JLabel pwd_label = new JLabel();
    JLabel pwdCheck_label = new JLabel();
    JLabel num_label = new JLabel();
    JLabel firstName_label = new JLabel();
    JLabel lastName_label = new JLabel();
    JLabel age_label = new JLabel();
    JLabel sex_label = new JLabel();
    JTextField id_text = new JTextField();
    JPasswordField pwd_text = new JPasswordField();
    JPasswordField pwdCheck_text = new JPasswordField();
    JTextField num_text = new JTextField();
    JTextField firstName_text = new JTextField();
    JTextField lastName_text = new JTextField();
    JTextField age_text = new JTextField();
    JComboBox sex_box = new JComboBox();
    JButton cancel = new JButton();
    JButton regis = new JButton();
    public RegisBox(String serverIP, int serverPort, int myServerPort) {
        super();
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.myServerPort = myServerPort;
        setLayout(null);
        setTitle("注册");
        id_label.setBounds(60, 23, 50, 20);
        pwd_label.setBounds(60, 53, 50, 20);
        pwdCheck_label.setBounds(50, 83, 70, 20);
        num_label.setBounds(60, 113, 50, 20);
        firstName_label.setBounds(60, 143, 50, 20);
        lastName_label.setBounds(60, 173, 50, 20);
        age_label.setBounds(60, 203, 50, 20);
        sex_label.setBounds(60, 233, 50, 20);
        id_text.setBounds(130, 20, 200, 26);
        pwd_text.setBounds(130, 50, 200, 26);
        pwdCheck_text.setBounds(130, 80, 200, 26);
        num_text.setBounds(130, 110, 200, 26);
        firstName_text.setBounds(130, 140, 200, 26);
        lastName_text.setBounds(130, 170, 200, 26);
        age_text.setBounds(130, 200, 200, 26);
        sex_box.setBounds(130, 230, 200, 26);
        regis.setBounds(190, 270, 70, 30);
        cancel.setBounds(270, 270, 70, 30);
        id_label.setText("账号");
        pwd_label.setText("密码");
        pwdCheck_label.setText("密码确认");
        num_label.setText("编号");
        firstName_label.setText("名");
        lastName_label.setText("姓");
        age_label.setText("年龄");
        sex_label.setText("性别");
        sex_box.addItem("男");
        sex_box.addItem("女");
        regis.setText("提交");
        cancel.setText("取消");
        regis.setBackground(Color.LIGHT_GRAY);
        cancel.setBackground(Color.LIGHT_GRAY);
        sex_box.setBackground(Color.LIGHT_GRAY);
        regis.addActionListener(this);
        cancel.addActionListener(this);
        this.add(id_label);
        this.add(pwd_label);
        this.add(pwdCheck_label);
        this.add(num_label);
        this.add(firstName_label);
        this.add(lastName_label);
        this.add(age_label);
        this.add(sex_label);
        this.add(id_text);
        this.add(pwd_text);
        this.add(pwdCheck_text);
        this.add(num_text);
        this.add(firstName_text);
        this.add(lastName_text);
        this.add(age_text);
        this.add(sex_box);
        this.add(regis);
        this.add(cancel);
        setBounds(200, 200, 500, 400);
        validate();
        setResizable(false);
        setVisible(true);
    }
    boolean regis(String serverIP, int serverPort, String id, String pwd,
            int myServerPort) {
        Send sendClient = new Send();
        sendClient.connect(serverIP, serverPort, 1000);
        if (sendClient.send("regi" + id + "," + pwd).compareTo("yes") == 0) {
            return true;
        }
        return false;
    }
    boolean check() {
        String id = id_text.getText();
        String pwd = String.valueOf(pwd_text.getPassword());
        String pwdc = String.valueOf(pwdCheck_text.getPassword());
        String fName = firstName_text.getText();
        String lName = lastName_text.getText();
        String age = age_text.getText();
        String num = num_text.getText();
        String sex = sex_box.getSelectedItem().toString();
        if (id.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("账号不能为空");
            return false;
        }
        if (pwd.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("密码不能为空");
            return false;
        }
        if (pwdc.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("请确认密码");
            return false;
        }
        if (pwd.compareTo(pwdc) != 0) {
            ChatRoomUtil.showErrorBox("两次输入的密码不符");
            return false;
        }
        if (num.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("编号不能为空");
            return false;
        }
        if (fName.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("名不能为空");
            return false;
        }
        if (lName.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("姓不能为空");
            return false;
        }
        if (age.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("年龄不能为空");
            return false;
        }
        if (sex.compareTo("") == 0) {
            ChatRoomUtil.showErrorBox("性别不能为空");
            return false;
        }
        if (regis(serverIP, serverPort, id, pwd, myServerPort)) {
            dispose();
        } else {
            ChatRoomUtil.showErrorBox("注册失败。该用户已存在");
            return false;
        }
        return true;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object t = e.getSource();
        if (e.getSource().getClass() == JButton.class) {
            JButton button = (JButton) (t);
            if (button.getText().compareTo("提交") == 0) {
                check();
            }
            if (button.getText().compareTo("取消") == 0) {
                dispose();
            }
        }
    }
}