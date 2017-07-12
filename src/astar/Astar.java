package astar;

import astar.util.Node;
import java.io.*;
import java.util.*;

/**
 * Main class to implement A* path finding.
 * @author Ron
 */
public class Astar {
	public final static int OBJ_STRAIGHT = 0;
	public final static int OBJ_STANDARD = 1;
	public final static int OBJ_STEALTH = 2;
	
	public final static boolean PRIORITY_STRAIGHT = false;
	
    /** Destination symbol */
    public final static char SYM_DEST = 'D';

    /** Source symbol */
    public final static char SYM_SRC = 'S';

    /** Obstacle symbol */
    public final static char SYM_OBSTACLE = '#';
    
    public final static char SYM_BUG = '?';

    /** Free (open) symbol */
    public final static char SYM_FREE = '.';
    
    public final static int NO_LIMIT = 10000;

    private BufferedReader reader;
    private int width;
    private int height;
    private char[][] tileMap;
    private int destX;
    private int destY;
    private int srcX;
    private int srcY;
    private LinkedList openList = new LinkedList();
    private LinkedList closedList = new LinkedList();
    private boolean stealthy = true;

    // Offsets relative to current position in map
    private int[][] xyOffsets = {
        {-1,0},      // W
        {-1,-1},     // NW
        {0,-1},      // N
        {1,-1},      // NE
        {1,0},       // E
        {1,1},       // SE
        {0,1},       // S
        {-1,1} };    // SW

    // Next offset index
    private int indOffset;

    /**
     * Constructor.
     * @param name File name.
    */
    public Astar(String name) {
        try {
          reader = new BufferedReader(new FileReader(name));
        }
        catch(Exception e) {

        }
    }

    /**
     * Constructor.
     * @param tileMap Tile map.
     */
    public Astar(char[][] tileMap,int srcX,int srcY,int destX, int destY) {
      this.tileMap = tileMap;
      this.srcX = srcX;
      this.srcY = srcY;
      this.destX = destX;
      this.destY = destY;
      this.width = tileMap[0].length;
      this.height = tileMap.length;
    }

    /**
     * Find the path from source to destination.
     * @return
     */
    public Node find() {
    	return find(OBJ_STANDARD,Integer.MAX_VALUE);
    }
    
    public Node find(int objective) {
    	return find(objective,Integer.MAX_VALUE);
    }
    
//    public Node find(int limit) {
//    	return find(OBJ_STANDARD,limit);
//    }
    
    /**
     * Find path find source to destination.
     * @param limit Maximum number of nodes to generate.
     * @return Destination node if path found, null if no path found.
     */
    public Node find(int objective,int limit) {
        Node dest = new Node(destX,destY);

        moveToOpen(new Node(srcX,srcY));

        while(!openList.isEmpty()) {
            Node curNode = getLowestCostNode();

            if(curNode.equals(dest))
                return relink(curNode);

            moveToClosed(curNode);

            // Reset the adjacency state
            reset();

            do {
                // Get next adjacent to current node
                Node adj = getAdjacent(curNode);

                if(adj == null)
                    break;

                double heuristic = calculateHeuristic(adj,dest);
                double steps = adj.getSteps();
                double cost = steps + heuristic;
                
                if(objective == OBJ_STEALTH && curNode.getParent() != null) {
                	int dx = curNode.getX() - adj.getX();
                	int dy = curNode.getY() - adj.getY();
                	
//                	if(dx != 0 && dy == 0 || dx == 0 && dy != 0)
//                		cost -= heuristic * 0.05;
                	
                	if(hugsWall(adj)) {
                		cost -= heuristic * 0.10;
                	}                		
                }
                
                else if(objective == OBJ_STRAIGHT && curNode.getParent() != null) {
                	Node parent = curNode.getParent();
                	int dx1 = parent.getX() - curNode.getX();
                	int dy1 = parent.getY() - curNode.getY();
                	int dx2 = curNode.getX() - adj.getX();
                	int dy2 = curNode.getY() - adj.getY();
                	
                	boolean zags = dx1 != dx2 || dy1 != dy2;
                	
                	if(!zags)
                		adj.setInertia(curNode.getInertia()+1);
                	
                	/* Rabin algorithm
                	if(zags)
                		cost += .001;
                	*/
                	///*
                	double strength = adj.getInertia();
               	
                	if(strength < 8) {
                		boolean hugs = hugsWall(adj);

                		if(hugs && !zags) {
                			cost += 10;
                		}
                		else if(!hugs && zags) {
                			cost += 2;
                		}
                		else if(hugs && zags) {
                			cost += 13;
                		}
                	}//*/
                }
                adj.setCost(cost);
                
                openList.add(adj);
                
                if(Node.idCount > limit)
                	return null;
            } while(true);

        }
        return null;
    }
    
