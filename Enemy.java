package com.mygame.platformer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class Enemy {
    protected float x, y;
    protected int maxHP, currentHP;
    protected boolean facingRight = true;
    protected boolean isDead = false;

    public Enemy(float x, float y, int maxHP) {
        this.x = x;
        this.y = y;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
    }

    public abstract void update(float delta);

    public abstract void render(SpriteBatch batch);

    public abstract void dispose();

    public Rectangle getHitbox() {
        return new Rectangle(x, y, 50, 50); // Can be overridden
    }
    private float lastHitTime = -999f;
    private float hitCooldown = 0.5f;


    public void takeDamage(int amount) {
        currentHP -= amount;
        if (currentHP <= 0) {
            currentHP = 0;
            die();
        }
    }
    public boolean canBeHit() {
        float currentTime = TimeUtils.nanoTime() / 1_000_000_000f; // seconds
        return (currentTime - lastHitTime) >= hitCooldown;
    }

    public void registerHit() {
        lastHitTime = TimeUtils.nanoTime() / 1_000_000_000f;
    }


    protected void die() {
        isDead = true;
    }

    public boolean isDead() {
        return isDead;
    }
}
