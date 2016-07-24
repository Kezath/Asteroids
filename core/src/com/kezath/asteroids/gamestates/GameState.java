package com.kezath.asteroids.gamestates;

import com.kezath.asteroids.managers.GameStateManager;

/**
 * Created by Sebastian on 23.07.2016.
 */
public abstract class GameState {

    protected GameStateManager gameStateManager;

    protected GameState(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;

        init();
    }

    public abstract void init();
    public abstract void update(float deltaTime);
    public abstract void draw();
    public abstract void handleInput();
    public abstract void dispose();
}
