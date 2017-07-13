package astar.util;

/**
 * Node class.
 */
public class Node {
    public static int idCount;
    private int x;
    private int y;
    private Node parent;
    private Node child;
    private double cost = 0;
    private int steps = 0;
    private int id;
    private double inertia;

    /**
     * Constructor.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public Node(int x,int y) {
        this.x = x;
        this.y = y;
        this.cost = Long.MAX_VALUE;
        this.id = idCount++;
    }

    /** Constructor.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param parent Parent node of node.
     */
    public Node(int x,int y,Node parent) {
        this(x,y);

        this.parent = parent;
        parent.child = this;
        this.steps = parent.steps + 1;        
    }

    /** Copy constructor
     * @param node Node to copy.
     */
    public Node(Node node) {
      this(node.x,node.y);
    }
    
    /**
     * Get the parent node.
     * @return Parent node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Get  cost of the node, namely, steps plus heuristic.
     * @return Cost.
     */
    public double getCost() {
        return cost;
    }

    /**
     * Tests if this node equal to another node.
     * @param node Node.
     * @return True, if two nodes equal, false otherwise.
     */
    public boolean equals(Node node) {
        return node.x == x && node.y == y;
    }
    
    /**
     * Gets the node at x, y if it exists going forward this node.
     * @param x
     * @param y
     * @return
     */
    public Node getNode(int x, int y) {
    	if(equals(x,y))
    		return this;
    	
    	Node anode = this.getChild();
    	while(anode != null) {
    		if(anode.equals(x,y))
    			return anode;
    		anode = anode.getChild();
    	}
    	return null;
    }
    
    /**
     * Tests if the node is equal to the x, y coordinate.
     * @param x
     * @param y
     * @return
     */
    public boolean equals(int x, int y) {
    	return this.x == x && this.y == y;
    }

    /**
     * Get X coordinate of node.
     * @return X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Get Y coordinate of node.
     * @return Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Get steps from parent.
     * @return Steps from parent.
     */
    public int getSteps() {
        return steps;
    }

    /** Set the parent node.
     * @param parent Parent node.
     */
    public void setParent(Node parent){
        this.parent = parent;
    }

    /**
     * Set cost of node.
     * @param cost Cost of node.
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Print the node path.
     */
    public void print() {
      Node node = this;

      do {
        System.out.println("("+node.getX()+","+node.getY()+")");

        node = node.getParent();
      } while(node != null);
    }

	public Node getChild() {
		return child;
	}
	
	public double getInertia() {
		return inertia;
	}

	public void setChild(Node child) {
		this.child = child;
	}
	
	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setInertia(double strength) {
		this.inertia = strength;
	}
	
	public void incInertia() {
		inertia += 1;
	}
	
	public String toString() {
		String pars = "par(none)";
		if(parent != null) {
			int parx, pary;
			parx = parent.getX();
			pary = parent.getY();
			pars = "par(x:"+parx+" y:"+pary+")";
		}
		
		String chis = "chi(none)";
		if(child != null) {
			int chix, chiy;
			chix = child.getX();
			chiy = child.getY();
			chis = "chi(x:"+chix+" y:"+chiy+")";
		}
		
		return "(x:"+x+" y:"+y+") "+pars+" "+chis;
	}
}

