package com.mygdx.game2.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game2.Buttons.JumpButton;

import java.lang.reflect.Array;
import java.util.Iterator;

public class Character {
    TiledMap tiledMap;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> moveAnimation;
    Texture idleSheet;
    Texture moveSheet;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    private Vector2 position;
    float stateTime;
    boolean shouldBeLefted;
//    Array<Rectangle> platformRects;

    public Character(OrthographicCamera cam, TiledMap tM) {
//        platformRects = new Array<Rectangle>();
        shouldBeLefted = false;
        this.tiledMap = tM;
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
        position.set(camera.position.x-Gdx.graphics.getWidth()/2,camera.position.y-Gdx.graphics.getHeight()/2);

        //position detection

        if(position.x<-826-32/2 || position.x>1466 || position.y>540 || position.y<0){
            TextureRegion[][] currentFrameFall = TextureRegion.split(new Texture("Main Characters/Mask Dude/Fall (32x32).png"),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getWidth(),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getHeight());
            spriteBatch.begin();
            spriteBatch.draw(currentFrameFall[0][0], (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();
            camera.position.y-=10;
            if(position.y<-200){
                camera.position.y = 540;
                camera.position.x = 1078;
            }else if (Gdx.input.isTouched()){
                if(sideOfTouch()){
                    shouldBeLefted = false;
                    camera.position.x += 4;
                }else {
                    shouldBeLefted = true;
                    camera.position.x -= 4;
                }
            }
        }else if(false){

        } else if (Gdx.input.isTouched()){
            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrameRun = moveAnimation.getKeyFrame(stateTime, true);

            if(sideOfTouch()){
                shouldBeLefted = false;
                camera.position.x+=6;
                if(currentFrameRun.isFlipX())
                    currentFrameRun.flip(true,false);

            }else{
                shouldBeLefted = true;
                camera.position.x-=6;
                if(!currentFrameRun.isFlipX())
                    currentFrameRun.flip(shouldBeLefted,false);

            }

            spriteBatch.begin();
            spriteBatch.draw(currentFrameRun, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();
            currentFrameRun.flip(false,false);
        }else if(!Gdx.input.isTouched()){

            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);

            if(shouldBeLefted && !currentFrame.isFlipX())currentFrame.flip(true,false);
            else if(!shouldBeLefted && currentFrame.isFlipX())currentFrame.flip(true,false);

            spriteBatch.begin();
            spriteBatch.draw(currentFrame, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();

        }

        Gdx.app.log("aaaaaaaaaaaaaaaaaaaaaaaaaa",position.toString());





    }
    public boolean sideOfTouch(){
        if(Gdx.input.getX()>Gdx.graphics.getWidth()/2) return true;
        return false;
    }

    public void dispose() {
        spriteBatch.dispose();
        idleSheet.dispose();
    }
    public Vector2 getPosition(){
//        position.set(camera.position.x,camera.position.y);
        return position;
    }
}
