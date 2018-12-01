package network;

import java.io.Serializable;

public class NetLoginAck implements Serializable {
	public static final int LOGIN_SUCCESS = 1;
	public static final int LOGIN_EXISTNICK = 2;
	public static final int LOGIN_DIFFERENTVERSION = 3;
	public static final int LOGIN_BAN = 4;
	
	private int ret; // 결과
	private boolean use_message; // 메시지 보여줄까
	private String message; // 메시지 내용
	private String nick; // 조정된 닉네임
	
	public void setRet(int r) { ret = r; }
	public void setUseMsg(boolean u) { use_message = u; }
	public void setMsg(String m) { message = m; }
	public void setNick(String n) { nick = n; }
	
	public int getRet() { return ret; }
	public boolean getUseMsg() { return use_message; }
	public String getMsg() { return message; }
	public String getNick() { return nick; }
}
