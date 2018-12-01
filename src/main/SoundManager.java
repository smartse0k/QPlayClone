package main;

import java.io.BufferedInputStream;
import java.util.HashMap;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SoundManager {
	public static SoundManager sound = null;
	public static boolean mute = false;
	
	private SoundPlayer player_bgm;
	private Thread thread_bgm;
	
	private SoundPlayer player_effect;
	
	public static SoundManager getInstance() {
		if( sound == null ) {
			sound = new SoundManager();
		}

		return sound;
	}
	
	public static void setMute(boolean b) {
		mute = b;
		
		if( b == true ) {
			getInstance().StopBGM();
		}
		
		if( b == false ) {
			if( getInstance().player_bgm.getUrl() == null )
				return;
			
			getInstance().PlayBGM( getInstance().player_bgm.getUrl(), 
					               getInstance().player_bgm.getLoop()); 
		}
	}
	
	public SoundManager() {
		try {
			player_bgm = new SoundPlayer();
			player_effect = new SoundPlayer();
		} catch (Exception e) {
			Main.getMain().showError("사운드 객체 생성에 실패하였습니다.\n\n"
					+ "라이브러리 파일이 빠졌을 수도 있습니다.");
			System.out.println("사운드 객체 생성 실패");
		}
	}
	
	public static final String BGM_TITLE = "res/title_bgm.mp3";
	public static final String BGM_AQUA = "res/aqua_bgm.mp3";
	
	synchronized public void PlayBGM(String url, boolean loop) {
		// 음소거 상태
		if( mute == true )
			return;
		
		// 같은 음악이면 패스
		if( player_bgm.getUrl().equals(url) == true )
			return;
		
		StopBGM();
		
		player_bgm.setSound( url, loop );
		
		thread_bgm = new Thread(player_bgm);
		thread_bgm.start();
	}
	
	synchronized public void StopBGM() {
		player_bgm.stopSound();
		
		if( thread_bgm != null ) {
			thread_bgm.interrupt();
		}
		
		thread_bgm = null;
	}
	
	public void PlaySound(String url) {
		// 음소거 상태
		if( mute == true )
			return;
				
		player_effect.setSound( url, false );
		
		new Thread(player_effect).start();
	}
	
	public void PlayMouseDown() {
		PlaySound("res/common_mousedn.mp3");
	}
	
	public void PlayJoinRoom() {
		PlaySound("res/room_join.mp3");
	}
	
	public static final String SOUND_AQUA_HIT1 = "res/aqua_hit1.mp3";
	public static final String SOUND_AQUA_HIT2 = "res/aqua_hit2.mp3";
	public static final String SOUND_AQUA_HIT3 = "res/aqua_hit3.mp3";
	public static final String SOUND_AQUA_HIT4 = "res/aqua_hit4.mp3";
	public static final String SOUND_AQUA_WRECK = "res/aqua_wreck.mp3";
	public static final String SOUND_AQUA_START = "res/aqua_start.mp3";
	public static final String SOUND_AQUA_WIN = "res/aqua_win.mp3";
	public static final String SOUND_AQUA_LOSE = "res/aqua_lose.mp3";
}
