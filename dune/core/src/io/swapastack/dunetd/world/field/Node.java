package io.swapastack.dunetd.world.field;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Node class.
 *
 * @author Mattis Bühler
 */
public class Node {
    //other nodes
    private Node prev;
    private final ArrayList<Node> neighbors = new ArrayList<>();

    //values of the node
    private final Point point;
    public int distance;
    public boolean isManned = false;

    /**
     * The Constructor of the Node.
     *
     * @param distance the distance to get to the node.
     * @param point the position of the node.
     */
    public Node (int distance, Point point) {
        this.distance = distance;
        this.point = point;
    }

    /**
     * Adds a neighbor to the node.
     *
     * @param node the node which is the new neighbor.
     */
    public void addNeighbor(Node node) {
        neighbors.add(node);
    }

    /**
     * Returns the neighbors of a node.
     *
     * @return arraylist of the neighbors.
     */
    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    /**
     * Sets the prev to the given node.
     *
     * @param node the node will be the prev node.
     */
    public void setPrev(Node node) {
        this.prev = node;
    }

    /**
     * Returns the prev node.
     *
     * @return the node of the reference prev.
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * Returns the position of the node.
     *
     * @return a Point with the position of the node.
     */
    public Point getPoint() {
        return point;
    }

}

