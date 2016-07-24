package com.kezath.asteroids.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class Particle extends GameObject {
    private float timer;
    private float time;

    private boolean remove;

    public Particle(float x, float y) {
        this.x = x;
        this.y = y;

        this.width = this.height = 2;

        this.speed = 50;
        this.radians = MathUtils.random(2 * (float)Math.PI);
        this.dx = MathUtils.cos(radians) * speed;
        this.dy = MathUtils.sin(radians) * speed;

        this.timer = 0;
        this.time = 1;
    }

    public boolean shouldRemove() { return remove; }

    public void update(float deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;

        timer += deltaTime;
        if (timer > time) {
            remove = true;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(x - width / 2, y - width / 2, width / 2);
        shapeRenderer.end();
    }
}
