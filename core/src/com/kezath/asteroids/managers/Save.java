package com.kezath.asteroids.managers;

import com.badlogic.gdx.Gdx;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class Save {

    public static GameData gameData;

    private static final String SAVE_FILE_NAME = "highscores.sav";

    public static void save() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(Gdx.files.local(SAVE_FILE_NAME).file())
            );
            objectOutputStream.writeObject(gameData);
            objectOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static void load() {
        try {
            if (!isSaveFileExists()) {
                init();
                return;
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(Gdx.files.local(SAVE_FILE_NAME).file())
            );
            gameData = (GameData) objectInputStream.readObject();
            objectInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static boolean isSaveFileExists() {
        return Gdx.files.local(SAVE_FILE_NAME).file().exists();
    }

    public static void init() {
        gameData = new GameData();
        gameData.init();
        save();
    }
}
