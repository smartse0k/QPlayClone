package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerAcceptor implements Runnable {
	private Server parent;
	
	private ServerSocket ss = null;
	private int port;
	
	public ServerAcceptor(Server p, int port) {
		parent = p;
		this.port = port;
	}
	
	public void run() {
		try {
			ss = new ServerSocket(port);
			Socket temp_socket = null;
			
			while( true ) {
				temp_socket = ss.accept();
				
				System.out.println("[ServerAcceptor] 연결이 수락되었습니다. (" + temp_socket.getInetAddress().getHostAddress() + ")");
				
				Session session = new Session(parent, temp_socket);
				parent.addSession(session);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
