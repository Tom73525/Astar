package astar;

import astar.aes.World;
import astar.geometry.Euclidean;
import astar.geometry.IGeometry;
import astar.pcg.AbstractLevelGenerator;
import astar.pcg.BasicGenerator;
import astar.pcg.WellsGenerator;
import astar.util.Node;
import astar.util.Objective;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class to implement A* path finding.
 * @author Ron
 */
public class Astar {          
    public final double SQRT_2 = Math.sqrt(2);
    public final static char SYM_DEST = 'D';
    public final static char SYM_SRC = 'S';
    public final static char SYM_OBSTACLE = '#';
    public final static char SYM_BUG = '?';
    public final static char SYM_FREE = '.';    

    public final static boolean PRIORITY_STRAIGHT = false;
    public final static int NO_LIMIT = 10000;
    
    public IGeometry geometry = new Euclidean(); 
    public AbstractLevelGenerator levelGenerator = new WellsGenerator();
    
    protected BufferedReader reader;
    protected int width;
    protected int height;
    protected char[][] tileMap;
    protected int destX = -1;
    protected int destY = -1;
    protected int srcX = -1;
    protected int srcY = -1;
    protected LinkedList openList = new LinkedList();
    protected LinkedList closedList = new LinkedList();    
    protected Node dest;
    protected Node src;
    protected int level = 5;
    protected int seed = 0;
    protected boolean debug;


    
    // Offsets relative to current position in map
    protected int[][] xyOffsets = {
        {-1, 0},  // W
        {-1, -1}, // NW
        {0, -1},  // N
        {1, -1},  // NE
        {1, 0},   // E
        {1, 1},   // SE
        {0, 1},   // S
        {-1, 1}   // SW
    };

    // Next offset index
    protected int indOffset;
    
    public Astar() {
        initConfig();
        
        initLevel();
    }
    
