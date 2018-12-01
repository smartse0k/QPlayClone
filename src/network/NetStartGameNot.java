package network;

import java.io.Serializable;
import java.util.Vector;

public class NetStartGameNot implements Serializable {
	private int gamemode;
	
	private Vector<String> playernicks;
	private Vector<Integer> teams;
	private Vector<Integer> startX;
	private Vector<Integer> startY;
	
	public void setGameMode(int g) { gamemode = g; }
	public int getGameMode() { return gamemode; }
	
	public void addPlayerNick(String p) { playernicks.add(p); }
	public Vector<String> getPlayerNick(){ return playernicks; }
	
	public void addTeam(int t) { teams.add(t); }
	public Vector<Integer> getTeam(){ return teams; }
	
	public void addStartXY(int x, int y) { startX.add(x); startY.add(y); }
	public Vector<Integer> getStartX(){ return startX; }
	public Vector<Integer> getStartY(){ return startY; }
	
	public NetStartGameNot() {
		playernicks = new Vector<String>();
		teams = new Vector<Integer>();
		startX = new Vector<Integer>();
		startY = new Vector<Integer>();
	}
}
