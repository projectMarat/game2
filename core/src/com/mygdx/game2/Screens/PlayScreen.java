package com.mygdx.game2.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
import com.mygdx.game2.values;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;


public class PlayScreen implements Screen {
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
    boolean finish;

    //Box2d variables
    private World world1;
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Mario player;

    private Music music;

    private Array<Item> items;

    ImageButton imageButton;

    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    BodyDef bdef;
    ArrayList<Body> body;
    ArrayList<Body> goldenPlank;
    PolygonShape shape;
    FixtureDef fdef;


    private Viewport viewport;
    private Stage stage;
    Sprite sprite;

    public PlayScreen(final MarioBros game) {
        sprite = new Sprite(new Texture("Background/Yellow.png"));
//        sprite.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getWidth());
        body = new ArrayList<>();
        goldenPlank = new ArrayList<>();
        Scanner s = new Scanner("worldNumber");
        while (s.hasNextInt())
            values.worldNumber = s.nextInt();
        s.close();
        finish = false;
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
        if(values.worldNumber>values.maxWorld)values.worldNumber=1;
        map = maploader.load("maps/map"+values.worldNumber+".tmx");


        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();
        player = new Mario(this);
        creator = new B2WorldCreator(this, player);
        //create mario in our game world
        bdef = creator.bdef;
        shape = creator.shape;
        fdef = creator.fdef;
        body = new ArrayList<Body>();
        world1 = creator.world;
        world.setContactListener(new WorldContactListener());
//        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
//        music.setLooping(true);
//        music.setVolume(0.3f);
//        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
        int cnt = 0;
        try {
            for (MapObject object : map.getLayers().get("metalPlanks").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
                body.add(world1.createBody(bdef));
                shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
                fdef.shape = shape;
                body.get(cnt).createFixture(fdef);
                cnt++;
            }

        }catch (NullPointerException e){}
        try {
            cnt = 0;
            for(MapObject object : map.getLayers().get("goldenPlank").getObjects()){
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
                goldenPlank.add(world1.createBody(bdef));
                shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
                fdef.shape = shape;
                goldenPlank.get(cnt).createFixture(fdef);
                cnt++;
            }
        }catch (NullPointerException e){}

//        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
//        stage = new Stage(viewport, ((MarioBros) game).batch);
//        imageButton = new ImageButton(new TextureRegionDrawable(new Texture("Menu/Buttons/Restart.png")));
//        imageButton.setSize(100,100);
//        imageButton.setPosition(125,Gdx.graphics.getHeight()-125);
//        imageButton.addListener(new InputListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                game.setScreen(new PlayScreen(game));
//                dispose();
//                return false;
//            }
//        });
//        stage.addActor(imageButton);
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }


    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {


    }

    public void handleInput(float dt) {
        //control our player using immediate impulses
        if (player.currentState != Mario.State.DEAD) {
            if (IsJumpLeft() || IsJumpRight())
                player.jump();
            if (IsRight() && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (IsLeft() && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        //detecting input
    }

    public boolean IsRight() {
        if (Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 2) return true;
        return false;
    }

    public boolean IsLeft() {
        if (Gdx.input.isTouched() && !(Gdx.input.getX() > Gdx.graphics.getWidth() / 2)) return true;
        return false;
    }

    public boolean IsJumpLeft() {
        if ((Gdx.input.justTouched() && Gdx.input.getX() <= Gdx.graphics.getWidth() / 4 && Gdx.input.getY() >= Gdx.graphics.getHeight() / 2))
            return true;
        return false;
    }

    public boolean IsJumpRight() {
        if ((Gdx.input.justTouched() && Gdx.input.getX() >= Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 4 && Gdx.input.getY() >= Gdx.graphics.getHeight() / 2))
            return true;
        return false;
    }


    public void update(float dt) {

        //handle user input first
        handleInput(dt);
        handleSpawningItems();

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }


        for (Item item : items)
            item.update(dt);

        hud.update(dt);

        //attach our gamecam to our players.x coordinate
        if (player.currentState != Mario.State.DEAD) {
//            if()
            gamecam.position.x = player.b2body.getPosition().x;
            gamecam.position.y = player.b2body.getPosition().y;
            if (gamecam.position.y < gamePort.getWorldHeight() / 2)
                gamecam.position.y = gamePort.getWorldHeight() / 2;

        }
        int cnt = 0;
        try {
            for (MapObject object : map.getLayers().get("metalPlanks").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (!((player.b2body.getPosition().y) - 1 / MarioBros.PPM > (rect.y + rect.height) / MarioBros.PPM)) {
                    body.get(cnt).setActive(false);
                } else if (!body.get(cnt).isActive()) {
                    body.get(cnt).setActive(true);
                }
                cnt++;
            }
        }catch (NullPointerException e){}




        for (MapObject object : map.getLayers().get("goldenPlank").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            if(player.b2body.getPosition().x>=rect.x/MarioBros.PPM && player.b2body.getPosition().x <= (rect.x+rect.width)/MarioBros.PPM && player.b2body.getPosition().y>= (rect.y+rect.height)/MarioBros.PPM && player.b2body.getPosition().y<=(rect.y+rect.height+8)/MarioBros.PPM)finish = true;

        }
        for (MapObject object : map.getLayers().get("killers").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            if(rect.contains(player.b2body.getPosition().x*MarioBros.PPM,player.b2body.getPosition().y*MarioBros.PPM))player.die();
        }
        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world.
        renderer.setView(gamecam);

    }

    public void spriteDraw(Sprite sprite) {
        batch.begin();
        for (int i = 0; i < Gdx.graphics.getHeight()+64; i += 64) {
            for (int j = 0; j < Gdx.graphics.getWidth()+64; j += 64) {
                sprite.setPosition(j, i);
                sprite.draw(batch);
            }
        }
        batch.end();
    }

    @Override
    public void render(float delta) {
//        fill background
//        System.out.println(player.b2body.getPosition().toString());
        player.setCenter(player.b2body.getPosition().x, (float) (player.b2body.getPosition().y + 0.09));
        spriteDraw(sprite);

        if (player.b2body.getPosition().y < 0) {
            player.die();
        }
        //separate our update logic from render
        update(delta);

        //render our game map
        renderer.render();
        //renderer our Box2DDebugLines
//        b2dr.render(world, gamecam.combined);

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
        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        if(finish()){
            game.setScreen(new PlayScreen(game));
            dispose();
        }

//        stage.draw();
//        stage.act(delta);

    }

    public boolean gameOver() {
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }
    public boolean finish(){
        if(finish && !player.isDead()){
            values.worldNumber++;
            try {
                FileWriter writer = new FileWriter("worldNumber.txt", true);
                writer.write(values.worldNumber);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    @Override
    public void resize(int width, int height) {
        //updated our game viewport
        gamePort.update(width, height);

    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
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
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud() {
        return hud;
    }
}
