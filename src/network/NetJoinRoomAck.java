package network;

import java.io.Serializable;

public class NetJoinRoomAck implements Serializable {	
	private int ok;
	private int gamemode;
	
	public void setOk(int o) { ok = o; }
	public int getOk() { return ok; }
	
	public void setGamemode(int g) { gamemode = g; }
	public int getGamemode() { return gamemode; }
}
