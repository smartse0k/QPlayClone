package network;

import java.io.Serializable;

public class NetJoinRoomReq implements Serializable {
	private int roomid;
	
	public void setRoomId(int i) { roomid = i; }
	public int getRoomId() { return roomid; }
}
