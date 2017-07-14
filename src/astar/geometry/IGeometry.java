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
public interface IGeometry {
    double distance(Node a, Node b);
}
