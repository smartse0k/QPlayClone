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
	
	// ���� ����Ʈ ������
	public void sendPlayerlist() {
		NetPlayerListNot packet = new NetPlayerListNot();
		
		packet.add("���", "����");
		
		for(int i=0; i<sessions.size(); i++) {
			String nick = sessions.get(i).getNick();
			String location = "?";
			
			if( sessions.get(i).getCurrentRoom() == null ) {
				location = "�κ�";
			} else {
				location = Config.GAMEMODE[sessions.get(i).getCurrentRoom().getGamemode()] +
						  (sessions.get(i).getCurrentRoom().getPlaying() == true ? "������" : "����");
			}
			
			packet.add(nick, location);
		}
		
		// �κ� �ִ� �ֵ����׸� �����ش�
		for(int i=0; i<sessions.size(); i++)
			if( sessions.get(i).getCurrentRoom() == null )
				sessions.get(i).sendPacket(Config.NETWORK_PLAYER_LIST_NOT, packet);
	}
	
	// �� ����Ʈ ������
	public void sendRoomlistAllPlayer() {
		// �κ� �ִ� �ֵ����׸� �����ش�
		for(int i=0; i<sessions.size(); i++)
			if( sessions.get(i).getCurrentRoom() == null )
				sessions.get(i).sendRoomlist(null);
	}
	
	// �� �����
	synchronized public Room makeRoom(NetMakeRoomReq packet) {
		// ������ �� ��ȣ�� ���Ѵ�
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
	
	// �� ���ֱ�
	synchronized public void removeRoom(Room r) {		
		rooms.remove(r);
	}
	
	// ���� ���ֱ�
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
