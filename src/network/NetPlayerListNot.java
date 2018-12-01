package network;

import java.io.Serializable;
import java.util.Vector;

public class NetPlayerListNot implements Serializable {
	private Vector<String> player_nick;
	private Vector<String> player_location;
	
	public NetPlayerListNot() {
		player_nick = new Vector<String>();
		player_location = new Vector<String>();
		clearNick();
		clearLocation();
	}
	
	public Vector<String> getVectorNick(){ return player_nick; }
	public Vector<String> getVectorLocation(){ return player_location; }
	
	public void clearNick() { player_nick.clear(); }
	public void clearLocation() { player_location.clear(); }
	
	public void add(String nick, String location) {
		player_nick.add(nick);
		player_location.add(location);
	}
}
