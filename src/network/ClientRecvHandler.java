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
		
		Main.getMain().showError("������ ������ ���������ϴ�.");
	}
	
	private void process(int command) {
		try {
			switch(command) {
			case Config.NETWORK_LOGIN_ACK: // �α��� �Ϸ�
				NETWORK_LOGIN_ACK( (NetLoginAck)ois.readObject() );
				break;
			case Config.NETWORK_CHAT_REQACK: // ä��
				NETWORK_CHAT_REQACK( (NetChatReqAck)ois.readObject() );
				break;
			case Config.NETWORK_PLAYER_LIST_NOT: // ���� ���
				NETWORK_PLAYER_LIST_NOT( (NetPlayerListNot)ois.readObject() );
				break;
			case Config.NETWORK_ROOMLIST_NOT: // �� ���
				NETWORK_ROOMLIST_NOT( (NetRoomListNot)ois.readObject() );
				break;
			case Config.NETWORK_MAKEROOM_ACK: // �� ����� �Ϸ�
				NETWORK_MAKEROOM_ACK( (NetMakeRoomAck)ois.readObject() );
				break;
			case Config.NETWORK_ROOMINFO_NOT: // �� ���� ����
				NETWORK_ROOMINFO_NOT( (NetRoomInfoNot)ois.readObject() );
				break;
			case Config.NETWORK_JOINROOM_ACK: // �� ���� �Ϸ�
				NETWORK_JOINROOM_ACK( (NetJoinRoomAck)ois.readObject() );
				break;
			case Config.NETWORK_KICKROOM_NOT: // �濡�� ����
				NETWORK_KICKROOM_NOT( (NetKickRoomNot)ois.readObject() );
				break;
			case Config.NETWORK_STARTGAME_NOT: // ���ӽ���
				NETWORK_STARTGAME_NOT( (NetStartGameNot)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_PLAYER_NOT: // ����� �÷��̾�
				NETWORK_AQUA_PLAYER_NOT( (NetAquaPlayerNot)ois.readObject() );
				break;
			case Config.NETWORK_AQUA_SHOOT_NOT: // ����� �̻���
				NETWORK_AQUA_SHOOT_NOT( (NetAquaShootNot)ois.readObject() );
				break;
			case Config.NETWORK_ENDGAME_NOT: // ���� ����
				NETWORK_ENDGAME_NOT( (NetEndGameNot)ois.readObject() );
				break;
			default: // �� �� ���� ��Ŷ
				ois.readObject(); // �׳� ������.
				break;
			}
		} catch (IOException e) {

		} catch(ClassNotFoundException e) {
			
		}
	}

	private void NETWORK_LOGIN_ACK( NetLoginAck packet ) {
		switch( packet.getRet() ) {
		case NetLoginAck.LOGIN_SUCCESS: // �α��� ����
			System.out.println("NETWORK_LOGIN_ACK() " + packet.getNick());
			Main.getMain().setNick(packet.getNick());
			Main.getMain().changeScene(Scene.SCENE_LOBBY);
			break;
		case NetLoginAck.LOGIN_EXISTNICK: // �г���
			JOptionPane.showMessageDialog(null, "�ٸ� ����� ���� ������� �г����Դϴ�.");
			break;
		case NetLoginAck.LOGIN_DIFFERENTVERSION: // ����
			JOptionPane.showMessageDialog(null, "������ �޶� ������ �� �����ϴ�. ������ ���� ������ ���� ������ �ٿ�ε� �޾� �������ּ���.");
			break;
		case NetLoginAck.LOGIN_BAN: // ��
			JOptionPane.showMessageDialog(null, "���� ������ ������ �����Ǿ� �ֽ��ϴ�.");
			break;
		default: // ��Ÿ
			JOptionPane.showMessageDialog(null, "��Ÿ ������ �߻��Ͽ����ϴ�.");
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
		// �κ��϶��� ó���Ѵ�
		if( Main.getMain().getSceneState() == Scene.SCENE_LOBBY ) {
			Main.getMain().getSceneLobby().setPlayerList( packet );
		}
	}
	
	private void NETWORK_ROOMLIST_NOT(NetRoomListNot packet ) {
		// �κ��϶��� ó���Ѵ�
		if( Main.getMain().getSceneState() == Scene.SCENE_LOBBY ) {
			Main.getMain().getSceneLobby().setRoomList( packet );
		}
	}

	private void NETWORK_MAKEROOM_ACK(NetMakeRoomAck packet) {
		if( packet.getOk() == 1 ) {
			Main.getMain().setRoom(packet.getRoomId());
			
			switch( packet.getRoomGameMode() ) {
			case 0: // �����
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
		JOptionPane.showMessageDialog(null, "���� ����µ� �����Ͽ����ϴ�.");
	}
	
	private void NETWORK_ROOMINFO_NOT(NetRoomInfoNot packet) {
		// �����?
		if( Main.getMain().getSceneState() == Scene.SCENE_AQUAROOM )
			Main.getMain().getSceneAquaRoom().setRoomInfo(packet);
	}

	private void NETWORK_JOINROOM_ACK(NetJoinRoomAck packet) {
		if( packet.getOk() == -1 ) {
			JOptionPane.showMessageDialog(null, "�������� �ʴ� ���Դϴ�.");
			return;
		}
		
		if( packet.getOk() == -2 ) {
			JOptionPane.showMessageDialog(null, "������ ���۵� ���Դϴ�.");
			return;
		}
		
		if( packet.getOk() == -3 ) {
			JOptionPane.showMessageDialog(null, "�� �ο��� �� á���ϴ�.");
			return;
		}
		
		// �� ����
		
		switch( packet.getGamemode() ) {
		case 0:
			Main.getMain().changeScene(Scene.SCENE_AQUAROOM);
			break;
		default:
			JOptionPane.showMessageDialog(null, "���� ���Ӹ�尡 �߸��Ǿ����ϴ�.");
			break;
		}
	}

	private void NETWORK_KICKROOM_NOT(NetKickRoomNot packet) {
		switch( packet.getWhy() ) {
		case NetKickRoomNot.WHY_HOSTQUIT:
			JOptionPane.showMessageDialog(null, "������ ���� �������ϴ�.");
			break;
		case NetKickRoomNot.WHY_KICK:
			JOptionPane.showMessageDialog(null, "������ ����� �����Ͽ����ϴ�.");
			break;
		}
		
		Main.getMain().changeScene(Scene.SCENE_LOBBY);
	}

	private void NETWORK_STARTGAME_NOT(NetStartGameNot packet) {
		switch( packet.getGameMode() ) {
		case 0: // �����
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
