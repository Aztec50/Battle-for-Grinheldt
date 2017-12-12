package com.mygdx.game.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.mygdx.game.ai.Node;

public class HeuristicImp implements Heuristic<Node> {
	@Override
	public float estimate(Node startNode, Node endNode) {
		int startIndex = startNode.getIndex();
		int endIndex = endNode.getIndex();
		
		int startY = startIndex / 32;
		int startX = startIndex % 32;
		
		int endY = endIndex / 32;
		int endX = endIndex % 32;
		
		float distance = Math.abs(startX-endX) + Math.abs(startY - endY);
		
		return distance;
	}
}