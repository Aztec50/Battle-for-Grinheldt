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

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.utils.Array;

import com.mygdx.game.objects.Troop;



public class DandDWars extends ApplicationAdapter implements InputProcessor {
	
	OrthographicCamera camera;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	
	SpriteBatch batch;
	String currentMap;
	
	Troop troop;
	
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
	
		currentMap = "maps/GrassMap.tmx";
	
        tiledMap = new TmxMapLoader().load(currentMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		camera = new OrthographicCamera();
        camera.setToOrtho(false,screenw,screenh);
        camera.update();
		
		troop = new Troop("knight", 1, 0, 0);
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
		
		//More code goes here
		
		tiledMapRenderer.render();
		
		batch.begin();
		troop.render(batch);
		batch.end();
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
				troop.updatePos(0, 1);
			break;
			case Input.Keys.S:
				troop.updatePos(0, -1);
			break;
			case Input.Keys.A:
				troop.updatePos(-1, 0);
			break;
			case Input.Keys.D:
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
