package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.objects.Troop;

import com.mygdx.game.ai.GraphPathImp;
import com.mygdx.game.ai.Node;
import com.mygdx.game.ai.GraphImp;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.mygdx.game.ai.HeuristicImp;

public class EnemyTroop extends Troop{
	private IndexedAStarPathFinder<Node> pathfinder;
	private GraphPathImp resultPath = new GraphPathImp();
	
	public EnemyTroop (String type, String t, int posx, int posy, 
						boolean[][] troopOn, boolean[][] troopTeam) {
		super(type, t, posx, posy, troopOn, troopTeam);
		
	}
	
	public void findTarget(GraphImp graph, Troop target, SpriteBatch batch, Texture highlightTile) {
		pathfinder = new IndexedAStarPathFinder<Node>(graph, false);
		GraphPathImp tempPath = new GraphPathImp();
		int startX = (int)position.x;
		int startY = (int)position.y;
		
		int endX = (int)target.getPos().x;
		int endY = (int)target.getPos().y;
		
		//Gdx.app.log("start: ", "X: " + startX + "  Y: " + startY);
		//Gdx.app.log("start: ", "X: " + startX + "  Y: " + startY);
		
		Node startNode = graph.getNodeByXY(startX, startY);
		Node endNode = graph.getNodeByXY(endX, endY);
		
		pathfinder.searchNodePath(startNode, endNode, new HeuristicImp(), tempPath);
		resultPath = tempPath;
		tempPath = new GraphPathImp();
		//Gdx.app.log("Path: ", Integer.toString(resultPath.getCount()));
		for (Node n : resultPath) {
			//Gdx.app.log("nodePath: ", "X: " + n.x + "  Y: " + n.y);
			batch.draw(highlightTile, (n.x)*32, (n.y)*32, 32, 32);
		}
	}
}
