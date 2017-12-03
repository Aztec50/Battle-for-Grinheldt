package com.mygdx.game.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.ai.Node;
import com.game.DandDWars;

public class GraphImp{
	private Array<Node> nodes = new Array<Node>();
	int mapWidth;
	
	public GraphImp(Array<Node> nodes, int width) {
		this.nodes = nodes;
		mapWidth = width;
	}
	
	
	public Array<Connection<Node>> getConnections(Node fromNode) {
		
		return fromNode.getConnections();
	}
	
	public int getIndex (Node node){
		return node.getIndex();
	}
	
	public int getNodeCount() {
		int x = 0;
		for (Node n : nodes){
			x++;
		}
		return x;
	}
	
	public Node getNodeByXY(int x, int y) {
		int modX = x / 32;
		int modY = y / 32;
		
		return nodes.get(mapWidth*modY + modX);
	}
}