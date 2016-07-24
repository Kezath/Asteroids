package com.kezath.asteroids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.kezath.asteroids.Game;
import com.kezath.asteroids.entities.Asteroid;
import com.kezath.asteroids.managers.GameInputProcessor;
import com.kezath.asteroids.managers.GameStateManager;
import com.kezath.asteroids.managers.Save;

import java.util.ArrayList;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class MenuState extends GameState {

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private String[] menuItems;
    private Rectangle[] menuItemsHitBoxes;

    private ArrayList<Asteroid> asteroids;

    public MenuState(GameStateManager gameStateManager) {
        super(gameStateManager);
    }

    @Override
    public void init() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        menuItems = new String[] {
                "Play",
                "Highscores",
                "Exit"
        };

        menuItemsHitBoxes = new Rectangle[menuItems.length];

        GlyphLayout glyphLayout = new GlyphLayout();
        for (int i = 0; i < menuItems.length; i++) {
            glyphLayout.setText(Game.STANDARD_FONT, menuItems[i]);
            menuItemsHitBoxes[i] = new Rectangle(
                    (Game.WIDTH - glyphLayout.width) / 2,
                    800 - (i * glyphLayout.height * 3),
                    glyphLayout.width,
                    glyphLayout.height);
        }

        Save.load();

        asteroids = new ArrayList<Asteroid>();
        for (int i = 0; i < 6; i++) {
            float x = MathUtils.random(Game.WIDTH/8, Game.WIDTH - Game.WIDTH/8);
            float y = MathUtils.random(Game.HEIGHT, Game.HEIGHT * 2);

            asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
        }
    }

    @Override
    public void update(float deltaTime) {
        handleInput();

        for (Asteroid asteroid : asteroids) {
            asteroid.update(deltaTime);
            if (asteroid.shouldRemove()) {
                asteroid.spawnAgain();
            }
        }
    }

    @Override
    public void draw() {
        spriteBatch.setProjectionMatrix(Game.camera.combined);
        shapeRenderer.setProjectionMatrix(Game.camera.combined);

        //draw asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(shapeRenderer);
        }

        spriteBatch.begin();

        //draw title
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(Game.TITLE_FONT, Game.TITLE);
        Game.TITLE_FONT.draw(
                    spriteBatch,
                    Game.TITLE,
                    Game.TITLES_POSITION.x - (glyphLayout.width / 2),
                    Game.TITLES_POSITION.y);

        //draw menu
        for (int i = 0; i < menuItems.length; i++) {
            Game.STANDARD_FONT.draw(
                    spriteBatch,
                    menuItems[i],
                    menuItemsHitBoxes[i].getX(),
                    menuItemsHitBoxes[i].getY() + menuItemsHitBoxes[i].getHeight());
        }

        spriteBatch.end();
    }

    @Override
    public void handleInput() {
        if (GameInputProcessor.isReleased()) {
            final int releasedX = GameInputProcessor.getReleasedX();
            final int releasedY = GameInputProcessor.getReleasedY();

            int selectedItem = -1;
            for (int i = 0; i < menuItemsHitBoxes.length; i++) {
                if (menuItemsHitBoxes[i].contains(releasedX, releasedY)) {
                    selectedItem = i;
                    break;
                }
            }

            if (selectedItem == 0) {
                gameStateManager.setState(GameStateManager.PLAY);
            } else if (selectedItem == 1) {
                gameStateManager.setState(GameStateManager.HIGH_SCORES);
            } else if (selectedItem == 2) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
