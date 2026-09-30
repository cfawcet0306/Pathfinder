package edu.ncsu.csc411.ps02.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import edu.ncsu.csc411.ps02.environment.Tile;
import edu.ncsu.csc411.ps02.environment.TileStatus;
import edu.ncsu.csc411.ps02.environment.Action;
import edu.ncsu.csc411.ps02.environment.Environment;
import edu.ncsu.csc411.ps02.environment.Position;

/**
	Represents an intelligent agent moving through a particular room.	
	The robot only has two sensors - the ability to retrieve the 
	the status of all its neighboring tiles, including itself, and the
	ability to retrieve to location of the TARGET tile.
	
	Your task is to modify the getAction method below so that it reaches
	TARGET with a minimal number of steps.
*/

public class Robot {
	
	/**
	 * Used to put into a priority queue so I can easily compare them
	 * Can't change a node once it's made
	 */
	private class PathNode implements Comparable<PathNode> {
		/** The position of the node */
		private Position data;
		/** The inherent cost of the node */
		private int cost;
		
		/**
		 * Constructor for a node
		 * 
		 * @param data the position of the node
		 * @param cost the cost of the node
		 */
		public PathNode(Position data, int cost) {
			this.data = data;
			this.cost = cost;
		}
		
		/**
		 * Gives the position of the node
		 * 
		 * @return the position the node holds
		 */
		public Position getData() {
			return data;
		}
		
		/**
		 * Gives the cost of the node
		 * 
		 * @return the cost the node has
		 */
		public int getCost() {
			return cost;
		}

		
		@Override
		public int compareTo(PathNode o) {
			//Its only used here so I don't really need to check types
			return cost - o.getCost();
		}
		
	}
	
	/**
	 * Returns the number of tiles you would have to travel to get to the goal from the current tile
	 * 
	 * @param goal the tile you want to reach
	 * @param current the tile you are at
	 * @return the distance from current to the goal
	 */
	private int getDistanceFromGoal(Position goal, Position current) {
		int colDist = Math.abs(goal.getCol() - current.getCol());
		int rowDist = Math.abs(goal.getRow() - current.getRow());
		return colDist + rowDist;
	}
	
	
	private Environment env;
	
	/** Initializes a Robot on a specific tile in the environment. */
	public Robot (Environment env) { this.env = env; }
	
	/**
    Problem Set 02 - Modify the getAction method below in order to simulate
    the passage of a single time-step. At each time-step, the Robot decides
    which tile to move to.
    
    Your task for this Problem Set is to modify the method below such that
    the Robot agent is able to reach the TARGET tile on a given Environment. 
    5 out of the 10 graded test cases, with explanations on how to create new
    Environments, are available under the test package.
    
    This method should return a single Action from the Action class.
    	- Action.DO_NOTHING
    	- Action.MOVE_UP
    	- Action.MOVE_DOWN
    	- Action.MOVE_LEFT
    	- Action.MOVE_RIGHT
	 */

