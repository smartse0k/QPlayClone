package main;

import java.awt.Color;
import java.awt.Font;

public class Config {
	public static final String SERVER_LIST[] = {"�÷���������"};
	public static final String SERVER_LIST_MORE[] = {"�÷���������", "�ٸ� ����� ���� ����", "���� �����"};
	public static final String SERVER_PLAYGCKOM = "qplayclone.gckom.com";
	public static final String HOMEPAGE = "http://www.gckom.com/qplayclone";
	public static final int SERVER_PORT = 10100;
	
	public static final int GAMEWINDOW_WIDTH = 1024;
	public static final int GAMEWINDOW_HEIGHT = 768;
	
	public static final String GAME_VERSION = "2017.09.22";
	
	public static final String GAMEMODE[] = {"�����", "�ܹ���", "�ö�Ÿ��"};
	
	public static final Font GULIMCHE9 = new Font("����ü", Font.PLAIN, 12);
	public static final Font GULIMCHE10 = new Font("����ü", Font.PLAIN, 13);
	public static final Font GULIMCHE12 = new Font("����ü", Font.PLAIN, 15);
	
	public static final Color COLOR_DARKBLUE = new Color(0, 0, 128);
	public static final Color COLOR_PURPLE = new Color(128, 0, 255);
	
	public static final int NETWORK_LOGIN_REQ = 1; // �α��� ��û
	public static final int NETWORK_LOGIN_ACK = 2; // �α��� ���
	public static final int NETWORK_CHAT_REQACK = 3; // ä��
	public static final int NETWORK_PLAYER_LIST_NOT = 4; // ���� ����Ʈ
	public static final int NETWORK_ROOMLIST_REQ = 5; // �� ��� ��û
	public static final int NETWORK_ROOMLIST_NOT = 6; // �� ��� ����
	public static final int NETWORK_MAKEROOM_REQ = 7; // �� ����� ��û
	public static final int NETWORK_MAKEROOM_ACK = 8; // �� ����� ���
	public static final int NETWORK_JOINROOM_REQ = 9; // �� ���� ��û
	public static final int NETWORK_JOINROOM_ACK = 10; // �� ���� ���
	public static final int NETWORK_ROOMINFO_NOT = 11; // �� ���� ����
	public static final int NETWORK_CHANGETEAM_REQ = 12; // �� ���� ����
	public static final int NETWORK_QUITROOM_REQ = 13; // �� ������ ��û
	public static final int NETWORK_KICKROOM_NOT = 14; // �濡�� ����
	public static final int NETWORK_STARTGAME_REQ = 15; // ���ӽ��� ��û
	public static final int NETWORK_STARTGAME_NOT = 16; // ���ӽ��� �ˤ�
	public static final int NETWORK_AQUA_PLAYER_NOT = 17; // ����� �÷��̾� ����
	public static final int NETWORK_AQUA_SHOOT_NOT = 18; // ����� �̻��� �߻�
	public static final int NETWORK_ENDGAME_NOT = 19; // ��������
	
	// ����� ����
	public static final int AQUA_DEVICE_WIDTH = 200;
	public static final int AQUA_DEVICE_HEIGHT = 178;
	public static final int AQUA_MAP_WIDTH = 2250;
	public static final int AQUA_MAP_HEIGHT = 1152;
	public static final int AQUA_BULLET_WIDTH[] = {106, 105, 105, 54};
	public static final int AQUA_BULLET_HEIGHT[] = {32, 32, 32, 32};
	
	public static int GET_GAME_MAXPLAYER(int GAMEMODE) {
		if( GAMEMODE == 0 ) return 6; // �����
		return -1;
	}
}
