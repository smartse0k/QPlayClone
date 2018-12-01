package network;

import java.io.Serializable;

public class NetLoginAck implements Serializable {
	public static final int LOGIN_SUCCESS = 1;
	public static final int LOGIN_EXISTNICK = 2;
	public static final int LOGIN_DIFFERENTVERSION = 3;
	public static final int LOGIN_BAN = 4;
	
	private int ret; // ���
	private boolean use_message; // �޽��� �����ٱ�
	private String message; // �޽��� ����
	private String nick; // ������ �г���
	
	public void setRet(int r) { ret = r; }
	public void setUseMsg(boolean u) { use_message = u; }
	public void setMsg(String m) { message = m; }
	public void setNick(String n) { nick = n; }
	
	public int getRet() { return ret; }
	public boolean getUseMsg() { return use_message; }
	public String getMsg() { return message; }
	public String getNick() { return nick; }
}
