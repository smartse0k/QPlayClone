package network;

import java.io.Serializable;

public class NetAquaPlayerNot implements Serializable {
	private String nick;

	private int team;
	
	private int device_x;
	private int device_y;
	private int device_hp;
	
	private boolean dir_up;
	private boolean dir_down;
	private boolean dir_left;
	private boolean dir_right;
	
	private double speed_x;
	private double speed_y;
	
	// ¾î·Ú
	private boolean shoot;
	private int shoot_type;
	private int shoot_startx;
	private int shoot_starty;
	private int shoot_x;
	private int shoot_y;
	
	public String getNick() { return nick; }
	public int getTeam() { return team; }
	
	public void setNick(String n) { nick = n; }
	public void setTeam(int t) { team = t; }
	
	public void setDeviceHp(int h) { device_hp = h; }
	public int getDeviceHp() { return device_hp; }
	
	public void setUp(boolean b) { dir_up = b; }
	public void setDown(boolean b) { dir_down = b; }
	public void setLeft(boolean b) { dir_left = b; }
	public void setRight(boolean b) { dir_right = b;}
	
	public boolean getUp() { return dir_up; }
	public boolean getDown() { return dir_down; }
	public boolean getLeft() { return dir_left; }
	public boolean getRight() { return dir_right; }
	
	public void setDeviceX(int x) { device_x = x; }
	public void setDeviceY(int y) { device_y = y; }
	public int getDeviceX() { return device_x; }
	public int getDeviceY() { return device_y; }
	
	public void setSpeedX(double x) { speed_x = x; }
	public void setSpeedY(double y) { speed_y = y; }
	public double getSpeedX() { return speed_x; }
	public double getSpeedY() { return speed_y; }
	
	public void setShoot(boolean b) { shoot = b; }
	public void setShootType(int t) { shoot_type = t; }
	public void setShootStartX(int x) { shoot_startx = x; }
	public void setShootStartY(int y) { shoot_starty = y; }
	public void setShootX(int x) { shoot_x = x; }
	public void setShootY(int y) { shoot_y = y; }
	
	public boolean getShoot() { return shoot; }
	public int getShootType() { return shoot_type; }
	public int getShootStartX() { return shoot_startx; }
	public int getShootStartY() { return shoot_starty; }
	public int getShootX() { return shoot_x; }
	public int getShootY() { return shoot_y; }
}
