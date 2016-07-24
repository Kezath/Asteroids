package com.kezath.asteroids.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kezath.asteroids.Game;
import com.kezath.asteroids.managers.Jukebox;

import java.util.ArrayList;

/**
 * Created by Sebastian on 23.07.2016.
 */
public class Player extends GameObject {

    private final float SHOOT_COOLDOWN = 0.3f;
    private float lastShoot;

    private ArrayList<Bullet> bullets;

    private boolean left;
    private boolean right;
    private boolean up;

    private float maxSpeed;
    private float acceleration;
//    private float deceleration;

    private boolean hit;
    private boolean dead;

    private float hitTimer;
    private float hitTime;

    private Vector2[] hitLinesFirstPoint;
    private Vector2[] hitLinesSecondPoint;
    private Vector2[] hitLinesVector;

    private int extraLives;
    private long score;
    private long requiredScore;

    public Player(ArrayList<Bullet> bullets) {

        this.bullets = bullets;

        this.x = Game.WIDTH / 2;
        this.y = Game.HEIGHT / 16;

        this.maxSpeed = 300;
        this.acceleration = 300;
//        this.deceleration = 400;

        this.shapex = new float[4];
        this.shapey = new float[4];

        this.radians = (float) Math.PI / 2;
        this.rotationSpeed = 3;

        this.hit = false;
        this.dead = false;
        this.hitTimer = 0;
        this.hitTime = 2;

        this.score = 0;
        this.extraLives = 3;
        this.requiredScore = 10000;
    }

    private void setShape() {
        shapex[0] = x + MathUtils.cos(radians) * 50;
        shapey[0] = y + MathUtils.sin(radians) * 50;

        shapex[1] = x + MathUtils.cos(radians - 4 * (float)Math.PI / 5) * 50;
        shapey[1] = y + MathUtils.sin(radians - 4 * (float)Math.PI / 5) * 50;

        shapex[2] = x + MathUtils.cos(radians + (float)Math.PI) * 20;
        shapey[2] = y + MathUtils.sin(radians + (float)Math.PI) * 20;

        shapex[3] = x + MathUtils.cos(radians + 4 * (float)Math.PI / 5) * 50;
        shapey[3] = y + MathUtils.sin(radians + 4 * (float)Math.PI / 5) * 50;
    }

    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        setShape();
    }

    public void setLeft(boolean b) {
        left = b;
    }
    public void setRight(boolean b) {
        right = b;
    }
    public void setUp(boolean b) {
        if (b && !up && !hit) {
            Jukebox.loop("thruster");
        } else if (!b) {
            Jukebox.stop("thruster");
        }
        up = b;
    }

    public boolean isHit() { return hit; }
    public boolean isDead() { return dead; }

    public int getLives() { return extraLives; }
    public long getScore() { return score; }

    public void looseLive() {
        extraLives--;
    }

    public void incrementScore(long scoreToAdd) {
        score += scoreToAdd;
    }

    public void reset() {
        this.x = Game.WIDTH / 2;
        this.y = Game.HEIGHT / 16;

        setShape();

        hit = dead = false;
    }

    public void shoot(float deltaTime) {
        lastShoot += deltaTime;
        if (lastShoot < SHOOT_COOLDOWN) {
            return;
        }
        lastShoot = 0;
        Jukebox.play("shoot", 0.2f);

        bullets.add(new Bullet(shapex[0], shapey[0], radians));
    }

    public void hit() {
        if (hit) {
            return;
        }

        hit = true;
        dx = dy = 0;
        left = right = up = false;
        Jukebox.stop("thruster");

        hitLinesFirstPoint = new Vector2[4];
        hitLinesSecondPoint = new Vector2[4];
        for (int i = 0, j = hitLinesFirstPoint.length - 1; i < hitLinesFirstPoint.length; j = i++) {
            hitLinesFirstPoint[i] = new Vector2(shapex[i], shapey[i]);
            hitLinesSecondPoint[i] = new Vector2(shapex[j], shapey[j]);
        }

        hitLinesVector = new Vector2[4];
        hitLinesVector[0] = new Vector2(
                MathUtils.cos(radians + 1.5f),
                MathUtils.sin(radians + 1.5f)
        );
        hitLinesVector[1] = new Vector2(
                MathUtils.cos(radians - 1.5f),
                MathUtils.sin(radians - 1.5f)
        );
        hitLinesVector[2] = new Vector2(
                MathUtils.cos(radians - 2.8f),
                MathUtils.sin(radians - 2.8f)
        );
        hitLinesVector[3] = new Vector2(
                MathUtils.cos(radians + 2.8f),
                MathUtils.sin(radians + 2.8f)
        );
    }

    public void update(float deltaTime) {
        //handle hit
        if (hit) {
            hitTimer += deltaTime;
            if (hitTimer > hitTime) {
                dead = true;
                hitTimer = 0;
            }
            for (int i = 0; i < hitLinesFirstPoint.length; i++) {
                hitLinesFirstPoint[i].add(hitLinesVector[i].x * 10 * deltaTime, hitLinesVector[i].y * 10 * deltaTime);
                hitLinesSecondPoint[i].add(hitLinesVector[i].x * 10 * deltaTime, hitLinesVector[i].y * 10 * deltaTime);
            }
            return;
        }

        if (score >= requiredScore) {
            extraLives++;
            requiredScore += 10000;
            Jukebox.play("extralife");
        }

        //set position
        x -= Gdx.input.getAccelerometerX() * acceleration * deltaTime;
        y += dy * deltaTime;

        if (x < 0) {
            x = 0;
        } else if (x > Game.WIDTH) {
            x = Game.WIDTH;
        }

        setShape();

        if (bullets != null) {
            shoot(deltaTime);
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        //check if hit
        if (hit) {
            for (int i = 0; i < hitLinesFirstPoint.length; i++) {
                shapeRenderer.line(
                        hitLinesFirstPoint[i].x,
                        hitLinesFirstPoint[i].y,
                        hitLinesSecondPoint[i].x,
                        hitLinesSecondPoint[i].y
                );
            }
            shapeRenderer.end();
            return;
        }

        //draw player ship
        for (int i = 0, j = shapex.length - 1; i < shapex.length; j = i++) {
            shapeRenderer.line(shapex[i], shapey[i], shapex[j], shapey[j]);
        }
        shapeRenderer.end();
    }
}
