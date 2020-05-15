package com.mygdx.game2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game2.Map.MapTiled;

public class Game extends ApplicationAdapter {
	Sprite sprite,sprite1;
	Batch batch;
	MapTiled mapTiled;
	boolean flag;
	@Override
	public void create () {
		flag = true;
		Gdx.app.log("resolution",Gdx.graphics.getWidth()+" x "+ Gdx.graphics.getHeight());
		sprite = new Sprite(new Texture("Background/Yellow.png"));
		sprite1 = new Sprite(new Texture("Background/Brown.png"));
		batch = new SpriteBatch();
		mapTiled = new MapTiled(this);

	}

	@Override
	public void render () {
		if(flag){
			batch.begin();
			spriteDraw(sprite);
			batch.end();
			mapTiled.render();
		}else{
			batch.begin();
			finishDraw(sprite1);
			batch.end();
		}

	}
	
	@Override
	public void dispose () {
		flag = false;

	}


	public void spriteDraw(Sprite sprite){
		for (int i = 0; i < Gdx.graphics.getHeight(); i+=64) {
			for (int j = 0; j < Gdx.graphics.getWidth(); j+=64) {
				sprite.setPosition(j,i);
				sprite.draw(batch);
			}
		}
	}
	public void finishDraw(Sprite sprite){
		for (int i = 0; i < Gdx.graphics.getHeight(); i+=64) {
			for (int j = 0; j < Gdx.graphics.getWidth(); j+=64) {
				sprite.setPosition(j,i);
				sprite.draw(batch);
			}
		}
	}

}
