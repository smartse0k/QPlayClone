package scene;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.*;

import game.aqua.AquaPlayer;
import main.AllUtil;
import main.Config;
import main.Main;
import main.SoundManager;
import network.NetChangeTeamReq;
import network.NetEndGameNot;
import network.NetQuitRoomReq;
import network.NetRoomInfoNot;
import network.NetRoomListReq;
import network.NetStartGameReq;

public class SceneAquaRoom extends Scene {
	
	private Image img_bg;
	private JPanel panel_bg;
	
	private AquaPlayer players[];
	private JLabel label_player_nick[];
	private JLabel label_player_team[];
	
	private JTextArea textarea_chat;
	private JScrollPane scroll_chat;
	private JTextField text_chat;
	
	private Runnable runnable_ChatScroll;
	
	private Image img_team[];
	
	private JLabel label_teamselect;
	private JButton button_team[];
	private ChangeTeamListener changeteamlistener;
	
	private JButton button_gamestart;
	private JButton button_quit;

	public SceneAquaRoom(JFrame parent) {
		super(parent);
		
		img_bg = AllUtil.getResImage("res/aqua_room_bg.png");
		panel_bg = new JPanel() {
			
			@Override
			protected void paintComponent(Graphics g) {
				draw_panel(g);
			}
		};
		
		// 아쿠아 플레이어
		players = new AquaPlayer[6];
		for( int i=0; i<6; i++ )
			players[i] = new AquaPlayer();
		
		label_player_nick = new JLabel[6];
		label_player_team = new JLabel[6];
		for( int i=0; i<6; i++ ) {
			label_player_nick[i] = new JLabel("");
			label_player_nick[i].setFont(Config.GULIMCHE12);
			label_player_nick[i].setText("* 접속 대기중 *");
			
			label_player_team[i] = new JLabel("");
		}
		
		// 팀
		label_teamselect = new JLabel("팀선택 ---▶");
		img_team = new Image[6];
		button_team = new JButton[6];
		changeteamlistener = new ChangeTeamListener();
		for( int i=0; i<6; i++ ) {
			img_team[i] = AllUtil.getResImage("res/team" + (i + 1) + ".png");
			button_team[i] = new JButton("팀" + (i + 1));
			button_team[i].setIcon(new ImageIcon(img_team[i]));
			button_team[i].addActionListener(changeteamlistener);
		}
		
		button_gamestart = new JButton("게임시작");
		button_gamestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameStart();
			}
		});
		
		button_quit = new JButton("방나가기");
		button_quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitRoom();
			}
		});
		
		textarea_chat = new JTextArea();
		scroll_chat = new JScrollPane(textarea_chat);
		text_chat = new JTextField();
		
		text_chat.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if( arg0.getKeyCode() == KeyEvent.VK_ENTER ) {
					Main.getMain().getClient().sendChat(text_chat.getText());
					text_chat.setText("");
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		
		runnable_ChatScroll = new Runnable() {
			public void run() {
				scroll_chat.getVerticalScrollBar().setValue(scroll_chat.getVerticalScrollBar().getMaximum());
			}
		};
	}

	public void draw() {
		Container cp = super.parent.getContentPane();
		
		cp.setLayout(null);
		
		panel_bg.setLocation(0, 0);
		panel_bg.setSize(1024, 768);
		panel_bg.setLayout(null);
		cp.add(panel_bg);
		
		label_player_nick[0].setBounds(90, 120, 400, 25);
		label_player_nick[1].setBounds(570, 120, 400, 25);
		label_player_nick[2].setBounds(90, 260, 400, 25);
		label_player_nick[3].setBounds(570, 260, 400, 25);
		label_player_nick[4].setBounds(90, 400, 400, 25);
		label_player_nick[5].setBounds(570, 400, 400, 25);
		panel_bg.add(label_player_nick[0]);
		panel_bg.add(label_player_nick[1]);
		panel_bg.add(label_player_nick[2]);
		panel_bg.add(label_player_nick[3]);
		panel_bg.add(label_player_nick[4]);
		panel_bg.add(label_player_nick[5]);
		
		label_teamselect.setBounds(70, 520, 80, 25);
		button_team[0].setBounds(150, 520, 50, 25);
		button_team[1].setBounds(250, 520, 50, 25);
		button_team[2].setBounds(350, 520, 50, 25);
		button_team[3].setBounds(450, 520, 50, 25);
		button_team[4].setBounds(550, 520, 50, 25);
		button_team[5].setBounds(650, 520, 50, 25);
		panel_bg.add(label_teamselect);
		panel_bg.add(button_team[0]);
		panel_bg.add(button_team[1]);
		panel_bg.add(button_team[2]);
		panel_bg.add(button_team[3]);
		panel_bg.add(button_team[4]);
		panel_bg.add(button_team[5]);
		
		button_gamestart.setBounds(760, 520, 200, 25);
		panel_bg.add(button_gamestart);
		
		button_quit.setBounds(760, 600, 200, 25);
		panel_bg.add(button_quit);
		
		textarea_chat.setFont(Config.GULIMCHE12);
		textarea_chat.setText("");
		textarea_chat.setEditable(false);
		
		scroll_chat.setBounds(50, 560, 650, 160);
		panel_bg.add(scroll_chat);
		
		text_chat.setBounds(50, 730, 650, 25);
		panel_bg.add(text_chat);
		
		cp.revalidate();
		cp.repaint();
	}
	
	private void draw_panel(Graphics g) {
		// 배경 그리기
		g.drawImage(img_bg, 0, 0, 1024, 768, panel_bg);
		
		// 팀 깃발 그리기
		for( int i=0; i<6; i++ ) {
			if( players[i].getActive() == true ) {
				g.drawImage(img_team[ players[i].getTeam() ], getFlagX(i), getFlagY(i), panel_bg);
			}
		}
	}
	
	private int getFlagX(int slot) { if( slot % 2 == 0 ) return 390; else return 870; }
	private int getFlagY(int slot) { switch( slot ) { case 0: case 1: return 120; case 2: case 3: return 260; case 4: case 5: return 400; } return 0; }
	
	public void setRoomInfo(NetRoomInfoNot packet) {
		SoundManager.getInstance().PlayJoinRoom();
		
		for( int i=0; i<packet.getMaxPlayerCount(); i++ ) {
			players[i].setActive( packet.getPlayerActive(i) );
			players[i].setNick( packet.getPlayerNick(i) );
			players[i].setTeam( packet.getPlayerTeam(i) );
			
			if( packet.getPlayerActive(i) == true ) {
				label_player_nick[i].setText( players[i].getNick() );
			} else {
				label_player_nick[i].setText("-");
			}
		}
		
		panel_bg.repaint();
	}

	public void addChatMsg(String nick, String chat) {
		textarea_chat.append("\n" + String.format("%-10s : %s", nick, chat));
		
		textarea_chat.repaint();
		scroll_chat.repaint();
		
		SwingUtilities.invokeLater(runnable_ChatScroll);
	}
	
	private void gameStart() {
		SoundManager.getInstance().PlayMouseDown();
		
		NetStartGameReq packet = new NetStartGameReq();
		Main.getMain().getClient().sendPacket(Config.NETWORK_STARTGAME_REQ, packet);
	}
	
	private void quitRoom() {
		SoundManager.getInstance().PlayMouseDown();
		
		NetQuitRoomReq packet = new NetQuitRoomReq();
		Main.getMain().getClient().sendPacket(Config.NETWORK_QUITROOM_REQ, packet);
		
		Main.getMain().changeScene(Scene.SCENE_LOBBY);
	}
	
	private class ChangeTeamListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			SoundManager.getInstance().PlayMouseDown();
			
			int newteam = -1;
			if( e.getSource() == button_team[0] ) newteam = 0;
			if( e.getSource() == button_team[1] ) newteam = 1;
			if( e.getSource() == button_team[2] ) newteam = 2;
			if( e.getSource() == button_team[3] ) newteam = 3;
			if( e.getSource() == button_team[4] ) newteam = 4;
			if( e.getSource() == button_team[5] ) newteam = 5;
			
			NetChangeTeamReq packet = new NetChangeTeamReq();
			packet.setTeam(newteam);
			Main.getMain().getClient().sendPacket(Config.NETWORK_CHANGETEAM_REQ, packet);
		}
	}

	public void setWinner(NetEndGameNot packet) {
		Vector<String> v = packet.getVecWinPlayer();
		
		boolean isWin = false;
		for( int i=0; i<v.size(); i++ ) {
			if( v.get(i).equals(Main.getMain().getNick()) == true )
				isWin = true;
		}
		
		if( isWin == true ) {
			SoundManager.getInstance().PlaySound(SoundManager.SOUND_AQUA_WIN);
			addChatMsg("*서버*", "이겼습니다!!");
		} else {
			SoundManager.getInstance().PlaySound(SoundManager.SOUND_AQUA_LOSE);
			addChatMsg("*서버*", "졌네요 ㅠ-ㅠ...");
		}
	}
}
