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

import com.badlogic.gdx.math.Rectangle;

public class DandDWars extends ApplicationAdapter implements InputProcessor {
	
	OrthographicCamera camera;
	
	TiledMap tiledMap;
	TiledMapTileLayer landscape;
	TiledMapRenderer tiledMapRenderer;
	
	SpriteBatch batch;
	ShapeRenderer sr;
	ShapeRenderer tileDraw;
	BitmapFont font;
	
	String currentMap;
	
	//turn game states
	enum TURNGS {
		PLAYER1UPKEEP,
		PLAYER1TURN,
		PLAYER2UPKEEP,
		PLAYER2TURN
	}
	
	TURNGS turnState;
	
	//overall game game states
	enum GAMEGS {
		START,
		INFO,
		GAMERUNNING,
		PAUSE
	}

	GAMEGS gameState;

	//textures for UI
	Texture troopScroll;
	Texture plainsTroopScroll;
	Texture forestTroopScroll;
	Texture mountainTroopScroll;
	Texture waterTroopScroll;
	Cell currTroopCell;

	Texture startScreen;
	Texture infoScreen;
	Texture pauseScreen;
	Rectangle startButton;
	Rectangle infoButton;
	Rectangle infoBackButton;
	Rectangle pauseButton;
	Rectangle resumeButton;

	boolean[][] troopOn;
	boolean[][] troopTeam;
	Array<Troop> RedTroops;
	Array<Troop> BlueTroops;
	Troop currTroop;
	Cell currTile;
	
	boolean drawCheck;
	boolean hasDrawnTiles;
	boolean[][] drawTiles;
	
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
		tileDraw = new ShapeRenderer();
		
		RedTroops = new Array<Troop>();
		BlueTroops = new Array<Troop>();

		turnState = TURNGS.PLAYER1UPKEEP;


		//for testing game stuff, change to GAMERUNNING so its faster to get to the game
		gameState = GAMEGS.GAMERUNNING;
		

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
		
		drawCheck = false;
		hasDrawnTiles = false;
		drawTiles = new boolean[landscape.getWidth()][landscape.getHeight()];
		
		for (int i = 0; i < landscape.getWidth(); i++) {
			for(int j = 0; j < landscape.getHeight(); j++) {
				drawTiles[i][j] = false;
			}
		}
		
		
		camera = new OrthographicCamera();
        camera.setToOrtho(false,screenw,screenh);
        camera.update();

		troopScroll = new Texture(Gdx.files.internal("land_tiles/scroll.png"));
		plainsTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_grass.png"));
		forestTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_forest.png"));
		mountainTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_mountain.png"));
		waterTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_water.png"));

		startScreen = new Texture(Gdx.files.internal("game_menus/start.png"));
		startButton = new Rectangle(140, 136, 120, 120);
		infoScreen = new Texture(Gdx.files.internal("game_menus/info.png"));
		infoButton = new Rectangle(346, 147, 118, 120);
		infoBackButton = new Rectangle(18, 18, 69, 66);
		pauseButton = new Rectangle( screenw-31, screenh-34, 30, 32);
		pauseScreen = new Texture(Gdx.files.internal("game_menus/pause.png"));
		resumeButton = new Rectangle( 234, 160, 130, 132);

		for (int i = 0; i < 16; i++) {
		    Troop troop = new Troop("knight", "red", i+3, 0, troopOn, troopTeam);
			Troop troop2 = new Troop("knight", "blue", i+3, 12, troopOn, troopTeam);
			RedTroops.add((Troop)troop);
			BlueTroops.add((Troop)troop2);
		}
		
		for (int i = 0; i < 16; i++) {
		    Troop troop = new Troop("wizard", "red", i+3, 20, troopOn, troopTeam);
			Troop troop2 = new Troop("wizard", "blue", i+3, 22, troopOn, troopTeam);
			RedTroops.add((Troop)troop);
			BlueTroops.add((Troop)troop2);
		}
		
