package com.mygdx.game.ai;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.pfa.Connection;
import com.mygdx.game.ai.ConnectionImp;

public class Node {
	private Array<Connection<Node>> connections = new Array<Connection<Node>>();
	public int type;
	public int x;
	public int y;
	public int index;
	public int moveCost;
	
	public Node(int x, int y, int mv) {
		index = Node.Indexer.getIndex();
		this.x = x;
		this.y = y;
		moveCost = mv;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Array<Connection<Node>> getConnections() {
		return connections;
	}
	
	public void createConnection( Node toNode, float cost) {
		connections.add(new ConnectionImp(this, toNode, cost));
	}
	
	public static class Indexer {
		public static int index = 0;
		
		public static int getIndex() {
			return index++;
		}
	}
}