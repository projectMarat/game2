package com.mygdx.game2.Player;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Character {

    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> moveAnimation;
    Texture idleSheet;
    Texture moveSheet;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    private Vector2 position;
    float stateTime;
    boolean shouldBeLefted;


    public Character(OrthographicCamera cam) {
        shouldBeLefted = false;
        this.camera = cam;
        position = new Vector2();
        idleSheet = new Texture("Main Characters/Mask Dude/Idle (32x32).png");

        moveSheet = new Texture("Main Characters/Mask Dude/Run (32x32).png");

        TextureRegion[][] tmp = TextureRegion.split(idleSheet,
                idleSheet.getWidth() / 11,
                idleSheet.getHeight() / 1);

        TextureRegion[][] tmp1 = TextureRegion.split(moveSheet,
                idleSheet.getWidth() / 11,
                idleSheet.getHeight() / 1);


        TextureRegion[] idle = new TextureRegion[11];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 11; j++) {
                idle[index++] = tmp[i][j];
            }
        }
        TextureRegion[] move = new TextureRegion[11];
        index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 11; j++) {
                move[index++] = tmp1[i][j];
            }
        }

        idleAnimation = new Animation<TextureRegion>(0.025f, idle);
        moveAnimation = new Animation<TextureRegion>(0.025f, move);

        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    public void render() {

        if (Gdx.input.isTouched()){
            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = moveAnimation.getKeyFrame(stateTime, true);

            if(sideOfTouch()){
                shouldBeLefted = false;

                if(currentFrame.isFlipX())
                    currentFrame.flip(true,false);

            }else{
                shouldBeLefted = true;

                if(!currentFrame.isFlipX())
                    currentFrame.flip(shouldBeLefted,false);

            }

            spriteBatch.begin();
            spriteBatch.draw(currentFrame, (float) (Gdx.graphics.getWidth()/10), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();
            currentFrame.flip(false,false);
        }else{

            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);

            if(shouldBeLefted && !currentFrame.isFlipX())currentFrame.flip(true,false);
            else if(!shouldBeLefted && currentFrame.isFlipX())currentFrame.flip(true,false);

            spriteBatch.begin();
            spriteBatch.draw(currentFrame, (float) (Gdx.graphics.getWidth()/10), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();

        }

    }
    public boolean sideOfTouch(){
        if(Gdx.input.getX()>=Gdx.graphics.getWidth()/2) return true;
        return false;
    }

    public void dispose() {
        spriteBatch.dispose();
        idleSheet.dispose();
    }
    public Vector2 getPosition(){
        return position;
    }
}