    /**
     * Relink the child nodes properly since the child references
     * are leftover references from scanning the adjacent nodes.
     * @param path
     * @return
     */
    protected Node relink(Node path) {
    	Node anode = path;
    	Node child = null;
    	while(anode != null) {
    		anode.setChild(child);
    		child = anode;
    		anode = anode.getParent();
    	}
    	
    	return path;
    }

    protected boolean hugsWall(Node anode) {
    	int x = anode.getX();
    	int y = anode.getY();
    	
    	return isObstacle(x-1,y) ||
    	       isObstacle(x+1,y) ||
    	       isObstacle(x,y-1) ||
    	       isObstacle(x,y+1);
    }
    
    /**
     * Get next adjacent node relative to parent node.
     * @return Next adjacent node.
     */
    protected Node getAdjacent(Node parent) {
        int x = parent.getX();
        int y = parent.getY();

        while(indOffset < xyOffsets.length) {
            int adjX = x + xyOffsets[indOffset][0];
            int adjY = y + xyOffsets[indOffset][1];

            indOffset++;

            if(adjX < 0 || adjX >= width || adjY < 0 || adjY >= height)
                continue;

            if(onOpenList(adjX,adjY) || onClosedList(adjX,adjY) || isObstacle(adjX,adjY))
                continue;

            return new Node(adjX,adjY,parent);
        }

        return null;
    }

    /**
     * Move node to open list.
     * @param node Node to put on open list.
     */
    protected void moveToOpen(Node node) {
        openList.add(node);
    }

    /** Determines if node on open list.
     * @param node Node to test.
     * @return True if node is on open list.
     */
    protected boolean onOpenList(Node node) {
      return onOpenList(node.getX(),node.getY());
    }

    /** Determines if node at x, y on open list.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if node at coordinate on open list.
     */
    protected boolean onOpenList(int x,int y) {
        ListIterator iter = openList.listIterator();

        while(iter.hasNext()) {
            Node candidate = (Node)iter.next();
            if(candidate.getX() == x && candidate.getY() == y) {
              return true;
            }
        }

        return false;
    }

    /**
     * Determines if node on closed list.
     * @param node Node to test.
     * @return True if node on closed list.
     */
    protected boolean onClosedList(Node node) {
        return onClosedList(node.getX(),node.getY());
    }

    /**
     * Determines if node at x, y on closed list.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if node at coordinate on closed list.
     */
    protected boolean onClosedList(int x,int y) {
        ListIterator iter = closedList.listIterator();

        while(iter.hasNext()) {
            Node candidate = (Node)iter.next();
            if(candidate.getX() == x && candidate.getY() == y) {
              return true;
            }
        }

        return false;
    }

    /**
     * Determines if node is an obstacle.
     * @param node Node to test.
     * @return True if node an obstacle.
     */
    protected boolean isObstacle(Node node) {
      return isObstacle(node.getX(),node.getY());
    }

    /**
     * Determines if node at x, y is an obstacle.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if node an obstacle.
     */
    protected boolean isObstacle(int x,int y) {
        char sym = tileMap[y][x];

        return sym == SYM_OBSTACLE;
    }

    /**
     * Move node to closed list.
     * @param node Node to move to closed list.
     */
    protected void moveToClosed(Node node) {
        ListIterator iter = openList.listIterator();

        while(iter.hasNext()) {
            Node candidate = (Node)iter.next();
            if(candidate == node) {
                iter.remove();
                closedList.add(node);
            }
        }
    }

