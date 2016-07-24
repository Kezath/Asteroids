package com.kezath.asteroids.gamestates;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kezath.asteroids.Game;
import com.kezath.asteroids.managers.GameInputProcessor;
import com.kezath.asteroids.managers.GameStateManager;
import com.kezath.asteroids.managers.Save;

import java.util.Locale;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class HighScoresState extends GameState {

    private SpriteBatch spriteBatch;

    private static final String HIGH_SCORES_TITLE = "High Scores";

    private long[] highScores;
    private String[] names;

    public HighScoresState(GameStateManager gameStateManager) {
        super(gameStateManager);
    }

    @Override
    public void init() {
        spriteBatch = new SpriteBatch();

        highScores = Save.gameData.getHighScores();
        names = Save.gameData.getNames();
    }

    @Override
    public void update(float deltaTime) {
        handleInput();
    }

    @Override
    public void draw() {
        spriteBatch.setProjectionMatrix(Game.camera.combined);
        spriteBatch.begin();

        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(Game.TITLE_FONT, HIGH_SCORES_TITLE);
        Game.TITLE_FONT.draw(spriteBatch, HIGH_SCORES_TITLE, Game.TITLES_POSITION.x - glyphLayout.width / 2, Game.TITLES_POSITION.y);

        for (int i = 0; i < highScores.length; i++) {
            String string = String.format(Locale.ENGLISH, "%2d. %7s %s", i + 1, highScores[i], names[i]);

            glyphLayout.setText(Game.STANDARD_FONT, string);
            Game.STANDARD_FONT.draw(spriteBatch, string, (Game.WIDTH - glyphLayout.width) / 2, 1300 - (i * glyphLayout.height * 2));
        }

        spriteBatch.end();
    }

    @Override
    public void handleInput() {
        if (GameInputProcessor.isBackPressed()) {
            gameStateManager.setState(GameStateManager.MENU);
            return;
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
