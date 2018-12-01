package network;

import java.io.Serializable;

public class NetRoomInfoNot implements Serializable {
	private int maxplayercount;
	
	private boolean playeractive[];
	private String playernick[];
	private int playerteam[];
	
	public NetRoomInfoNot(int maxplayercount) {
		this.maxplayercount = maxplayercount;
		
		playeractive = new boolean[maxplayercount];
		playernick = new String[maxplayercount];
		playerteam = new int[maxplayercount];
	}
	
	public int getMaxPlayerCount() { return maxplayercount; }
	
	public boolean getPlayerActive(int index) { return playeractive[index]; }
	public String getPlayerNick(int index) { return playernick[index]; }
	public int getPlayerTeam(int index) { return playerteam[index]; }
	
	public void setPlayerActive(int index, boolean b) { playeractive[index] = b; }
	public void setPlayerNick(int index, String n) { playernick[index] = n; }
	public void setPlayerTeam(int index, int t) { playerteam[index] = t; }
}
