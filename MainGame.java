package com.mygame.platformer;
import com.badlogic.gdx.utils.Array;
import com.mygame.platformer.Enemy;
import com.mygame.platformer.Orc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.math.Rectangle;



public class MainGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private CharacterAnimation character;

    private float x = 100;
    private float y = 100;
    private final float playerWidth = 50;
    private final float playerHeight = 80;
    private float velocityY = 0;
    private final float GRAVITY = -500;
    private final float JUMP_VELOCITY = 300;
    private boolean onGround = true;
    private float playerDamageCooldown = 1f;
    private float lastTimePlayerDamaged = -999f;

    private ShapeRenderer shapeRenderer;
    private HpBar hpBar;
    private ArrayList<FloatingText> damageTexts = new ArrayList<>();

    private Array<Enemy> enemies;

    @Override
    public void create() {
        batch = new SpriteBatch();
        character = new CharacterAnimation();
        shapeRenderer = new ShapeRenderer();
        hpBar = new HpBar(100);
        enemies = new Array<>();
        enemies.add(new Orc(300, 100));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        boolean walking = false;

        if (!character.isDead()) {
            // Jump
                      if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && onGround) {
                velocityY = JUMP_VELOCITY;
                onGround = false;
                character.setJumping(true);
            }

            // Attack
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                character.queueNextAttack();
            }

            // Move
            if (!character.isAttacking()) {
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    x += 100 * delta;
                    character.setFacingRight(true);
                    walking = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    x -= 100 * delta;
                    character.setFacingRight(false);
                    walking = true;
                }
            }

            // Gravity
            velocityY += GRAVITY * delta;
            y += velocityY * delta;

            if (y <= 100) {
                y = 100;
                velocityY = 0;
                onGround = true;
                character.setJumping(false);
            }

            character.setWalking(walking);

            // Manual damage (testing key)
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                hpBar.setHp(hpBar.getHp() - 10);
                damageTexts.add(new FloatingText("-10", x, y + 100));
                if (hpBar.isDead()) {
                    character.die();
                }
            }
        }

        // --- HITBOXES ---
        Rectangle playerHitbox = new Rectangle(x, y, playerWidth, playerHeight);

        // Clear screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render
        batch.begin();
        character.render(batch, x, y);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.render(batch);

            Rectangle enemyHitbox = enemy.getHitbox();

            // Player takes damage on contact
            if (!character.isDead() && playerHitbox.overlaps(enemyHitbox) && (!enemy.isDead)) {
                float currentTime = TimeUtils.nanoTime() / 1_000_000_000f; // convert to seconds
                if (currentTime - lastTimePlayerDamaged >= playerDamageCooldown) {
                    lastTimePlayerDamaged = currentTime;
                    hpBar.setHp(hpBar.getHp() - 10);
                    damageTexts.add(new FloatingText("-10", x, y + 100));
                    if (hpBar.isDead()) {
                        character.die();
                    }
                }
            }


            // Enemy takes damage if player is attacking and overlaps
            if (character.isAttacking() && !enemy.isDead() && playerHitbox.overlaps(enemyHitbox) && enemy.canBeHit()) {
                enemy.takeDamage(15);

                enemy.registerHit();
                damageTexts.add(new FloatingText("-15", enemyHitbox.x, enemyHitbox.y + 60));
            }
        }

        // Floating damage texts
        Iterator<FloatingText> iter = damageTexts.iterator();
        while (iter.hasNext()) {
            FloatingText ft = iter.next();
            ft.update(delta);
            ft.render(batch);
            if (ft.isFinished()) iter.remove();
        }

        batch.end();

        // HP Bar above player
        float frameWidth = character.getCurrentFrameWidth();
        float hpBarWidth = 60;
        float hpBarHeight = 8;

        float hpX = x + (frameWidth / 2f) - (hpBarWidth / 2f);
        float hpY = y + 85;

        hpBar.render(shapeRenderer, hpX, hpY, hpBarWidth, hpBarHeight);
    }


    @Override
    public void dispose() {
        batch.dispose();
        character.dispose();
        shapeRenderer.dispose();
    }
}
