package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;

import main.Config;
import main.Main;
import network.*;
import scene.Scene;

public class ServerRecvHandler implements Runnable {
	private Session parentsession = null;
	private Socket parentsocket = null;
	
	private InputStream is = null;
	private ObjectInputStream ois = null;
	
	public ServerRecvHandler(Session s) {
		parentsession = s;
		parentsocket = s.getSocket();
	}
	
	public void run() {
		try {
			is = parentsocket.getInputStream();
			ois = new ObjectInputStream(is);
			
			while( true ) {
				Integer command = (Integer)ois.readObject();
				process(command);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// ����� ���� ������ ������ ������ ���.
		if( parentsession.getCurrentRoom() != null ) // �濡�� ��������
			parentsession.getCurrentRoom().quitSession(parentsession);
		
		parentsession.close();
		parentsession.getServer().removeSession(parentsession);
		
		sendPlayerList(); // �ٸ� ������� ���� ���� ����
	}
	
	private void process(int command) {
		try {
			switch(command) {
			case Config.NETWORK_LOGIN_REQ: // �α��� ��û
				NETWORK_LOGIN_REQ( (NetLoginReq)ois.readObject() );
				break;
			case Config.NETWORK_CHAT_REQACK: // ä��
				NETWORK_CHAT_REQACK( (NetChatReqAck)ois.readObject() );
				break;
			case Config.NETWORK_ROOMLIST_REQ: // ���� ��û
				NETWORK_ROOMLIST_REQ( (NetRoomListReq)ois.readObject() );
				break;
			case Config.NETWORK_MAKEROOM_REQ: // �� ����� ��û
				NETWORK_MAKEROOM_REQ( (NetMakeRoomReq)ois.readObject() );
				break;
			case Config.NETWORK_JOINROOM_REQ: // �� ���� ��û
				NETWORK_JOINROOM_REQ( (NetJoinRoomReq)ois.readObject() );
				break;
			case Config.NETWORK_CHANGETEAM_REQ: // �� �ٲٱ� ��û
				NETWORK_CHANGETEAM_REQ( (NetChangeTeamReq)ois.readObject() );
				break;
			case Config.NETWORK_QUITROOM_REQ: // �� ������ ��û
				NETWORK_QUITROOM_REQ( (NetQuitRoomReq)ois.readObject() );
				break;
			case Config.NETWORK_STARTGAME_REQ: // ���ӽ��� ��û
				NETWORK_STARTGAME_REQ( (NetStartGameReq)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_PLAYER_NOT: // ����� �÷��̾� ����
				NETWORK_AQUA_PLAYER_NOT( (NetAquaPlayerNot)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_SHOOT_NOT: // ����� �÷��̾� �̻��� �߻�
				NETWORK_AQUA_SHOOT_NOT( (NetAquaShootNot)ois.readObject() );
				break;
			case Config.NETWORK_ENDGAME_NOT: // ���� ��
				NETWORK_ENDGAME_NOT( (NetEndGameNot)ois.readObject() );
				break;
			default: // �� �� ���� ��Ŷ
				ois.readObject(); // �׳� ������.
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void sendPlayerList() {
		parentsession.getServer().sendPlayerlist(); // �÷��̾� ����Ʈ ����
	}

	private void NETWORK_LOGIN_REQ( NetLoginReq packet ) {
		System.out.println("ServerRecvHandler.NETWORK_LOGIN_REQ()");
		
		boolean isLoginSuccess = true;
		String nick = packet.getNick();
		
		NetLoginAck ret = new NetLoginAck();
		
		ret.setRet(NetLoginAck.LOGIN_SUCCESS);
		
		// �г��� �˻�
		if( parentsession.getServer().findSessionByNick(nick) != null ) {
			ret.setRet(NetLoginAck.LOGIN_EXISTNICK);
			isLoginSuccess = false;
		}
		
		// ���� �˻�
		if( packet.getVersion().equals(Config.GAME_VERSION) == false ) {
			ret.setRet(NetLoginAck.LOGIN_DIFFERENTVERSION);
			isLoginSuccess = false;
		}
		
		ret.setNick(nick);
		ret.setUseMsg(true);
		ret.setMsg("�׽�Ʈ�� �޽����Դϴ�.");
		
		parentsession.sendPacket(Config.NETWORK_LOGIN_ACK, ret);
		
		if( isLoginSuccess == true ) {
			// �α��� ����
			parentsession.setNick(nick); // �г��� ����
			sendPlayerList(); // �÷��̾� ����Ʈ ����
			parentsession.sendRoomlist(null); // �渮��Ʈ ����
		} else {
			parentsession.close();
		}
	}
	
	private void NETWORK_CHAT_REQACK( NetChatReqAck packet ) {
		System.out.println("ServerRecvHandler.NETWORK_CHAT_REQACK()");
		
		String nick = parentsession.getNick();
		String chat = packet.getChat();
		
		// �ٸ� ��� ���ǿ� ������
		NetChatReqAck ret = new NetChatReqAck();
		ret.setNick(nick);
		ret.setChat(chat);
		
		Vector<Session> v = parentsession.getServer().getSessions();
		for( int i=0; i<v.size(); i++ ) {
			// ���� �濡 �ִ� �ֵ����׸� ä���� �����ش�
			if( v.get(i).getCurrentRoom() == parentsession.getCurrentRoom() )
				v.get(i).sendPacket(Config.NETWORK_CHAT_REQACK, ret);
		}
	}
	
	private void NETWORK_ROOMLIST_REQ( NetRoomListReq packet ) {
		System.out.println("ServerRecvHandler.NETWORK_ROOMLIST_REQ()");
		parentsession.sendRoomlist(packet);
		sendPlayerList(); // �÷��̾� ����Ʈ ����
	}
	
	private void NETWORK_MAKEROOM_REQ(NetMakeRoomReq packet) {
		NetMakeRoomAck ret = new NetMakeRoomAck();
		ret.setOk(-99);
		
		// ���Ӹ�� �˻� (�ϴ� ������ ����Ƹ�..)
		if( packet.getGamemode() != 0 ) {
			ret.setOk(-1);
			parentsession.sendPacket(Config.NETWORK_MAKEROOM_ACK, ret);
			return;
		}
		
		// �� �����
		Room roomret = parentsession.getServer().makeRoom(packet);

		if( roomret != null ) {
			// ���� ����µ� ����
			ret.setOk(1);
	
			roomret.setHost(0); // 0���� ����
			roomret.setSlotSession(0, parentsession); // �濡 �־��ְ�
			parentsession.setCurrentRoom(roomret); // ���� ���� ����
		}
	
		ret.setRoomId(roomret.getId());
		ret.setRoomGameMode(roomret.getGamemode());
		
		parentsession.sendPacket(Config.NETWORK_MAKEROOM_ACK, ret);
		
		// �� ���� ����
		roomret.sendRoomInfo();
		
		parentsession.getServer().sendRoomlistAllPlayer(); // �� ���� ����
		sendPlayerList(); // �÷��̾� ����Ʈ ����
	}

	synchronized private void NETWORK_JOINROOM_REQ(NetJoinRoomReq packet) {
		System.out.println("NETWORK_JOINROOM_REQ()");
		
		NetJoinRoomAck ret = new NetJoinRoomAck();
		
		int roomid = packet.getRoomId();
		
		Vector<Room> rooms = parentsession.getServer().getRooms();
		Room room = null;
		
		for( int i=0; i<rooms.size(); i++ )
			if( rooms.get(i).getId() == roomid )
				room = rooms.get(i);
		
		// ���� ��ã��
		if( room == null ) {
			ret.setOk(-1);
			parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
			return;
		}
		
		// ������ ���۵� ����
		if( room.getPlaying() == true ) {
			ret.setOk(-2);
			parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
			return;
		}
		
		// ���� ���� ������ ���� Ž��
		int myslot = room.getEmptySlot();
		if( myslot == -1 ) {
			ret.setOk(-3);
			parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
			return;
		}
		
		// �� ���Ӹ�� ���
		ret.setGamemode(room.getGamemode());
		
		room.setSlotSession(myslot, parentsession); // �濡 ���� �ֱ�
		room.setSlotTeam(myslot, myslot); // ���� �׳� ���� ��ȣ��..
		room.setSlotLoadDone(myslot, false); // Ȥ�� �𸣴�..
		parentsession.setCurrentRoom(room); // ���� �� ����
		parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
		
		room.sendRoomInfo(); // �� ���� ������
		
		parentsession.getServer().sendRoomlistAllPlayer(); // ���ǿ� �� ��� ������
		sendPlayerList(); // �÷��̾� ����Ʈ ����
	}

	private void NETWORK_CHANGETEAM_REQ(NetChangeTeamReq packet) {
		//�� �ٲٱ�
		
		Room r = parentsession.getCurrentRoom(); 
				
		if( r == null )
			return;
		
		int mypos = r.findSlotBySession(parentsession);
		
		if( mypos == -1 )
			return;
		
		r.setSlotTeam(mypos, packet.getTeam());
		
		r.sendRoomInfo();
	}

	private void NETWORK_QUITROOM_REQ(NetQuitRoomReq packet) {
		//�� ������
		Room r = parentsession.getCurrentRoom();
		
		if( r == null )
			return;
		
		r.quitSession(parentsession);

		parentsession.getServer().sendRoomlistAllPlayer(); // ���ǿ� �� ��� ������
		sendPlayerList(); // �÷��̾� ����Ʈ ����
	}
	
	private void NETWORK_STARTGAME_REQ(NetStartGameReq packet) {
		// ���ӽ���
		
		Room r = parentsession.getCurrentRoom();
		
		if( r == null )
			return;
		
		// ���常 ������ �� �ִ�
		int mypos = r.findSlotBySession(parentsession);
		if( mypos != r.getHost() ) {
			NetChatReqAck chat =  new NetChatReqAck();
			chat.setNick("*����*");
			chat.setChat("���ӽ����� ���常 �� �� �ֽ��ϴ�.");
			parentsession.sendPacket(Config.NETWORK_CHAT_REQACK, chat);
			return;
		}
		
		r.setPlaying(true); // ���� ���ӽ��� ���·�.
		
		// ���ӽ��� ����
		r.sendStartGame();
	}

	private void NETWORK_AQUA_PLAYER_NOT(NetAquaPlayerNot packet) {
		Room r = parentsession.getCurrentRoom();
		
		// ���� ����
		if( r == null )
			return;
		
		//System.out.println("[����] NETWORK_AQUA_PLAYER_NOT() ���Ӹ�� �˻�...");
		
		// ����ư� �ƴ�
		if( r.getGamemode() != 0 )
			return;
		
		//System.out.println("[����] NETWORK_AQUA_PLAYER_NOT() ���ӽ��� ���� �˻�...");
		
		// ���� ���� ���°� �ƴ�
		if( r.getPlaying() == false )
			return;
		
		//System.out.println("[����] NETWORK_AQUA_PLAYER_NOT() ��Ŷ ����...");
		
		// �� ������ �״�� �� �������� ���� �Ѹ���
		for( int i=0; i<r.getMaxPlayer(); i++ )
			if( r.getSlotSession(i) != null )
				r.getSlotSession(i).sendPacket(Config.NETWORK_AQUA_PLAYER_NOT, packet);
	}
	


	private void NETWORK_AQUA_SHOOT_NOT(NetAquaShootNot packet) {
		Room r = parentsession.getCurrentRoom();
		
		// ���� ����
		if( r == null )
			return;
		
		// ����ư� �ƴ�
		if( r.getGamemode() != 0 )
			return;
		
		// ���� ���� ���°� �ƴ�
		if( r.getPlaying() == false )
			return;
		
		// �� ������ �״�� �� �������� ���� �Ѹ���
		for( int i=0; i<r.getMaxPlayer(); i++ )
			if( r.getSlotSession(i) != null )
				r.getSlotSession(i).sendPacket(Config.NETWORK_AQUA_SHOOT_NOT, packet);
	}
	
	private void NETWORK_ENDGAME_NOT(NetEndGameNot packet) {
		Room r = parentsession.getCurrentRoom();
		
		// ���� ����
		if( r == null )
			return;
		
		// ���� ���� ���°� �ƴ�
		if( r.getPlaying() == false )
			return;
		
		// ���岨�� ó���Ѵ�
		int mypos = r.findSlotBySession(parentsession);
		if( mypos != r.getHost() )
			return;
		
		// �� ������ �״�� �� �������� ���� �Ѹ���
		for( int i=0; i<r.getMaxPlayer(); i++ )
			if( r.getSlotSession(i) != null )
				r.getSlotSession(i).sendPacket(Config.NETWORK_ENDGAME_NOT, packet);
		
		// �׸��� ���� �������� �ƴѰɷ� ǥ��
		r.setPlaying(false);
	}
}
