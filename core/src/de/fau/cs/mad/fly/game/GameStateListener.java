package de.fau.cs.mad.fly.game;

import de.fau.cs.mad.fly.game.GameController.GameState;

public interface GameStateListener {
    public void gameStateChanged(GameState newGameState);
}
