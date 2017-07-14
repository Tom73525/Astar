
package astar.interactive;

import astar.Astar;
import astar.aes.World;
import astar.util.Node;

/**
 * This class implements a single step A*.
 * @author Ron Coleman
 */
public class SingleStepAstar extends Astar {
    
    public SingleStepAstar() {
        super();
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
