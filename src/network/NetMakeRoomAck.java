package network;

import java.io.Serializable;

import server.Room;

public class NetMakeRoomAck implements Serializable {
	private int ok;
	private int roomid;
	private int roomgamemode;
	
	public void setOk(int o) { ok = o; };
	public void setRoomId(int i) { roomid = i; }
	public void setRoomGameMode(int g) { roomgamemode = g; }
	
	public int getOk() { return ok; }
	public int getRoomId() { return roomid; }
	public int getRoomGameMode() { return roomgamemode; }
}
