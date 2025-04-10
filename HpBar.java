package com.mygame.platformer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HpBar {
    private int maxHp;
    private int currentHp;

    public HpBar(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    public void setHp(int hp) {
        this.currentHp = Math.max(0, Math.min(hp, maxHp));
    }

    public int getHp() {
        return currentHp;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void render(ShapeRenderer shapeRenderer, float x, float y, float width, float height) {
        float hpPercent = (float) currentHp / maxHp;

        // Determine HP bar color (green -> red)
        Color barColor = new Color(1 - hpPercent, hpPercent, 0, 1);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(x, y, width, height);

        // Foreground (HP)
        shapeRenderer.setColor(barColor);
        shapeRenderer.rect(x, y, width * hpPercent, height);

        shapeRenderer.end();
    }
}
