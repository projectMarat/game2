package com.mygdx.game2.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game2.MarioBros;
import com.mygdx.game2.Scenes.Hud;
import com.mygdx.game2.Sprites.Enemies.Enemy;
import com.mygdx.game2.Sprites.Items.Item;
import com.mygdx.game2.Sprites.Items.ItemDef;
import com.mygdx.game2.Sprites.Items.Mushroom;
import com.mygdx.game2.Sprites.Mario;
import com.mygdx.game2.Tools.B2WorldCreator;
import com.mygdx.game2.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;


public class PlayScreen implements Screen{
    //Reference to our Game, used to set Screens
    private MarioBros game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;
    private SpriteBatch batch;
    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(MarioBros game){
        batch = new SpriteBatch();

//        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        //create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("maps/newMap.tmx");

        renderer = new OrthogonalTiledMapRenderer(map,1  / MarioBros.PPM);
        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        //create mario in our game world
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

//        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
//        music.setLooping(true);
//        music.setVolume(0.3f);
        //music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }


    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {


    }

    public void handleInput(float dt){
        //control our player using immediate impulses
        if(player.currentState != Mario.State.DEAD) {
            if (IsJumpRight() && IsJumpLeft())
                player.fire();
            else if (IsJumpLeft() || IsJumpRight())
                player.jump();
            if (IsRight() && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (IsLeft() && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    //detecting input
    }
    public boolean IsRight(){
        if(Gdx.input.isTouched() && Gdx.input.getX()>Gdx.graphics.getWidth()/2)return true;
        return false;
    }
    public boolean IsLeft(){
        if(Gdx.input.isTouched() && !(Gdx.input.getX()>Gdx.graphics.getWidth()/2))return true;
        return false;
    }
    public boolean IsJumpLeft(){
        if((Gdx.input.justTouched() && Gdx.input.getX() <= Gdx.graphics.getWidth()/4 && Gdx.input.getY() >=Gdx.graphics.getHeight()/2)) return true;
        return false;
    }
    public boolean IsJumpRight(){
        if((Gdx.input.justTouched() && Gdx.input.getX() >= Gdx.graphics.getWidth()-Gdx.graphics.getWidth()/4 && Gdx.input.getY()>=Gdx.graphics.getHeight()/2))return true;
        return false;
    }


    public void update(float dt){

        //handle user input first
        handleInput(dt);
        handleSpawningItems();

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for(Item item : items)
            item.update(dt);

        hud.update(dt);

        //attach our gamecam to our players.x coordinate
        if(player.currentState != Mario.State.DEAD) {
//            if()
            gamecam.position.x = player.b2body.getPosition().x;
            gamecam.position.y = player.b2body.getPosition().y;
            if(gamecam.position.y < gamePort.getWorldHeight() / 2)
                gamecam.position.y = gamePort.getWorldHeight() / 2;

        }

        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world.
        renderer.setView(gamecam);

    }

    public void spriteDraw(Sprite sprite){
        batch.begin();
        for (int i = 0; i < Gdx.graphics.getHeight(); i+=64) {
            for (int j = 0; j < Gdx.graphics.getWidth(); j+=64) {
                sprite.setPosition(j,i);
                sprite.draw(batch);
            }
        }
        batch.end();
    }

    @Override
    public void render(float delta) {
        //fill background
//        System.out.println(player.b2body.getPosition().toString());
        player.setCenter(player.b2body.getPosition().x, (float) (player.b2body.getPosition().y+0.09));
        spriteDraw(new Sprite(new Texture("Background/Yellow.png")));

        if(player.b2body.getPosition().y<0){
            player.die();
        }
        //separate our update logic from render
        update(delta);

        //render our game map
        int layers[] = {1};
        renderer.render(layers);

        //renderer our Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
//        System.out.println(player.b2body.getPosition().toString());

    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        //updated our game viewport
        gamePort.update(width,height);

    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
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
        //dispose of all our opened resources
        map.dispose();
        map.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud(){ return hud; }
}
