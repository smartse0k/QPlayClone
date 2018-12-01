package main;

import java.awt.Color;
import java.awt.Font;

public class Config {
	public static final String SERVER_LIST[] = {"플레이지씨컴"};
	public static final String SERVER_LIST_MORE[] = {"플레이지씨컴", "다른 사람이 만든 서버", "서버 만들기"};
	public static final String SERVER_PLAYGCKOM = "qplayclone.gckom.com";
	public static final String HOMEPAGE = "http://www.gckom.com/qplayclone";
	public static final int SERVER_PORT = 10100;
	
	public static final int GAMEWINDOW_WIDTH = 1024;
	public static final int GAMEWINDOW_HEIGHT = 768;
	
	public static final String GAME_VERSION = "2017.09.22";
	
	public static final String GAMEMODE[] = {"아쿠아", "햄버거", "올라타자"};
	
	public static final Font GULIMCHE9 = new Font("굴림체", Font.PLAIN, 12);
	public static final Font GULIMCHE10 = new Font("굴림체", Font.PLAIN, 13);
	public static final Font GULIMCHE12 = new Font("굴림체", Font.PLAIN, 15);
	
	public static final Color COLOR_DARKBLUE = new Color(0, 0, 128);
	public static final Color COLOR_PURPLE = new Color(128, 0, 255);
	
	public static final int NETWORK_LOGIN_REQ = 1; // 로그인 요청
	public static final int NETWORK_LOGIN_ACK = 2; // 로그인 결과
	public static final int NETWORK_CHAT_REQACK = 3; // 채팅
	public static final int NETWORK_PLAYER_LIST_NOT = 4; // 유저 리스트
	public static final int NETWORK_ROOMLIST_REQ = 5; // 방 목록 요청
	public static final int NETWORK_ROOMLIST_NOT = 6; // 방 목록 전송
	public static final int NETWORK_MAKEROOM_REQ = 7; // 방 만들기 요청
	public static final int NETWORK_MAKEROOM_ACK = 8; // 방 만들기 결과
	public static final int NETWORK_JOINROOM_REQ = 9; // 방 입장 요청
	public static final int NETWORK_JOINROOM_ACK = 10; // 방 입장 결과
	public static final int NETWORK_ROOMINFO_NOT = 11; // 방 정보 전송
	public static final int NETWORK_CHANGETEAM_REQ = 12; // 방 정보 전송
	public static final int NETWORK_QUITROOM_REQ = 13; // 방 나가기 요청
	public static final int NETWORK_KICKROOM_NOT = 14; // 방에서 강퇴
	public static final int NETWORK_STARTGAME_REQ = 15; // 게임시작 요청
	public static final int NETWORK_STARTGAME_NOT = 16; // 게임시작 알ㄻ
	public static final int NETWORK_AQUA_PLAYER_NOT = 17; // 아쿠아 플레이어 전송
	public static final int NETWORK_AQUA_SHOOT_NOT = 18; // 아쿠아 미사일 발사
	public static final int NETWORK_ENDGAME_NOT = 19; // 게임종료
	
	// 아쿠아 관련
	public static final int AQUA_DEVICE_WIDTH = 200;
	public static final int AQUA_DEVICE_HEIGHT = 178;
	public static final int AQUA_MAP_WIDTH = 2250;
	public static final int AQUA_MAP_HEIGHT = 1152;
	public static final int AQUA_BULLET_WIDTH[] = {106, 105, 105, 54};
	public static final int AQUA_BULLET_HEIGHT[] = {32, 32, 32, 32};
	
	public static int GET_GAME_MAXPLAYER(int GAMEMODE) {
		if( GAMEMODE == 0 ) return 6; // 아쿠아
		return -1;
	}
}
