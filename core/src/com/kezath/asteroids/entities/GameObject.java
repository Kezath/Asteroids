package com.kezath.asteroids.entities;

/**
 * Created by Sebastian on 23.07.2016.
 */
public class GameObject {
    protected float x;
    protected float y;

    protected float dx;
    protected float dy;

    protected float radians;
    protected float speed;
    protected float rotationSpeed;

    protected int width;
    protected int height;

    protected float[] shapex;
    protected float[] shapey;

    public float getX() { return x; }
    public float getY() { return y; }

    public float[] getShapex() { return shapex; }
    public float[] getShapey() { return shapey; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean instersects (GameObject gameObject) {
        float sx[] = gameObject.getShapex();
        float sy[] = gameObject.getShapey();
        for (int i = 0; i < sx.length; i++) {
            if (contains(sx[i], sy[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(float x, float y) {
        boolean ret = false;
        for (int i = 0, j = shapex.length - 1; i < shapex.length; j = i++) {
            if ((shapey[i] > y) != (shapey[j] > y) &&
                (x < (shapex[j] - shapex[i]) *
                (y - shapey[i]) / (shapey[j] - shapey[i])
                + shapex[i])) {
                ret = !ret;
            }
        }
        return ret;
    }
}
