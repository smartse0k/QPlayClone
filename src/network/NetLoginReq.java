package network;

import java.io.Serializable;

public class NetLoginReq implements Serializable {
	private String nick;
	private String version;
	
	public void setNick(String n) {
		nick = n;
	}
	
	public void setVersion(String v) {
		version = v;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getVersion() {
		return version;
	}
}
