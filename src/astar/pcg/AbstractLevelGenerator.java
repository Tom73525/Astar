/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.pcg;

/**
 *
 * @author roncoleman
 */
abstract public class AbstractLevelGenerator {
    protected final int seed;

    abstract public char[][] generateLevel(int level);
    abstract public void dump();
    
    public AbstractLevelGenerator(Integer seed) {
        this.seed = seed;
    }

}