    /**
     * Calculate heuristic part of cost using Manhattan distance.
     * @param adj Adjacent node.
     * @param dest Destination node.
     * @return Distance.
     */
    protected double calculateHeuristic(Node adj,Node dest) {
        double dx = adj.getX() - dest.getX();
        
        double dy = adj.getY() - dest.getY();
        
        double h = goEuclidean(dx,dy);
//        double h = goManhattan(dx,dy);
        //double h = goSSE(dx,dy);
        //double h = goCheckers(dx,dy);
        
        return h;
    }
    
    private double goCheckers(double dx,double dy) {
    	return Math.max(Math.abs(dx), Math.abs(dy));
    }
    
    private double goSSE(double dx,double dy) {
    	return dx * dx + dy * dy;
    }
    
    private double goManhattan(double dx,double dy) {
    	return Math.abs(dx) + Math.abs(dy);
    }
    
    private double goEuclidean(double dx, double dy) {
    	return Math.sqrt(goSSE(dx,dy));
    }

    /** Find lowest cost node.
     *  Could be improved if nodes added using insertion sort.
     *  @return Node with lowest cost.
     */
    protected Node getLowestCostNode() {
        ListIterator iter = openList.listIterator();
        double minCost = Double.MAX_VALUE;
        Node minNode = null;
        while(iter.hasNext()) {
            Node node = (Node)iter.next();
            double cost = node.getCost();
            if(cost < minCost) {
                minCost = cost;
                minNode = node;
            }
        }
        return minNode;
    }

    /**
     * Reset the offset index.
     */
    protected void reset() {
        indOffset = 0;
    }

    public char[][] getTileMap() {
    	return tileMap;
    }
    
    /**
     * Load the tile map from a file.
     * Must invoke constructor with file parameter before invoking this method.
     */
    public void loadMap() {
        try {
        	StringTokenizer dims = new StringTokenizer(reader.readLine());
        	
            width = Integer.parseInt(dims.nextToken());

            height = Integer.parseInt(dims.nextToken());

            tileMap = new char[height][width];

            for(int k=0; k < height; k++) {
                String srow = reader.readLine();

                if(srow.length() != width)
                    throw new Exception("bad row width");

                for(int j=0; j < width; j++){
                  char sym = srow.charAt(j);
                  tileMap[k][j] = sym;

                  if(sym == SYM_DEST) {
                      destX = j;
                      destY = k;
                  }

                  if(sym == SYM_SRC) {
                      srcX = j;
                      srcY = k;
                  }
                }
            }
        }
        catch(Exception e) {
          e.printStackTrace();
        }
    }

    /**
     * Main method (for debugging).
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      long totalt = 0;
      
      for(int j=0; j < 1000; j++) {
        
        LevelGenerator lg = new LevelGenerator(25,25,101L+j);
        
        lg.layoutSrcDest();
        
        //lg.print();

        Node.idCount = 0;
        Astar astar0 =
            new Astar(lg.getMap(),lg.getSrcX(),lg.getSrcY(),lg.getDestX(),lg.getDestY());

        Node node0 = astar0.find(NO_LIMIT);
        
        // Get line of sight steps
        int los = node0.getSteps();
        
        //Astar Astar = new Astar(args[0]);
        //Astar.loadMap();
        
        Node.idCount = 0;
        Astar astar1 =
          new Astar(lg.getMap(),lg.getSrcX(),lg.getSrcY(),lg.getDestX(),lg.getDestY());
        
        lg.layoutBarriers();

        double dx = Math.abs(lg.getSrcX()-lg.getDestX());
        double dy = Math.abs(lg.getSrcY()-lg.getDestY());

        double md = dx + dy;       // Manhattan estimate
        //double md = Math.sqrt(dx*dx + dy*dy);  // Euclidean estimate
        //double md = Math.max(dx,dy);  // Checkers estimate
        //double md = dx*dx + dy*dy;  // SSE estimate
        
        int limit = (int) ((md-1) * 5 + 8 + 0.5);
        
        long t0 = System.currentTimeMillis();        
        Node node = astar1.find(limit);
        long t1 = System.currentTimeMillis();
        
        totalt += (t1 - t0);

        if(node != null) {
          //node.print();          
          ;//System.out.println(Node.idCount+" "+((md-1)*5+8)+" "+node.getSteps()+" "+los);
          //lg.print();
        }
        else
          ;//System.out.println("NO PATH "+Node.idCount+" "+limit);
      }
      
      System.out.println("runtime = "+totalt);
    }
}
