package de.fau.cs.mad.fly.game;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.GameOverlay;
import de.fau.cs.mad.fly.res.Level;

public class Game {
	private Fly game;
	private GameOverlay gameOverlay;
	private CameraController cameraController;
	
	private Level level;

	private boolean isRunning;
	
	public Game(Fly game, GameOverlay gameOverlay, CameraController cameraController) {
		this.game = game;
		this.gameOverlay = gameOverlay;
		this.cameraController = cameraController;
	}
	
	public void initGame() {
		//level = new Level("Level XYZ");
		// Level-Constructor includes:
		//   load level from file
		//   load models, textures, ... needed for this level from files
		//   create 3D objects for the level
		//   stores List of Rings, Lists of other stuff in the level
		
		//player = new Player();
		// Player-Constructor includes:
		//   mix/connect with camera controller ?
		//   create 3D objects for the player
		//   stores position and other attributes of player
		
		//gameOverlay.initOverlay()
	}
	
	public void startGame() {
		isRunning = true;
	}
	
	public void stopGame() {
		isRunning = false;
		
		//level.dispose();
		//player.dispose();
		// ...
	}
	
	public void setRunning(boolean running) {
		isRunning = running;
	}

	public void render(float delta) {
		if(!isRunning)
			return;
		
		// check if game is finished
		//stopGame();
		
		// fetch input from camera controller
		
		// calculate new positions, camera etc.
		
		// do collision stuff, level internal and with player
		// level.checkCollision(player);
		
		// render level (static + dynamic -> split render method?)
		// level.render();
		
		// render player
		// player.render();
		
		// update game overlay
		// gameOverlay.setXYZ();
		
		// update time, points, fuel, whatever.. (here, in level or in player class?)
	}
	

}
