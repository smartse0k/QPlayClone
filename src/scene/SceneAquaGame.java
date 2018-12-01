package scene;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import game.aqua.*;
import main.AllUtil;
import main.Config;
import main.Main;
import main.SoundManager;
import network.*;

public class SceneAquaGame extends Scene {
	private AquaPlayer[] player;
	private int myslot;
	
	private GameScreen gamescreen;
	private BufferedImage bimg_gamecanvas;
	private Image img_gamescreen;
	private Graphics g_gamescreen;
	
	private AquaKeyListener aquakeylistener;
	private boolean[] wasKeyPressed;
	
	private BufferedImage bimg_map;
	private BufferedImage[] bimg_device;
	private BufferedImage[] bimg_water;
	private BufferedImage[] bimg_bullet_l;
	private BufferedImage[] bimg_bullet_r;
	
	private double last_sendmils;
	
	// ���� ���
	public static final int GAMESTATE_WAIT = 0;
	public static final int GAMESTATE_PLAYING = 1;
	public static final int GAMESTATE_ENDING = 2;
	private int game_state;
	
	public int getGameState() {
		return game_state;
	}
	
	public void NextGameFrame() {
		// ����� �̵�
		for( int i=0; i<6; i++ ) {
			if( player[i].getActive() == false )
				continue;
			
			player[i].nextmove();
		}
		
		// ��� 0.5�ʿ� �ѹ��� ������ �����ش� (�ܼ��ϰ� Ű�� ������ ���װ� �ֳ���)
		if( last_sendmils + 500 <= System.currentTimeMillis() ) {
			last_sendmils = System.currentTimeMillis();
			sendAquaPlayer();
		}
		
		// �̻��� �浹 �˻�
		checkHit();
		
		// ���� ��ũ�� ����
		gamescreen.repaint();
	}
	
	public void checkHit() {
		// �̻���
		for( int i=0; i<6; i++ ) {
			if( player[i].getActive() == false )
				continue;
			
			if( player[i].getShoot() == false )
				continue;
			
			int mtype = player[i].getShootType();
			int mteam = player[i].getTeam();
			int mx = player[i].getShootX();
			int my = player[i].getShootY();
			
			// ������� �ҷ��ͼ� �˻�
			for( int jj=0; jj<6; jj++ ) {
				if( player[jj].getActive() == false )
					continue;
				
				int team = player[jj].getTeam();
				int dx = player[jj].getDeviceX();
				int dy = player[jj].getDeviceY();
				
				// ���� ���̸� �浹�˻縦 ���� �ʴ´�.
				if( mteam == team )
					continue;
				
				// ���� ������� �浹�˻� ���� �ʴ´�.
				if( player[jj].getDeviceHp() <= 0 )
					continue;
				
				boolean isHit = false;

				int pad = 20; // ����
				
				if( mx + Config.AQUA_BULLET_WIDTH[mtype] - pad > dx && mx < dx + Config.AQUA_DEVICE_WIDTH + pad )
					if( my + Config.AQUA_BULLET_HEIGHT[mtype] - pad > dy && my < dy + Config.AQUA_DEVICE_HEIGHT + pad )
						isHit = true;
				
				// �¾Ҵ�
				if( isHit ) {
					player[i].setShoot(false);
					
					switch( mtype ) {
					case 0: // �Ϲ� ����
						SoundManager.getInstance().PlaySound( SoundManager.SOUND_AQUA_HIT1 );
						player[jj].setDeviceHp( player[jj].getDeviceHp() - 1 );
						player[jj].setSpeedX( player[jj].getSpeedX() + (player[i].getShootDir() * 4) );
						break;
					case 1: // �����Ѱ�
						SoundManager.getInstance().PlaySound( SoundManager.SOUND_AQUA_HIT2 );
						player[jj].setDeviceHp( player[jj].getDeviceHp() - 2 );
						player[jj].setSpeedX( player[jj].getSpeedX() + (player[i].getShootDir() * 5) );
						break;
					case 2: // ����
						SoundManager.getInstance().PlaySound( SoundManager.SOUND_AQUA_HIT3 );
						player[jj].setDisabled();
						break;
					case 3: // ������
						SoundManager.getInstance().PlaySound( SoundManager.SOUND_AQUA_HIT4 );
						player[jj].setDeviceHp( player[jj].getDeviceHp() - 1 );
						player[jj].setSpeedX( player[jj].getSpeedX() + (player[i].getShootDir() * 4) );
						break;
					}
					
					// ħ��?
					if( player[jj].getDeviceHp() <= 0 )
						SoundManager.getInstance().PlaySound( SoundManager.SOUND_AQUA_WRECK );
					
					checkGameEnd();
				}
			}
		}
	}
	
