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
	private static JFrame gamewindow = null; // 게임 윈도우
	private JLabel label_loading = null;
	private JTextArea textarea_error = null;
	private JScrollPane scroll_error = null;
	
	private Runnable runnable_scene = null;
	private Thread thread_scene = null;
	private int scenestate = -1; // 씬 번호
	private static SceneLogin scenelogin = null;
	private static SceneLobby scenelobby = null;
	private static SceneMakeRoom scenemakeroom = null;
	private static SceneAquaRoom sceneaquaroom = null;
	private static SceneAquaGame sceneaquagame = null;
	
	private static Config config = null; // 옵션
	
	private Client client = null; // 클라이언트
	private String nick;
	
	private int currentroomid;
	
	private Server server = null; // 서버가 되는 경우..
	private boolean server_expand;
	
	// 게임 윈도우 가져오기
	public static JFrame getGameWindow() {
		if( gamewindow == null ) gamewindow = new JFrame();
		return gamewindow;
	}
	
	// 씬 가져오기
	public int getSceneState() { return scenestate; }
	public SceneLogin getSceneLogin() { return scenelogin; } // 로그인
	public SceneLobby getSceneLobby() { return scenelobby; } // 로비
	public SceneMakeRoom getSceneMakeRoom() { return scenemakeroom; } // 방 만들기
	public SceneAquaRoom getSceneAquaRoom() { return sceneaquaroom; } // 아쿠아 방
	public SceneAquaGame getSceneAquaGame() { return sceneaquagame; } // 아쿠아 게임
	
	// 옵션 get
	public static Config getConfig() {
		if( config == null ) config = new Config();
		return config;
	}
	
	// 메인 get
	public static Main getMain() { return main; }
	
	// 클라이언트 get/set
	public Client getClient() { return client; }
	public void setClient(Client c) { client = c; }
	
	public Server getServer() { return server; }
	public void setServer(Server s) { server = s; }
	
	// 닉네임 get/set
	public String getNick() { if( nick == null ) return "닉네임없음"; else return nick; }
	public void setNick(String n) { nick = n; }
	
	// 방 getset
	public int getRoom() { return currentroomid; }
	public void setRoom(int i) { currentroomid = i; }
	
	// 화면 전환
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
		
		System.out.println("화면 그리기 - " + scenestate);
	}
	
	// 초기화
	public void start() {
		System.out.println("Config 초기화...");
		getConfig();
		
		System.out.println("GameWindow 초기화...");
		getGameWindow().getContentPane().setPreferredSize(new Dimension(Config.GAMEWINDOW_WIDTH, Config.GAMEWINDOW_HEIGHT));
		getGameWindow().getContentPane().setLayout(null);
		getGameWindow().setResizable(false);
		getGameWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getGameWindow().setVisible(true);
		getGameWindow().pack();
		getGameWindow().setTitle("큐플레이 클론 " + Config.GAME_VERSION + " by http://lovemomory.blog.me");
		
		label_loading = new JLabel("로딩중입니다....");
		label_loading.setBounds(450, 300, 100, 30);
		getGameWindow().getContentPane().add(label_loading);
		
		System.out.println("Scene 초기화...");
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
	
		setRoom(-1); // 방이 없다고 표시
		changeScene(Scene.SCENE_LOGIN); // 로그인 씬
	}
	
	// 서버 리스트 확장
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
		
		textarea_error.setText("오류가 발생하였습니다.\n\n" + exceptionAsString);
		
		getGameWindow().getContentPane().repaint();
	}
	
	public void showError(String e) {
		getGameWindow().getContentPane().removeAll();
		
		textarea_error = new JTextArea();
		textarea_error.setEditable(false);
		
		scroll_error = new JScrollPane(textarea_error);
		scroll_error.setBounds(25, 25, Config.GAMEWINDOW_WIDTH - 50, Config.GAMEWINDOW_HEIGHT - 50);
		
		getGameWindow().getContentPane().add(scroll_error);
		
		textarea_error.setText("오류가 발생하였습니다.\n\n" + e);
		
		getGameWindow().getContentPane().repaint();
	}
	
	// 완전 시작
	public static void main(String[] args) {		
		main = new Main();
		
		for( int i=0; i<args.length; i++ ) {
			System.out.println(args[i]);
			
			if( args[i].equals("customserver") == true ) {
				main.setServerListExpand();
			}
		}
		
		// 그냥 서버 리스트 확장
		//main.setServerListExpand();
		
		main.start();
		
		SoundManager.getInstance().PlayBGM("res/title_bgm.mp3", true);
	}
}
