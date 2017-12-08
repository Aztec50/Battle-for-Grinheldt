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
import com.mygdx.game.objects.EnemyTroop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.mygdx.game.ai.GraphGenerator;
import com.mygdx.game.ai.Node;
import com.mygdx.game.ai.GraphImp;


public class DandDWars extends ApplicationAdapter implements InputProcessor {
	
	OrthographicCamera camera;
	
	TiledMap tiledMap;
	public TiledMapTileLayer landscape;
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
		PLAYER2TURN,
		AIUPKEEPANDTURN
	}
	
	TURNGS turnState;
	
	//overall game game states
	enum GAMEGS {
		START,
		INFO,
		GAMERUNNING,
		PAUSE,
		ENDRED,
		ENDBLUE,
		ENDAI
	}

	GAMEGS gameState;

	//textures for UI
	Texture troopScroll;
	Texture plainsTroopScroll;
	Texture forestTroopScroll;
	Texture mountainTroopScroll;
	Texture waterTroopScroll;
	Cell currTroopCell;
	
	int panOffsetX = 0;
	int panOffsetY = 0;

	Texture startScreen;
	Texture infoScreen;
	Texture pauseScreen;
	Texture endRedScreen;
	Texture endBlueScreen;
	Texture endAIScreen;
	Rectangle startButton;
	Rectangle infoButton;
	Rectangle infoBackButton;
	Rectangle pauseButton;
	Rectangle resumeButton;

	//Team variables
	boolean[][] troopOn;
	boolean[][] troopTeam;
	Array<Troop> RedTroops;
	Array<Troop> BlueTroops;
	Array<EnemyTroop> EnemyTroops;
	Troop currTroop;
	Cell currTile;


  Texture buttonOffPlaque;
	Texture buttonOnPlaque;
	Texture redBanner;
	Texture blueBanner;

  
  
  //HUD object variables
	Rectangle attackButton;
	Rectangle moveButton;
	Rectangle nextTurnButton;
	Rectangle playerTurnBanner;
	
	//Dynamic tile draw variables
	boolean drawCheck;
	boolean hasDrawnTiles;
	boolean[][] drawTiles;
	Texture movementTile;
	Texture attackTile;
	Texture highlightTile;
	
	//Damage draw variables
	String displayDamageValue;
	Vector2 displayDamageValuePos;
	Vector2 displayDamageValuePosTarget;
	float displayDamageTime;
	float displayDamageTimeCap;
	boolean	displayDamage;
	
	//Screen resolution variables
	float screenw;
	float screenh;
	
	//Determines how zoomed in you are:
	int zoomLevel;
	
	//ai shenanigans
	GraphImp graph;
	GraphGenerator GG;
	
	
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
		GG = new GraphGenerator();
		
		RedTroops = new Array<Troop>();
		BlueTroops = new Array<Troop>();
		EnemyTroops = new Array<EnemyTroop>();

		turnState = TURNGS.PLAYER1UPKEEP;


		//for testing game stuff, change to GAMERUNNING so its faster to get to the game
		gameState = GAMEGS.START;
		

		currentMap = "maps/TestingMap.tmx";
		tiledMap = new TmxMapLoader().load(currentMap);
		landscape = (TiledMapTileLayer)tiledMap.getLayers().get(0);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2f);
		graph = GG.generateGraph(landscape);

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
		
		movementTile = new Texture(Gdx.files.internal("land_tiles/tile_movement.png"));
		attackTile = new Texture(Gdx.files.internal("land_tiles/tile_attack.png"));
		highlightTile = new Texture(Gdx.files.internal("land_tiles/tile_highlight.png"));

		displayDamageValue = "";
		displayDamageValuePos = new Vector2();
		displayDamageValuePosTarget = new Vector2();
		displayDamageTime = 0f;
		displayDamageTimeCap = 1.0f;
		displayDamage = false;	
  
  
		camera = new OrthographicCamera();
        camera.setToOrtho(false,screenw,screenh);
        
        camera.update();

		troopScroll = new Texture(Gdx.files.internal("land_tiles/scroll.png"));
		plainsTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_grass.png"));
		forestTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_forest.png"));
		mountainTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_mountain.png"));
		waterTroopScroll = new Texture(Gdx.files.internal("land_tiles/tile_water.png"));
		buttonOffPlaque = new Texture(Gdx.files.internal("land_tiles/buttonOffPlaque.png"));
		buttonOnPlaque = new Texture(Gdx.files.internal("land_tiles/buttonOnPlaque.png"));
		redBanner = new Texture(Gdx.files.internal("land_tiles/redBanner.png"));
		blueBanner = new Texture(Gdx.files.internal("land_tiles/blueBanner.png"));	
		
		startScreen = new Texture(Gdx.files.internal("game_menus/start.png"));
		startButton = new Rectangle(140, 136, 120, 120);
		infoScreen = new Texture(Gdx.files.internal("game_menus/DandInfo.png"));
		infoButton = new Rectangle(346, 147, 118, 120);
		infoBackButton = new Rectangle(18, 18, 69, 66);
		pauseButton = new Rectangle( screenw-31, screenh-34, 30, 32);
		pauseScreen = new Texture(Gdx.files.internal("game_menus/pause.png"));
		resumeButton = new Rectangle(234, 160, 130, 132);
		attackButton = new Rectangle(screenw-130, 485, 90, 25);
		moveButton = new Rectangle(screenw-130, 455, 90, 25);
		nextTurnButton = new Rectangle (screenw-140, 520, 110, 25);
		playerTurnBanner = new Rectangle(208,600,200,32);
		endRedScreen = new Texture(Gdx.files.internal("game_menus/endRed.png"));
		endBlueScreen = new Texture(Gdx.files.internal("game_menus/endBlue.png"));
		endAIScreen = new Texture(Gdx.files.internal("game_menus/endAI.png"));
		
		for (int i = 0; i < 10; i++) {
		    Troop troop = new Troop("knight", "red", i+6, 4, troopOn, troopTeam);
			//EnemyTroop troop2 = new EnemyTroop("knight", "blue", i+7, 5, troopOn, troopTeam, landscape.getWidth(), landscape.getHeight());
			RedTroops.add((Troop)troop);
			//EnemyTroops.add((EnemyTroop)troop2);
		}
		for (int i = 0; i < 2; i++) {
		    Troop troop = new Troop("wizard", "red", i+10, 2, troopOn, troopTeam);
			EnemyTroop troop2 = new EnemyTroop("wizard", "blue", i+9, 11, troopOn, troopTeam, landscape.getWidth(), landscape.getHeight());
			RedTroops.add((Troop)troop);
			EnemyTroops.add((EnemyTroop)troop2);
		}
		
		for (int i = 0; i < 5; i++) {
		    Troop troop = new Troop("archer", "red", i+8, 3, troopOn, troopTeam);
			EnemyTroop troop2 = new EnemyTroop("archer", "blue", i+10, 12, troopOn, troopTeam, landscape.getWidth(), landscape.getHeight());
			RedTroops.add((Troop)troop);
			EnemyTroops.add((EnemyTroop)troop2);

		}
		
  
  
		//Troop troop = new Troop("knight", "red", 6, 6, troopOn, troopTeam);
		//EnemyTroop enemy = new EnemyTroop("dragon", "blue", 8, 12, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//EnemyTroops.add((EnemyTroop)enemy);
		//
		//troop = new Troop("knight", "red", 6, 5, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//troop = new Troop("archer", "red", 7, 5, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//troop = new Troop("wizard", "red", 8, 5, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//troop = new Troop("barbarian", "red", 9, 5, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//troop = new Troop("rogue", "red", 10, 5, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//troop = new Troop("mystic", "red", 11, 5, troopOn, troopTeam);
		//RedTroops.add((Troop)troop);
		//troop = new Troop("dragon", "red", 12, 5, troopOn, troopTeam);		
		//RedTroops.add((Troop)troop);
		//
        //
		Troop troop2 = new Troop("wizard", "blue", 18, 34, troopOn, troopTeam);
		BlueTroops.add((Troop)troop2);  
  
  
  
  
	//Troop troop = new Troop("knight", "red", 6, 6, troopOn, troopTeam);
	//EnemyTroop enemy = new EnemyTroop("knight", "blue", 8, 9, troopOn, troopTeam);
	//RedTroops.add((Troop)troop);
	//EnemyTroops.add((EnemyTroop)enemy);
  
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
							t.upkeep();
						}
						currTroop = null;
						currTile = null;
						turnState = TURNGS.PLAYER1TURN;
						if (RedTroops.random() == null && EnemyTroops.random() == null) {
							gameState = GAMEGS.ENDBLUE;
						}
					break;
					case PLAYER2UPKEEP:
						//STUUUUUUUFF
						
						for (Troop t2 : BlueTroops) {
							t2.upkeep();
						}
						currTroop = null;
						currTile = null;
						turnState = TURNGS.PLAYER2TURN;
						if (BlueTroops.random() == null && EnemyTroops.random() == null) { //returns null if nothing in the "array"
							gameState = GAMEGS.ENDRED;
						}
					break;
					case AIUPKEEPANDTURN:
						for (EnemyTroop e : EnemyTroops) {
							e.upkeep();
							Vector2 temp = e.getPos();
							Array<Troop> AllTroops = new Array<Troop>();
							AllTroops.addAll(RedTroops);
							AllTroops.addAll(BlueTroops);
							
							drawAttackTiles((int)temp.x/32, (int)temp.y/32, e.attackRangeMin, e.attackRangeMax, e.attackRangeMin);
							e.findTarget(graph, AllTroops, drawTiles);
							for (int i = 0; i < landscape.getWidth(); i++) {
								for(int j = 0; j < landscape.getHeight(); j++) {
									drawTiles[i][j] = false;
								}
							}
							
							drawMovementTiles((int)temp.x / 32, (int)temp.y / 32, e.speed);
							e.moveToTarget(troopOn, troopTeam, drawTiles);
							for (int i = 0; i < landscape.getWidth(); i++) {
								for(int j = 0; j < landscape.getHeight(); j++) {
									drawTiles[i][j] = false;
								}
							}
							drawAttackTiles((int)temp.x/32, (int)temp.y/32, e.attackRangeMin, e.attackRangeMax, e.attackRangeMin);
							if (e.target.team == Troop.TEAM.RED)
								e.attackTarget(RedTroops, troopOn, drawTiles);
							if (e.target.team == Troop.TEAM.BLUE)
								e.attackTarget(BlueTroops, troopOn, drawTiles);
							for (int i = 0; i < landscape.getWidth(); i++) {
								for(int j = 0; j < landscape.getHeight(); j++) {
									drawTiles[i][j] = false;
								}
							}
						}
						turnState = TURNGS.PLAYER1UPKEEP;
						if (BlueTroops.random() == null && RedTroops.random() == null) { //returns null if nothing in the "array"
							gameState = GAMEGS.ENDAI;
						}
					break;
					}
				
		
				for (Troop t : RedTroops) {
					t.update(Gdx.graphics.getDeltaTime());
				}
				for (Troop t2 : BlueTroops) {
					t2.update(Gdx.graphics.getDeltaTime());
				}
				
				for (EnemyTroop e : EnemyTroops) {
					e.update(Gdx.graphics.getDeltaTime());
				}
		
				//More code goes here
		
				tiledMapRenderer.render();
				batch.begin();
				if(currTroop != null){
					//tileDraw.setProjectionMatrix(camera.combined);
					//tileDraw.begin(ShapeType.Filled);
					if (currTroop.state == Troop.ACTION.MOVE){
						
						for (int i = 0; i < landscape.getWidth(); i++) {
							for(int j = 0; j < landscape.getHeight(); j++) {
								//if(drawTiles[i][j] == true) tileDraw.rect(i*16, j*16, 16, 16);
								//if(drawTiles[i][j] == true) batch.draw(movementTile, i*16, j*16);
                if(drawTiles[i][j] == true) batch.draw(movementTile, i*32, j*32, 32, 32);
							}
						}

					}
					else if (currTroop.state == Troop.ACTION.ATTACK){
						
						for (int i = 0; i < landscape.getWidth(); i++) {
							for(int j = 0; j < landscape.getHeight(); j++) {
								//if(drawTiles[i][j] == true) tileDraw.rect(i*16, j*16, 16, 16);
								//if(drawTiles[i][j] == true) batch.draw(attackTile, i*16, j*16);
                if(drawTiles[i][j] == true) batch.draw(attackTile, i*32, j*32, 32, 32);
							}
						}

					}
					batch.draw(highlightTile, currTroop.getPos().x, currTroop.getPos().y, 32, 32);
					//tileDraw.end();
				}
				batch.end();
				if(currTroop == null){
					for (int i = 0; i < landscape.getWidth(); i++) {
						for(int j = 0; j < landscape.getHeight(); j++) {
							drawTiles[i][j] = false;
						}
					}
				}
		
		
		
				batch.begin();
				for (Troop t : RedTroops) {
					t.render(batch, sr, panOffsetX, panOffsetY);
					/* DEBUG TURN ON BOUNDING BOXES
					batch.end();
					sr.begin(ShapeType.Filled);
					sr.setColor(Color.RED);
					sr.rect(t.bounds.x-panOffsetX, t.bounds.y-panOffsetY, t.bounds.width, t.bounds.height);
					sr.end();
					batch.begin();
					*/
				}
				for (Troop t2 : BlueTroops) {
					t2.render(batch, sr, panOffsetX, panOffsetY);
					/* DEBUG TURN ON BOUNDING BOXES
					batch.end();
					sr.begin(ShapeType.Filled);
					sr.setColor(Color.CYAN);
					sr.rect(t2.bounds.x-panOffsetX, t2.bounds.y-panOffsetY, t2.bounds.width, t2.bounds.height);
					sr.end();
					batch.begin();
					*/
				}
				for (EnemyTroop e : EnemyTroops) {
					e.render(batch, sr, panOffsetX, panOffsetY);
				}
				if(currTroop != null && drawCheck == false && !(currTroop.moved) && currTroop.state == Troop.ACTION.MOVE){
					Vector2 temp = currTroop.getPos();
					drawMovementTiles((int)temp.x / 32, (int)temp.y / 32, currTroop.speed);
					drawCheck = true;
				}
				if(currTroop != null && drawCheck == false && !(currTroop.attacked) && currTroop.state == Troop.ACTION.ATTACK){
					Vector2 temp = currTroop.getPos();
					drawAttackTiles((int)temp.x/32, (int)temp.y/32, currTroop.attackRangeMin, currTroop.attackRangeMax, currTroop.attackRangeMin);
					drawCheck = true;
				}

				
				if(displayDamage == true){
					//displayDamageValuePos.lerp(displayDamageValuePosTarget, 0);
					float fadeoutValue;
					fadeoutValue = 1 - (displayDamageTime / displayDamageTimeCap);
					
					font.setColor(255f, 0f, 0f, fadeoutValue);
					font.draw(batch, displayDamageValue, displayDamageValuePos.x, displayDamageValuePos.y);
					font.setColor(Color.BLACK);
					displayDamageTime += Gdx.graphics.getDeltaTime();
					if(displayDamageTime > displayDamageTimeCap){
						displayDamageTime = 0f;
						displayDamage = false;
					}
				}
				
				
				drawHUD();
				drawMinimap();
				batch.end();
				
				
				
			break;
			case START: 
				batch.begin();
				batch.draw(startScreen, 0+panOffsetX, 0+panOffsetY);
				batch.end();
				
			break;
			case INFO:
				batch.begin();
				batch.draw(infoScreen, 0+panOffsetX, 0+panOffsetY);
				batch.end();
			break;
			case PAUSE:
				batch.begin();
				batch.draw(pauseScreen, 0+panOffsetX, 0+panOffsetY);
				batch.end();
			break;
			case ENDRED:
				batch.begin();
				batch.draw(endRedScreen, 0+panOffsetX, 0+panOffsetY);
				batch.end();
			break;
			case ENDBLUE:
				batch.begin();
				batch.draw(endBlueScreen, 0+panOffsetX, 0+panOffsetY);
				batch.end();
			break;
			case ENDAI:
				batch.begin();
				batch.draw(endAIScreen, 0+panOffsetX, 0+panOffsetY);
				batch.end();
			break;
		}
		
		
	}
	
	public void drawHUD() {
		
		//draw big scroll
		batch.draw(troopScroll, screenw-192+panOffsetX, 261+panOffsetY, 192, 192);
		if (currTroop != null){
			//draw troop name
			switch(currTroop.troopType) {
					case KNIGHT: {
						font.draw(batch, "Knight", screenw-115+panOffsetX, 435+panOffsetY);
						break;
					}
					case ARCHER: {
						font.draw(batch, "Archer", screenw-115+panOffsetX, 435+panOffsetY);
						break;
					}
					case WIZARD: {
						font.draw(batch, "Wizard", screenw-115+panOffsetX, 435+panOffsetY);
						break;
					}
			}
			
			
			
			//draw troop scaled up over current tile
	
			//get the cell "under" the troop position
			currTroopCell = landscape.getCell(((int)currTroop.getPos().x)/32, ((int)currTroop.getPos().y)/32);

			//based on movement cost, draw the right one. this assumes cant move on sea/water
			switch(currTroopCell.getTile().getProperties().get("moveCost", Integer.class)) {
				case 1:
					batch.draw(plainsTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					break;
				case 2:
					batch.draw(forestTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					break;
				case 3:
					batch.draw(mountainTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					break;
			}

			TextureRegion reg = null;
			reg = currTroop.animation.getKeyFrame(currTroop.stateTime,true);
			batch.draw(reg.getTexture(), screenw-115+panOffsetX, 346+panOffsetY, 48, 48,
					   reg.getRegionX(), reg.getRegionY(),
					   reg.getRegionWidth(), reg.getRegionHeight(),
					   false, false);

			//draw stats of said troop
			font.draw(batch, "HP: " + Integer.toString(currTroop.health), screenw-150+panOffsetX, 336+panOffsetY);
			font.draw(batch, "DEF: " + Integer.toString(currTroop.defense), screenw-95+panOffsetX, 336+panOffsetY);					
			font.draw(batch, "SPD: " + Integer.toString(currTroop.speed), screenw-150+panOffsetX, 316+panOffsetY);
			font.draw(batch, "DMG: " + Integer.toString(currTroop.damage), screenw-95+panOffsetX, 316+panOffsetY);
		} else if (currTile != null) {
			//draw troop name
			switch(currTile.getTile().getProperties().get("moveCost", Integer.class)) {
				case 1: {
					font.draw(batch, "Plains", screenw-115+panOffsetX, 435+panOffsetY);
					batch.draw(plainsTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					font.draw(batch, "Move Cost: 1", screenw-140+panOffsetX, 336+panOffsetY);
					break;
				}
				case 2: {
					font.draw(batch, "Forest", screenw-115+panOffsetX, 435+panOffsetY);
					batch.draw(forestTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					font.draw(batch, "Move Cost: 2", screenw-140+panOffsetX, 336+panOffsetY);
					break;
				}
				case 3: {
					font.draw(batch, "Mountain", screenw-118+panOffsetX, 435+panOffsetY);
					batch.draw(mountainTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					font.draw(batch, "Move Cost: 3", screenw-140+panOffsetX, 336+panOffsetY);
					break;
				}
				case -1: {
					font.draw(batch, "Water", screenw-113+panOffsetX, 435+panOffsetY);
					batch.draw(waterTroopScroll, screenw-115+panOffsetX, 346+panOffsetY, 48, 48);
					font.draw(batch, "Move Cost: N/A", screenw-150+panOffsetX, 336+panOffsetY);
					break;
				}
			}
		}
		//draw pause button
		batch.draw(troopScroll, screenw-32+panOffsetX, 606+panOffsetY, 32, 32);
		switch (turnState) {
				case PLAYER1TURN:
					batch.draw(redBanner, playerTurnBanner.x+panOffsetX, playerTurnBanner.y+panOffsetY, 
									playerTurnBanner.width, playerTurnBanner.height);
				break;
				case PLAYER2TURN:
					batch.draw(blueBanner, playerTurnBanner.x+panOffsetX, playerTurnBanner.y+panOffsetY, 
									playerTurnBanner.width, playerTurnBanner.height);
				break;
		}
		
		batch.end();

		sr.begin(ShapeType.Filled);
		sr.setColor(Color.BLACK);
		sr.rect( screenw-14, 613, 5, 17);
		sr.rect( screenw-22, 613, 5, 17);
		sr.setColor(Color.GREEN);
		sr.rect(nextTurnButton.x, nextTurnButton.y, nextTurnButton.width, nextTurnButton.height);
		sr.end();

		batch.begin();

		if (currTroop != null) {
			if(!currTroop.attacked)
				batch.draw(buttonOnPlaque, attackButton.x+panOffsetX, attackButton.y+panOffsetY, attackButton.width, attackButton.height);
			else
				batch.draw(buttonOffPlaque, attackButton.x+panOffsetX, attackButton.y+panOffsetY, attackButton.width, attackButton.height);
			if(!currTroop.moved)
				batch.draw(buttonOnPlaque, moveButton.x+panOffsetX, moveButton.y+panOffsetY, moveButton.width, moveButton.height);
			else
				batch.draw(buttonOffPlaque, moveButton.x+panOffsetX, moveButton.y+panOffsetY, moveButton.width, moveButton.height);
		}
		font.draw(batch, "END TURN", nextTurnButton.x+18+panOffsetX, nextTurnButton.y+15+panOffsetY);
		if (currTroop != null) {
			if (!currTroop.moved)
				font.draw(batch, "(M)OVE", moveButton.x+19+panOffsetX, moveButton.y+18+panOffsetY);
			else
				font.draw(batch, "MOVED", moveButton.x+19+panOffsetX, moveButton.y+18+panOffsetY);
				
				
			if (!currTroop.attacked)
				font.draw(batch, "(A)TTACK", attackButton.x+13+panOffsetX, attackButton.y+18+panOffsetY);
			else
				font.draw(batch, "ATTACKED", attackButton.x+8+panOffsetX, attackButton.y+18+panOffsetY);
		}
		switch (turnState) {
				case PLAYER1TURN:
					font.draw(batch, "Player 1 Turn", playerTurnBanner.x+40+panOffsetX, playerTurnBanner.y+22+panOffsetY);
				break;
				case PLAYER2TURN:
					font.draw(batch, "Player 2 Turn", playerTurnBanner.x+40+panOffsetX, playerTurnBanner.y+22+panOffsetY);
				break;
		}
	}

	public void drawMinimap() {
	    int offsetX = (int)screenw-(landscape.getWidth()*3)-64;
	    int offsetY = 69; // nice

	    
	    batch.draw(troopScroll, screenw-256+panOffsetX, 5+panOffsetY, 256, 256);
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
			sr.rect( ((((int)t.getPos().x)/32)*3)+offsetX, ((((int)t.getPos().y)/32)*3)+offsetY, 3, 3);
		}
		

	    sr.setColor(Color.CYAN);
		for (Troop t2 : BlueTroops) {
	    	sr.rect( ((((int)t2.getPos().x)/32)*3)+offsetX, ((((int)t2.getPos().y)/32)*3)+offsetY, 3, 3);
		}
		
		
	    sr.end();
		sr.begin(ShapeType.Line);
		sr.setColor(Color.WHITE);
		sr.rect(offsetX+((panOffsetX/32)*3), offsetY+((panOffsetY/32)*3), 60, 60);
		sr.end();
	    batch.begin();
	}
	
	public void drawMovementTiles(int troopX, int troopY, int move){
		String report = String.format("move: %d  X: %d  Y: %d", move, troopX, troopY);
		//Gdx.app.log("Info: ", report);
		report = String.format("getWidth: %d  getHeight: %d", landscape.getWidth(), landscape.getHeight());
		//Gdx.app.log("Info: ", report);
		
		
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
		//Gdx.app.log("Test", "Test");
		if(troopX-1 > -1 &&
		   landscape.getCell(troopX-1, troopY).getTile().getProperties().get("moveCost", Integer.class) != -1 &&
		   !troopOn[troopX-1][troopY]){
			drawMovementTiles(troopX-1, troopY, (move - landscape.getCell(troopX-1, troopY).getTile().getProperties().get("moveCost", Integer.class)));
		}					
		if(troopY+1 < landscape.getHeight() &&
		   landscape.getCell(troopX, troopY+1).getTile().getProperties().get("moveCost", Integer.class) != -1 &&
		   !troopOn[troopX][troopY+1]){
			drawMovementTiles(troopX, troopY+1, (move - landscape.getCell(troopX, troopY+1).getTile().getProperties().get("moveCost", Integer.class)));
		}
		if(troopX+1 < landscape.getWidth() &&
		   landscape.getCell(troopX+1, troopY).getTile().getProperties().get("moveCost", Integer.class) != -1 &&
		   !troopOn[troopX+1][troopY]){
			drawMovementTiles(troopX+1, troopY, (move - landscape.getCell(troopX+1, troopY).getTile().getProperties().get("moveCost", Integer.class)));
		}
		if(troopY-1 > -1 &&
		   landscape.getCell(troopX, troopY-1).getTile().getProperties().get("moveCost", Integer.class) != -1 &&
		   !troopOn[troopX][troopY-1]){
			drawMovementTiles(troopX, troopY-1, (move - landscape.getCell(troopX, troopY-1).getTile().getProperties().get("moveCost", Integer.class)));
		}
		
	}
	
	public void drawAttackTiles(int troopX, int troopY, int atkMin, int atkMax, int draw){
		String report = String.format("draw: %d  X: %d  Y: %d", draw, troopX, troopY);
		//Gdx.app.log("Info: ", report);
		
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
			case Input.Keys.M:
			if(currTroop != null){
				if (!currTroop.moved){
					currTroop.state = Troop.ACTION.MOVE;
					for (int i = 0; i < landscape.getWidth(); i++) {
						for(int j = 0; j < landscape.getHeight(); j++) {
							drawTiles[i][j] = false;
						}
					}
					drawCheck = false;
				}
			}
			break;
			case Input.Keys.A:
			if(currTroop != null) {
				if (!currTroop.attacked){
					currTroop.state = Troop.ACTION.ATTACK;
					for (int i = 0; i < landscape.getWidth(); i++) {
						for(int j = 0; j < landscape.getHeight(); j++) {
							drawTiles[i][j] = false;
						}
					}
					drawCheck = false;
				}
			}
			break;
			case Input.Keys.UP:
				if (panOffsetY+32 < 672) {
					camera.translate(0, 32);
					camera.update();
					panOffsetY+=32;
				}
			break;
			case Input.Keys.DOWN:
				if (panOffsetY-32 > -32) {
					camera.translate(0, -32);
					camera.update();
					panOffsetY-=32;
				}
			break;
			case Input.Keys.LEFT:
				if (panOffsetX-32 > -32) {
					camera.translate(-32, 0);
					camera.update();
					panOffsetX-=32;
				}
			break;
			case Input.Keys.RIGHT:
				if (panOffsetX+32 < 672) {
					camera.translate(32, 0);
					camera.update();
					panOffsetX+=32;
				}
			break;
			//for now, space ends a player turn
			case Input.Keys.SPACE:
				
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
		clickLocation = String.format("(%d, %d)", screenX/32+panOffsetX/32, screenY/32+panOffsetY/32);
		//click still not working.
		//Gdx.app.log("Click Location:", clickLocation);


		switch(gameState) {
			case GAMERUNNING:
				//bound clicks to the map, also stops going out of troopOn bounds
				if (screenX/32+panOffsetX/32>-1&&screenX/32+panOffsetX/32<landscape.getWidth()&&screenY/32+panOffsetY/32>-1&&screenY/32+panOffsetY/32<landscape.getHeight()){
					if (pauseButton.contains(screenX, screenY)) { 
						if (currTroop != null)
							currTroop.state = Troop.ACTION.IDLE;
						currTroop = null;
						currTile = null;
						if(gameState != GAMEGS.PAUSE) {
							//Gdx.app.log("?", "game PAUSE");
							gameState = GAMEGS.PAUSE;
						}
					} 
					//check if end turn is pressed
					else if (nextTurnButton.contains(screenX, screenY)) { 
						if (gameState == GAMEGS.GAMERUNNING) {
							if (turnState == TURNGS.PLAYER1TURN)
								turnState = TURNGS.PLAYER2UPKEEP;
							else if (turnState == TURNGS.PLAYER2TURN)
								turnState = TURNGS.AIUPKEEPANDTURN;
						}
					} 
					//checks if attack button was pressed
					else if (attackButton.contains(screenX, screenY) && currTroop != null) { 
						if(currTroop != null) {
							if (!currTroop.attacked){
								currTroop.state = Troop.ACTION.ATTACK;
								for (int i = 0; i < landscape.getWidth(); i++) {
									for(int j = 0; j < landscape.getHeight(); j++) {
										drawTiles[i][j] = false;
									}
								}
								drawCheck = false;
							}
						}
					}
					//check if move button is pressed
					else if (moveButton.contains(screenX, screenY) && currTroop != null) { 
						if(currTroop != null) {
							if (!currTroop.moved){
								currTroop.state = Troop.ACTION.MOVE;
								for (int i = 0; i < landscape.getWidth(); i++) {
									for(int j = 0; j < landscape.getHeight(); j++) {
										drawTiles[i][j] = false;
									}
								}
								drawCheck = false;
							}
						}
					}
					//clicking from one troop to another
					else if (currTroop != null && currTroop.state == Troop.ACTION.ATTACK && troopOn[screenX/32+panOffsetX/32][screenY/32+panOffsetY/32]) {
							//red attacks blue
							if (turnState == TURNGS.PLAYER1TURN) {
								for (Troop t2 : BlueTroops) {
									if(t2.bounds.contains(screenX+panOffsetX, screenY+panOffsetY) && drawTiles[screenX/32+panOffsetX/32][screenY/32+panOffsetY/32]){
										//Gdx.app.log("?", "attackin");
										int temp = currTroop.giveDamage(t2.defense);
										displayDamageValue = String.format("%d", temp);
										displayDamageValuePos.x = t2.getPos().x + 10;
										displayDamageValuePos.y = t2.getPos().y + 22;
										displayDamageValuePosTarget.x = displayDamageValuePos.x + 16;
										displayDamageValuePosTarget.y = displayDamageValuePos.y + 16;
										displayDamage = true;
										t2.updateHealth(temp);
										//t2.updateHealth(currTroop.giveDamage(t2.defense));
										if (t2.dead) {
											troopOn[(int)t2.getPos().x/32][(int)t2.getPos().y/32] = false;
											BlueTroops.removeIndex(BlueTroops.indexOf(t2, false));
										}
										currTroop.attacked = true;
										if (currTroop != null)
											currTroop.state = Troop.ACTION.IDLE;
										for (int i = 0; i < landscape.getWidth(); i++) {
											for(int j = 0; j < landscape.getHeight(); j++) {
												drawTiles[i][j] = false;
											}
										}
									}
								}
								
							} 
							for (EnemyTroop e : EnemyTroops) {
									if(e.bounds.contains(screenX+panOffsetX, screenY+panOffsetY) && drawTiles[screenX/32+panOffsetX/32][screenY/32+panOffsetY/32]){
										//Gdx.app.log("?", "attackin");
										int temp = currTroop.giveDamage(e.defense);
										displayDamageValue = String.format("%d", temp);
										displayDamageValuePos.x = e.getPos().x + 10;
										displayDamageValuePos.y = e.getPos().y + 22;
										displayDamageValuePosTarget.x = displayDamageValuePos.x + 16;
										displayDamageValuePosTarget.y = displayDamageValuePos.y + 16;
										displayDamage = true;
										e.updateHealth(temp);
										//e.updateHealth(currTroop.giveDamage(e.defense));
										if (e.dead) {
											troopOn[(int)e.getPos().x/32][(int)e.getPos().y/32] = false;
											EnemyTroops.removeIndex(EnemyTroops.indexOf(e, false));
										}
										currTroop.attacked = true;
										if (currTroop != null)
											currTroop.state = Troop.ACTION.IDLE;
										for (int i = 0; i < landscape.getWidth(); i++) {
											for(int j = 0; j < landscape.getHeight(); j++) {
												drawTiles[i][j] = false;
											}
										}
									}
								}
							//blue attacks red
							if (turnState == TURNGS.PLAYER2TURN) {
								for (Troop t : RedTroops) {
									if(t.bounds.contains(screenX+panOffsetX, screenY+panOffsetY) && drawTiles[screenX/32+panOffsetX/32][screenY/32+panOffsetY/32]){//DOTHISTO EVERYTHING
										int temp = currTroop.giveDamage(t.defense);
										displayDamageValue = String.format("%d", temp);
										displayDamageValuePos.x = t.getPos().x + 10;
										displayDamageValuePos.y = t.getPos().y + 22;
										displayDamageValuePosTarget.x = displayDamageValuePos.x + 16;
										displayDamageValuePosTarget.y = displayDamageValuePos.y + 16;
										displayDamage = true;
										t.updateHealth(temp);
										//t.updateHealth(currTroop.giveDamage(t.defense));
										if (t.dead) {
											troopOn[(int)t.getPos().x/32][(int)t.getPos().y/32] = false;
											RedTroops.removeIndex(RedTroops.indexOf(t, false));
										}
										currTroop.attacked = true;
										if (currTroop != null)
											currTroop.state = Troop.ACTION.IDLE;
										for (int i = 0; i < landscape.getWidth(); i++) {
											for(int j = 0; j < landscape.getHeight(); j++) {
												drawTiles[i][j] = false;
											}
										}
									}
								}
							}
					}
					//moving a troop
					else if (currTroop != null && currTroop.state == Troop.ACTION.MOVE) {
							//cant move freely in the space. this results in infinite movement if otherwise, and not sure how to fix...
							if (drawTiles[screenX/32+panOffsetX/32][screenY/32+panOffsetY/32] && !(currTroop.moved)) {
								currTroop.updatePos(screenX/32+panOffsetX/32, screenY/32+panOffsetY/32, troopOn, troopTeam, drawTiles);
								currTroop.moved = true;
								if (currTroop != null)
									currTroop.state = Troop.ACTION.IDLE;
								for (int i = 0; i < landscape.getWidth(); i++) {
									for(int j = 0; j < landscape.getHeight(); j++) {
										drawTiles[i][j] = false;
									}
								}
				
							} 
							else {
								if (currTroop != null)
									currTroop.state = Troop.ACTION.IDLE;
								currTroop = null;
								currTile = landscape.getCell(screenX/32+panOffsetX/32, screenY/32+panOffsetY/32);		
							}
					}
					
					else if (troopOn[screenX/32+panOffsetX/32][screenY/32+panOffsetY/32]){
						if (turnState == TURNGS.PLAYER1TURN) {
							
								for (Troop t : RedTroops) {
									if(t.bounds.contains(screenX+panOffsetX, screenY+panOffsetY)){
										//Gdx.app.log("?", "Touched");
										if (currTroop != null)
											currTroop.state = Troop.ACTION.IDLE;
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
									if(t2.bounds.contains(screenX+panOffsetX, screenY+panOffsetY)){
										//Gdx.app.log("?", "Touched");
										if (currTroop != null)
											currTroop.state = Troop.ACTION.IDLE;
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
					else {
						if (currTroop != null)
							currTroop.state = Troop.ACTION.IDLE;
						currTroop = null;
						currTile = landscape.getCell(screenX/32+panOffsetX/32, screenY/32+panOffsetY/32);
					}
					
				}
			break;
			case START: 
				if (startButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.GAMERUNNING) {
						//Gdx.app.log("?", "game START");
						gameState = GAMEGS.GAMERUNNING;
					}
				}
				if (infoButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.INFO) {
					//	Gdx.app.log("?", "game INFO");
						gameState = GAMEGS.INFO;
					}
				}
			break;
			case INFO:
				if (infoBackButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.START) {
						//Gdx.app.log("?", "game STARTMENU");
						gameState = GAMEGS.START;
					}
				}
			break;
			case PAUSE:
				if (resumeButton.contains(screenX, screenY)) { 
					if(gameState != GAMEGS.GAMERUNNING) {
						//Gdx.app.log("?", "game resume");
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
