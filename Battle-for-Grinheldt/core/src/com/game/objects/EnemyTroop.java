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
import com.badlogic.gdx.math.MathUtils;

public class EnemyTroop extends Troop{
	private IndexedAStarPathFinder<Node> pathfinder;
	private GraphPathImp resultPath = new GraphPathImp();
	public Troop target;
	boolean[][] attackTiles;
	int w;
	int h;
	
	public EnemyTroop (String type, String t, int posx, int posy, 
						boolean[][] troopOn, boolean[][] troopTeam,int width, int height) {
		super(type, t, posx, posy, troopOn, troopTeam);
		attackTiles = new boolean[width][height];
		w = width;
		h = height;
	}
	
	public void findTarget(GraphImp graph, Array<Troop> potentialTargets, boolean[][] drawTiles) { //, SpriteBatch batch, Texture highlightTile) { DEBUG
		pathfinder = new IndexedAStarPathFinder<Node>(graph, false);
		GraphPathImp tempPath = new GraphPathImp();
		int startX = (int)position.x;
		int startY = (int)position.y;
		int endX;
		int endY;
		int topTargetVal = -1000;
		int temp;
		
		for (Troop t : potentialTargets) {
			endX = (int)t.getPos().x;
			endY = (int)t.getPos().y;
		
			//Gdx.app.log("start: ", "X: " + startX + "  Y: " + startY);
			//Gdx.app.log("start: ", "X: " + startX + "  Y: " + startY);
		
			Node startNode = graph.getNodeByXY(startX, startY);
			Node endNode = graph.getNodeByXY(endX, endY);
		
			pathfinder.searchNodePath(startNode, endNode, new HeuristicImp(), tempPath);
			drawAttackTiles((int)position.x/32, (int)position.y/32, attackRangeMin, attackRangeMax, attackRangeMin);
			temp = targetValue(t, tempPath);
			for (int i = 0; i < w; i++) {
				for(int j = 0; j < h; j++) {
					attackTiles[i][j] = false;
				}
			}
			if (temp > topTargetVal) {
				resultPath = tempPath;
				target = t;
				topTargetVal = temp;
			}
			
			else if (temp == topTargetVal){
				if (MathUtils.random(1,100) < 10){
					//Gdx.app.log("newPath: ", "one");
					resultPath = tempPath;
					target = t;
				}
			}
			tempPath = null;
			tempPath = new GraphPathImp();
		}
		//Gdx.app.log("newPath: ", Integer.toString(resultPath.getCount()));
		//for (Node n : resultPath) {
		//	Gdx.app.log("nodePath: ", "X: " + n.x + "  Y: " + n.y);
		//	//batch.draw(highlightTile, (n.x)*32, (n.y)*32, 32, 32);
		//}
	}
	
	public void moveToTarget(boolean[][] troopOn, boolean[][] troopTeam, boolean[][] drawTiles){
		boolean newMove = true;
		//Gdx.app.log("moving: ", "" + speed);
		for (Node n : resultPath) {
			if (!newMove ) {
				drawAttackTiles((int)position.x/32, (int)position.y/32, attackRangeMin, attackRangeMax, attackRangeMin);
				if(!moved && !(attackTiles[(int)target.getPos().x/32][(int)target.getPos().y/32])){// && !troopOn[n.x][n.y]){
					super.updatePos(n.x, n.y, troopOn, troopTeam, drawTiles);
					speed -= n.moveCost;
					if (speed <= 0){
						moved = true;
					}
				}
				for (int i = 0; i < w; i++) {
					for(int j = 0; j < h; j++) {
						attackTiles[i][j] = false;
					}
				}
			} 
			else {
				newMove = false;
			}
		}
	}
	
	public void drawAttackTiles(int troopX, int troopY, int atkMin, int atkMax, int draw){
		String report = String.format("draw: %d  X: %d  Y: %d", draw, troopX, troopY);
		//Gdx.app.log("Info: ", report);
		
		if(draw == atkMax) return;
		//drawTiles[troopX][troopY] = true;
		if(draw >= atkMin) {
			
			for (int i = 0; i < draw; i++) {
				if(troopX-(draw-i) > -1 && troopY+i < h){
					attackTiles[troopX-(draw-i)][troopY+i] = true;
				}				
				if(troopY-(draw-i) > -1 && troopX-i > -1){
					attackTiles[troopX-i][troopY-(draw-i)] = true;
				}
				if(troopY+(draw-i) < h && troopX+i < w){
					attackTiles[troopX+i][troopY+(draw-i)] = true;
				}
				if(troopX+(draw-i) < w && troopY-i > -1){
					attackTiles[troopX+(draw-i)][troopY-i] = true;
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
	
	public void attackTarget(Array<Troop> targets, boolean[][] troopOn, boolean[][] drawTiles){
		//Gdx.app.log("nodePathMove: ", "X: " + (int)target.getPos().x + "  Y: " + (int)target.getPos().y);
		if (target != null && drawTiles[(int)target.getPos().x/32][(int)target.getPos().y/32]) {
			
			target.updateHealth(super.giveDamage(target.defense));	
			if (target.dead) {
				troopOn[(int)target.getPos().x/32][(int)target.getPos().y/32] = false;
				if (targets.random() != null)
					targets.removeIndex(targets.indexOf(target, false));
			}
			attacked = true;
		}
	}
	
	int targetValue(Troop potentialTarget, GraphPathImp targetPath) {
		int val = 0;
		if (potentialTarget.health <= damage){
			val += 1;
		}
		//else {
		//	val -= 5;
		//}
		if (potentialTarget.dead){
			val-=900;
		}
		if (attackTiles[(int)potentialTarget.getPos().x/32][(int)potentialTarget.getPos().y/32]){
			val += 20;
		}
		else{
			//Gdx.app.log("in Range", "no");
			val -= 30;
		}
		if (targetPath.getCount()-2 < speed){
			val += 5;
		}
		else {
			val -= 5;
		}
		if (troopType != TROOP_TYPE.MYSTIC && potentialTarget.troopType == TROOP_TYPE.MYSTIC){
			val += 1;
		}
		return val;
	}
}


















