package com.kezath.asteroids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.kezath.asteroids.Game;
import com.kezath.asteroids.entities.Asteroid;
import com.kezath.asteroids.entities.Bullet;
import com.kezath.asteroids.entities.Particle;
import com.kezath.asteroids.entities.Player;
import com.kezath.asteroids.managers.GameInputProcessor;
import com.kezath.asteroids.managers.GameStateManager;
import com.kezath.asteroids.managers.Jukebox;
import com.kezath.asteroids.managers.Save;

import java.util.ArrayList;

/**
 * Created by Sebastian on 23.07.2016.
 */
public class PlayState extends GameState {

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private BitmapFont font;

    //Visualize extra lives
    private Player hudPlayer;

    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Asteroid> asteroids;

    private ArrayList<Particle> particles;

    private int level;
    private boolean showLevelInfo;
    private float levelInfoTimer;
    private final float levelInfoDuration = 2f;

    private int totalAsteroids;
    private int numAsteroidsLeft;

    private float maxDelay;
    private float minDelay;
    private float currentDelay;
    private float bgTimer;
    private boolean playLowPulse;

    public PlayState(GameStateManager gameStateManager) {
        super(gameStateManager);
    }

    @Override
    public void init() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HyperspaceBold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        font = generator.generateFont(parameter);

        bullets = new ArrayList<Bullet>();
        asteroids = new ArrayList<Asteroid>();
        particles = new ArrayList<Particle>();

        player = new Player(bullets);

        hudPlayer = new Player(null);

        level = 0;
        levelInfoTimer = 0f;
        showLevelInfo = true;
        spawnAsteroids();

        //Set up bg music
        maxDelay = 1;
        minDelay = 0.25f;
        currentDelay = maxDelay;
        bgTimer = maxDelay;
        playLowPulse = true;
    }

    private void createParticles(float x, float y) {
        for (int i = 0; i < 6; i++) {
            particles.add(new Particle(x, y));
        }
    }

    private void splitAsteroids(Asteroid asteroid) {
        createParticles(asteroid.getX(), asteroid.getY());
        numAsteroidsLeft--;
        currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft / totalAsteroids) + minDelay;
        if (asteroid.getType() == Asteroid.LARGE) {
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.MEDIUM));
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.MEDIUM));
        }
        if (asteroid.getType() == Asteroid.MEDIUM) {
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.SMALL));
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.SMALL));
        }
    }

    private void spawnAsteroids() {
        asteroids.clear();

        int numToSpawn = 4 + level  - 1;
        totalAsteroids = numToSpawn * 7;
        numAsteroidsLeft = totalAsteroids;
        currentDelay = maxDelay;

        for (int i = 0; i < numToSpawn; i++) {
            float x = MathUtils.random(Game.WIDTH/8, Game.WIDTH - Game.WIDTH/8);
            float y = MathUtils.random(Game.HEIGHT, Game.HEIGHT * 2);

            asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
        }
    }

    @Override
    public void update(float deltaTime) {
        handleInput();

        if (showLevelInfo) {
            levelInfoTimer += deltaTime;
            if (levelInfoTimer >= levelInfoDuration) {
                showLevelInfo = false;
                levelInfoTimer = 0;
            }
        }

        //next level
        if (asteroids.size() == 0) {
            level++;
            showLevelInfo = true;
            spawnAsteroids();
        }

        //update player
        player.update(deltaTime);
        if (player.isDead()) {
            if (player.getLives() == 0) {
                Jukebox.stopAll();
                Save.gameData.setTentativeScore(player.getScore());
                gameStateManager.setState(GameStateManager.GAME_OVER);
                return;
            }
            player.reset();
            player.looseLive();
            return;
        }

        //update bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update(deltaTime);
            if (bullets.get(i).shouldRemove()) {
                bullets.remove(i);
                i--;
            }
        }

        //update asteroids
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).update(deltaTime);
            if (asteroids.get(i).shouldRemove()) {
                asteroids.remove(i);
                i--;
            }
        }

        //update particles
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).update(deltaTime);
            if (particles.get(i).shouldRemove()) {
                particles.remove(i);
                i--;
            }
        }

        checkCollisions();

        //Play bg music
        bgTimer += deltaTime;
        if (!player.isHit() && bgTimer >= currentDelay) {
            if (playLowPulse) {
                Jukebox.play("pulselow");
            } else {
                Jukebox.play("pulsehigh");
            }
            playLowPulse = !playLowPulse;
            bgTimer = 0;
        }
    }

    private void checkCollisions() {
        //player-asteroid collision
        if (!player.isHit()) {
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid asteroid = asteroids.get(i);
                if (asteroid.instersects(player)) {
                    player.hit();
                    asteroids.remove(i);
                    i--;
                    splitAsteroids(asteroid);
                    Jukebox.play("explode", 0.2f);
                    break;
                }
            }
        }

        //bullet-asteroid collision
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid asteroid = asteroids.get(j);
                if (asteroid.contains(bullet.getX(), bullet.getY())) {
                    bullets.remove(i);
                    i--;
                    asteroids.remove(j);
                    j--;
                    splitAsteroids(asteroid);
                    Jukebox.play("explode", 0.2f);
                    // increment player score
                    player.incrementScore(asteroid.getScore());
                    break;
                }
            }
        }
    }

    @Override
    public void draw() {
        spriteBatch.setProjectionMatrix(Game.camera.combined);
        shapeRenderer.setProjectionMatrix(Game.camera.combined);

        player.draw(shapeRenderer);

        //draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(shapeRenderer);
        }

        //update asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(shapeRenderer);
        }

        //draw particles
        for (Particle particle : particles) {
            particle.draw(shapeRenderer);
        }

        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 1);

        //draw score
        font.draw(spriteBatch, Long.toString(player.getScore()), 50, Game.HEIGHT - 50);

        if(showLevelInfo) {
            final String levelInfo = "Stage: " + (level + 1);
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(font, levelInfo);
            font.draw(spriteBatch, levelInfo, (Game.WIDTH - glyphLayout.width) / 2, Game.HEIGHT / 2 + glyphLayout.height);
        }

        spriteBatch.end();

        //draw extra lives
        for (int i = 0; i < player.getLives(); i++) {
            hudPlayer.setPosition(60 + i * 70, Game.HEIGHT - 50 - 110);
            hudPlayer.draw(shapeRenderer);
        }
    }

    @Override
    public void handleInput() {
        if (GameInputProcessor.isBackPressed()) {
            gameStateManager.setState(GameStateManager.MENU);
            return;
        }

        if (!player.isHit()) {
            if (GameInputProcessor.isLeftScreenPartPressed()) {
                player.setLeft(true);
                player.setRight(false);
            }

            if (GameInputProcessor.isRightScreenPartPressed()) {
                player.setRight(true);
                player.setLeft(false);
            }

            if (!GameInputProcessor.isPressed()) {
                player.setLeft(false);
                player.setRight(false);
            }
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}
