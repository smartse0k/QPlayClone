package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import main.Config;
import network.*;

public class Session {
	private Server parentserver;
	private Socket socket;
	
	private OutputStream os = null;
	private ObjectOutputStream oos = null;
	
	private ServerRecvHandler recvHandler = null;
	private Thread threadRecvHandler = null;
	
	private String nick;
	private Room currentroom = null;
	
	public Session(Server server, Socket socket) {
		this.parentserver = server;
		this.socket = socket;

		try {
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		recvHandler = new ServerRecvHandler(this);
		threadRecvHandler = new Thread(recvHandler);
		threadRecvHandler.start();
	}
	
	public Server getServer() { return parentserver; }
	public Socket getSocket() { return socket; }
	public String getNick() { if( nick == null ) return "*"; else return nick; }
	public void setNick(String n) { nick = n; }
	public void setCurrentRoom(Room r) { currentroom = r; }
	public Room getCurrentRoom() { return currentroom; }
	
	synchronized public void sendPacket(int command, Object obj) {
		try {
			oos.writeObject(command);
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parentserver.removeSession(this);
	}
	
	public void sendRoomlist(NetRoomListReq packet) {
		Vector<Room> vecRoom = getServer().getRooms();
		
		NetRoomListNot ret = new NetRoomListNot();
		
		for( int i=0; i<vecRoom.size(); i++ )
			ret.addRoom(vecRoom.get(i).getId(), 
					    vecRoom.get(i).getGamemode(),
					    vecRoom.get(i).getTitle(),
					    vecRoom.get(i).getPasswd().length() > 0 ? true : false);
		
		sendPacket(Config.NETWORK_ROOMLIST_NOT, ret);
	}
}
