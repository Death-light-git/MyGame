package com.mygame.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CharacterAnimation {
    public enum State {
        IDLE, WALK, JUMP, ATTACK1, ATTACK2, ATTACK3, DEAD
    }

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> attack1Animation;
    private Animation<TextureRegion> attack2Animation;
    private Animation<TextureRegion> attack3Animation;
    private Animation<TextureRegion> deathAnimation;

    private State currentState = State.IDLE;
    private float stateTime = 0f;
    private boolean facingRight = true;

    private boolean isJumping = false;
    private boolean isWalking = false;
    private boolean attackQueued = false;
    private float comboResetTimer = 0f;
    private float comboResetDelay = 1.0f;
    private int attackStep = 0;

    private TextureRegion currentFrame;

    public CharacterAnimation() {
        idleAnimation = createAnimation(new Texture("idle.png"), 6, 1, 0.2f);
        walkAnimation = createAnimation(new Texture("walk.png"), 8, 1, 0.1f);
        jumpAnimation = createAnimation(new Texture("jump.png"), 12, 1, 0.15f);
        attack1Animation = createAnimation(new Texture("Attack_1.png"), 6, 1, 0.1f);
        attack2Animation = createAnimation(new Texture("Attack_2.png"), 4, 1, 0.1f);
        attack3Animation = createAnimation(new Texture("Attack_3.png"), 3, 1, 0.1f);
        deathAnimation = createAnimation(new Texture("Dead.png"), 3, 1, 0.3f);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int cols, int rows, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                frames[index++] = tmp[i][j];
        return new Animation<>(frameDuration, frames);
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }

    public void setJumping(boolean jumping) {
        if (!isDead()) {
            isJumping = jumping;
        }
    }

    public void setFacingRight(boolean right) {
        this.facingRight = right;
    }

    public void queueNextAttack() {
        if (!isAttacking() && !isDead()) {
            startAttack();
        } else {
            attackQueued = true;
        }
    }

    private void startAttack() {
        attackStep++;
        if (attackStep > 3) attackStep = 1;

        switch (attackStep) {
            case 1: currentState = State.ATTACK1; break;
            case 2: currentState = State.ATTACK2; break;
            case 3: currentState = State.ATTACK3; break;
        }

        stateTime = 0;
        comboResetTimer = 0;
    }

    public boolean isAttacking() {
        return currentState == State.ATTACK1 || currentState == State.ATTACK2 || currentState == State.ATTACK3;
    }

    public boolean isDead() {
        return currentState == State.DEAD;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void die() {
        currentState = State.DEAD;
        stateTime = 0;
    }

    public void render(SpriteBatch batch, float x, float y) {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        Animation<TextureRegion> currentAnim;

        // Choose animation based on flags
        if (isDead()) {
            currentAnim = deathAnimation;
            currentState = State.DEAD;
        } else if (isAttacking()) {
            switch (currentState) {
                case ATTACK1: currentAnim = attack1Animation; break;
                case ATTACK2: currentAnim = attack2Animation; break;
                case ATTACK3: currentAnim = attack3Animation; break;
                default: currentAnim = attack1Animation; break;
            }
        } else if (isJumping) {
            if (currentState != State.JUMP) {
                currentState = State.JUMP;
                stateTime = 0f; // Start jump animation from frame 0
            } currentAnim = jumpAnimation;}
            else if (isWalking) {
            currentAnim = walkAnimation;
            currentState = State.WALK;
        } else {
            currentAnim = idleAnimation;
            currentState = State.IDLE;
        }

        // ✅ Fix: Only loop idle & walk animations
        boolean looping = currentState == State.IDLE || currentState == State.WALK;
        currentFrame = currentAnim.getKeyFrame(stateTime, looping);

        // Flip character sprite based on direction
        if (!facingRight && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (facingRight && currentFrame.isFlipX()) currentFrame.flip(true, false);

        int refWidth = idleAnimation.getKeyFrame(0).getRegionWidth();
        int refHeight = idleAnimation.getKeyFrame(0).getRegionHeight();
        int frameWidth = currentFrame.getRegionWidth();
        int frameHeight = currentFrame.getRegionHeight();

        float drawX = x - (frameWidth - refWidth) / 2f;
        float drawY = y - (frameHeight - refHeight) / 2f;

        batch.draw(currentFrame, drawX, drawY);

        // Combo attack logic
        if (isAttacking() && currentAnim.isAnimationFinished(stateTime)) {
            if (attackQueued && attackStep < 3) {
                attackQueued = false;
                startAttack();
            } else {
                attackStep = 0;
                if (isJumping) {
                    currentState = State.JUMP;
                } else if (isWalking) {
                    currentState = State.WALK;
                } else {
                    currentState = State.IDLE;
                }
                attackQueued = false;
            }
        }

        // Combo reset timer
        if (!isAttacking() && attackStep > 0) {
            comboResetTimer += delta;
            if (comboResetTimer >= comboResetDelay) {
                attackStep = 0;
                comboResetTimer = 0;
            }
        }


        if (!isAttacking() && attackStep > 0) {
            comboResetTimer += delta;
            if (comboResetTimer >= comboResetDelay) {
                attackStep = 0;
                comboResetTimer = 0;
            }
        }
    }

    public int getCurrentFrameWidth() {
        Animation<TextureRegion> anim;
        switch (currentState) {
            case WALK: anim = walkAnimation; break;
            case JUMP: anim = jumpAnimation; break;
            case ATTACK1: anim = attack1Animation; break;
            case ATTACK2: anim = attack2Animation; break;
            case ATTACK3: anim = attack3Animation; break;
            case DEAD: anim = deathAnimation; break;
            case IDLE:
            default: anim = idleAnimation; break;
        }

        return anim.getKeyFrame(stateTime, false).getRegionWidth();
    }

    public int getCurrentFrameHeight() {
        return currentFrame != null ? currentFrame.getRegionHeight() : 60;
    }

    public void dispose() {
        // Optional: dispose textures if needed
    }
}
