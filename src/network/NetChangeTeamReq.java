package network;

import java.io.Serializable;

public class NetChangeTeamReq implements Serializable {
	private int team;
	
	public void setTeam(int t) { team = t; }
	public int getTeam() { return team; }
}
