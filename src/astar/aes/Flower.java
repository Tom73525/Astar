package astar.aes;

import astar.pcg.WellsGenerator;
import astar.util.Node;

public class Flower {
	private WellsGenerator lg;
	private Node fullPath;
	private Node basicPath;
	
	public Flower(WellsGenerator lg) {
		this.lg = lg;
	}
	
	private Node findFullPath() {
		startBasic();
		
		return null;
	}
	
	private void startBasic() {
		int x = lg.getPlayerStartX();
		int y = lg.getPlayerStartY();
		
		char[][] tileMap = lg.getTileMap();
		// If Y is more South, pick position North
	}

}
