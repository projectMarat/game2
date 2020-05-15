package com.mygdx.game2.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game2.Player.Character;


public class MapTiled extends Game {
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer renderer;
    Character character;
//    Stage stage;
//    Viewport viewport;
    com.mygdx.game2.Game game;
    public MapTiled(com.mygdx.game2.Game game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        tiledMap = new TmxMapLoader().load("maps/newMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        camera.zoom = (float) 0.4;
        camera.position.x = Gdx.graphics.getWidth()/2;
        camera.position.y = Gdx.graphics.getHeight()/2;
        camera.update();

//        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),camera);
//        stage = new Stage(viewport);
//        Gdx.input.setInputProcessor(stage);
//        stage.addActor(new MapTiledActor(new Texture("Menu/Buttons/Next.png"),this));

        character = new Character(camera,tiledMap,this,game);


    }

    @Override
    public void create() {}

    public void render(){
        camera.update();
        renderer.setView(camera);
        renderer.render();
        character.render();
//        stage.draw();
//        stage.act(Gdx.graphics.getDeltaTime());
    }
    public void stop(){
        game.dispose();
        tiledMap.dispose();
        dispose();
    }
    public void dispose(){

    }
}
