/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.geometry;

import astar.util.Node;

/**
 *
 * @author roncoleman
 */
public class Manhattan implements IGeometry {
    @Override
    public double distance(Node a, Node b) {
        double dx = a.getCol() - b.getCol();
        
        double dy = a.getRow() - b.getRow();
        
        double dist = Math.abs(dx) + Math.abs(dy);
        
        return dist;
    }    
}
