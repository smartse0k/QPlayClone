package scene;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import main.*;
import network.*;
import server.Server;

public class SceneLogin extends Scene {
	private JLabel label_server;
	private JComboBox<String> combo_server;
	private JLabel label_nick;
	private JTextField text_nick;
	private JButton button_enter;
	private JButton button_mute;
	private JButton button_test;
	
	public SceneLogin(JFrame parent) {
		super(parent);
		
		label_server = new JLabel("서버");
		if( Main.getMain().getServerListExpand() )
			combo_server = new JComboBox<String>(Config.SERVER_LIST_MORE);
		else
			combo_server = new JComboBox<String>(Config.SERVER_LIST);
		label_nick = new JLabel("닉네임");
		text_nick = new JTextField(10);
		button_enter = new JButton("접속");
		button_mute = new JButton("소리 끄기");
		button_test = new JButton("테스트 (개발용)");
		
		button_enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login();
			}
		});
		
		button_mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mute();
			}
		});
		
		button_test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Test();
			}
		});
	}

	public void draw() {
		Container cp = super.parent.getContentPane();
		
		cp.setLayout(null);
		
		label_server.setLocation(375, 500);
		label_server.setSize(200, 25);
		cp.add(label_server);
		
		combo_server.setLocation(425, 500);
		combo_server.setSize(200, 25);
		cp.add(combo_server);
		
		label_nick.setLocation(375, 550);
		label_nick.setSize(200, 25);
		cp.add(label_nick);
		
		text_nick.setLocation(425, 550);
		text_nick.setSize(200, 25);
		cp.add(text_nick);
		
		button_enter.setLocation(375, 600);
		button_enter.setSize(250, 25);
		cp.add(button_enter);
		
		button_mute.setBounds(375, 650, 250, 25);
		cp.add(button_mute);
		
		button_test.setLocation(375, 650);
		button_test.setSize(250, 25);
		cp.add(button_test);
		
		cp.revalidate();
		cp.repaint();
	}
	
	public void Login() {
		SoundManager.getInstance().PlayMouseDown();
		
		if( text_nick.getText().trim().length() == 0 ) {
			JOptionPane.showMessageDialog(null, "닉네임을 입력해주세요.");
			return;
		}
		
		button_enter.setEnabled(false);
		button_enter.setText("접속중...");
		
		String server = null;
		
		switch( combo_server.getSelectedIndex() ) {
		case 0: // 플지컴 서버
			server = Config.SERVER_PLAYGCKOM;
			break;
		case 1: // 서버 입력
			server = JOptionPane.showInputDialog("서버 주소를 입력해주세요.");
			break;
		case 2: // 서버 만들기
			Main.getMain().setServer(new Server());
			server = "127.0.0.1";
			break;
		}
		
		Client c = new Client(server, Config.SERVER_PORT);
		
		if( c.getIsConnected() == false ) {
			JOptionPane.showMessageDialog(null, "서버에 연결이 되지 않습니다. :(");
			
			button_enter.setEnabled(true);
			button_enter.setText("접속");
			
			return;
		}
		
		Main.getMain().setClient(c); // 클라이언트 할당
		
		NetLoginReq packet = new NetLoginReq();
		packet.setNick(text_nick.getText());
		packet.setVersion(Config.GAME_VERSION);
		Main.getMain().getClient().sendPacket(Config.NETWORK_LOGIN_REQ, packet);
	}
	
	public void Test() {
		SoundManager.getInstance().PlayMouseDown();
		
		NetStartGameNot testpacket = new NetStartGameNot();
		testpacket.setGameMode(0);
		testpacket.addPlayerNick("테스트");
		testpacket.addTeam(0);
		testpacket.addStartXY(50, 50);
		testpacket.addPlayerNick("지엠준석");
		testpacket.addTeam(1);
		testpacket.addStartXY(250, 250);
		
		Main.getMain().setNick("테스트");
		Main.getMain().changeScene(Scene.SCENE_AQUAGAME);
		Main.getMain().getSceneAquaGame().setGamePlayer(testpacket);
		Main.getMain().getSceneAquaGame().startGame();
	}
	
	public void Mute() {
		if( button_mute.getText().equals("소리 끄기") ) {
			button_mute.setText("소리 켜기");
			SoundManager.getInstance().setMute(true);
		} else {
			button_mute.setText("소리 끄기");
			SoundManager.getInstance().setMute(false);
		}
	}
}
