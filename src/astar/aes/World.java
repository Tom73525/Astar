package astar.aes;


/**
 * A game world (or level) contains two layers of entities: unmoving tiles
 * drawn in the background and dynamic actors drawn over the top. Levels are
 * generated usign the LevelGenerator. Note that certain tile types can
 * become Actors when they are encountered by the player.
 */

public class World
{
   public static final int TILE_HEIGHT = 16;
   public static final int TILE_WIDTH = 16;
   public static final int TILE_HALF_HEIGHT = TILE_HEIGHT / 2;
   public static final int TILE_HALF_WIDTH = TILE_WIDTH / 2;
   public static final int TILE_CORRIDOR_MIN = 5;
   public static final int TILE_ROOM_MIN_WIDTH = 7;
   public static final int TILE_ROOM_MIN_HEIGHT = 7;
   public static final int TILE_ROOM_MAX_WIDTH = 10;
   public static final int TILE_ROOM_MAX_HEIGHT = 10;



   // tile types
   public static final char NO_TILE = '.';
   public static final byte START_REAL_TILE = 1;
   public static final char WALL_TILE = '#';
   public static final char GATEWAY_TILE = '$';
   public static final char PLAYER_START_TILE = 'X';
   public static final byte END_REAL_TILE = 2;
   // activator tiles "become" an actor when the player gets in range
   // they're used so we don't have to bother with cycling actors in the
   // world before the player has encountered them.
   public static final byte START_ACTIVATOR_TILE = 100;
   public static final char DRONE_ACTIVATOR_TILE = 'D';
   public static final char TURRET_ACTIVATOR_TILE = 'T';
   public static final char FIGHTER_ACTIVATOR_TILE = 'F';
   public static final byte END_ACTIVATOR_TILE = 102;


}















