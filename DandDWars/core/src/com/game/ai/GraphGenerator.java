package com.mygdx.game.ai;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.ai.Node;

public class GraphGenerator {
	public GraphImp generateGraph(TiledMapTileLayer landscape) {
		Array<Node> nodes = new Array<Node>();
		int mapHeight = landscape.getHeight();
		int mapWidth = landscape.getWidth();
		
		for (int j = 0; j < mapHeight; j++) {
			for (int i = 0; i < mapWidth; i++) {
				Node node = new Node();
				nodes.add(node);
			}
		}
		TiledMapTileLayer.Cell up;
		TiledMapTileLayer.Cell down;
		TiledMapTileLayer.Cell left;
		TiledMapTileLayer.Cell right;
		
		for (int j = 0; j < mapHeight; j++) {
			for (int i = 0; i < mapWidth; i++) {
				TiledMapTileLayer.Cell target = landscape.getCell(i, j);
				if(j != mapHeight-1)
					up = landscape.getCell(i, j+1);
				else 
					up = null;
				if(j != 0)
					down = landscape.getCell(i, j-1);
				else 
					down = null;
				if(i != 0)
					left = landscape.getCell(i-1, j);
				else 
					left = null;
				if(i != mapWidth-1)
					right = landscape.getCell(i+1, j);
				else 
					right = null;
				
				Node targetNode = nodes.get(mapWidth*j + i);
				if (target != null) {
					if(j != mapHeight-1 && up != null && up.getTile().getProperties().get("moveCost", Integer.class) != -1) {
						Node upNode = nodes.get(mapWidth * (j+1) + i);
						targetNode.createConnection(upNode, (float)up.getTile().getProperties().get("moveCost", Integer.class));
					}
					if(j != 0 && down != null && down.getTile().getProperties().get("moveCost", Integer.class) != -1) {
						Node downNode = nodes.get(mapWidth * (j-1) + i);
						targetNode.createConnection(downNode, (float)down.getTile().getProperties().get("moveCost", Integer.class));
					}
					if(i != 0 && left != null && left.getTile().getProperties().get("moveCost", Integer.class) != -1) {
						Node leftNode = nodes.get(mapWidth * j + (i-1));
						targetNode.createConnection(leftNode, (float)left.getTile().getProperties().get("moveCost", Integer.class));
					}
					if(i != mapWidth-1 && right != null && right.getTile().getProperties().get("moveCost", Integer.class) != -1) {
						Node rightNode = nodes.get(mapWidth * j + (i+1));
						targetNode.createConnection(rightNode, (float)right.getTile().getProperties().get("moveCost", Integer.class));
					}
				}
			}
		}
		
		return new GraphImp(nodes, mapWidth);
	}
}