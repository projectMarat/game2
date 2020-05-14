package com.mygdx.game2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game2.Map.MapTiled;

public class Game extends ApplicationAdapter {
	Sprite sprite;
	Texture img;
	Batch batch;
	MapTiled mapTiled;
	@Override
	public void create () {
		Gdx.app.log("resolution",Gdx.graphics.getWidth()+" x "+ Gdx.graphics.getHeight());
		img = new Texture("Background/Yellow.png");
		sprite = new Sprite(img);
		batch = new SpriteBatch();
		mapTiled = new MapTiled();

	}

	@Override
	public void render () {
		batch.begin();
		spriteDraw(sprite);
		batch.end();
		mapTiled.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}


	public void spriteDraw(Sprite sprite){
		for (int i = 0; i < Gdx.graphics.getHeight(); i+=64) {
			for (int j = 0; j < Gdx.graphics.getWidth(); j+=64) {
				sprite.setPosition(j,i);
				sprite.draw(batch);
			}
		}
	}
}