	/**
		My implementation relies on the A* search that was presented in lecture. I heard about it in lecture
		and from there I thought that it would be perfect for this problem. The real cost, or g(n)
		of a pathway is just the previous path + 1, while the heuristic cost, or h(n), is the distance from
		the target position. 
			I made a PositionNode inner class simply for ease of use with a priority queue,
		as then I can just put my own compare method that uses the cost to go to a position. Then, I looked
		at the slide for A* with the Python implementation, and then read up online about how to translate it.
		It is largely the same, with the one exception being that I had to include a statement that prevents
		impassable tiles from being considered at all. After it makes the map, you can backtrack until you get
		right before the start. Once you get that, you just see if it was left, right, up, or down, and move
		appropriately. 
			And if all else fails, I just told it to do nothing, but it should be alright.
	 */
	public Action getAction () {
		// This example code demonstrates the available methods and actions,
		// such as retrieving its senses (neighbor positions), getting the status of
		// those tiles, and returning the different available Actions
		
		Position selfPos = env.getRobotPosition(this);
		// For this problem set, the Environment class allows for complete
		// observability. You are able to find all neighbor positions for 
		// any given Position object by passing a Position object to
		// getNeighborPositions
		Map<String, Position> neighbors = env.getNeighborPositions(selfPos);
		Position abovePos = neighbors.get("above"); // Either a Tile or null
		Position belowPos = neighbors.get("below"); // Either a Tile or null
		Position leftPos = neighbors.get("left");   // Either a Tile or null
		Position rightPos = neighbors.get("right"); // Either a Tile or null
		
		// You are able to get their tile using the getTiles method
		Map<Position, Tile> positions = env.getTiles();
		//Ended up not needing this
		//Tile selfTile = positions.get(selfPos);
		
		// The Environment now has a getTarget() method which will return
		// the Position of the Target node.
		Position targetPos = env.getTarget();
		
		if (selfPos.equals(targetPos)) {
			
			return Action.DO_NOTHING;
		} 
		
		//My code starts here
		
		//Setup stuff for A*
		PriorityQueue<PathNode> frontier = new PriorityQueue<PathNode>();
		PathNode start = new PathNode(selfPos, 0);
		frontier.add(start);
		HashMap<Position, Position> cameFrom = new HashMap<Position, Position>();
		HashMap<Position, Integer> costSoFar = new HashMap<Position, Integer>();
		cameFrom.put(selfPos, null);
		costSoFar.put(selfPos, 0);
		

		
		
		
		//Space left in queue, check whats next
		while(!frontier.isEmpty()) {
			Position current = frontier.poll().getData();
			//You made it lil bro
			if(current.equals(targetPos)) {
				break;
			}
			
			//Map of neighbors for the for each loop
			Set<Map.Entry<String, Position>> currentNeighbors = env.getNeighborPositions(current).entrySet();
			
            for(Map.Entry<String, Position> currentPathway: currentNeighbors) {
            	Position currentPos = currentPathway.getValue();
            	//As long as its not impassable youre good
            	if(positions.get(currentPos).getStatus() != TileStatus.IMPASSABLE) {
            		int newCost = costSoFar.get(current) + 1;
                	
                	if(costSoFar.get(currentPos) == null || newCost < costSoFar.get(currentPos)) {
                		costSoFar.put(currentPos, newCost);
                		int priority = newCost + getDistanceFromGoal(targetPos, currentPos);
                		PathNode currentNode = new PathNode(currentPos, priority);
                		frontier.add(currentNode);
                		cameFrom.put(currentPos, current);
                		
                		
                	}
            	}
            	
            }
			
			
		}
		
		
		
		Position currentEndpoint = targetPos;
		
		//Find the spot it should move to by backtracking
		while(!cameFrom.get(currentEndpoint).equals(selfPos)) {
			currentEndpoint = cameFrom.get(currentEndpoint);
			//This bit is part of my +X implementation
			//positions.get(currentEndpoint).setInPath(true);
		}
		
		
		if(rightPos != null && currentEndpoint.equals(rightPos)) {
			
			return Action.MOVE_RIGHT;
		}
		if(leftPos != null && currentEndpoint.equals(leftPos)) {
			
			return Action.MOVE_LEFT;
		}
		if(abovePos != null && currentEndpoint.equals(abovePos)) {
			
			return Action.MOVE_UP;
		}
		if(belowPos != null && currentEndpoint.equals(belowPos)) {
			
			return Action.MOVE_DOWN;
		}
		
		
		
		
		
		// The Position class has also been updated to include an equals method
		
		return Action.DO_NOTHING;
		
		/**
		else {
			// You are STRONGLY encouraged to design a search tree.
			// NOTE: There are Java.util implementations for many of the data
			// structures taught in 316. You should create Nodes and utilize
			// these data structures.
			return Action.DO_NOTHING;
		}
		*/
	}
	
	
}