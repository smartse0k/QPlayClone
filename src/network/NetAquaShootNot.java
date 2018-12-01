package network;

import java.io.Serializable;

public class NetAquaShootNot implements Serializable {
	private String nick;

	private int team;
	
	private boolean shoot;
	private int shoot_dir;
	private int shoot_type;
	private int shoot_startx;
	private int shoot_starty;
	private int shoot_x;
	private int shoot_y;

	public String getNick() { return nick; }
	public int getTeam() { return team; }
	
	public void setNick(String n) { nick = n; }
	public void setTeam(int t) { team = t; }
	
	public void setShoot(boolean b) { shoot = b; }
	public void setShootDir(int d) { shoot_dir = d; }
	public void setShootType(int t) { shoot_type = t; }
	public void setShootStartX(int x) { shoot_startx = x; }
	public void setShootStartY(int y) { shoot_starty = y; }
	public void setShootX(int x) { shoot_x = x; }
	public void setShootY(int y) { shoot_y = y; }
	
	public boolean getShoot() { return shoot; }
	public int getShootDir() { return shoot_dir; }
	public int getShootType() { return shoot_type; }
	public int getShootStartX() { return shoot_startx; }
	public int getShootStartY() { return shoot_starty; }
	public int getShootX() { return shoot_x; }
	public int getShootY() { return shoot_y; }
}
