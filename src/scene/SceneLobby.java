package scene;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;

import main.*;
import network.*;
import server.Room;

public class SceneLobby extends Scene {
	private JLabel label_roomlist;
	private Vector<String> vec_roomlist_str;
	private JList<String> list_roomlist;
	private JScrollPane scroll_roomlist;
	
	private JButton button_refreshroom;
	private JButton button_joinroom;
	private JButton button_makeroom;
	
	private JLabel label_userlist;
	private Vector<String> vec_userlist_str;
	private JList<String> list_userlist;
	private JScrollPane scroll_userlist;
	private Vector<Integer> vec_roomid;
	private Vector<Integer> vec_roomgamemode;
	private Vector<String> vec_roomtitle;
	private Vector<Boolean> vec_roompasswd;
	
	private JTextArea textarea_chat;
	private JScrollPane scroll_chat;
	private JTextField text_chat;
	
	private Runnable runnable_ChatScroll;
	private Runnable runnable_ListUser;
	private Runnable runnable_ListRoom;

	public SceneLobby(JFrame parent) {
		super(parent);
		
		label_roomlist = new JLabel("< 방 >");
		vec_roomlist_str = new Vector<String>();
		list_roomlist = new JList<String>(vec_roomlist_str);
		scroll_roomlist = new JScrollPane(list_roomlist);
		vec_roomid = new Vector<Integer>();
		vec_roomgamemode = new Vector<Integer>();
		vec_roomtitle = new Vector<String>();
		vec_roompasswd = new Vector<Boolean>();
		
		button_refreshroom = new JButton("새로고침");
		button_joinroom = new JButton("방 입장");
		button_makeroom = new JButton("방 만들기");
		
		label_userlist = new JLabel("< 플레이어 >");
		vec_userlist_str = new Vector<String>();
		vec_userlist_str.clear();
		list_userlist = new JList<String>(vec_userlist_str);
		scroll_userlist = new JScrollPane(list_userlist);
		
		textarea_chat = new JTextArea();
		scroll_chat = new JScrollPane(textarea_chat);
		text_chat = new JTextField();
		
		button_refreshroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SoundManager.getInstance().PlayMouseDown();
				Main.getMain().getClient().sendPacket(Config.NETWORK_ROOMLIST_REQ, new NetRoomListReq());
			}
		});
		
		button_joinroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinRoom();
			}
		});
		
		button_makeroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeRoom();
			}
		});
		
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
		
		runnable_ListUser = new Runnable() {
			public void run() {
				list_userlist.updateUI();
				list_userlist.repaint();
			}
		};
		
		runnable_ListRoom = new Runnable() {
			public void run() {
				list_roomlist.updateUI();
				list_roomlist.repaint();
			}
		};
	}

	public void draw() {
		Container cp = super.parent.getContentPane();
		
		cp.setLayout(null);
		
		label_roomlist.setLocation(30, 20);
		label_roomlist.setSize(200, 25);
		cp.add(label_roomlist);
		
		scroll_roomlist.setLocation(30, 50);
		scroll_roomlist.setSize(500, 350);
		cp.add(scroll_roomlist);
		
		button_refreshroom.setLocation(30, 425);
		button_refreshroom.setSize(150, 25);
		cp.add(button_refreshroom);
		
		button_joinroom.setLocation(200, 425);
		button_joinroom.setSize(150, 25);
		cp.add(button_joinroom);
		
		button_makeroom.setLocation(370, 425);
		button_makeroom.setSize(150, 25);
		cp.add(button_makeroom);
		
		label_userlist.setLocation(600, 20);
		label_userlist.setSize(200, 25);
		cp.add(label_userlist);
		
		scroll_userlist.setLocation(600, 50);
		scroll_userlist.setSize(200, 400);
		cp.add(scroll_userlist);
		
		textarea_chat.setFont(Config.GULIMCHE12);
		textarea_chat.setText("< 공지 > 큐플레이 클론입니다!");
		textarea_chat.setEditable(false);
		
		if( Main.getMain().getServer() != null ) {
			textarea_chat.setText("< 공지 > 큐플레이 클론입니다!");
		}
		
		scroll_chat.setLocation(30, 500);
		scroll_chat.setSize(500, 200);
		cp.add(scroll_chat);
		
		text_chat.setLocation(30, 710);
		text_chat.setSize(500, 25);
		cp.add(text_chat);
		
		vec_roomlist_str.add("불러오는 중...");
		vec_userlist_str.add("불러오는 중...");
		
		cp.revalidate();
		cp.repaint();
	}
	
	public void addChatMsg(String nick, String chat) {
		textarea_chat.append("\n" + String.format("%-10s : %s", nick, chat));
		
		textarea_chat.repaint();
		scroll_chat.repaint();
		
		SwingUtilities.invokeLater(runnable_ChatScroll);
	}
	
	public void setRoomList(NetRoomListNot packet) {
		System.out.println("SceneLobby.setRoomList()");
		
		vec_roomlist_str.clear();
		
		for( int i=0; i<packet.getVecId().size(); i++ ) {
			vec_roomid = packet.getVecId();
			vec_roomgamemode = packet.getVecGamemode();
			vec_roomtitle = packet.getVecTitle();
			vec_roompasswd = packet.getVecPasswd();
			
			int id = vec_roomid.get(i);
			String game = Config.GAMEMODE[vec_roomgamemode.get(i)];
			String title = vec_roomtitle.get(i);
			String passwd = vec_roompasswd.get(i) == true ? "비밀" : "공개";
			
			vec_roomlist_str.add(String.format("[%02d] [%s] [%s] %s", id, game, passwd, title));
		}
		
		SwingUtilities.invokeLater(runnable_ListRoom);
	}
	
	public void setPlayerList(NetPlayerListNot packet) {
		System.out.println("SceneLobby.setPlayerList()");
		
		vec_userlist_str.clear();
		
		for( int i=0; i<packet.getVectorNick().size(); i++ ) {
			String temp;
			String nick = packet.getVectorNick().get(i);
			String location = packet.getVectorLocation().get(i);
			
			temp = String.format("%-10s(%s)", nick, location);
			vec_userlist_str.add(temp);
		}
		
		SwingUtilities.invokeLater(runnable_ListUser);
	}
	
	public void joinRoom() {
		SoundManager.getInstance().PlayMouseDown();
		
		int index = list_roomlist.getSelectedIndex();
		
		if( index == -1 )
			return;
		
		NetJoinRoomReq packet = new NetJoinRoomReq();
		packet.setRoomId( vec_roomid.get(index) );
		Main.getMain().getClient().sendPacket(Config.NETWORK_JOINROOM_REQ, packet);
	}
	
	public void makeRoom() {
		SoundManager.getInstance().PlayMouseDown();
		
		Main.getMain().changeScene(SCENE_MAKEROOM);
	}
}
