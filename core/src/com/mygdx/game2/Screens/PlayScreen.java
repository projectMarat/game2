package com.mygdx.game2.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

import java.io.FileNotFoundException;
import java.io.FileReader;
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



    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    BodyDef bdef;
    ArrayList<Body> body;
    ArrayList<Body> goldenPlank;
    PolygonShape shape;
    FixtureDef fdef;


    private Viewport viewport;
    private Stage stage;
    Sprite sprite;
    TextureRegion heart[][];

    public PlayScreen(final MarioBros game) {
        heart = new TextureRegion().split(new Texture("mini-heart.png"),new Texture("mini-heart.png").getWidth()/2, new Texture("mini-heart.png").getHeight());
        sprite = new Sprite(new Texture("Background/Yellow.png"));
//        sprite.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getWidth());
        body = new ArrayList<>();
        goldenPlank = new ArrayList<>();

        finish = false;
        batch = new SpriteBatch();

        this.game = game;
        gamecam = new OrthographicCamera();

        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);

        hud = new Hud(game.batch);

        maploader = new TmxMapLoader();

        try {
            Scanner scan = new Scanner(new FileReader("maps/worldNumber.txt"));
            int a = scan.nextInt();
            values.lives = a%10;
            values.worldNumber = a/10%10;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(values.worldNumber>values.maxWorld || values.worldNumber<1)values.worldNumber=1;
        map = maploader.load("maps/map"+values.worldNumber+".tmx");


        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        player = new Mario(this);
        creator = new B2WorldCreator(this, player);
        bdef = creator.bdef;
        shape = creator.shape;
        fdef = creator.fdef;
        body = new ArrayList<Body>();
        world1 = creator.world;
        world.setContactListener(new WorldContactListener());

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
        if(values.lives<=0){
            values.lives=3;
        }
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
        if (player.currentState != Mario.State.DEAD) {
            if (IsJumpLeft() || IsJumpRight())
                player.jump();
            if (IsRight() && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (IsLeft() && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
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

        handleInput(dt);
        handleSpawningItems();

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

        if (player.currentState != Mario.State.DEAD) {
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
        gamecam.update();
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
        player.setCenter(player.b2body.getPosition().x, (float) (player.b2body.getPosition().y + 0.09));
        spriteDraw(sprite);

        if (player.b2body.getPosition().y < 0) {
            player.die();
        }
        update(delta);

        renderer.render();

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        for (int i = 0; i < 3; i++){
            game.batch.begin();
            game.batch.draw(heart[0][1], (float) (0+i*heart[0][1].getRegionWidth()*(Gdx.graphics.getWidth()/650)), (float) (Gdx.graphics.getHeight()/3.1), (float) (heart[0][1].getRegionHeight()*Gdx.graphics.getWidth()/689.92), (float) (heart[0][1].getRegionHeight()*Gdx.graphics.getWidth()/689.92));
            game.batch.end();
        }
        for (int i = 0; i < values.lives; i++) {
            game.batch.begin();
            game.batch.draw(heart[0][0], (float) (0+i*heart[0][0].getRegionWidth()*(Gdx.graphics.getWidth()/650)), (float) (Gdx.graphics.getHeight()/3.1), (float) (heart[0][0].getRegionHeight()*Gdx.graphics.getWidth()/689.92), (float) (heart[0][0].getRegionHeight()*Gdx.graphics.getWidth()/689.92));
            game.batch.end();
        }
        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        if(finish()){
            game.setScreen(new PlayScreen(game));
            dispose();
        }


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
                FileWriter writer = new FileWriter("maps/worldNumber.txt", true);
                writer.write(values.worldNumber+""+values.lives);
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
        map.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud() {
        return hud;
    }
}
