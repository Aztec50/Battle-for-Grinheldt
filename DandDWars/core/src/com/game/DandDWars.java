package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class DandDWars extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	
	OrthographicCamera camera;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	
	String currentMap;
	
	float screenw;
	float screenh;
	
	
	
	@Override
	public void create () {
		screenw = 640f; //screen resolution
        screenh = 640f;  //screen resolution
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		currentMap = "maps/GrassMap.tmx";
	
        tiledMap = new TmxMapLoader().load(currentMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		camera = new OrthographicCamera();
        camera.setToOrtho(false,screenw,screenh);
        camera.update();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.begin();
		batch.end();
		
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
	
	@Override
	public void resize(int width, int height){
		camera.viewportWidth = width;
		camera.viewportHeight = height;
	}

	
}
