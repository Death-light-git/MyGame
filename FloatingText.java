package com.mygame.platformer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FloatingText {
    private static final float LIFESPAN = 1.0f;

    private String text;
    private float x, y;
    private float elapsed;
    private BitmapFont font;

    public FloatingText(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.elapsed = 0;
        this.font = new BitmapFont(); // default font
        this.font.setColor(Color.RED);
    }

    public void update(float delta) {
        elapsed += delta;
        y += delta * 30; // float upward
    }

    public void render(SpriteBatch batch) {
        font.draw(batch, text, x, y);
    }

    public boolean isFinished() {
        return elapsed >= LIFESPAN;
    }
}
