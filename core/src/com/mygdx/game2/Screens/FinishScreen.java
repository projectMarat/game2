package com.mygdx.game2.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game2.MarioBros;

public class FinishScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private Game game;

    public FinishScreen(Game game){
        batch = new SpriteBatch();
        this.game = game;
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBros) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.BLACK);

        Label to_be_continued = new Label("To be continued", font);
        to_be_continued.setBounds(650,50,0,0);

        stage.addActor(to_be_continued);
    }

    public void spriteDraw(Sprite sprite, SpriteBatch batch) {
        batch.begin();
        for (int i = 0; i < Gdx.graphics.getHeight(); i += 64) {
            for (int j = 0; j < Gdx.graphics.getWidth(); j += 64) {
                sprite.setPosition(j, i);
                sprite.draw(batch);
            }
        }
        batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        if(Gdx.input.justTouched()) {
//            game.setScreen(new PlayScreen((MarioBros) game));
//            dispose();
//        }
        spriteDraw(new Sprite(new Texture("Background/Brown.png")),batch);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
