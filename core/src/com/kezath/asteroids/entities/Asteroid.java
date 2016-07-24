package com.kezath.asteroids.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.kezath.asteroids.Game;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class Asteroid extends GameObject {
    private int type;
    public static final int SMALL = 0;
    public static final int MEDIUM = 1;
    public static final int LARGE = 2;

    private int numPoints;
    private float[] dists;

    private int score;

    private boolean remove;

    public Asteroid(float x, float y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;

        if (type == SMALL) {
            numPoints = 8;
            width = height = 120;
            speed = MathUtils.random(170, 200);
            score = 100;
        } else if (type == MEDIUM) {
            numPoints = 10;
            width = height = 200;
            speed = MathUtils.random(150, 160);
            score = 50;
        } else if (type == LARGE) {
            numPoints = 12;
            width = height = 400;
            speed = MathUtils.random(130, 140);
            score = 20;
        }

        rotationSpeed = MathUtils.random(-1, 1);

        radians = MathUtils.random(2 * (float)Math.PI);
        dx = MathUtils.random(-30f, 30f);
        dy = -1 * MathUtils.random(200f, 500f);
//        dx = MathUtils.cos(radians) * speed;
//        dy = MathUtils.sin(radians) * speed;

        shapex = new float[numPoints];
        shapey = new float[numPoints];
        dists = new float[numPoints];

        int radius = width / 2;
        for (int i = 0; i < numPoints; i++) {
            dists[i] = MathUtils.random(radius / 2, radius);
        }

        setShape();
    }

    private void setShape() {
        float angle = 0;
        for (int i = 0; i < numPoints; i++) {
            shapex[i] = x + MathUtils.cos(angle + radians) * dists[i];
            shapey[i] = y + MathUtils.sin(angle + radians) * dists[i];
            angle += 2 * (float)Math.PI / numPoints;
        }
    }

    public int getType() { return type; }
    public boolean shouldRemove() { return remove; }
    public int getScore() { return score; }

    public void update(float deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;

        radians += rotationSpeed * deltaTime;

        setShape();

        if (y < 0 - height) {
            remove = true;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0, j = shapex.length - 1; i < shapex.length; j = i++) {
            shapeRenderer.line(shapex[i], shapey[i], shapex[j], shapey[j]);
        }
        shapeRenderer.end();
    }

    public void spawnAgain() {
        y = Game.HEIGHT + height;
        setShape();
        remove = false;
    }
}