    /**
     * Constructor.
     * @param name File name.
    */
    public Astar(String name) {
        this();
        
        try {
          reader = new BufferedReader(new FileReader(name));
        }
        catch(FileNotFoundException e) {

        }
    }

    
    /**
     * Constructor.
     * @param tileMap Tile map.
     * @param srcX Source x in world
     * @param srcY Source y in world
     * @param destX Destination x in world
     * @param destY Destination y in world
     */
    public Astar(char[][] tileMap, int srcX, int srcY, int destX, int destY) {
        this();
        
        this.tileMap = tileMap;
        this.srcX = srcX;
        this.srcY = srcY;
        this.destX = destX;
        this.destY = destY;
        this.width = tileMap[0].length;
        this.height = tileMap.length;
    }
    /**
     * Initializes the configuration.
     */
    protected final void initConfig() {
        String value = System.getProperty("astar.debug");
        if(value != null && value.equals("true"))
            debug = true;
            
        value = System.getProperty("astar.seed");
        if (value != null) {
            seed = Integer.parseInt(value);
        }

        value = System.getProperty("astar.level");
        if (value != null) {
            level = Integer.parseInt(value);
        }
        
        String className = System.getProperty("astar.geometry");
        if(className != null) {
            try {
                Class<?> cl = Class.forName(className);
                 geometry = (IGeometry) cl.newInstance();
                 
            } catch (ClassNotFoundException ex) {
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Astar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    protected final void initLevel() {
        String className = System.getProperty("astar.lg");
        
        if (className == null)
            className = "astar.pcg.WellsGenerator";

        try {
            Class<?> clzz = Class.forName(className);

            Constructor<?> cons = clzz.getConstructor(Integer.class);

            this.levelGenerator = (AbstractLevelGenerator) cons.newInstance(seed);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            System.err.println("bad level generator");
            System.exit(1);
        }
        
        this.tileMap = levelGenerator.generateLevel(level);
        this.width = tileMap[0].length;
        this.height = tileMap.length;
        
        for(int row=0; row < tileMap.length; row++) {
            for(int col=0; col < tileMap[0].length; col++) {
                char tile = tileMap[row][col];
                
                switch(tile) {
                    case World.PLAYER_START_TILE:
                        this.srcX = col;
                        this.srcY = row;
                        break;
                        
                    case World.GATEWAY_TILE:
                        this.destX = col;
                        this.destY = row;
                        break;
                }
                
                if(this.srcX < 0 && this.srcY < 0 && this.destX >=0 && this.destY >= 0) {
                    System.err.println("bad tile map");
                    System.exit(1);
                }
            }
        }        
    }
    
    /**
     * Find the path from source to destination.
     * @return
     */
    public Node find() {
    	return find(Objective.STANDARD);
    }
    
    public void begin() {
        dest = src = null;
    }
    
    /**
     * Find path find source to destination.
     * @param objective Shape of walk, standard (or shortest) , straight, or stealthy
     * @return Destination node if path found, null if no path found.
     */
    public Node find(Objective objective) {
        dest = new Node(destX,destY);
        src = new Node(srcX,srcY);

        moveToOpen(src);

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
                
                if(objective == Objective.STEALTHY && curNode.getParent() != null) {
                	int dx = curNode.getCol() - adj.getCol();
                	int dy = curNode.getRow() - adj.getRow();
                	
//                	if(dx != 0 && dy == 0 || dx == 0 && dy != 0)
//                		cost -= heuristic * 0.05;
                	
                	if(hugsWall(adj)) {
                		cost -= heuristic * 0.10;
                	}                		
                }
                
                else if(objective == Objective.PRETTY && curNode.getParent() != null) {
                	Node parent = curNode.getParent();
                        
                	int dx1 = parent.getCol() - curNode.getCol();
                        
                	int dy1 = parent.getRow() - curNode.getRow();
                        
                	int dx2 = curNode.getCol() - adj.getCol();
                        
                	int dy2 = curNode.getRow() - adj.getRow();
                	
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
                	}
                }
                adj.setCost(cost);
                
                openList.add(adj);
                
                if(Node.idCount > Integer.MAX_VALUE)
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

    /**
     * Returns true if this node is adjacent to an obstacle.
     * @param node
     * @return 
     */
    protected boolean hugsWall(Node node) {
    	int col = node.getCol();
    	int row = node.getRow();
    	
    	return isObstacle(col-1, row) ||
    	       isObstacle(col+1, row) ||
    	       isObstacle(col, row-1) ||
    	       isObstacle(col, row+1);
    }
    
    /**
     * Get next adjacent node relative to parent node.
     * @param parent Parent to this node
     * @return Next adjacent node.
     */
    protected Node getAdjacent(Node parent) {
        int x = parent.getCol();
        int y = parent.getRow();

        while(indOffset < xyOffsets.length) {
            int adjX = x + xyOffsets[indOffset][0];
            int adjY = y + xyOffsets[indOffset][1];

            indOffset++;

            if(adjX < 0 || adjX >= width || adjY < 0 || adjY >= height)
                continue;

            if(onOpenList(adjX, adjY) || onClosedList(adjX, adjY) || isObstacle(adjX, adjY))
                continue;

            return new Node(adjX, adjY, parent);
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
      return onOpenList(node.getCol(),node.getRow());
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
            if(candidate.getCol() == x && candidate.getRow() == y) {
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
        return onClosedList(node.getCol(),node.getRow());
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
            if(candidate.getCol() == x && candidate.getRow() == y) {
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
      return isObstacle(node.getCol(),node.getRow());
    }

    /**
     * Determines if node at x, y is an obstacle.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if node an obstacle.
     */
    protected boolean isObstacle(int x, int y) {
        if(y < 0 || x < 0 || y >= height || x >= width)
            return false;
        
        char sym = tileMap[y][x];

        return sym == World.WALL_TILE;
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
     * Calculate heuristic part of cost using default geometry.
     * @param adj Adjacent node.
     * @param dest Destination node.
     * @return Distance.
     */
    protected double calculateHeuristic(Node adj, Node dest) {
        double dist = geometry.distance(adj, dest);
        
        return dist;
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

    public Node getSrc() {
        return src;
    }
    
    public Node getDest() {
        return dest;
    }
    
    public char[][] getTileMap() {
    	return tileMap;
    }
    
    public LinkedList getOpen() {
        return this.openList;
    }
    
    public LinkedList getClosed() {
        return this.closedList;
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
          System.err.println(e);
        }
    }

    /**
     * Main method (for debugging).
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      long totalt = 0;
      
      for(int j=0; j < 1000; j++) {
        
        BasicGenerator lg = new BasicGenerator(101);
        
        lg.layoutSrcDest();
        
        //lg.print();

        Node.idCount = 0;
        Astar astar0 =
            new Astar(lg.getMap(),lg.getSrcX(),lg.getSrcY(),lg.getDestX(),lg.getDestY());

        Node node0 = astar0.find();
        
        // Get line of sight steps
        double los = node0.getSteps();
        
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
        Node node = astar1.find();
        long t1 = System.currentTimeMillis();
        
        totalt += (t1 - t0);

      }
      
      System.out.println("runtime = "+totalt);
    }
}
