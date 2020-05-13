package com.mygdx.game2.Player;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character {
    private static final int FRAME_COLS = 11, FRAME_ROWS = 1;

    // Objects used
    Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
    Texture walkSheet;
    SpriteBatch spriteBatch;


    float stateTime;


    public Character() {
        walkSheet = new Texture("Main Characters/Mask Dude/Idle (32x32).png");

        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        walkAnimation = new Animation<TextureRegion>(0.025f, walkFrames);
        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    public void render() {

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, (float) (Gdx.graphics.getWidth()/10), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
        spriteBatch.end();

    }


    public void dispose() {
        spriteBatch.dispose();
        walkSheet.dispose();
    }
}
