package com.kezath.asteroids.managers;

import com.badlogic.gdx.Gdx;
import com.kezath.asteroids.gamestates.GameOverState;
import com.kezath.asteroids.gamestates.GameState;
import com.kezath.asteroids.gamestates.HighScoresState;
import com.kezath.asteroids.gamestates.MenuState;
import com.kezath.asteroids.gamestates.PlayState;

/**
 * Created by Sebastian on 23.07.2016.
 */
public class GameStateManager {

    private GameState currentGameState;

    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int HIGH_SCORES = 2;
    public static final int GAME_OVER = 3;

    public GameStateManager() {
        setState(MENU);
    }

    public void setState(int state) {
        Gdx.input.setInputProcessor(new GameInputProcessor());
        if (currentGameState != null) {
            currentGameState.dispose();
        }

        if (state == MENU) {
            currentGameState = new MenuState(this);
        } else if (state == PLAY) {
            currentGameState = new PlayState(this);
        } else if (state == HIGH_SCORES) {
            currentGameState = new HighScoresState(this);
        } else if (state == GAME_OVER) {
            currentGameState = new GameOverState(this);
        }
    }

    public void update(float deltaTime) {
        currentGameState.update(deltaTime);
    }

    public void draw() {
        currentGameState.draw();
    }
}
