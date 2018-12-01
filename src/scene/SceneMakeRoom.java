package scene;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import main.Config;
import main.Main;
import main.SoundManager;
import network.NetMakeRoomReq;

public class SceneMakeRoom extends Scene {
	private JLabel label_gamemode;
	private JComboBox<String> combo_gamemode;
	private JLabel label_title;
	private JTextField text_title;
	private JLabel label_passwd;
	private JTextField text_passwd;
	private JButton button_done;
	private JButton button_cancel;
	
	public SceneMakeRoom(JFrame parent) {
		super(parent);
		
		label_gamemode = new JLabel("게임");
		combo_gamemode = new JComboBox<String>(Config.GAMEMODE);
		label_title = new JLabel("제목");
		text_title = new JTextField(10);
		label_passwd = new JLabel("비번");
		text_passwd = new JTextField(10);
		button_done = new JButton("만들기");
		button_cancel = new JButton("취소");
		
		button_done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeRoom();
			}
		});
		
		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SoundManager.getInstance().PlayMouseDown();
				Main.getMain().changeScene(Scene.SCENE_LOBBY);
			}
		});
	}

	public void draw() {
		Container cp = super.parent.getContentPane();
		
		cp.setLayout(null);
		
		button_done.setEnabled(true);
		button_cancel.setEnabled(true);
		combo_gamemode.setSelectedIndex(0);
		text_title.setText("즐거운 플지컴~");
		text_passwd.setText("");
		
		label_gamemode.setLocation(375, 250);
		label_gamemode.setSize(200, 25);
		cp.add(label_gamemode);
		
		combo_gamemode.setLocation(425, 250);
		combo_gamemode.setSize(200, 25);
		cp.add(combo_gamemode);
		
		label_title.setLocation(375, 300);
		label_title.setSize(200, 25);
		cp.add(label_title);
		
		text_title.setLocation(425, 300);
		text_title.setSize(200, 25);
		cp.add(text_title);
		
		label_passwd.setLocation(375, 350);
		label_passwd.setSize(200, 25);
		cp.add(label_passwd);
		
		text_passwd.setLocation(425, 350);
		text_passwd.setSize(200, 25);
		cp.add(text_passwd);
		
		button_done.setLocation(375, 400);
		button_done.setSize(250, 25);
		cp.add(button_done);
		
		button_cancel.setLocation(375, 450);
		button_cancel.setSize(250, 25);
		cp.add(button_cancel);
		
		cp.revalidate();
		cp.repaint();
	}
	
	public void makeRoom() {
		SoundManager.getInstance().PlayMouseDown();
		
		button_done.setEnabled(false);
		button_cancel.setEnabled(false);
		
		NetMakeRoomReq packet = new NetMakeRoomReq();
		
		packet.setGamemode( combo_gamemode.getSelectedIndex() );
		packet.setTitle( text_title.getText() );
		packet.setPasswd( text_passwd.getText() );
		
		Main.getMain().getClient().sendPacket(Config.NETWORK_MAKEROOM_REQ, packet);
	}
}
