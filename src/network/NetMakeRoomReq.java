package network;

import java.io.Serializable;

public class NetMakeRoomReq implements Serializable {
	private int gamemode;
	private String title;
	private String passwd;

	public void setGamemode(int g) { gamemode = g; }
	public void setTitle(String t) { title = t; }
	public void setPasswd(String p) { passwd = p; }
	
	public int getGamemode() { return gamemode; }
	public String getTitle() { return title; }
	public String getPasswd() { return passwd; }
}
