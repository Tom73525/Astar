
package astar.interactive;

import astar.Astar;
import astar.aes.World;
import astar.util.Node;

/**
 * This class implements a single step A*.
 * @author Ron Coleman
 */
public class SingleStepAstar extends Astar {
    
    public SingleStepAstar(char[][] tileMap) {

        this.tileMap = tileMap;
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
                
                if(srcX >=0 && srcY >= 0 && destX >=0 && destY >= 0)
                    break;
            }
        }
        
        if(srcX < 0 || srcY < 0 || destX < 0 || destY < 0)
            System.err.println("bad tile map");
    } 
    
    public Node find1() {
        if(src == null && dest == null) {
            dest = new Node(destX, destY);
            src = new Node(srcX, srcY);

            moveToOpen(src);
        }

        while (!openList.isEmpty()) {
            Node curNode = getLowestCostNode();

            if (curNode.equals(dest)) {
                return relink(curNode);
            }

            moveToClosed(curNode);

            // Reset the adjacency state
            reset();

            // Put all the adjacent nodes on the open list of possibilities
            do {
                // Get next adjacent to current node
                Node adj = getAdjacent(curNode);

                // If there are no more adjacents, we're here
                if (adj == null) {
                    break;
                }

                double heuristic = calculateHeuristic(adj, dest);
                double steps = adj.getSteps();
                double cost = steps + heuristic;

                adj.setCost(cost);

                openList.add(adj);

            } while (true);
            
            return curNode;
        }
        
        return null;
    }
}
