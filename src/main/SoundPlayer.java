package main;

import java.io.BufferedInputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SoundPlayer implements Runnable {
	String url;
	BufferedInputStream bis;
	private Player player;
	private boolean loop;

	public SoundPlayer() {
		url = "";
		bis = null;
		player = null;
		loop = false;
	}
	
	public String getUrl() { return url; }
	public boolean getLoop() { return loop; }
	
	public void setSound(String url, boolean loop) {
		this.url = url;
		this.loop = loop;
	}
	
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	public void stopSound() {
		loop = false;
		if( player != null )
			player.close();
	}
	
	public void run() {
		do {
			try {
				bis = new BufferedInputStream( Main.getMain().getClass().getClassLoader().getResourceAsStream(url) );
				
				player = new Player(bis);
				player.play();
				
				player = null;
				bis = null;
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
		}while( loop );
	}
}
