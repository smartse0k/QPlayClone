package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import main.Config;
import main.Main;
import main.SoundManager;
import scene.Scene;

public class ClientRecvHandler implements Runnable {
	private Client parentclient = null;
	private Socket parentsocket = null;
	
	private InputStream is = null;
	private ObjectInputStream ois = null;
	
	public ClientRecvHandler(Client c) {
		parentclient = c;
		parentsocket = c.getSocket();
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

		} catch(ClassNotFoundException e) {
			
		}
		
		Main.getMain().showError("서버와 연결이 끊어졌습니다.");
	}
	
	private void process(int command) {
		try {
			switch(command) {
			case Config.NETWORK_LOGIN_ACK: // 로그인 완료
				NETWORK_LOGIN_ACK( (NetLoginAck)ois.readObject() );
				break;
			case Config.NETWORK_CHAT_REQACK: // 채팅
				NETWORK_CHAT_REQACK( (NetChatReqAck)ois.readObject() );
				break;
			case Config.NETWORK_PLAYER_LIST_NOT: // 유저 목록
				NETWORK_PLAYER_LIST_NOT( (NetPlayerListNot)ois.readObject() );
				break;
			case Config.NETWORK_ROOMLIST_NOT: // 방 목록
				NETWORK_ROOMLIST_NOT( (NetRoomListNot)ois.readObject() );
				break;
			case Config.NETWORK_MAKEROOM_ACK: // 방 만들기 완료
				NETWORK_MAKEROOM_ACK( (NetMakeRoomAck)ois.readObject() );
				break;
			case Config.NETWORK_ROOMINFO_NOT: // 방 정보 수신
				NETWORK_ROOMINFO_NOT( (NetRoomInfoNot)ois.readObject() );
				break;
			case Config.NETWORK_JOINROOM_ACK: // 방 입장 완료
				NETWORK_JOINROOM_ACK( (NetJoinRoomAck)ois.readObject() );
				break;
			case Config.NETWORK_KICKROOM_NOT: // 방에서 강퇴
				NETWORK_KICKROOM_NOT( (NetKickRoomNot)ois.readObject() );
				break;
			case Config.NETWORK_STARTGAME_NOT: // 게임시작
				NETWORK_STARTGAME_NOT( (NetStartGameNot)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_PLAYER_NOT: // 아쿠아 플레이어
				NETWORK_AQUA_PLAYER_NOT( (NetAquaPlayerNot)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_SHOOT_NOT: // 아쿠아 미사일
				NETWORK_AQUA_SHOOT_NOT( (NetAquaShootNot)ois.readObject() );
				break;
			case Config.NETWORK_ENDGAME_NOT: // 게임 종료
				NETWORK_ENDGAME_NOT( (NetEndGameNot)ois.readObject() );
				break;
			default: // 알 수 없는 패킷
				ois.readObject(); // 그냥 버린다.
				break;
			}
		} catch (IOException e) {

		} catch(ClassNotFoundException e) {
			
		}
	}

	private void NETWORK_LOGIN_ACK( NetLoginAck packet ) {
		switch( packet.getRet() ) {
		case NetLoginAck.LOGIN_SUCCESS: // 로그인 성공
			System.out.println("NETWORK_LOGIN_ACK() " + packet.getNick());
			Main.getMain().setNick(packet.getNick());
			Main.getMain().changeScene(Scene.SCENE_LOBBY);
			break;
		case NetLoginAck.LOGIN_EXISTNICK: // 닉네임
			JOptionPane.showMessageDialog(null, "다른 사람이 먼저 사용중인 닉네임입니다.");
			break;
		case NetLoginAck.LOGIN_DIFFERENTVERSION: // 버전
			JOptionPane.showMessageDialog(null, "버전이 달라 접속할 수 없습니다. 서버와 같은 버전의 게임 파일을 다운로드 받아 접속해주세요.");
			break;
		case NetLoginAck.LOGIN_BAN: // 밴
			JOptionPane.showMessageDialog(null, "현재 서버에 접속이 금지되어 있습니다.");
			break;
		default: // 기타
			JOptionPane.showMessageDialog(null, "기타 오류가 발생하였습니다.");
			break;
		}
		
		if( packet.getUseMsg() ) {
			JOptionPane.showMessageDialog(null, packet.getMsg());
		}
	}
	
	private void NETWORK_CHAT_REQACK( NetChatReqAck packet ) {
		switch( Main.getMain().getSceneState() ) {
		case Scene.SCENE_LOBBY:
			Main.getMain().getSceneLobby().addChatMsg(packet.getNick(), packet.getChat());
			break;
		case Scene.SCENE_AQUAROOM:
			Main.getMain().getSceneAquaRoom().addChatMsg(packet.getNick(), packet.getChat());
		default:
			break;
		}
	}
	
	private void NETWORK_PLAYER_LIST_NOT( NetPlayerListNot packet ) {
		// 로비일때만 처리한다
		if( Main.getMain().getSceneState() == Scene.SCENE_LOBBY ) {
			Main.getMain().getSceneLobby().setPlayerList( packet );
		}
	}
	
	private void NETWORK_ROOMLIST_NOT(NetRoomListNot packet ) {
		// 로비일때만 처리한다
		if( Main.getMain().getSceneState() == Scene.SCENE_LOBBY ) {
			Main.getMain().getSceneLobby().setRoomList( packet );
		}
	}

	private void NETWORK_MAKEROOM_ACK(NetMakeRoomAck packet) {
		if( packet.getOk() == 1 ) {
			Main.getMain().setRoom(packet.getRoomId());
			
			switch( packet.getRoomGameMode() ) {
			case 0: // 아쿠아
				Main.getMain().changeScene(Scene.SCENE_AQUAROOM);
				break;
			default:
				FAIL_MAKEROOM();
				break;
			}
		} else {
			FAIL_MAKEROOM();
		}
	}
	
	private void FAIL_MAKEROOM() {
		Main.getMain().changeScene(Scene.SCENE_LOBBY);
		JOptionPane.showMessageDialog(null, "방을 만드는데 실패하였습니다.");
	}
	
	private void NETWORK_ROOMINFO_NOT(NetRoomInfoNot packet) {
		// 아쿠아?
		if( Main.getMain().getSceneState() == Scene.SCENE_AQUAROOM )
			Main.getMain().getSceneAquaRoom().setRoomInfo(packet);
	}

	private void NETWORK_JOINROOM_ACK(NetJoinRoomAck packet) {
		if( packet.getOk() == -1 ) {
			JOptionPane.showMessageDialog(null, "존재하지 않는 방입니다.");
			return;
		}
		
		if( packet.getOk() == -2 ) {
			JOptionPane.showMessageDialog(null, "게임이 시작된 방입니다.");
			return;
		}
		
		if( packet.getOk() == -3 ) {
			JOptionPane.showMessageDialog(null, "방 인원이 꽉 찼습니다.");
			return;
		}
		
		// 방 입장
		
		switch( packet.getGamemode() ) {
		case 0:
			Main.getMain().changeScene(Scene.SCENE_AQUAROOM);
			break;
		default:
			JOptionPane.showMessageDialog(null, "방의 게임모드가 잘못되었습니다.");
			break;
		}
	}

	private void NETWORK_KICKROOM_NOT(NetKickRoomNot packet) {
		switch( packet.getWhy() ) {
		case NetKickRoomNot.WHY_HOSTQUIT:
			JOptionPane.showMessageDialog(null, "방장이 방을 떠났습니다.");
			break;
		case NetKickRoomNot.WHY_KICK:
			JOptionPane.showMessageDialog(null, "방장이 당신을 강퇴하였습니다.");
			break;
		}
		
		Main.getMain().changeScene(Scene.SCENE_LOBBY);
	}

	private void NETWORK_STARTGAME_NOT(NetStartGameNot packet) {
		switch( packet.getGameMode() ) {
		case 0: // 아쿠아
			Main.getMain().changeScene(Scene.SCENE_AQUAGAME);
			Main.getMain().getSceneAquaGame().setGamePlayer(packet);
			Main.getMain().getSceneAquaGame().startGame();
			break;
		}
	}

	private void NETWORK_AQUA_PLAYER_NOT(NetAquaPlayerNot packet) {		
		if( Main.getMain().getSceneState() != Scene.SCENE_AQUAGAME )
			return;
		
		Main.getMain().getSceneAquaGame().processAquaPlayerNot(packet);
	}

	private void NETWORK_AQUA_SHOOT_NOT(NetAquaShootNot packet) {		
		if( Main.getMain().getSceneState() != Scene.SCENE_AQUAGAME )
			return;
		
		Main.getMain().getSceneAquaGame().processAquaShootNot(packet);
	}
	
	private void NETWORK_ENDGAME_NOT(NetEndGameNot packet) {
		if( Main.getMain().getSceneState() == Scene.SCENE_AQUAGAME ) {
			Main.getMain().changeScene(Scene.SCENE_AQUAROOM);
			Main.getMain().getSceneAquaRoom().setWinner(packet);
		}
	}
}
