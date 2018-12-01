package network;

import java.io.Serializable;
import java.util.Vector;

import server.Room;

public class NetRoomListNot implements Serializable {
	private Vector<Integer> id;
	private Vector<Integer> gamemode;
	private Vector<String> title;
	private Vector<Boolean> passwd;
	
	public NetRoomListNot() {
		id = new Vector<Integer>();
		gamemode = new Vector<Integer>();
		title = new Vector<String>();
		passwd = new Vector<Boolean>();
		
		id.clear();
		gamemode.clear();
		title.clear();
		passwd.clear();
	}
	
	public void addRoom(int id, int gamemode, String title, boolean passwd) { 
		this.id.add(id);
		this.gamemode.add(gamemode);
		this.title.add(title);
		this.passwd.add(passwd);
	}
	
	public Vector<Integer> getVecId(){ return id; }
	public Vector<Integer> getVecGamemode(){ return gamemode; }
	public Vector<String> getVecTitle(){ return title; }
	public Vector<Boolean> getVecPasswd(){ return passwd; }
}
