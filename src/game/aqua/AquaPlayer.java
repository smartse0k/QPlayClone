package game.aqua;

import main.Config;
import main.Main;
import network.NetAquaShootNot;
import scene.SceneAquaGame;

public class AquaPlayer {
	private boolean active;
	private String nick;
	private int team;
	
	private int device_x;
	private int device_y;
	private int device_hp;
	
	private boolean dir_up;
	private boolean dir_down;
	private boolean dir_left;
	private boolean dir_right;
	
	private final int max_speed = 7;
	private double speed_x;
	private double speed_y;
	
	private final int max_disabled = 100;
	private int disabled_left;
	
	// 어뢰
	private boolean shoot;
	private int shoot_dir;
	private int shoot_type;
	private int shoot_startx;
	private int shoot_starty;
	private int shoot_x;
	private int shoot_y;
	private double shoot_y_acc; // 포물선 전용
	
	public AquaPlayer() {
		active = false;	
	}
	
	public boolean getActive() { return active; }
	public String getNick() { return nick; }
	public int getTeam() { return team; }
	
	public void setActive(boolean b) { active = b; }
	public void setNick(String n) { nick = n; }
	public void setTeam(int t) { team = t; }
	
	public void init() {
		active = false;
		nick = "";
		team = 0;
		device_hp = 9;
		
		dir_up = false;
		dir_down = false;
		dir_left = false;
		dir_right = false;
		
		speed_x = 0;
		speed_y = 0;
		
		disabled_left = 0;
		
		shoot = false;
	}
	
	public void setDeviceHp(int h) { device_hp = h; }
	public int getDeviceHp() { return device_hp; }
	
	public void setUp(boolean b) { dir_up = b; if( dir_down ) dir_down = !b; }
	public void setDown(boolean b) { dir_down = b; if( dir_up ) dir_up = !b; }
	public void setLeft(boolean b) { dir_left = b; if( dir_right ) dir_right = !b; }
	public void setRight(boolean b) { dir_right = b; if( dir_left ) dir_left = !b; }
	
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
	public void setShootDir(int d) { shoot_dir = d; }
	public void setShootType(int t) { shoot_type = t; }
	public void setShootStartX(int x) { shoot_startx = x; }
	public void setShootStartY(int y) { shoot_starty = y; }
	public void setShootX(int x) { shoot_x = x; }
	public void setShootY(int y) { shoot_y = y; }
	public void setShootYAcc(double a) { shoot_y_acc = a; }
	
	public boolean getShoot() { return shoot; }
	public int getShootDir() { return shoot_dir; }
	public int getShootType() { return shoot_type; }
	public int getShootStartX() { return shoot_startx; }
	public int getShootStartY() { return shoot_starty; }
	public int getShootX() { return shoot_x; }
	public int getShootY() { return shoot_y; }
	
	public void setDisabled() { disabled_left = max_disabled; }
	
	public void requestShoot(int dir, int type) {
		if( shoot == true )
			return;
		
		shoot = true;
		
		shoot_dir = dir;
		shoot_type = type;
		
		shoot_startx = device_x + Config.AQUA_DEVICE_WIDTH / 2 - Config.AQUA_BULLET_WIDTH[type] / 2;
		shoot_starty = device_y + Config.AQUA_DEVICE_HEIGHT - Config.AQUA_BULLET_HEIGHT[type] / 2;
		
		if( type == 3 ) {
			shoot_startx = device_x + Config.AQUA_DEVICE_WIDTH / 2 - Config.AQUA_BULLET_WIDTH[type] / 2;
			shoot_starty = device_y + Config.AQUA_BULLET_HEIGHT[type] / 2;
		}
		
		shoot_x = shoot_startx;
		shoot_y = shoot_starty;
		shoot_y_acc = 0;
		
		sendAquaShootNot();
	}
	
	public void sendAquaShootNot() {
		NetAquaShootNot packet = new NetAquaShootNot();
		
		packet.setNick( nick );
		packet.setTeam( team );
		packet.setShoot( shoot );
		packet.setShootDir( shoot_dir );
		packet.setShootType( shoot_type );
		packet.setShootStartX( shoot_startx );
		packet.setShootStartY( shoot_starty );
		//packet.setShootX( shoot_x );
		//packet.setShootY( shoot_y );
		
		Main.getMain().getClient().sendPacket(Config.NETWORK_AQUA_SHOOT_NOT, packet);
	}

