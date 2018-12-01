package server;

import java.util.Random;
import java.util.Vector;

import main.AllUtil;
import main.Config;
import network.NetKickRoomNot;
import network.NetRoomInfoNot;
import network.NetStartGameNot;

public class Room {
	private int id;
	private int gamemode;
	private String title;
	private String passwd;
	
	private int max_player;
	
	private Session[] slot_s;
	private int slot_host; // ����
	private int[] slot_team; // ��
	private boolean[] slot_loaddone; // ���� �ε��� �Ϸ�Ǿ��°�
	
	private boolean playing;
	
	public int getId() { return id; }
	public int getGamemode() { return gamemode; }
	public String getTitle() { return title; }
	public String getPasswd() { if( passwd == null ) return ""; return passwd; }
	
	public int getMaxPlayer() { return max_player; }
	
	public void setSlotSession(int index, Session s) { slot_s[index] = s; }
	public Session getSlotSession(int index) { return slot_s[index]; }
	
	public void setHost(int i) { slot_host = i; }
	public int getHost() { return slot_host; }
	
	public void setSlotLoadDone(int index, boolean b) { slot_loaddone[index] = b; }
	public boolean getSlotLoadDone(int index) { return slot_loaddone[index]; }
	
	public void setSlotTeam(int index, int t) { slot_team[index] = t; }
	public int getSlotTeam(int index) { return slot_team[index]; }
	
	public void setPlaying(boolean b) { playing = b; }
	public boolean getPlaying() { return playing; }
	
	public Room(int id, int gamemode, String title, String passwd, int maxplayer) {
		this.id = id;
		this.gamemode = gamemode;
		this.title = title;
		this.passwd = passwd;
		this.max_player = maxplayer;
		this.playing = false;
		
		slot_s = new Session[max_player];
		slot_team = new int[max_player];
		slot_loaddone = new boolean[max_player];
		
		for( int i=0; i<max_player; i++ ) {
			slot_s[i] = null;
			slot_team[i] = 0;
			slot_loaddone[i] = false;
		}
	}
	
	// ���� �濡 �ִ� �ֵ����� ������ �ѷ��ش�
	public void sendRoomInfo() {
		NetRoomInfoNot packet = new NetRoomInfoNot(max_player);
		
		for( int i=0; i<max_player; i++ ) {
			packet.setPlayerActive(i, false);
			
			if( slot_s[i] != null ) {
				packet.setPlayerActive(i, true);
				packet.setPlayerNick(i, slot_s[i].getNick());
				packet.setPlayerTeam(i, slot_team[i]);
			}
		}
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] != null )
				slot_s[i].sendPacket(Config.NETWORK_ROOMINFO_NOT, packet);
	}
	
	// �� ���� ã��
	public int getEmptySlot() {
		int slot = -1;
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] == null ) {
				slot = i;
				break;
			}
		
		return slot;
	}
	
	// �������� ����ã��
	public int findSlotBySession(Session s) {
		int slot = -1;
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] == s )
				slot = i;
		
		return slot;
	}
	
	// ���ӽ��� �˸�
	public void sendStartGame() {
		setPlaying(true);
		
		NetStartGameNot packet = new NetStartGameNot();
		
		packet.setGameMode( this.gamemode );
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] != null ) {
				packet.addPlayerNick( slot_s[i].getNick() );
				packet.addTeam( slot_team[i] );
				
				// ����ƴ� ���� ��ġ�� �����ش�
				if( gamemode == 0 ) {
					packet.addStartXY( AllUtil.getRand(0, Config.AQUA_MAP_WIDTH - Config.AQUA_DEVICE_WIDTH),
							           AllUtil.getRand(0, Config.AQUA_MAP_HEIGHT - Config.AQUA_DEVICE_HEIGHT)
							         );
				}
			}
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] != null )
				slot_s[i].sendPacket(Config.NETWORK_STARTGAME_NOT, packet);
	}
	
	// �濡�� ������
	public void quitSession(Session s) {
		int mypos = findSlotBySession(s);
		
		setSlotSession(mypos, null); // �濡�� ���ش�
		s.setCurrentRoom(null); // ����� ���ش�
		
		sendRoomInfo(); // �� ���� ����
		
		// �����ΰ�?
		if( getHost() == mypos ) {
			// �ٸ� ������ �� ���ش�
			
			NetKickRoomNot kickpacket = new NetKickRoomNot();
			kickpacket.setWhy(NetKickRoomNot.WHY_HOSTQUIT);
			
			for( int i=0; i<getMaxPlayer(); i++ ) {
				Session ss = getSlotSession(i);
				
				// �濡 �����Ѵٸ�
				if( ss != null ) {
					setSlotSession(i, null); // �濡�� ���ְ�
					ss.setCurrentRoom(null); // ����� �����
					ss.sendPacket(Config.NETWORK_KICKROOM_NOT, kickpacket);
				}
			}
			
			// ���� ���ش�
			s.getServer().removeRoom(this);
		}
	}
}
