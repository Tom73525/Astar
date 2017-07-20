/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.plugijn;

import astar.util.Node;

/**
 *
 * @author roncoleman
 */
public interface IModel {
    public void init(char[][] tileMap);
    public double expense(double heuristic, Node curNode, Node adjNode);
    public void tweak(Node curNode);
}
