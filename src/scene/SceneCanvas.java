package scene;

import main.Main;

public class SceneCanvas implements Runnable {
	public void run() {
		Main.getMain();
		Main.getMain().getNick();
		
		while( true ) {
			switch( Main.getMain().getSceneState() ) {
			case Scene.SCENE_LOBBY:
				UpdateLobby();
				break;
			case Scene.SCENE_AQUAROOM:
				updateAquaRoom();
				break;
			case Scene.SCENE_AQUAGAME:
				updateAquaGame();
				sleep(1);
				break;
			default:
				sleep(1);
				break;
			}
		}
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void UpdateLobby() {
		
		sleep(1);
	}
	
	public void updateAquaRoom() {
		//Main.getMain().getSceneAquaRoom().
		
		sleep(1);
	}
	
	private void updateAquaGame() {
		if( Main.getMain().getSceneAquaGame().getGameState() == SceneAquaGame.GAMESTATE_PLAYING ) {
			Main.getMain().getSceneAquaGame().NextGameFrame();
		}
		
		sleep(10);
	}
}