		for (int i = 0; i < 16; i++) {
		    Troop troop = new Troop("archer", "red", i+18, 21, troopOn, troopTeam);
			Troop troop2 = new Troop("archer", "blue", i+18, 23, troopOn, troopTeam);
			RedTroops.add((Troop)troop);
			BlueTroops.add((Troop)troop2);
		}
		
		
		
	}

	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		camera.viewportWidth = screenw;
		camera.viewportHeight = screenh;
		
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		
		tiledMapRenderer.setView(camera);
		
		//various things for game state. i put the running one first as it is the
		//most important
		switch(gameState) {
			case GAMERUNNING:
				switch(turnState) {
					case PLAYER1UPKEEP: 
						//stuuuuuuff
						for (Troop t : RedTroops) {
							t.moved = false;
						}
						currTroop = null;
						currTile = null;
						turnState = TURNGS.PLAYER1TURN;
					break;
					case PLAYER2UPKEEP:
						//STUUUUUUUFF
						for (Troop t2 : BlueTroops) {
							t2.moved = false;
						}
						currTroop = null;
						currTile = null;
						turnState = TURNGS.PLAYER2TURN;
					break;
					}
				
		
				for (Troop t : RedTroops) {
					t.update(Gdx.graphics.getDeltaTime());
				}
				for (Troop t2 : BlueTroops) {
					t2.update(Gdx.graphics.getDeltaTime());
				}
		
				//More code goes here
		
				tiledMapRenderer.render();
				
				if(currTroop != null){
					tileDraw.setProjectionMatrix(camera.combined);
					tileDraw.begin(ShapeType.Filled);
					tileDraw.setColor(new Color(1, 1, 1, 0.1f));
					for (int i = 0; i < landscape.getWidth(); i++) {
						for(int j = 0; j < landscape.getHeight(); j++) {
							if(drawTiles[i][j] == true) tileDraw.rect(i*16, j*16, 16, 16);
						}
					}
					tileDraw.end();
				}
				if(currTroop == null){
					for (int i = 0; i < landscape.getWidth(); i++) {
						for(int j = 0; j < landscape.getHeight(); j++) {
							drawTiles[i][j] = false;
						}
					}
				}
		
		
		
				batch.begin();
				for (Troop t : RedTroops) {
					t.render(batch);
				}
				for (Troop t2 : BlueTroops) {
					t2.render(batch);
				}
				if(currTroop != null && drawCheck == false && !(currTroop.moved)){
					Vector2 temp = currTroop.getPos();
					//drawMovementTiles((int)temp.x / 16, (int)temp.y / 16, currTroop.speed);
					
					drawAttackTiles((int)temp.x/16, (int)temp.y/16, currTroop.attackRangeMin, currTroop.attackRangeMax, currTroop.attackRangeMin);
					//attack tiles: do the same as move but make sure that it is within the bounds
		
					drawCheck = true;
				}

				
				drawHUD();
				drawMinimap();
				batch.end();
				
			break;
			case START: 
				batch.begin();
				batch.draw(startScreen, 0, 0);
				batch.end();
				
			break;
			case INFO:
				batch.begin();
				batch.draw(infoScreen, 0, 0);
				batch.end();
			break;
			case PAUSE:
				batch.begin();
				batch.draw(pauseScreen, 0, 0);
				batch.end();
			break;
		}
		
		
	}
	
	public void drawHUD() {
		//draw big scroll
		batch.draw(troopScroll, screenw-192, 261, 192, 192);
		if (currTroop != null){
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
		} else if (currTile != null) {
			//draw troop name
			switch(currTile.getTile().getProperties().get("moveCost", Integer.class)) {
				case 1: {
					font.draw(batch, "Plains", screenw-115, 435);
					batch.draw(plainsTroopScroll, screenw-115, 346, 48, 48);
					font.draw(batch, "Move Cost: 1", screenw-140, 336);
					break;
				}
				case 2: {
					font.draw(batch, "Forest", screenw-115, 435);
					batch.draw(forestTroopScroll, screenw-115, 346, 48, 48);
					font.draw(batch, "Move Cost: 2", screenw-140, 336);
					break;
				}
				case 3: {
					font.draw(batch, "Mountain", screenw-118, 435);
					batch.draw(mountainTroopScroll, screenw-115, 346, 48, 48);
					font.draw(batch, "Move Cost: 3", screenw-140, 336);
					break;
				}
				case -1: {
					font.draw(batch, "Water", screenw-113, 435);
					batch.draw(waterTroopScroll, screenw-115, 346, 48, 48);
					font.draw(batch, "Move Cost: N/A", screenw-150, 336);
					break;
				}
			}
		}
		//draw pause button
		batch.draw(troopScroll, screenw-32, 606, 32, 32);
		batch.end();
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.BLACK);
		sr.rect( screenw-14, 613, 5, 17);
		sr.rect( screenw-22, 613, 5, 17);
		sr.end();
		batch.begin();
	}

	public void drawMinimap() {
	    int offsetX = (int)screenw-(landscape.getWidth()*3)-64;
	    int offsetY = 69; // nice

	    
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
	
	public void drawMovementTiles(int troopX, int troopY, int move){
		String report = String.format("move: %d  X: %d  Y: %d", move, troopX, troopY);
		Gdx.app.log("Info: ", report);
		report = String.format("getWidth: %d  getHeight: %d", landscape.getWidth(), landscape.getHeight());
		Gdx.app.log("Info: ", report);
		
		
		drawTiles[troopX][troopY] = true;
		if(move <= 0) return;
		if(troopX < 0) return;
		if(troopY < 0) return;
		if(troopX > landscape.getWidth()) return;
		if(troopY > landscape.getHeight()) return;
		
		
		//need to use troopOn;
		//		      troopTeam;
		//Checks tiles in order: left <, up ^, right >, down v
		//This allows for movement onto any terrain so long as the unit has 1 move left
		Gdx.app.log("Test", "Test");
		if(troopX-1 > -1 &&
		   landscape.getCell(troopX-1, troopY).getTile().getProperties().get("moveCost", Integer.class) != -1){
			drawMovementTiles(troopX-1, troopY, (move - landscape.getCell(troopX-1, troopY).getTile().getProperties().get("moveCost", Integer.class)));
		}					
		if(troopY+1 < landscape.getHeight() &&
		   landscape.getCell(troopX, troopY+1).getTile().getProperties().get("moveCost", Integer.class) != -1){
			drawMovementTiles(troopX, troopY+1, (move - landscape.getCell(troopX, troopY+1).getTile().getProperties().get("moveCost", Integer.class)));
		}
		if(troopX+1 < landscape.getWidth() &&
		   landscape.getCell(troopX+1, troopY).getTile().getProperties().get("moveCost", Integer.class) != -1){
			drawMovementTiles(troopX+1, troopY, (move - landscape.getCell(troopX+1, troopY).getTile().getProperties().get("moveCost", Integer.class)));
		}
		if(troopY-1 > -1 &&
		   landscape.getCell(troopX, troopY-1).getTile().getProperties().get("moveCost", Integer.class) != -1){
			drawMovementTiles(troopX, troopY-1, (move - landscape.getCell(troopX, troopY-1).getTile().getProperties().get("moveCost", Integer.class)));
		}
		
	}
	
	public void drawAttackTiles(int troopX, int troopY, int atkMin, int atkMax, int draw){
		String report = String.format("draw: %d  X: %d  Y: %d", draw, troopX, troopY);
		Gdx.app.log("Info: ", report);
		
		if(draw == atkMax) return;
		//drawTiles[troopX][troopY] = true;
		if(draw >= atkMin) {
			
			for (int i = 0; i < draw; i++) {
				if(troopX-(draw-i) > -1 && troopY+i < landscape.getHeight()){
					drawTiles[troopX-(draw-i)][troopY+i] = true;
				}				
				if(troopY-(draw-i) > -1 && troopX-i > -1){
					drawTiles[troopX-i][troopY-(draw-i)] = true;
				}
				if(troopY+(draw-i) < landscape.getHeight() && troopX+i < landscape.getWidth()){
					drawTiles[troopX+i][troopY+(draw-i)] = true;
				}
				if(troopX+(draw-i) < landscape.getWidth() && troopY-i > -1){
					drawTiles[troopX+(draw-i)][troopY-i] = true;
				}
				
			}
			/*
			if(troopY+draw < landscape.getHeight()){
				drawTiles[troopX][troopY+draw] = true;
			}
			
			*/
			drawAttackTiles(troopX, troopY, atkMin, atkMax, draw+1);
		} // draw here
		
		
	
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
			//for now, space ends a player turn
			case Input.Keys.SPACE:
				if (gameState == GAMEGS.GAMERUNNING) {
					if (turnState == TURNGS.PLAYER1TURN)
						turnState = TURNGS.PLAYER2UPKEEP;
					else if (turnState == TURNGS.PLAYER2TURN)
						turnState = TURNGS.PLAYER1UPKEEP;
				}
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
		clickLocation = String.format("(%d, %d)", screenX/16, screenY/16);
		Gdx.app.log("Click Location:", clickLocation);


		switch(gameState) {
			case GAMERUNNING:
				//bound clicks to the map, also stops going out of troopOn bounds
				if (screenX/16 > -1 && screenX/16 < landscape.getWidth() && screenY/16 > -1 && screenY/16 < landscape.getHeight()) {
					if (troopOn[screenX/16][screenY/16]){
						if (turnState == TURNGS.PLAYER1TURN) {
							for (Troop t : RedTroops) {
								if(t.bounds.contains(screenX, screenY)){
									Gdx.app.log("?", "Touched");
									currTroop = null;
									for (int i = 0; i < landscape.getWidth(); i++) {
										for(int j = 0; j < landscape.getHeight(); j++) {
											drawTiles[i][j] = false;
										}
									}
									currTroop = t;
									currTile = null;
									drawCheck = false;
									break;
								}
							}
						}
						if (turnState == TURNGS.PLAYER2TURN) {
							for (Troop t2 : BlueTroops) {
								if(t2.bounds.contains(screenX, screenY)){
									Gdx.app.log("?", "Touched");
									currTroop = null;
									for (int i = 0; i < landscape.getWidth(); i++) {
										for(int j = 0; j < landscape.getHeight(); j++) {
											drawTiles[i][j] = false;
										}
									}
									currTroop = t2;
									currTile = null;
										drawCheck = false;
						 			break;
								}
							}
						}			
					}
					else if (currTroop != null) {
							//cant move freely in the space. this results in infinite movement if otherwise, and not sure how to fix...
							if (drawTiles[screenX/16][screenY/16] && !(currTroop.moved)) {
								currTroop.updatePos(screenX/16, screenY/16, troopOn, troopTeam, drawTiles);
								currTroop.moved = true;
				
							} else {
								currTroop = null;
								currTile = landscape.getCell(screenX/16, screenY/16);		
							}
					}
					else {
						currTroop = null;
						currTile = landscape.getCell(screenX/16, screenY/16);
					}
					if (pauseButton.contains(screenX, screenY)) { 
						currTroop = null;
						currTile = null;
						if(gameState != GAMEGS.PAUSE) {
							Gdx.app.log("?", "game PAUSE");
							gameState = GAMEGS.PAUSE;
						}
					}
				}
			break;
			case START: 
				if (startButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.GAMERUNNING) {
						Gdx.app.log("?", "game START");
						gameState = GAMEGS.GAMERUNNING;
					}
				}
				if (infoButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.INFO) {
						Gdx.app.log("?", "game INFO");
						gameState = GAMEGS.INFO;
					}
				}
			break;
			case INFO:
				if (infoBackButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.START) {
						Gdx.app.log("?", "game STARTMENU");
						gameState = GAMEGS.START;
					}
				}
			break;
			case PAUSE:
				if (resumeButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.GAMERUNNING) {
						Gdx.app.log("?", "game resume");
						gameState = GAMEGS.GAMERUNNING;
					}
				}
			break;
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
