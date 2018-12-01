package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import main.Config;
import main.Main;

public class Client {
	private Socket s = null;
	private boolean isConnected = false;
	
	private ClientRecvHandler recvHandler = null;
	private Thread threadRecvHandler = null;
	
	private OutputStream os = null;
	private ObjectOutputStream oos = null;
	
	public Client(String Host, int Port) {
		isConnected = false;
		
		try {
			s = new Socket();
			s.connect(new InetSocketAddress(InetAddress.getByName(Host), Port), 3000);
			
			os = s.getOutputStream();
			oos = new ObjectOutputStream(os);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		recvHandler = new ClientRecvHandler(this);
		threadRecvHandler = new Thread(recvHandler);
		threadRecvHandler.start();
		
		isConnected = true;
	}
	
	public Socket getSocket() {
		return s;
	}
	
	public boolean getIsConnected() {
		return isConnected;
	}
	
	synchronized public void sendPacket(int command, Object obj) {
		try {
			oos.writeObject(command);
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendChat(String chat) {
		NetChatReqAck packet = new NetChatReqAck();
		packet.setNick(Main.getMain().getNick());
		packet.setChat(chat);
		sendPacket(Config.NETWORK_CHAT_REQACK, packet);
	}
}
