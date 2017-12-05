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
import com.badlogic.gdx.utils.Array;

import com.mygdx.game.ai.GraphPathImp;
import com.mygdx.game.ai.Node;
import com.mygdx.game.ai.GraphImp;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.mygdx.game.ai.HeuristicImp;

public class EnemyTroop extends Troop{
	private IndexedAStarPathFinder<Node> pathfinder;
	private GraphPathImp resultPath = new GraphPathImp();
	private Troop target;
	
	public EnemyTroop (String type, String t, int posx, int posy, 
						boolean[][] troopOn, boolean[][] troopTeam) {
		super(type, t, posx, posy, troopOn, troopTeam);
		
	}
	
	public void findTarget(GraphImp graph, Array<Troop> potentialTargets) { //, SpriteBatch batch, Texture highlightTile) { DEBUG
		pathfinder = new IndexedAStarPathFinder<Node>(graph, false);
		GraphPathImp tempPath = new GraphPathImp();
		int startX = (int)position.x;
		int startY = (int)position.y;
		int endX;
		int endY;
		boolean newPath = true;
		for (Troop t : potentialTargets) {
			endX = (int)t.getPos().x;
			endY = (int)t.getPos().y;
		
			//Gdx.app.log("start: ", "X: " + startX + "  Y: " + startY);
			//Gdx.app.log("start: ", "X: " + startX + "  Y: " + startY);
		
			Node startNode = graph.getNodeByXY(startX, startY);
			Node endNode = graph.getNodeByXY(endX, endY);
		
			pathfinder.searchNodePath(startNode, endNode, new HeuristicImp(), tempPath);
			if (resultPath.getCount() > tempPath.getCount() || newPath) {
				resultPath = tempPath;
				target = t;
				newPath = false;
			}
			tempPath = new GraphPathImp();
		}
		Gdx.app.log("newPath: ", Integer.toString(resultPath.getCount()));
		for (Node n : resultPath) {
			Gdx.app.log("nodePath: ", "X: " + n.x + "  Y: " + n.y);
			//batch.draw(highlightTile, (n.x)*32, (n.y)*32, 32, 32);
		}
	}
	
	public void moveToTarget(boolean[][] troopOn, boolean[][] troopTeam, boolean[][] drawTiles){
		boolean newMove = true;
		Gdx.app.log("moving: ", "" + speed);
		for (Node n : resultPath) {
			if (!newMove) {
				if(!moved){// && !troopOn[n.x][n.y]){
					super.updatePos(n.x, n.y, troopOn, troopTeam, drawTiles);
					
					speed -= n.moveCost;
					if (speed <= 0){
						moved = true;
					}
				}
			} 
			else {
				newMove = false;
			}
		}
	}
	
	public void attackTarget(Array<Troop> targets, boolean[][] troopOn, boolean[][] drawTiles){
		//Gdx.app.log("nodePathMove: ", "X: " + (int)target.getPos().x + "  Y: " + (int)target.getPos().y);
		if (drawTiles[(int)target.getPos().x/32][(int)target.getPos().y/32]) {
			
			target.updateHealth(super.giveDamage(target.defense));	
			if (target.dead) {
				troopOn[(int)target.getPos().x/32][(int)target.getPos().y/32] = false;
				targets.removeIndex(targets.indexOf(target, false));
			}
			attacked = true;
		}
		
	}
}
