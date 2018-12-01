package network;

import java.io.Serializable;

public class NetChatReqAck implements Serializable {
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_WHISPER = 2;
	public static final int TYPE_ADMIN = 3;
	
	private String nick; // 닉네임
	private String chat; // 채팅
	
	public void setChat(String c) { chat = c; }
	public void setNick(String n) { nick = n; }
	
	public String getChat() { return chat; }
	public String getNick() { return nick; }
}
