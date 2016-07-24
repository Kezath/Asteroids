package com.kezath.asteroids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kezath.asteroids.Game;
import com.kezath.asteroids.managers.GameInputProcessor;
import com.kezath.asteroids.managers.GameStateManager;
import com.kezath.asteroids.managers.Save;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class GameOverState extends GameState {

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private static final String GAME_OVER_TITLE = "Game Over";

    private static final String SAVE_TITLE = "Save";
    private static Rectangle SAVE_TITLE_HIT_BOX;
    private static Rectangle NEW_NAME_HIT_BOX;

    private boolean newHighScore;
    private TextField newName;
    private TextButton buttonSave;

    private Stage stage;

    public GameOverState(GameStateManager gameStateManager) {
        super(gameStateManager);
    }

    @Override
    public void init() {

        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(Game.STANDARD_FONT, SAVE_TITLE);
        SAVE_TITLE_HIT_BOX = new Rectangle((Game.WIDTH - glyphLayout.width) / 2, 300, glyphLayout.width, glyphLayout.height);
        glyphLayout.setText(Game.STANDARD_FONT, "Name");
        NEW_NAME_HIT_BOX = new Rectangle((Game.WIDTH - glyphLayout.width) / 2, 800, glyphLayout.width, glyphLayout.height);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        newHighScore = Save.gameData.isHighScore(Save.gameData.getTentativeScore());
        if (newHighScore) {
            stage = new Stage();

            Gdx.input.setInputProcessor(stage);

            TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
            textFieldStyle.font = Game.STANDARD_FONT;
            textFieldStyle.fontColor = Color.WHITE;

            newName = new TextField("Name", textFieldStyle);
            newName.setPosition(NEW_NAME_HIT_BOX.getX(), NEW_NAME_HIT_BOX.getY());
            newName.setSize(NEW_NAME_HIT_BOX.getWidth(), NEW_NAME_HIT_BOX.getHeight());
            newName.setMaxLength(6);
            newName.addListener(new InputListener() {
                @Override
                public boolean keyTyped(InputEvent event, char character) {
                    GlyphLayout glyphLayout = new GlyphLayout();
                    glyphLayout.setText(Game.STANDARD_FONT, newName.getText());
                    NEW_NAME_HIT_BOX = new Rectangle((Game.WIDTH - glyphLayout.width) / 2, 800, glyphLayout.width, glyphLayout.height);
                    newName.setPosition(NEW_NAME_HIT_BOX.getX(), NEW_NAME_HIT_BOX.getY());
                    newName.setSize(NEW_NAME_HIT_BOX.getWidth(), NEW_NAME_HIT_BOX.getHeight());
                    return true;
                }
            });
            stage.addActor(newName);

            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = Game.STANDARD_FONT;
            textButtonStyle.fontColor = Color.WHITE;

            buttonSave = new TextButton("Save", textButtonStyle);
            buttonSave.setPosition(SAVE_TITLE_HIT_BOX.getX(), SAVE_TITLE_HIT_BOX.getY());
            buttonSave.setSize(SAVE_TITLE_HIT_BOX.getWidth(), SAVE_TITLE_HIT_BOX.getHeight());
            buttonSave.addListener(new ClickListener() {
                @Override
                public void touchUp(InputEvent e, float x, float y, int point, int button) {
                    Save.gameData.addHighScore(Save.gameData.getTentativeScore(), newName.getText());
                    Save.save();

                    gameStateManager.setState(GameStateManager.MENU);
                }
            });
            stage.addActor(buttonSave);
        }
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
        glyphLayout.setText(Game.TITLE_FONT, GAME_OVER_TITLE);

        Game.TITLE_FONT.draw(spriteBatch, GAME_OVER_TITLE, Game.TITLES_POSITION.x - glyphLayout.width / 2, Game.TITLES_POSITION.y);

        if (!newHighScore) {
            spriteBatch.end();
            return;
        }

        String newHighScoreString = "New High Score:";
        glyphLayout.setText(Game.STANDARD_FONT, newHighScoreString);
        Game.STANDARD_FONT.draw(spriteBatch, newHighScoreString, (Game.WIDTH - glyphLayout.width) / 2, 1200);

        glyphLayout.setText(Game.STANDARD_FONT, Save.gameData.getTentativeScore() + "");
        Game.STANDARD_FONT.draw(spriteBatch, Save.gameData.getTentativeScore() + "", (Game.WIDTH - glyphLayout.width) / 2, 1100);

        spriteBatch.end();

        stage.draw();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(NEW_NAME_HIT_BOX.getX(), NEW_NAME_HIT_BOX.getY(), NEW_NAME_HIT_BOX.getWidth(), NEW_NAME_HIT_BOX.getHeight());
        shapeRenderer.end();
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
        shapeRenderer.dispose();
        stage.dispose();
    }
}