	public void checkGameEnd() {
		// ������ �������� �˻��Ѵ�
		
		int last_team = -1;
		
		for( int i=0; i<6; i++ ) {
			if( player[i].getActive() == false )
				continue;
			
			if( player[i].getDeviceHp() > 0 ) {
				// ����ִ� �÷��̾��� ��
				int iteam = player[i].getTeam();
				
				if( last_team == -1 )
					last_team = iteam;
				else if ( last_team == iteam )
					last_team = iteam;
				else
					last_team = -2; // �ٸ� ���� ���� ����ִ�
			}
		}
		
		// �¸��� ��
		if( last_team >= 0 ) {
			NetEndGameNot packet = new NetEndGameNot();
			
			for( int i=0; i<6; i++ )
				if( player[i].getActive() == true )
					if( player[i].getTeam() == last_team )
						packet.addWin(player[i].getNick());
			
			// ��¥�� ���� ������ ���� ��Ŷ�� ó���ϵ����Ѵ�.
			Main.getMain().getClient().sendPacket(Config.NETWORK_ENDGAME_NOT, packet);
		}
	}

	public SceneAquaGame(JFrame parent) {
		super(parent);
		
		player = new AquaPlayer[6];
		for( int i=0; i<6; i++ )
			player[i] = new AquaPlayer();
		
		bimg_gamecanvas = new BufferedImage(Config.AQUA_MAP_WIDTH, Config.AQUA_MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		gamescreen = new GameScreen();
		
		aquakeylistener = new AquaKeyListener();
		wasKeyPressed = new boolean[255];
		
		bimg_device = new BufferedImage[6];
		bimg_water = new BufferedImage[9];
		bimg_bullet_l = new BufferedImage[4];
		bimg_bullet_r = new BufferedImage[4];
		
		try {
			bimg_map = ImageIO.read(AllUtil.getResURL("res/aqua_map.png"));
			
			for( int i=0; i<6; i++ )
				bimg_device[i] = ImageIO.read(AllUtil.getResURL("res/aqua_device" + (i + 1) + ".png")); 
			
			for( int i=0; i<9; i++ )
				bimg_water[i] = ImageIO.read(AllUtil.getResURL("res/aqua_water" + i + ".png"));
			
			for( int i=0; i<4; i++ )
				bimg_bullet_l[i] = ImageIO.read(AllUtil.getResURL("res/aqua_bullet" + (i + 1) + "_l.png"));
			
			for( int i=0; i<4; i++ )
				bimg_bullet_r[i] = ImageIO.read(AllUtil.getResURL("res/aqua_bullet" + (i + 1) + "_r.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		game_state = GAMESTATE_WAIT;
	}

	public void draw() {
		Container cp = super.parent.getContentPane();
		
		cp.setLayout(null);
		
		gamescreen.setLocation(0, 0);
		gamescreen.setSize(1024, 768);
		gamescreen.setLayout(null);
		cp.add(gamescreen);
		
		attachKeyListener(aquakeylistener);
		cp.requestFocus();
		
		cp.revalidate();
		cp.repaint();
	}

	public void setGamePlayer(NetStartGameNot packet) {
		// ���� ���� �ִ� ������ ����
		for( int i=0; i<6; i++ )
			player[i].init();
		
		for( int i=0; i<packet.getPlayerNick().size(); i++ ) {
			player[i].setActive(true);
			player[i].setNick(packet.getPlayerNick().get(i));
			player[i].setTeam(packet.getTeam().get(i));
			player[i].setDeviceX( packet.getStartX().get(i) );
			player[i].setDeviceY( packet.getStartY().get(i) );
			
			if( player[i].getNick().equals( Main.getMain().getNick() ) == true ) {
				System.out.println("myslot = " + i);
				myslot = i;
			}
		}
	}
	
	public void startGame() {
		SoundManager.getInstance().PlayBGM("res/aqua_bgm.mp3", true);
		SoundManager.getInstance().PlaySound(SoundManager.SOUND_AQUA_START);
		game_state = GAMESTATE_PLAYING;
	}
	
	private class GameScreen extends JComponent {
		public void paintComponent(Graphics g)
		{
			if( img_gamescreen == null )
				img_gamescreen = createImage(Config.GAMEWINDOW_WIDTH, Config.GAMEWINDOW_HEIGHT);
			
			if( g_gamescreen == null ) 
				g_gamescreen = img_gamescreen.getGraphics();
			
			// ȭ�� �����
			g_gamescreen.clearRect(0, 0, Config.GAMEWINDOW_WIDTH, Config.GAMEWINDOW_HEIGHT);
			
			switch( game_state ) {
			case GAMESTATE_PLAYING: // ������ ���¸� �׸���
				draw_playing();
				break;
			}
	
			g.drawImage(img_gamescreen,0,0,this);
		}
	}
	
	private void draw_playing() {
		Graphics fullcanvas = bimg_gamecanvas.getGraphics();
		fullcanvas.clearRect(0, 0, Config.AQUA_MAP_WIDTH, Config.AQUA_MAP_HEIGHT);
		
		// ī�޶�
		int cx = 0;
		int cy = 0;
		
		// �� �׸���
		fullcanvas.drawImage(bimg_map, 0, 0, Config.AQUA_MAP_WIDTH, Config.AQUA_MAP_HEIGHT, gamescreen);
		
		// �����, �г��� �׸���
		for( int i=0; i<6; i++ ) {
			if( player[i].getActive() == false )
				continue;
			
			// �� ������� ���� �׸��� (�׻���)
			if( i == myslot )
				continue;
			
			int x = player[i].getDeviceX();
			int y = player[i].getDeviceY();
			int hp = player[i].getDeviceHp();
			
			if( hp > 0 ) {
				fullcanvas.drawImage(bimg_device[ player[i].getTeam() ], x, y, gamescreen);
			
				if( hp < 9 )
					fullcanvas.drawImage(bimg_water[ hp ], x + 42, y + 36, gamescreen);
			
				fullcanvas.setColor(Color.BLACK);
				fullcanvas.drawString( player[i].getNick(), x + 60, y + 157 );
			}
		}
		
		// �� ����� �׸���
		int myx = player[myslot].getDeviceX();
		int myy = player[myslot].getDeviceY();
		int myhp = player[myslot].getDeviceHp();
		
		if( myhp > 0 ) {
			fullcanvas.drawImage(bimg_device[ player[myslot].getTeam() ], myx, myy, gamescreen);
		
			if( myhp < 9 )
				fullcanvas.drawImage(bimg_water[ myhp ], myx + 42, myy + 36, gamescreen);
		
			fullcanvas.setColor(Color.BLACK);
			fullcanvas.drawString( player[myslot].getNick(), myx + 60, myy + 157 );
		}
		// ------------------
		
		// �̻��� �׸���
		for( int i=0; i<6; i++ ) {
			if( player[i].getActive() == false )
				continue;
			
			if( player[i].getShoot() == false )
				continue;
			
			int x = player[i].getShootX();
			int y = player[i].getShootY();
			
			if( player[i].getShootDir() == -1 )
				fullcanvas.drawImage(bimg_bullet_l[ player[i].getShootType() ], x, y, gamescreen);
			else if ( player[i].getShootDir() == 1 )
				fullcanvas.drawImage(bimg_bullet_r[ player[i].getShootType() ], x, y, gamescreen);
		}
		
		// ���� ��������� ī�޶� ������ ������Ѵ�
		//if( player[myslot].getDeviceHp() > 0 ) {
		if( true ) {
			int x = player[myslot].getDeviceX();
			int y = player[myslot].getDeviceY();
			
			int limitX = Config.GAMEWINDOW_WIDTH / 2 - Config.AQUA_DEVICE_WIDTH / 2;
			int limitY = Config.GAMEWINDOW_HEIGHT / 2 - Config.AQUA_DEVICE_HEIGHT / 2;
			
			if( x > limitX )
				cx = x - limitX;
			
			if( y > limitY )
				cy = y - limitY;
			
			if( cx > Config.AQUA_MAP_WIDTH - Config.GAMEWINDOW_WIDTH )
				cx = Config.AQUA_MAP_WIDTH - Config.GAMEWINDOW_WIDTH;
			
			if( cy > Config.AQUA_MAP_HEIGHT - Config.GAMEWINDOW_HEIGHT )
				cy = Config.AQUA_MAP_HEIGHT - Config.GAMEWINDOW_HEIGHT;
		}
		
		BufferedImage camera_map = bimg_gamecanvas.getSubimage(cx, cy, Config.GAMEWINDOW_WIDTH, Config.GAMEWINDOW_HEIGHT);
		g_gamescreen.drawImage(camera_map, 0, 0, gamescreen);
		
		
		// �̴ϸ��� �׸���
		int pad_minimap = 10;
		g_gamescreen.setColor(Color.BLACK);
		g_gamescreen.drawRect(Config.GAMEWINDOW_WIDTH / 2 - Config.AQUA_MAP_WIDTH / 10 / 2 + pad_minimap,
				              0,
				              Config.AQUA_MAP_WIDTH / 10 - pad_minimap,
				              Config.AQUA_MAP_HEIGHT / 10 - pad_minimap);
		
		for( int i=0; i<6; i++ ) {
			if( player[i].getActive() == false )
				continue;
			
			if( player[i].getDeviceHp() <= 0 )
				continue;
			
			int dotX = Config.GAMEWINDOW_WIDTH / 2 - Config.AQUA_MAP_WIDTH / 10 / 2;
			int dotY = 0;
			
			dotX += player[i].getDeviceX() / 10 + pad_minimap;
			dotY += player[i].getDeviceY() / 10;
			
			switch( player[i].getTeam() ) {
			case 0: g_gamescreen.setColor(Color.RED); break;
			case 1: g_gamescreen.setColor(Color.YELLOW); break;
			case 2: g_gamescreen.setColor(Color.GREEN); break;
			case 3: g_gamescreen.setColor(Color.CYAN); break;
			case 4: g_gamescreen.setColor(Config.COLOR_DARKBLUE); break;
			case 5: g_gamescreen.setColor(Config.COLOR_PURPLE); break;
			}
			
			g_gamescreen.fillRect(dotX, dotY, 7, 7);
			
			// �̻��� ǥ��
			if( player[i].getShoot() ) {
				int dotMX = Config.GAMEWINDOW_WIDTH / 2 - Config.AQUA_MAP_WIDTH / 10 / 2;
				int dotMY = 0;
				dotMX += player[i].getShootX()/ 10 + 5;
				dotMY += player[i].getShootY() / 10 - 12;
				g_gamescreen.fillRect(dotMX, dotMY, 3, 3);
			}
		}
	}
	
	private void sendAquaPlayer() {
		NetAquaPlayerNot packet = new NetAquaPlayerNot();
		
		packet.setNick(Main.getMain().getNick());
		
		packet.setDeviceHp( player[myslot].getDeviceHp() );
		packet.setDeviceX( player[myslot].getDeviceX() );
		packet.setDeviceY( player[myslot].getDeviceY() );
		packet.setUp( player[myslot].getUp() );
		packet.setDown( player[myslot].getDown() );
		packet.setLeft( player[myslot].getLeft() );
		packet.setRight( player[myslot].getRight() );
		packet.setSpeedX( player[myslot].getSpeedX() );
		packet.setSpeedY( player[myslot].getSpeedY() );
		packet.setTeam( player[myslot].getTeam() );

		Main.getMain().getClient().sendPacket(Config.NETWORK_AQUA_PLAYER_NOT, packet);
		
		packet = null;
	}
	
	private class AquaKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) { 
			int keycode = e.getKeyCode();
			
			switch( keycode ) {
			case KeyEvent.VK_UP:
				player[myslot].setUp(true);
				if( wasKeyPressed[keycode] == false )
					sendAquaPlayer();
				break;
			case KeyEvent.VK_DOWN:
				player[myslot].setDown(true);
				if( wasKeyPressed[keycode] == false )
					wasKeyPressed[keycode] = true;
				break; 
			case KeyEvent.VK_LEFT:
				player[myslot].setLeft(true);
				if( wasKeyPressed[keycode] == false )
					wasKeyPressed[keycode] = true;
				break;
			case KeyEvent.VK_RIGHT:
				player[myslot].setRight(true);
				if( wasKeyPressed[keycode] == false )
					sendAquaPlayer();
				break;
			case KeyEvent.VK_F1:
				if( player[myslot].getLeft() == true )
					player[myslot].requestShoot(-1, 0);
				if( player[myslot].getRight() == true )
					player[myslot].requestShoot(1, 0);
				break;
			case KeyEvent.VK_F2:
				if( player[myslot].getLeft() == true )
					player[myslot].requestShoot(-1, 1);
				if( player[myslot].getRight() == true )
					player[myslot].requestShoot(1, 1);
				break;
			case KeyEvent.VK_F3:
				if( player[myslot].getLeft() == true )
					player[myslot].requestShoot(-1, 2);
				if( player[myslot].getRight() == true )
					player[myslot].requestShoot(1, 2);
				break;
			case KeyEvent.VK_F4:
				if( player[myslot].getLeft() == true )
					player[myslot].requestShoot(-1, 3);
				if( player[myslot].getRight() == true )
					player[myslot].requestShoot(1, 3);
				break;
			}

			wasKeyPressed[keycode] = true;
		}
		
		public void keyReleased(KeyEvent e) {
			int keycode = e.getKeyCode();
			
			switch( keycode ) {
			case KeyEvent.VK_UP:
				player[myslot].setUp(false);
				if( wasKeyPressed[keycode] == true )
					sendAquaPlayer();
				break;
			case KeyEvent.VK_DOWN:
				player[myslot].setDown(false);
				if( wasKeyPressed[keycode] == true )
					sendAquaPlayer();
				break; 
			case KeyEvent.VK_LEFT:
				player[myslot].setLeft(false);
				if( wasKeyPressed[keycode] == true )
					sendAquaPlayer();
				break;
			case KeyEvent.VK_RIGHT:
				player[myslot].setRight(false);
				if( wasKeyPressed[keycode] == true )
					sendAquaPlayer();
				break;
			}
			
			wasKeyPressed[keycode] = false;
		}
		
		public void keyTyped(KeyEvent e) { }
	}
	
	public void processAquaPlayerNot(NetAquaPlayerNot packet) {
		String targetnick = packet.getNick();
		
		//System.out.println(targetnick + "�� ������ ����");

		// ���� ���� ������ ������ ����
		if( targetnick.equals(Main.getMain().getNick()) == true )
			return;
		
		int slot = -1;
		
		for( int i=0; i<6; i++ )
			if( player[i].getActive() == true )
				if( player[i].getNick().equals(targetnick) )
					slot = i;
		
		if( slot == -1 )
			return;
		
		player[slot].setDeviceHp( packet.getDeviceHp() );
		player[slot].setDeviceX( packet.getDeviceX() );
		player[slot].setDeviceY( packet.getDeviceY() );
		player[slot].setUp( packet.getUp() );
		player[slot].setDown( packet.getDown() );
		player[slot].setLeft( packet.getLeft() );
		player[slot].setRight( packet.getRight() );
		player[slot].setSpeedX( packet.getSpeedX() );
		player[slot].setSpeedY( packet.getSpeedY() );
		player[slot].setTeam( packet.getTeam() );
	}
	
	public void processAquaShootNot(NetAquaShootNot packet) {
		String targetnick = packet.getNick();
		
		// ���� ���� ������ ������ ����
		if( targetnick.equals(Main.getMain().getNick()) == true )
			return;
		
		int slot = -1;
		
		for( int i=0; i<6; i++ )
			if( player[i].getActive() == true )
				if( player[i].getNick().equals(targetnick) )
					slot = i;
		
		if( slot == -1 )
			return;
		
		System.out.println( packet.getShoot() );
		System.out.println( packet.getShootDir() );
		System.out.println( packet.getShootType() );
		System.out.println( packet.getShootStartX() );
		System.out.println( packet.getShootStartY() );
		
		player[slot].setShoot( packet.getShoot() );
		player[slot].setShootDir( packet.getShootDir() );
		player[slot].setShootType( packet.getShootType() );
		player[slot].setShootStartX( packet.getShootStartX() );
		player[slot].setShootStartY( packet.getShootStartY() );
		player[slot].setShootYAcc(0);
		
		// �������� �ְ� ���� ��ġ�� Ŭ�� ó���Ѵ�
		player[slot].setShootX( packet.getShootStartX() );
		player[slot].setShootY( packet.getShootStartY() );
	}
}
