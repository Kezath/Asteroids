package com.kezath.asteroids.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.kezath.asteroids.Game;

/**
 * Created by Sebastian on 23.07.2016.
 */
public class GameInputProcessor extends InputAdapter {

    // true - pressed, false - not pressed
    private static boolean state = false;
    private static boolean previousState = false;

    private static int touchedX;
    private static int touchedY;

    private static int releasedX;
    private static int releasedY;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        state = true;
        touchedX = screenX;
        touchedY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        state = false;
        releasedX = screenX;
        releasedY = screenY;
        return true;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        touchedX = screenX;
        touchedY = screenY;
        return true;
    }

    public static void update() {
        previousState = state;
    }

    public static boolean isDown() {
        return state && !previousState;
    }

    public static boolean isPressed() {
        return state && previousState;
    }

    public static boolean isReleased() {
        return !state && previousState;
    }

    public static boolean isLeftScreenPartPressed() {
        return isPressed() && (touchedX < Game.WIDTH / 2);
    }

    public static boolean isRightScreenPartPressed() {
        return isPressed() && (touchedX > Game.WIDTH / 2);
    }

    public static int getReleasedX() {
        return releasedX;
    }

    public static int getReleasedY() {
        return Game.HEIGHT - releasedY;
    }

    public static boolean isBackPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.BACK);
    }
}
