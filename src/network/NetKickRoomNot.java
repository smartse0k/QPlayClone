package network;

import java.io.Serializable;

public class NetKickRoomNot implements Serializable {
	public static final int WHY_HOSTQUIT = 1;
	public static final int WHY_KICK = 2;
	
	private int why;
	
	public void setWhy(int w) { why = w; }
	public int getWhy() { return why; }
}
