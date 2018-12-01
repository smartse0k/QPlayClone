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
		
		// 여기로 나온 이유는 연결이 끊어진 경우.
		if( parentsession.getCurrentRoom() != null ) // 방에서 내보낸다
			parentsession.getCurrentRoom().quitSession(parentsession);
		
		parentsession.close();
		parentsession.getServer().removeSession(parentsession);
		
		sendPlayerList(); // 다른 사람한테 유저 정보 전송
	}
	
	private void process(int command) {
		try {
			switch(command) {
			case Config.NETWORK_LOGIN_REQ: // 로그인 요청
				NETWORK_LOGIN_REQ( (NetLoginReq)ois.readObject() );
				break;
			case Config.NETWORK_CHAT_REQACK: // 채팅
				NETWORK_CHAT_REQACK( (NetChatReqAck)ois.readObject() );
				break;
			case Config.NETWORK_ROOMLIST_REQ: // 방목록 요청
				NETWORK_ROOMLIST_REQ( (NetRoomListReq)ois.readObject() );
				break;
			case Config.NETWORK_MAKEROOM_REQ: // 방 만들기 요청
				NETWORK_MAKEROOM_REQ( (NetMakeRoomReq)ois.readObject() );
				break;
			case Config.NETWORK_JOINROOM_REQ: // 방 입장 요청
				NETWORK_JOINROOM_REQ( (NetJoinRoomReq)ois.readObject() );
				break;
			case Config.NETWORK_CHANGETEAM_REQ: // 팀 바꾸기 요청
				NETWORK_CHANGETEAM_REQ( (NetChangeTeamReq)ois.readObject() );
				break;
			case Config.NETWORK_QUITROOM_REQ: // 방 나가기 요청
				NETWORK_QUITROOM_REQ( (NetQuitRoomReq)ois.readObject() );
				break;
			case Config.NETWORK_STARTGAME_REQ: // 게임시작 요청
				NETWORK_STARTGAME_REQ( (NetStartGameReq)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_PLAYER_NOT: // 아쿠아 플레이어 정보
				NETWORK_AQUA_PLAYER_NOT( (NetAquaPlayerNot)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_SHOOT_NOT: // 아쿠아 플레이어 미사일 발사
				NETWORK_AQUA_SHOOT_NOT( (NetAquaShootNot)ois.readObject() );
				break;
			case Config.NETWORK_ENDGAME_NOT: // 게임 끝
				NETWORK_ENDGAME_NOT( (NetEndGameNot)ois.readObject() );
				break;
			default: // 알 수 없는 패킷
				ois.readObject(); // 그냥 버린다.
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void sendPlayerList() {
		parentsession.getServer().sendPlayerlist(); // 플레이어 리스트 전송
	}

	private void NETWORK_LOGIN_REQ( NetLoginReq packet ) {
		System.out.println("ServerRecvHandler.NETWORK_LOGIN_REQ()");
		
		boolean isLoginSuccess = true;
		String nick = packet.getNick();
		
		NetLoginAck ret = new NetLoginAck();
		
		ret.setRet(NetLoginAck.LOGIN_SUCCESS);
		
		// 닉네임 검사
		if( parentsession.getServer().findSessionByNick(nick) != null ) {
			ret.setRet(NetLoginAck.LOGIN_EXISTNICK);
			isLoginSuccess = false;
		}
		
		// 버전 검사
		if( packet.getVersion().equals(Config.GAME_VERSION) == false ) {
			ret.setRet(NetLoginAck.LOGIN_DIFFERENTVERSION);
			isLoginSuccess = false;
		}
		
		ret.setNick(nick);
		ret.setUseMsg(true);
		ret.setMsg("테스트용 메시지입니다.");
		
		parentsession.sendPacket(Config.NETWORK_LOGIN_ACK, ret);
		
		if( isLoginSuccess == true ) {
			// 로그인 성공
			parentsession.setNick(nick); // 닉네임 설정
			sendPlayerList(); // 플레이어 리스트 전송
			parentsession.sendRoomlist(null); // 방리스트 전송
		} else {
			parentsession.close();
		}
	}
	
	private void NETWORK_CHAT_REQACK( NetChatReqAck packet ) {
		System.out.println("ServerRecvHandler.NETWORK_CHAT_REQACK()");
		
		String nick = parentsession.getNick();
		String chat = packet.getChat();
		
		// 다른 모든 세션에 보낸다
		NetChatReqAck ret = new NetChatReqAck();
		ret.setNick(nick);
		ret.setChat(chat);
		
		Vector<Session> v = parentsession.getServer().getSessions();
		for( int i=0; i<v.size(); i++ ) {
			// 같은 방에 있는 애들한테만 채팅을 보내준다
			if( v.get(i).getCurrentRoom() == parentsession.getCurrentRoom() )
				v.get(i).sendPacket(Config.NETWORK_CHAT_REQACK, ret);
		}
	}
	
	private void NETWORK_ROOMLIST_REQ( NetRoomListReq packet ) {
		System.out.println("ServerRecvHandler.NETWORK_ROOMLIST_REQ()");
		parentsession.sendRoomlist(packet);
		sendPlayerList(); // 플레이어 리스트 전송
	}
	
	private void NETWORK_MAKEROOM_REQ(NetMakeRoomReq packet) {
		NetMakeRoomAck ret = new NetMakeRoomAck();
		ret.setOk(-99);
		
		// 게임모드 검사 (일단 지금은 아쿠아만..)
		if( packet.getGamemode() != 0 ) {
			ret.setOk(-1);
			parentsession.sendPacket(Config.NETWORK_MAKEROOM_ACK, ret);
			return;
		}
		
		// 방 만들기
		Room roomret = parentsession.getServer().makeRoom(packet);

		if( roomret != null ) {
			// 방을 만드는데 성공
			ret.setOk(1);
	
			roomret.setHost(0); // 0번이 방장
			roomret.setSlotSession(0, parentsession); // 방에 넣어주고
			parentsession.setCurrentRoom(roomret); // 현재 방을 설정
		}
	
		ret.setRoomId(roomret.getId());
		ret.setRoomGameMode(roomret.getGamemode());
		
		parentsession.sendPacket(Config.NETWORK_MAKEROOM_ACK, ret);
		
		// 방 정보 전송
		roomret.sendRoomInfo();
		
		parentsession.getServer().sendRoomlistAllPlayer(); // 방 정보 전송
		sendPlayerList(); // 플레이어 리스트 전송
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
		
		// 방을 못찾음
		if( room == null ) {
			ret.setOk(-1);
			parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
			return;
		}
		
		// 게임이 시작된 방임
		if( room.getPlaying() == true ) {
			ret.setOk(-2);
			parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
			return;
		}
		
		// 내가 들어가기 적정한 슬롯 탐색
		int myslot = room.getEmptySlot();
		if( myslot == -1 ) {
			ret.setOk(-3);
			parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
			return;
		}
		
		// 방 게임모드 담기
		ret.setGamemode(room.getGamemode());
		
		room.setSlotSession(myslot, parentsession); // 방에 나를 넣기
		room.setSlotTeam(myslot, myslot); // 팀을 그냥 슬롯 번호로..
		room.setSlotLoadDone(myslot, false); // 혹시 모르니..
		parentsession.setCurrentRoom(room); // 현재 방 설정
		parentsession.sendPacket(Config.NETWORK_JOINROOM_ACK, ret);
		
		room.sendRoomInfo(); // 룸 정보 보낸다
		
		parentsession.getServer().sendRoomlistAllPlayer(); // 대기실에 방 목록 보내기
		sendPlayerList(); // 플레이어 리스트 전송
	}

	private void NETWORK_CHANGETEAM_REQ(NetChangeTeamReq packet) {
		//팀 바꾸기
		
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
		//방 나가기
		Room r = parentsession.getCurrentRoom();
		
		if( r == null )
			return;
		
		r.quitSession(parentsession);

		parentsession.getServer().sendRoomlistAllPlayer(); // 대기실에 방 목록 보내기
		sendPlayerList(); // 플레이어 리스트 전송
	}
	
	private void NETWORK_STARTGAME_REQ(NetStartGameReq packet) {
		// 게임시작
		
		Room r = parentsession.getCurrentRoom();
		
		if( r == null )
			return;
		
		// 방장만 시작할 수 있다
		int mypos = r.findSlotBySession(parentsession);
		if( mypos != r.getHost() ) {
			NetChatReqAck chat =  new NetChatReqAck();
			chat.setNick("*서버*");
			chat.setChat("게임시작은 방장만 할 수 있습니다.");
			parentsession.sendPacket(Config.NETWORK_CHAT_REQACK, chat);
			return;
		}
		
		r.setPlaying(true); // 방을 게임시작 상태로.
		
		// 게임시작 전송
		r.sendStartGame();
	}

	private void NETWORK_AQUA_PLAYER_NOT(NetAquaPlayerNot packet) {
		Room r = parentsession.getCurrentRoom();
		
		// 방이 없음
		if( r == null )
			return;
		
		//System.out.println("[서버] NETWORK_AQUA_PLAYER_NOT() 게임모드 검사...");
		
		// 아쿠아가 아님
		if( r.getGamemode() != 0 )
			return;
		
		//System.out.println("[서버] NETWORK_AQUA_PLAYER_NOT() 게임시작 상태 검사...");
		
		// 게임 시작 상태가 아님
		if( r.getPlaying() == false )
			return;
		
		//System.out.println("[서버] NETWORK_AQUA_PLAYER_NOT() 패킷 전송...");
		
		// 이 정보를 그대로 방 유저한테 전부 뿌린다
		for( int i=0; i<r.getMaxPlayer(); i++ )
			if( r.getSlotSession(i) != null )
				r.getSlotSession(i).sendPacket(Config.NETWORK_AQUA_PLAYER_NOT, packet);
	}
	


	private void NETWORK_AQUA_SHOOT_NOT(NetAquaShootNot packet) {
		Room r = parentsession.getCurrentRoom();
		
		// 방이 없음
		if( r == null )
			return;
		
		// 아쿠아가 아님
		if( r.getGamemode() != 0 )
			return;
		
		// 게임 시작 상태가 아님
		if( r.getPlaying() == false )
			return;
		
		// 이 정보를 그대로 방 유저한테 전부 뿌린다
		for( int i=0; i<r.getMaxPlayer(); i++ )
			if( r.getSlotSession(i) != null )
				r.getSlotSession(i).sendPacket(Config.NETWORK_AQUA_SHOOT_NOT, packet);
	}
	
	private void NETWORK_ENDGAME_NOT(NetEndGameNot packet) {
		Room r = parentsession.getCurrentRoom();
		
		// 방이 없음
		if( r == null )
			return;
		
		// 게임 시작 상태가 아님
		if( r.getPlaying() == false )
			return;
		
		// 방장꺼만 처리한다
		int mypos = r.findSlotBySession(parentsession);
		if( mypos != r.getHost() )
			return;
		
		// 이 정보를 그대로 방 유저한테 전부 뿌린다
		for( int i=0; i<r.getMaxPlayer(); i++ )
			if( r.getSlotSession(i) != null )
				r.getSlotSession(i).sendPacket(Config.NETWORK_ENDGAME_NOT, packet);
		
		// 그리고 방을 게임중이 아닌걸로 표시
		r.setPlaying(false);
	}
}
