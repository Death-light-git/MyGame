package com.mygame.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Orc extends Enemy {
    private Animation<TextureRegion> walkAnim, hurtAnim, deadAnim;
    private float stateTime = 0f;
    private TextureRegion currentFrame;

    private float patrolDistance = 100f;
    private float patrolSpeed = 40f;
    private float startX;
    private boolean walkingForward = true;

    private float lastHitTime = -999f;
    private float hitCooldown = 0.5f;

    public Orc(float x, float y) {
        super(x, y, 50);
        startX = x;

        walkAnim = createAnim("WalkOrc.png", 7, 1, 0.1f);
        hurtAnim = createAnim("HurtOrc.png", 2, 1, 0.15f);
        deadAnim = createAnim("DeadOrc.png", 4, 1, 0.2f);
    }

    private Animation<TextureRegion> createAnim(String file, int cols, int rows, float frameDuration) {
        Texture sheet = new Texture(Gdx.files.internal(file));
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (TextureRegion[] row : tmp) {
            for (TextureRegion region : row) {
                frames[index++] = region;
            }
        }
        return new Animation<>(frameDuration, frames);
    }
    public boolean canBeHit() {
        return (TimeUtils.nanoTime() / 1_000_000_000f) - lastHitTime >= hitCooldown;
    }

    public void registerHit() {
        lastHitTime = TimeUtils.nanoTime() / 1_000_000_000f;
    }


    @Override
    public void update(float delta) {
        stateTime += delta;

        if (isDead) return;

        if (walkingForward) {
            x += patrolSpeed * delta;
            if (x > startX + patrolDistance) {
                walkingForward = false;
                facingRight = false;
            }
        } else {
            x -= patrolSpeed * delta;
            if (x < startX - patrolDistance) {
                walkingForward = true;
                facingRight = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> anim;
        if (isDead) {
            anim = deadAnim;
        } else {
            anim = walkAnim;
        }

        currentFrame = anim.getKeyFrame(stateTime, !isDead);

        if (!facingRight && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (facingRight && currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, x, y);
    }

    @Override
    public void dispose() {
        // Dispose textures if needed
    }
}
