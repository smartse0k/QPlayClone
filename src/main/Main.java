package main;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.*;

import network.Client;
import scene.*;
import server.Room;
import server.Server;

public class Main {
	private static Main main = null;
	private static JFrame gamewindow = null; // ���� ������
	private JLabel label_loading = null;
	private JTextArea textarea_error = null;
	private JScrollPane scroll_error = null;
	
	private Runnable runnable_scene = null;
	private Thread thread_scene = null;
	private int scenestate = -1; // �� ��ȣ
	private static SceneLogin scenelogin = null;
	private static SceneLobby scenelobby = null;
	private static SceneMakeRoom scenemakeroom = null;
	private static SceneAquaRoom sceneaquaroom = null;
	private static SceneAquaGame sceneaquagame = null;
	
	private static Config config = null; // �ɼ�
	
	private Client client = null; // Ŭ���̾�Ʈ
	private String nick;
	
	private int currentroomid;
	
	private Server server = null; // ������ �Ǵ� ���..
	private boolean server_expand;
	
	// ���� ������ ��������
	public static JFrame getGameWindow() {
		if( gamewindow == null ) gamewindow = new JFrame();
		return gamewindow;
	}
	
	// �� ��������
	public int getSceneState() { return scenestate; }
	public SceneLogin getSceneLogin() { return scenelogin; } // �α���
	public SceneLobby getSceneLobby() { return scenelobby; } // �κ�
	public SceneMakeRoom getSceneMakeRoom() { return scenemakeroom; } // �� �����
	public SceneAquaRoom getSceneAquaRoom() { return sceneaquaroom; } // ����� ��
	public SceneAquaGame getSceneAquaGame() { return sceneaquagame; } // ����� ����
	
	// �ɼ� get
	public static Config getConfig() {
		if( config == null ) config = new Config();
		return config;
	}
	
	// ���� get
	public static Main getMain() { return main; }
	
	// Ŭ���̾�Ʈ get/set
	public Client getClient() { return client; }
	public void setClient(Client c) { client = c; }
	
	public Server getServer() { return server; }
	public void setServer(Server s) { server = s; }
	
	// �г��� get/set
	public String getNick() { if( nick == null ) return "�г��Ӿ���"; else return nick; }
	public void setNick(String n) { nick = n; }
	
	// �� getset
	public int getRoom() { return currentroomid; }
	public void setRoom(int i) { currentroomid = i; }
	
	// ȭ�� ��ȯ
	public void changeScene(int scene) {
		if( scenestate != scene )
			getGameWindow().getContentPane().removeAll();
		
		scenestate = scene;
		
		switch(scenestate) {
		case Scene.SCENE_LOGIN:
			SoundManager.getInstance().PlayBGM(SoundManager.BGM_TITLE, true);
			getSceneLogin().draw();
			break;
		case Scene.SCENE_LOBBY:
			SoundManager.getInstance().PlayBGM(SoundManager.BGM_TITLE, true);
			getSceneLobby().draw();
			break;
		case Scene.SCENE_MAKEROOM:
			getSceneMakeRoom().draw();
			break;
		case Scene.SCENE_AQUAROOM:
			SoundManager.getInstance().StopBGM();
			getSceneAquaRoom().draw();
			break;
		case Scene.SCENE_AQUAGAME:
			SoundManager.getInstance().PlayBGM(SoundManager.BGM_AQUA, true);
			getSceneAquaGame().draw();
			break;
		}
		
		System.out.println("ȭ�� �׸��� - " + scenestate);
	}
	
	// �ʱ�ȭ
	public void start() {
		System.out.println("Config �ʱ�ȭ...");
		getConfig();
		
		System.out.println("GameWindow �ʱ�ȭ...");
		getGameWindow().getContentPane().setPreferredSize(new Dimension(Config.GAMEWINDOW_WIDTH, Config.GAMEWINDOW_HEIGHT));
		getGameWindow().getContentPane().setLayout(null);
		getGameWindow().setResizable(false);
		getGameWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getGameWindow().setVisible(true);
		getGameWindow().pack();
		getGameWindow().setTitle("ť�÷��� Ŭ�� " + Config.GAME_VERSION + " by http://lovemomory.blog.me");
		
		label_loading = new JLabel("�ε����Դϴ�....");
		label_loading.setBounds(450, 300, 100, 30);
		getGameWindow().getContentPane().add(label_loading);
		
		System.out.println("Scene �ʱ�ȭ...");
		try {
			runnable_scene = new SceneCanvas();
			thread_scene = new Thread(runnable_scene);
			thread_scene.start();
			scenelogin = new SceneLogin(getGameWindow());
			scenelobby = new SceneLobby(getGameWindow());
			scenemakeroom = new SceneMakeRoom(getGameWindow());
			sceneaquaroom = new SceneAquaRoom(getGameWindow());
			sceneaquagame = new SceneAquaGame(getGameWindow());
		} catch (Exception e) {
			showError(e);
			return;
		}
	
		setRoom(-1); // ���� ���ٰ� ǥ��
		changeScene(Scene.SCENE_LOGIN); // �α��� ��
	}
	
	// ���� ����Ʈ Ȯ��
	public void setServerListExpand() { server_expand = true; }
	public boolean getServerListExpand() { return server_expand; }
	
	public void showError(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		
		getGameWindow().getContentPane().removeAll();
		
		textarea_error = new JTextArea();
		textarea_error.setEditable(false);
		
		scroll_error = new JScrollPane(textarea_error);
		scroll_error.setBounds(25, 25, Config.GAMEWINDOW_WIDTH - 50, Config.GAMEWINDOW_HEIGHT - 50);
		
		getGameWindow().getContentPane().add(scroll_error);
		
		textarea_error.setText("������ �߻��Ͽ����ϴ�.\n\n" + exceptionAsString);
		
		getGameWindow().getContentPane().repaint();
	}
	
	public void showError(String e) {
		getGameWindow().getContentPane().removeAll();
		
		textarea_error = new JTextArea();
		textarea_error.setEditable(false);
		
		scroll_error = new JScrollPane(textarea_error);
		scroll_error.setBounds(25, 25, Config.GAMEWINDOW_WIDTH - 50, Config.GAMEWINDOW_HEIGHT - 50);
		
		getGameWindow().getContentPane().add(scroll_error);
		
		textarea_error.setText("������ �߻��Ͽ����ϴ�.\n\n" + e);
		
		getGameWindow().getContentPane().repaint();
	}
	
	// ���� ����
	public static void main(String[] args) {		
		main = new Main();
		
		for( int i=0; i<args.length; i++ ) {
			System.out.println(args[i]);
			
			if( args[i].equals("customserver") == true ) {
				main.setServerListExpand();
			}
		}
		
		// �׳� ���� ����Ʈ Ȯ��
		//main.setServerListExpand();
		
		main.start();
		
		SoundManager.getInstance().PlayBGM("res/title_bgm.mp3", true);
	}
}