	public void nextmove() {
		//System.out.println("nextmove() " + nick + " / " + device_x + " , " + device_y);
		
		if( disabled_left > 0 ) {
			// 감전 상태면 못 움직인다
			disabled_left--;
			speed_x = 0;
			speed_y = 0;
			return;
		} else {
			// 잠수함 이동
			movedevice();
		}
		
		if( shoot ) {
			// 미사일
			moveshoot();
		}
	}
	
	private void movedevice() {
		// 방향에 따라...
		if( dir_up )
			speed_y -= speed_y > 0 ? 0.15 : 0.1;
		
		if( dir_down )
			speed_y += speed_y < 0 ? 0.15 : 0.1;
		
		if( dir_left )
			speed_x -= speed_x > 0 ? 0.15 : 0.1;
		
		if( dir_right )
			speed_x += speed_x < 0 ? 0.15 : 0.1;
		
		// 손을 완전 떼어놓았다
		if( dir_up == false && dir_down == false ) {
			if( speed_y > 0 ) speed_y -= 0.05;
			if( speed_y < 0 ) speed_y += 0.05;
		}
		
		if( dir_left == false && dir_right == false ) {
			if( speed_x > 0 ) speed_x -= 0.05;
			if( speed_x < 0 ) speed_x += 0.05;
		}
		
		// 속도 한계 검사
		if( speed_x < (max_speed * -1) )
			speed_x = max_speed * -1;
		
		if( speed_x > max_speed )
			speed_x = max_speed;
		
		if( speed_y < (max_speed * -1) )
			speed_y = max_speed * -1;
		
		if( speed_y > max_speed )
			speed_y = max_speed;
		
		// 이동
		device_x += speed_x;
		device_y += speed_y;
		
		// 이동 한계 검사 + 속도를 줄여준다
		if( device_x < 0 ) {
			device_x = 0;
			if( speed_x < 0 ) speed_x += Math.abs(speed_x) > 0.3 ? 0.2 : 0.1;
		}
		
		if( device_x > Config.AQUA_MAP_WIDTH - Config.AQUA_DEVICE_WIDTH ) {
			device_x = Config.AQUA_MAP_WIDTH - Config.AQUA_DEVICE_WIDTH;
			if( speed_x > 0 ) speed_x -= Math.abs(speed_x) > 0.3 ? 0.2 : 0.1;
		}
		
		if( device_y < 0 ) {
			device_y = 0;
			if( speed_y < 0 ) speed_y += Math.abs(speed_y) > 0.3 ? 0.2 : 0.1;
		}
		
		if( device_y > Config.AQUA_MAP_HEIGHT - Config.AQUA_DEVICE_HEIGHT ) {
			device_y = Config.AQUA_MAP_HEIGHT - Config.AQUA_DEVICE_HEIGHT;
			if( speed_y > 0 ) speed_y -= Math.abs(speed_y) > 0.3 ? 0.2 : 0.1;
		}
	}
	
	private void moveshoot() {
		// 1,3 어뢰 이동
		if( shoot_type == 0 || shoot_type == 2 )
			shoot_x += shoot_dir * 6;
		
		// 2 어뢰 이동
		if( shoot_type == 1 )
			shoot_x += shoot_dir * 4;
		
		// 3 어뢰 포물선으로..
		if( shoot_type == 3 ) {
			double uplimit_x = shoot_startx + (shoot_dir * 300);

			shoot_x += shoot_dir * 5;
			
			if( shoot_dir == -1 && shoot_x > uplimit_x ) {
				shoot_y_acc -= 0.1;
				shoot_y -= (5 + shoot_y_acc);
			} else if( shoot_dir == -1 ) {
				if( shoot_y_acc < 0 ) shoot_y_acc = 0;
				shoot_y_acc += 0.2;
				shoot_y += (5 + shoot_y_acc);
			}
			
			if( shoot_dir == 1 && shoot_x < uplimit_x ) {
				shoot_y_acc -= 0.1;
				shoot_y -= (5 + shoot_y_acc);
			} else if( shoot_dir == 1 ) {
				if( shoot_y_acc < 0 ) shoot_y_acc = 0;
				shoot_y_acc += 0.2;
				shoot_y += (5 + shoot_y_acc);
			}
		}
		
		// 맵 이탈
		if( shoot_x < -106 || shoot_x > Config.AQUA_MAP_WIDTH + 106 )
			shoot = false;
		if( shoot_y > Config.AQUA_MAP_HEIGHT + 32 )
			shoot = false;
	}
}
