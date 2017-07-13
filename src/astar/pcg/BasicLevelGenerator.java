package astar.pcg;

import astar.Astar;
import astar.aes.World;
import java.util.Random;
/**
 *
 * @author RonColeman
 */
public class BasicLevelGenerator extends AbstractLevelGenerator {
  public final static int BUFZONE = 4;
  //public final static int BARRIER_FACTOR = 5;
  public final static int BARRIER_FACTOR = 2;
  protected char tileMap[][];
  protected int width;
  protected int height;
  protected int srcX;
  protected int srcY;
  protected int destX;
  protected int destY;
  protected Random ran = new Random();
   
  /** Creates a new instance of LevelGenerator
     * @param seed
   */
  public BasicLevelGenerator(Integer seed) {
    super(seed);
  }
  
  @Override
  public char[][] generateLevel(int level) {
    this.width = level * 5;
    this.height = level * 5;
      
    tileMap = new char[height][width];
        
    for(int i=0; i < height; i++) {
      for(int j=0; j < width; j++) {
        tileMap[i][j] = World.NO_TILE;
      }
    }
    
    ran.setSeed(seed);
    
    layoutSrcDest();
   
    layoutBarriers();
      
    return tileMap;
  }
  
  public char[][] getMap() {
    return tileMap;
  }
  
  public int getSrcX() {
    return srcX;
  }
  
  public int getSrcY() {
    return srcY;
  }
  
  public int getDestX() {
    return destX;
  }
  
  public int getDestY() {
    return destY;
  }
  
  @Override
  public void dump() {
    for(int j=0; j < height; j++) {
      for(int k=0; k < width; k++)
        System.out.print(tileMap[k][j]);
      System.out.println("");
    }
  }
    
  public void layoutSrcDest() {   
    do {
      srcX = ran.nextInt(width);
      srcY = ran.nextInt(height);
    } while(srcX - BUFZONE <= 0 || width - srcX <= BUFZONE || srcY - BUFZONE <= 0 || height - srcY <= BUFZONE);
      
    do {
      destX = ran.nextInt(width);
      destY = ran.nextInt(height);
    } while(Math.abs(destX - srcX) <= BUFZONE || Math.abs(destY - srcY) <= BUFZONE ||
            destX - BUFZONE <= 0 || width - destX <= BUFZONE || destY - BUFZONE <= 0 || height - destY <= BUFZONE);
      
    tileMap[srcY][srcX] = World.PLAYER_START_TILE;
    tileMap[destY][destX] = World.GATEWAY_TILE;     
  }
    
  public void layoutBarriers() {
	/*
    // Put down horizontal barrier
    if(srcY - destY > 0) {
      // Along top of src
      for(int k=srcX-2; k < srcX+2; k++)
        tileMap[k][srcY-2] = Astar.SYM_OBSTACLE;
    }
    else {
      // Along bottom of dest
      for(int k=srcX-2; k < srcX+2; k++)
        tileMap[k][srcY+2] = Astar.SYM_OBSTACLE;
    }

    // Put down vertical barrier
    int side = ran.nextInt(2) == 0 ? -2 : 2;
    for(int j=srcY-2; j < srcY+2; j++)
      tileMap[srcX+side][j] = Astar.SYM_OBSTACLE; 
    */
	  
    // Randomly deposit obstacles
    int nb = width * height / BARRIER_FACTOR;
    
    for(int i=0; i < nb; i++) {
      int x = ran.nextInt(height);
      int y = ran.nextInt(height);
      
      if(x == srcX && y == srcY)
         continue;
      
      if(x == destX && y == destY)
        continue;
      
      tileMap[y][x] = World.WALL_TILE;
    }
  }      
}

