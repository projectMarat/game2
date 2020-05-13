package com.mygdx.game2.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class MapTiled {
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer renderer;

    public MapTiled(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        tiledMap = new TmxMapLoader().load("maps/newMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        camera.zoom = (float) 0.4;
        camera.position.x = Gdx.graphics.getWidth()/2;
        camera.position.y = Gdx.graphics.getHeight()/2;
        camera.update();
    }

    public void render(){
        float x = Gdx.input.getDeltaX();
        camera.position.add(-x*camera.zoom, 0,0);
        camera.update();
        renderer.setView(camera);
        renderer.render();

    }
}
