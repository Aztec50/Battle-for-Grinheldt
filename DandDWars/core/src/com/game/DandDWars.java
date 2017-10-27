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
import com.badlogic.gdx.maps.MapProperties;
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
	TiledMapTileLayer landscape;
	TiledMapRenderer tiledMapRenderer;
	
	SpriteBatch batch;
	ShapeRenderer sr;
	BitmapFont font;
	
	String currentMap;
	
	//textures for UI
	Texture troopScroll;
	Texture plainsTroopScroll;
	Texture forestTroopScroll;
	Texture mountainTroopScroll;
	Cell currTroopCell;


	boolean[][] troopOn;
	boolean[][] troopTeam;
	Array<Troop> RedTroops;
	Array<Troop> BlueTroops;
	Troop currTroop;
	
	//Screen resolution variables
	float screenw;
	float screenh;
	
	//Determines how zoomed in you are:
	int zoomLevel;
	
	
	@Override
	public void create () {
		screenw = 640f; //screen resolution
        screenh = 640f;  //screen resolution
		zoomLevel = 1; // 1 = 100%, 2 = 200%, 3 = 400% zoom
		
		Gdx.input.setInputProcessor(this);
		
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		sr = new ShapeRenderer();

		RedTroops = new Array<Troop>();
		BlueTroops = new Array<Troop>();


		currentMap = "maps/TestingMap.tmx";
	
        tiledMap = new TmxMapLoader().load(currentMap);
		landscape = (TiledMapTileLayer)tiledMap.getLayers().get(0);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		troopOn = new boolean[landscape.getWidth()][landscape.getHeight()];
		troopTeam = new boolean[landscape.getWidth()][landscape.getHeight()];

 		for (int i = 0; i < landscape.getWidth(); i++) {
			for(int j = 0; j < landscape.getHeight(); j++) {
					troopOn[i][j] = landscape.getCell(i, j).getTile().getProperties().get("troopOn", Boolean.class);
					troopTeam[i][j] = landscape.getCell(i, j).getTile().getProperties().get("troopTeam", Boolean.class);
			}
		}
		
		camera = new OrthographicCamera();
        camera.setToOrtho(false,screenw,screenh);
        camera.update();

		troopScroll = new Texture(Gdx.files.internal("land_tiles/scroll.png"));
		plainsTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_grass.png"));
		forestTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_forest.png"));
		mountainTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_mountain.png"));

		for (int i = 0; i < 4; i++) {
		    Troop troop = new Troop("knight", "red", i+3, 0, troopOn, troopTeam);
			Troop troop2 = new Troop("knight", "blue", i+3, 12, troopOn, troopTeam);
			RedTroops.add((Troop)troop);
			BlueTroops.add((Troop)troop2);
			currTroop = troop;
		}
		
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
		
		
		
		for (Troop t : RedTroops) {
			t.update(Gdx.graphics.getDeltaTime());
		}
		for (Troop t2 : BlueTroops) {
			t2.update(Gdx.graphics.getDeltaTime());
		}
		
		//More code goes here
		
		tiledMapRenderer.render();
		
		batch.begin();
		for (Troop t : RedTroops) {
			t.render(batch);
		}
		for (Troop t2 : BlueTroops) {
			t2.render(batch);
		}
		drawHUD();
		drawMinimap();
		batch.end();
	}
	
	public void drawHUD() {
		//draw big scroll
		batch.draw(troopScroll, screenw-192, 261, 192, 192);
		
		//draw troop name
		switch(currTroop.troopType) {
				case KNIGHT: {
					font.draw(batch, "Knight", screenw-115, 435);
					break;
				}
				case ARCHER: {
					font.draw(batch, "Archer", screenw-115, 435);
					break;
				}
				case WIZARD: {
					font.draw(batch, "Wizard", screenw-115, 435);
					break;
				}
		}
		//draw troop scaled up over current tile

		//get the cell "under" the troop position
		currTroopCell = landscape.getCell(((int)currTroop.getPos().x)/16, ((int)currTroop.getPos().y)/16);

		//based on movement cost, draw the right one. this assumes cant move on sea/water
		switch(currTroopCell.getTile().getProperties().get("moveCost", Integer.class)) {
			case 1:
				batch.draw(plainsTroopScroll, screenw-115, 346, 48, 48);
				break;
			case 2:
				batch.draw(forestTroopScroll, screenw-115, 346, 48, 48);
				break;
			case 3:
				batch.draw(mountainTroopScroll, screenw-115, 346, 48, 48);
				break;
		}

		TextureRegion reg = null;
		reg = currTroop.animation.getKeyFrame(currTroop.stateTime,true);
		batch.draw(reg.getTexture(), screenw-115, 346, 48, 48,
			   reg.getRegionX(), reg.getRegionY(),
			   reg.getRegionWidth(), reg.getRegionHeight(),
			   false, false);

		//draw stats of said troop
		font.draw(batch, "HP: " + Integer.toString(currTroop.health), screenw-150, 336);
		font.draw(batch, "DEF: " + Integer.toString(currTroop.defense), screenw-95, 336);					
		font.draw(batch, "SPD: " + Integer.toString(currTroop.speed), screenw-150, 316);
		font.draw(batch, "DMG: " + Integer.toString(currTroop.damage), screenw-95, 316);
	}

	public void drawMinimap() {
	    int offsetX = (int)screenw-(landscape.getWidth()*3)-64;
	    int offsetY = 69;

	    //batch.draw(troopScroll, screenw, 0, 0, 0, 256, 256, 1, 1, 90f, 0, 0, 32, 32, false, true);
	    batch.draw(troopScroll, screenw-256, 5, 256, 256);
	    batch.end();
	    sr.begin(ShapeType.Filled);
	    
	    for (int i = 0; i < landscape.getWidth(); i++) {
			for(int j = 0; j < landscape.getHeight(); j++) {
		    	switch(landscape.getCell(i, j).getTile().getProperties().get("moveCost", Integer.class)) {
				case 1:
			    	sr.setColor(Color.OLIVE);
	    		    sr.rect((i*3)+offsetX, (j*3)+offsetY, 3, 3);
			    	break;
				case 2:
			    	sr.setColor(Color.FOREST);
	    		    sr.rect((i*3)+offsetX, (j*3)+offsetY, 3, 3);
			    	break;
				case 3:
			    	sr.setColor(Color.PURPLE);
	    		    sr.rect((i*3)+offsetX, (j*3)+offsetY, 3, 3);
			    	break;
				case -1:
			    	sr.setColor(Color.BLUE);
	    		    sr.rect((i*3)+offsetX, (j*3)+offsetY, 3, 3);
			    	break;
		    	}
	        }
	    }
	    sr.setColor(Color.RED);
		for (Troop t : RedTroops) {
			sr.rect( ((((int)t.getPos().x)/16)*3)+offsetX, ((((int)t.getPos().y)/16)*3)+offsetY, 3, 3);
		}
		

	    sr.setColor(Color.CYAN);
		for (Troop t2 : BlueTroops) {
	    	sr.rect( ((((int)t2.getPos().x)/16)*3)+offsetX, ((((int)t2.getPos().y)/16)*3)+offsetY, 3, 3);
		}


	    sr.end();
	    batch.begin();
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
				if (currTroop.getPos().y < screenh-16)
					currTroop.updatePos(0, 1, troopOn, troopTeam);
			break;
			case Input.Keys.S:
				if (currTroop.getPos().y > 0)
					currTroop.updatePos(0, -1, troopOn, troopTeam);
			break;
			case Input.Keys.A:
				if (currTroop.getPos().x > 0)
					currTroop.updatePos(-1, 0, troopOn, troopTeam);
			break;
			case Input.Keys.D:
				if (currTroop.getPos().x < screenw-16)
					currTroop.updatePos(1, 0, troopOn, troopTeam);
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
		
		for (Troop t : RedTroops) {
			if(t.bounds.contains(screenX, screenY)){
				Gdx.app.log("?", "Touched");
				currTroop = t;
			}
		}
		for (Troop t2 : BlueTroops) {
			if(t2.bounds.contains(screenX, screenY)){
				Gdx.app.log("?", "Touched");
				currTroop = t2;
			}
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
