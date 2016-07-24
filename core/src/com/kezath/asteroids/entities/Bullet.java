package com.kezath.asteroids.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.kezath.asteroids.Game;

/**
 * Created by Sebastian on 24.07.2016.
 */
public class Bullet extends GameObject {

    private boolean remove;

    public Bullet(float x, float y, float radians) {
        this.x = x;
        this.y = y;
        this.radians = radians;

        this.speed = 1000;
        this.dx = MathUtils.cos(radians) * speed;
        this.dy = MathUtils.sin(radians) * speed;

        this.width = this.height = 10;
    }

    public boolean shouldRemove() {
        return  remove;
    }

    public void update(float deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;

        if (y > Game.HEIGHT + this.height) {
            remove = true;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(x - width / 2, y - height / 2, width / 2);
        shapeRenderer.end();
    }
}
