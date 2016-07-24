package com.kezath.asteroids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.kezath.asteroids.managers.GameInputProcessor;
import com.kezath.asteroids.managers.GameStateManager;
import com.kezath.asteroids.managers.Jukebox;

/**
 * Created by Sebastian on 23.07.2016.
 */
public class Game extends ApplicationAdapter {
    public static int WIDTH;
    public static int HEIGHT;

	public final static String TITLE = "Asteroids";
	public static Vector2 TITLES_POSITION;
	public static BitmapFont TITLE_FONT;
	public static BitmapFont STANDARD_FONT;

    public static OrthographicCamera camera;

    private GameStateManager gameStateManager;
	
	@Override
	public void create () {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

		TITLES_POSITION = new Vector2(Game.WIDTH / 2, 1500);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HyperspaceBold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 130;
		parameter.color = Color.WHITE;
		TITLE_FONT = generator.generateFont(parameter);

		parameter.size = 80;
		parameter.color = Color.WHITE;
		STANDARD_FONT = generator.generateFont(parameter);

        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.translate(WIDTH / 2, HEIGHT / 2);
        camera.update();

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(new GameInputProcessor());

		Jukebox.load("sounds/explode.ogg", "explode");
		Jukebox.load("sounds/extralife.ogg", "extralife");
		Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
		Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
		Jukebox.load("sounds/pulselow.ogg", "pulselow");
		Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
		Jukebox.load("sounds/shoot.ogg", "shoot");
		Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");
		Jukebox.load("sounds/thruster.ogg", "thruster");

        gameStateManager = new GameStateManager();
	}

	@Override
	public void render () {

        //Clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameStateManager.update(Gdx.graphics.getDeltaTime());
        gameStateManager.draw();

        GameInputProcessor.update();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
        TITLE_FONT.dispose();
        STANDARD_FONT.dispose();
	}
}
