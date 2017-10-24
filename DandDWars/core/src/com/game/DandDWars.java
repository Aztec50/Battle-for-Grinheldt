package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.utils.Array;

import com.mygdx.game.objects.Troop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class DandDWars extends ApplicationAdapter implements InputProcessor {
	
	OrthographicCamera camera;
	
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	
	SpriteBatch batch;
	ShapeRenderer sr;
	BitmapFont font;
	
	String currentMap;
	
	
	
	Troop troop;
	Troop troop2;
	//Screen resolution variables
	float screenw;
	float screenh;
	
	//Determines how zoomed in you are
	int zoomLevel;
	
	
	@Override
	public void create () {
		screenw = 640f; //screen resolution
        screenh = 640f;  //screen resolution
		zoomLevel = 1; // 1 = 100%, 2 = 200%, 3 = 400% zoom
		
		Gdx.input.setInputProcessor(this);
		
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);
		sr = new ShapeRenderer();
	
		currentMap = "maps/GrassMap.tmx";
	
        tiledMap = new TmxMapLoader().load(currentMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		camera = new OrthographicCamera();
        camera.setToOrtho(false,screenw,screenh);
        camera.update();
		
		troop = new Troop("knight", "red", 0, 0);
		troop2 = new Troop("knight", "blue", 1, 1);
	}

	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.viewportWidth = screenw;
		camera.viewportHeight = screenh;
		
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		
		tiledMapRenderer.setView(camera);
		
		troop2.update(Gdx.graphics.getDeltaTime());
		troop.update(Gdx.graphics.getDeltaTime());
		
		//More code goes here
		
		tiledMapRenderer.render();
		
		batch.begin();
		troop.render(batch);
		troop2.render(batch);
		drawHUD(troop);
		batch.end();
	}
	
	public void drawHUD(Troop currTroop) {
		
		//draw big rectangle
		batch.end();
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.WHITE);
		sr.rect(0, 0, 120, 120);
		sr.end();
		batch.begin();
		//draw troop name
		switch(currTroop.troopType) {
				case KNIGHT: {
					font.draw(batch, "Knight", 40, 110);
					

		TextureRegion reg = null;
		reg = troop.animation.getKeyFrame(troop.stateTime,true);

		/*  This draw function was a doozy to figure out, but as it is
         *  it should draw and animate all of our units. In the case that
         *  something needs to be changed, the prototype is provided. 
		 *
         *  draw(Texture texture, float x, float y, float width, float height,
		 *       int srcX, int srcY,
		 *		 int srcWidth, int srcHeight,
		 *		 boolean flipX, boolean flipY);
		 */
		
		batch.draw(reg.getTexture(), 44, 60, 32, 32,
				   reg.getRegionX(), reg.getRegionY(),
				   reg.getRegionWidth(), reg.getRegionHeight(),
				   false, false);
					break;
				}
				case ARCHER: {
					font.draw(batch, "Archer", 30, 110);
					break;
				}
				case WIZARD: {
					font.draw(batch, "Wizard", 30, 110);
					break;
				}
		}
		//draw troop scaled up over current tile
		//draw stats of said troop
		//font.draw(batch, "PAUSED", width/2, height/2, width/2, 10, false);
	}
	
	@Override
    public boolean keyDown(int keycode) {
		switch(keycode){
			case Input.Keys.UP:
				camera.translate(0, 16);
			break;
			case Input.Keys.DOWN:
				camera.translate(0, -16);
			break;
			case Input.Keys.LEFT:
				camera.translate(-16, 0);
			break;
			case Input.Keys.RIGHT:
				camera.translate(16, 0);
			break;
			//This is just silly testing
			case Input.Keys.W:
				if (troop.getPos().y < screenh-16)
					troop.updatePos(0, 1);
			break;
			case Input.Keys.S:
				if (troop.getPos().y > 0)
					troop.updatePos(0, -1);
			break;
			case Input.Keys.A:
				if (troop.getPos().x > 0)
					troop.updatePos(-1, 0);
			break;
			case Input.Keys.D:
				if (troop.getPos().x < screenw-16)
					troop.updatePos(1, 0);
			break;
		}
		return false;
    }
	
	@Override
    public boolean keyUp(int keycode) {
		return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// First attempts at clicking units
		
		//flips origin from top left to bottom left
		screenY = (int)screenh - screenY;
		
		String clickLocation = "";
		clickLocation = String.format("(%d, %d)", screenX, screenY);
		Gdx.app.log("Click Location:", clickLocation);
		
		
		if(troop.bounds.contains(screenX, screenY)){
			Gdx.app.log("?", "Touched");
		}
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

	@Override
	public void dispose () {
	}
	
	@Override
	public void resize(int width, int height){
		//camera.viewportWidth = width;
		//camera.viewportHeight = height;
	}

	
}
