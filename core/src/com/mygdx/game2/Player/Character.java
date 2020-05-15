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
import com.mygdx.game2.Map.MapTiled;


import java.lang.reflect.Array;
import java.util.Iterator;

public class Character {
    int cnt,cnt1,x,y;
    TiledMap tiledMap;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> moveAnimation;
    Texture idleSheet;
    Texture moveSheet;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    MapTiled mapTiled;
    private Vector2 position;
    float stateTime;
    boolean shouldBeLefted;


    public Character(OrthographicCamera cam, TiledMap tM, MapTiled mapTiled) {
        cnt = 0;
        cnt1 = 0;
        shouldBeLefted = false;
        this.mapTiled = mapTiled;
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
        if(Gdx.input.justTouched()){
            x = Gdx.input.getX();
            y = Gdx.input.getY();
        }
//        position detection
//        falling
        if (metalPlank())cnt=20;
        if((position.x<-826-32/2 || position.x>1466 || position.y!=0) && cnt<=0 && !(normalPlank()) && !(metalPlank())){
            TextureRegion[][] currentFrame = TextureRegion.split(new Texture("Main Characters/Mask Dude/Fall (32x32).png"),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getWidth(),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getHeight());
            TextureRegion currentFrameFall = currentFrame[0][0];
            int [] a = new int[10];
            for (int i = 0; i < 10; i++) a[i]=i;
            for (int i:a) {
                if(position.y==i) {
                    spriteBatch.begin();
                    spriteBatch.draw(currentFrameFall, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
                    camera.position.y-=i;
                    spriteBatch.end();
                    break;
                }
            }
            spriteBatch.begin();
            if(shouldBeLefted)currentFrameFall.flip(true,false);
            spriteBatch.draw(currentFrameFall, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();

            camera.position.y-=10;
            if(position.y<-200){
                camera.position.y = Gdx.graphics.getHeight()/2;
                camera.position.x = Gdx.graphics.getWidth()/2;
            }else if (Gdx.input.isTouched()){
                if(sideOfTouch() && !shouldBeLefted){
                    shouldBeLefted = false;
                    camera.position.x += 4;
                    if(currentFrameFall.isFlipX())
                        currentFrameFall.flip(true,false);
                }else {
                    shouldBeLefted = true;
                    camera.position.x -= 4;
                    if(!currentFrameFall.isFlipX())
                        currentFrameFall.flip(true,false);
                }
                spriteBatch.begin();
                spriteBatch.draw(currentFrameFall, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
                spriteBatch.end();
            }

//            jumping to right

        }else if((cnt<=100 && cnt>0) || (Gdx.input.justTouched() && x >= Gdx.graphics.getWidth()-Gdx.graphics.getWidth()/4 && y>=Gdx.graphics.getHeight()/2)){
            TextureRegion[][] currentFrame = TextureRegion.split(new Texture("Main Characters/Mask Dude/Jump (32x32).png"),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getWidth(),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getHeight());
            TextureRegion currentFrameJump = currentFrame[0][0];
            if(cnt == 0){
                cnt = 15;
            }else{
                cnt-=1;
            }
            camera.position.y+=cnt;
            if (Gdx.input.isTouched()){
                if(!sideOfTouch()){
                    shouldBeLefted = true;
                    camera.position.x -= 4;
                    if(!currentFrameJump.isFlipX())
                        currentFrameJump.flip(true,false);
                }else {
                    shouldBeLefted = false;
                    camera.position.x += 4;
                    if(currentFrameJump.isFlipX())
                        currentFrameJump.flip(true,false);
                }
            }
            spriteBatch.begin();
            if(shouldBeLefted && !currentFrameJump.isFlipX())currentFrameJump.flip(true,false);
            spriteBatch.draw(currentFrameJump, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();
        }

//        jumping to left

        else if((Gdx.input.justTouched() && x <= Gdx.graphics.getWidth()/4 && y>=Gdx.graphics.getHeight()/2)){
            TextureRegion[][] currentFrame = TextureRegion.split(new Texture("Main Characters/Mask Dude/Jump (32x32).png"),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getWidth(),new Texture("Main Characters/Mask Dude/Fall (32x32).png").getHeight());
            TextureRegion currentFrameJump = currentFrame[0][0];
            if(cnt <= 0){
                cnt = 15;
            }else{
                cnt-=1;
            }
            camera.position.y+=cnt;
            if (Gdx.input.isTouched()){
                if(!sideOfTouch()){
                    shouldBeLefted = true;
                    camera.position.x -= 4;
                    if(!currentFrameJump.isFlipX())
                        currentFrameJump.flip(true,false);
                }else {
                    shouldBeLefted = false;
                    camera.position.x += 4;
                    if(currentFrameJump.isFlipX())
                        currentFrameJump.flip(true,false);
                }
            }
            spriteBatch.begin();
            if(shouldBeLefted && !currentFrameJump.isFlipX())currentFrameJump.flip(true,false);
            spriteBatch.draw(currentFrameJump, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();
        }

//        running

        else if (Gdx.input.isTouched()){
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
        }

//        do nothing

        else if(!Gdx.input.isTouched()){
            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
            if(position.y==365 && position.x>=-666 && position.x<=-648){
                dispose();

            }



            if(shouldBeLefted && !currentFrame.isFlipX())currentFrame.flip(true,false);
            else if(!shouldBeLefted && currentFrame.isFlipX())currentFrame.flip(true,false);

            spriteBatch.begin();
            spriteBatch.draw(currentFrame, (float) (Gdx.graphics.getWidth()/5), (float) (Gdx.graphics.getHeight()/4), (float) (Gdx.graphics.getWidth()/18.7), (float) (Gdx.graphics.getHeight()/9.4));
            spriteBatch.end();

        }

        Gdx.app.log("aaaaaaaaaaaaaaaaaaaaaaaaaa", String.valueOf(stateTime));





    }
    public boolean sideOfTouch(){
        if(x>Gdx.graphics.getWidth()/2) return true;
        return false;
    }

    public void dispose() {
//        TextureRegion[][] currentFrame = TextureRegion.split(new Texture("Main Characters/Mask Dude/Double Jump (32x32).png"), new Texture("Main Characters/Mask Dude/Double Jump (32x32).png").getWidth() / 6, new Texture("Main Characters/Mask Dude/Double Jump (32x32).png").getHeight());
//        TextureRegion[] curframe = new TextureRegion[6];
//        int index = 0;
//        for (int i = 0; i < 1; i++) {
//            for (int j = 0; j < 6; j++) {
//                curframe[index++] = currentFrame[i][j];
//            }
//        }
//        Animation<TextureRegion> flyAnimation = new Animation<TextureRegion>(1f, curframe);
//        spriteBatch.begin();
//        stateTime = 0;
//        while (stateTime < 10f){
//            Gdx.app.log("aaaaaaaaaaaaaaaaaaaaaaaaaa", String.valueOf(stateTime));
//            stateTime += Gdx.graphics.getDeltaTime()/2;
//            TextureRegion currentFrameFly = flyAnimation.getKeyFrame(stateTime, true);
//            spriteBatch.draw(currentFrameFly, (float) (Gdx.graphics.getWidth() / 5), (float) (Gdx.graphics.getHeight() / 4), (float) (Gdx.graphics.getWidth() / 18.7), (float) (Gdx.graphics.getHeight() / 9.4));
//        }
//        spriteBatch.end();
        spriteBatch.dispose();
        idleSheet.dispose();
        moveSheet.dispose();
        mapTiled.stop();
    }
    public Vector2 getPosition(){
//        position.set(camera.position.x,camera.position.y);
        return position;
    }
    public boolean normalPlank(){
        if((position.x<=-718+32/2 && position.x>=-754-32/2 && ((position.y<=70 && position.y>=60)||(position.y<=205 && position.y>=195)))||(position.x<=-618 && position.x>=-690 && ((position.y<=130 && position.y>=120)||(position.y<=365 && position.y>=355)))){
            if(position.y==130)camera.position.y-=5;
            if(position.y==70)camera.position.y-=10;
            if(position.y==195)camera.position.y+=10;
            if(position.y==360)camera.position.y+=5;
            return true;
        }

        return false;
    }
    public boolean metalPlank(){
        if(position.x<=-718+32/2 && position.x>=-754-32/2 && position.y<=275 && position.y>=265){
            return true;
        }


        return false;
    }

}
