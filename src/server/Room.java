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
	private int slot_host; // 방장
	private int[] slot_team; // 팀
	private boolean[] slot_loaddone; // 게임 로딩이 완료되었는가
	
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
	
	// 현재 방에 있는 애들한테 정보를 뿌려준다
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
	
	// 빈 슬롯 찾기
	public int getEmptySlot() {
		int slot = -1;
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] == null ) {
				slot = i;
				break;
			}
		
		return slot;
	}
	
	// 세션으로 슬롯찾기
	public int findSlotBySession(Session s) {
		int slot = -1;
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] == s )
				slot = i;
		
		return slot;
	}
	
	// 게임시작 알림
	public void sendStartGame() {
		setPlaying(true);
		
		NetStartGameNot packet = new NetStartGameNot();
		
		packet.setGameMode( this.gamemode );
		
		for( int i=0; i<max_player; i++ )
			if( slot_s[i] != null ) {
				packet.addPlayerNick( slot_s[i].getNick() );
				packet.addTeam( slot_team[i] );
				
				// 아쿠아는 시작 위치를 정해준다
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
	
	// 방에서 나간다
	public void quitSession(Session s) {
		int mypos = findSlotBySession(s);
		
		setSlotSession(mypos, null); // 방에서 없앤다
		s.setCurrentRoom(null); // 현재방 없앤다
		
		sendRoomInfo(); // 방 정보 전송
		
		// 방장인가?
		if( getHost() == mypos ) {
			// 다른 유저들 다 없앤다
			
			NetKickRoomNot kickpacket = new NetKickRoomNot();
			kickpacket.setWhy(NetKickRoomNot.WHY_HOSTQUIT);
			
			for( int i=0; i<getMaxPlayer(); i++ ) {
				Session ss = getSlotSession(i);
				
				// 방에 존재한다면
				if( ss != null ) {
					setSlotSession(i, null); // 방에서 없애고
					ss.setCurrentRoom(null); // 현재방 지우고
					ss.sendPacket(Config.NETWORK_KICKROOM_NOT, kickpacket);
				}
			}
			
			// 방을 없앤다
			s.getServer().removeRoom(this);
		}
	}
}
