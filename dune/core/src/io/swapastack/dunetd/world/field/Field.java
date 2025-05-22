package io.swapastack.dunetd.world.field;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Field class.
 *
 * @author Mattis Bühler
 */
public class Field {
    //Manned state
    public boolean [][] mannedTiles;

    //Field dimension
    private final int rows;
    private final int cols;

    /**
     * The constructor of the Field class, sets the dimension of the field. Also calls the create method.
     *
     * @param rows the rows of the field.
     * @param cols the cols of the field.
     */
    public Field(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        create();
    }

    /**
     * The create method initializes the manned tiles array.
     */
    public void create() {
        mannedTiles = new boolean[cols][rows];
    }

    /**
     * The method finds the fastest path on the playing field via dijkstra algorithm, please refer https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm.
     * Uses the Node class for calculations and returns the arraylist with the getPath method.
     *
     * @param startPoint the position, where the pathfinding starts.
     * @param endPoint the position, where the pathfinding ends.
     *
     * @return a list of points if there is a possible path or an empty list if there is non.
     *
     * @author Mattis Bühler
     */
    public ArrayList<Point> findWay(Point startPoint, Point endPoint) {

        ArrayList<Node> remainingNodes = new ArrayList<>();
        Node node;
        Node startNode = null;
        Node endNode = null;

        //creating all possible nodes with point values and manned boolean
        for (int i = 0; i<rows; i++) {
            for (int k = 0; k<cols; k++) {
                //mark end- and start-node
                if (k == startPoint.x && i == startPoint.y) {
                    startNode = new Node(0, new Point(k,i));
                    remainingNodes.add(startNode);
                } else if (k == endPoint.x && i == endPoint.y) {
                    endNode = new Node(10000, new Point(k,i));
                    remainingNodes.add(endNode);
                } else {
                    //set high value, which is higher than the highest possible distance
                    node = new Node(10000, new Point(k,i));
                    remainingNodes.add(node);

                    //check if manned
                    if (mannedTiles[k][i]) {
                        node.isManned = true;
                    }
                }
            }
        }

        //add possible neighbors to nodes
        for (int i = 0; i<remainingNodes.size(); i++) {
            node = remainingNodes.get(i);

            //top neighbor
            if (!(i < cols)) {
                if ((!remainingNodes.get(i-cols).isManned)) {
                    node.addNeighbor(remainingNodes.get(i-cols));
                }
            }

            //bottom neighbor
            if (!(i >= (cols*(rows-1)))) {
                if (!remainingNodes.get(i+cols).isManned) {
                    node.addNeighbor(remainingNodes.get(i+cols));
                }
            }

            //right neighbor
            if (!((i % cols) == (cols-1))){
                if (!remainingNodes.get(i+1).isManned) {
                    node.addNeighbor(remainingNodes.get(i+1));
                }
            }


            //left neighbor
            if (!((i % cols) == 0)) {
                if (!remainingNodes.get(i-1).isManned) {
                    node.addNeighbor(remainingNodes.get(i-1));
                }
            }
        }

        int min;
        int distance;
        Node firstNode;
        int alt;

        while (remainingNodes.size() != 0) {
            //get Node with the lowest distance by iterating through the remaining Nodes
            min = remainingNodes.get(0).distance;
            firstNode = remainingNodes.get(0);
            for (int i = 1; i < remainingNodes.size(); i++) {
                distance = remainingNodes.get(i).distance;
                if (distance < min) {
                    min = distance;
                    firstNode = remainingNodes.get(i);
                }
            }

            //remove the lowest distance node from remaining nodes
            remainingNodes.remove(firstNode);

            //end while loop if node with the lowest distance is the end-node
            assert endNode != null;
            if (firstNode.getPoint().x == endNode.getPoint().x && firstNode.getPoint().y == endNode.getPoint().y) {
                endNode = firstNode;
                break;
            }

            //check all neighbors for lower distance and replace values
            for (Node neighbor : firstNode.getNeighbors()) {
                alt = firstNode.distance + 1;
                if (alt < neighbor.distance) {
                    neighbor.distance = alt;
                    neighbor.setPrev(firstNode);
                }
            }

        }

        //get list of points from start- to end-node
        ArrayList<Point> path = new ArrayList<>();
        assert endNode != null;
        if (endNode.distance < 9999) {
            path = getPath(startNode, endNode, path);
        }

        return path;
    }

    /**
     * A recursive method, which returns a list of points from the start to the end node.
     *
     * @param startNode the node, the list starts from.
     * @param lastNode the node, list ends with.
     * @param path an empty Arraylist which is filled recursively.
     *
     * @return the arraylist of points to get to the endpoint.
     *
     * @author Mattis Bühler
     */
    private ArrayList<Point> getPath(Node startNode, Node lastNode, ArrayList<Point> path) {
        if (lastNode == startNode) {
            path.add(lastNode.getPoint());
            return path;
        }
        path = getPath(startNode, lastNode.getPrev(), path);
        path.add(lastNode.getPoint());
        return path;
    }
}


