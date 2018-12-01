package scene;

import java.awt.event.*;

import javax.swing.*;

public class Scene {
	public final static int SCENE_LOGIN = 1;
	public final static int SCENE_LOBBY = 2;
	public final static int SCENE_MAKEROOM = 3;
	public final static int SCENE_AQUAROOM = 4;
	public final static int SCENE_AQUAGAME = 5;
	
	protected KeyListener keylistener;
	
	protected JFrame parent = null;
	
	public Scene(JFrame parent) {
		this.parent = parent;
		keylistener = null;
	}
	
	public void removeKeyListener() {
		if( keylistener != null ) 
			parent.getContentPane().removeKeyListener(keylistener);
	}
	
	public void attachKeyListener(KeyListener l) {
		removeKeyListener();		
		keylistener = l;
		parent.getContentPane().addKeyListener(keylistener);
	}
	
	public void removeAll() { removeKeyListener(); parent.getContentPane().removeAll(); }
	public void repaint() { parent.getContentPane().revalidate(); parent.getContentPane().repaint(); } 
}
