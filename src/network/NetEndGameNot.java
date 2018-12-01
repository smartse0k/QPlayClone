package network;

import java.io.Serializable;
import java.util.Vector;

public class NetEndGameNot implements Serializable {
	private Vector<String> nick_winplayer;
	
	public NetEndGameNot() {
		nick_winplayer = new Vector<String>();
	}
	
	public void addWin(String nick) { nick_winplayer.add(nick); }
	public Vector<String> getVecWinPlayer(){ return nick_winplayer; }
}
