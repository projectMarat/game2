package com.mygdx.game2.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game2.MarioBros;
import com.mygdx.game2.Scenes.Hud;
import com.mygdx.game2.Screens.PlayScreen;
import com.mygdx.game2.Sprites.Mario;


public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
//            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
//        MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

}
