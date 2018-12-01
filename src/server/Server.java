package server;

import java.util.Vector;

import main.Config;
import network.*;
import scene.Scene;

public class Server {
	private ServerAcceptor sa;
	private Thread thread_sa;
	
	private Vector<Session> sessions;
	private Vector<Room> rooms;
	
	public Server() {
		sa = new ServerAcceptor(this, Config.SERVER_PORT);
		
		thread_sa = new Thread(sa);
		thread_sa.start();
		
		sessions = new Vector<Session>();
		sessions.clear();
		
		rooms = new Vector<Room>();
		rooms.clear();
	}
	
	public void addSession(Session s) { sessions.add(s); }
	public void removeSession(int idx) { sessions.remove(idx); }
	
	public Vector<Session> getSessions() { return sessions; }
	public Vector<Room> getRooms() { return rooms; }
	
	public Session findSessionByNick(String nick) { 
		for(int i=0; i<sessions.size(); i++) {
			if( sessions.get(i).getNick().equals(nick) )
				return sessions.get(i);
		} 
		return null; 
	}
	
	// 유저 리스트 보내기
	public void sendPlayerlist() {
		NetPlayerListNot packet = new NetPlayerListNot();
		
		packet.add("운영자", "서버");
		
		for(int i=0; i<sessions.size(); i++) {
			String nick = sessions.get(i).getNick();
			String location = "?";
			
			if( sessions.get(i).getCurrentRoom() == null ) {
				location = "로비";
			} else {
				location = Config.GAMEMODE[sessions.get(i).getCurrentRoom().getGamemode()] +
						  (sessions.get(i).getCurrentRoom().getPlaying() == true ? "게임중" : "대기방");
			}
			
			packet.add(nick, location);
		}
		
		// 로비에 있는 애들한테만 보내준다
		for(int i=0; i<sessions.size(); i++)
			if( sessions.get(i).getCurrentRoom() == null )
				sessions.get(i).sendPacket(Config.NETWORK_PLAYER_LIST_NOT, packet);
	}
	
	// 방 리스트 보내기
	public void sendRoomlistAllPlayer() {
		// 로비에 있는 애들한테만 보내준다
		for(int i=0; i<sessions.size(); i++)
			if( sessions.get(i).getCurrentRoom() == null )
				sessions.get(i).sendRoomlist(null);
	}
	
	// 방 만들기
	synchronized public Room makeRoom(NetMakeRoomReq packet) {
		// 적절한 방 번호를 구한다
		int roomid = 0;
		for( int i=0; i<rooms.size(); i++ ) {
			if( rooms.get(i).getId() != roomid ) {
				roomid = i;
				break;
			}
			roomid++;
		}
		
		Room room = new Room(roomid, packet.getGamemode(), packet.getTitle(), packet.getPasswd(), Config.GET_GAME_MAXPLAYER(packet.getGamemode()));
		rooms.add(room);
		
		return room;
	}
	
	// 방 없애기
	synchronized public void removeRoom(Room r) {		
		rooms.remove(r);
	}
	
	// 세션 없애기
	public void removeSessionByNick(String nick) {
		for( int i=0; i<sessions.size(); i++ )
			if( sessions.get(i).getNick().equals(nick) == true )
				sessions.remove(i);
	}
	
	public void removeSession(Session s) {
		for( int i=0; i<sessions.size(); i++ )
			if( sessions.get(i) == s )
				sessions.remove(i);
	}
}
