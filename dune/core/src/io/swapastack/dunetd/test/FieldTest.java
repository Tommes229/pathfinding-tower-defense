package io.swapastack.dunetd.test;

import io.swapastack.dunetd.world.field.Field;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void towerPlaceable() {
        Field field = new Field(1,2);
        field.mannedTiles[0][0] = true;

        assertTrue(field.mannedTiles[0][0]);
        assertFalse(field.mannedTiles[1][0]);
    }

    @Test
    void findFastestWay() {
        Field field = new Field(4,4);
        Point startPoint = new Point(0,0);
        Point endPoint = new Point(3,0);

        ArrayList<Point> fastestPath = new ArrayList<>();
        Collections.addAll(fastestPath, new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3),
                                        new Point(1,3), new Point(2,3), new Point(2,2), new Point(2, 1),
                                        new Point(3, 1), new Point(3,0));

        /*
        mannedTile
        {false, false, true,  false}
        {false, true,  false, false}
        {false, true,  false, true }
        {false, false, false, true }
         */
        field.mannedTiles[2][0] = true;
        field.mannedTiles[1][1] = true;
        field.mannedTiles[1][2] = true;
        field.mannedTiles[3][2] = true;
        field.mannedTiles[3][3] = true;

        assertEquals(fastestPath, field.findWay(startPoint, endPoint));

    }

    @Test
    void selectMultipleWays() {
        Field field = new Field(4,4);
        Point startPoint = new Point(0,0);
        Point endPoint = new Point(3,0);

        ArrayList<Point> fastestPath = new ArrayList<>();
        ArrayList<Point> fastestPath2 = new ArrayList<>();
        Collections.addAll(fastestPath, new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3),
                                        new Point(1,3), new Point(2,3), new Point(2,2), new Point(2, 1),
                                        new Point(3, 1), new Point(3,0));

        Collections.addAll(fastestPath2,    new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3),
                                            new Point(1,3), new Point(2,3), new Point(2,2), new Point(3, 2),
                                            new Point(3, 1), new Point(3,0));

        /*
        mannedTile
        {false, false, true,  false}
        {false, true,  false, false}
        {false, true,  false, false}
        {false, false, false, true }
         */
        field.mannedTiles[2][0] = true;
        field.mannedTiles[1][1] = true;
        field.mannedTiles[1][2] = true;
        field.mannedTiles[3][3] = true;

        assertTrue(fastestPath.equals(field.findWay(startPoint, endPoint)) || fastestPath2.equals(field.findWay(startPoint, endPoint)));
    }

    @Test
    void noPossibleWay() {
        Field field = new Field(4,4);
        Point startPoint = new Point(0,0);
        Point endPoint = new Point(3,0);

        ArrayList<Point> fastestPath = new ArrayList<>();
        ArrayList<Point> fastestPath2 = new ArrayList<>();
        Collections.addAll(fastestPath, new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3),
                new Point(1,3), new Point(2,3), new Point(2,2), new Point(2, 1),
                new Point(3, 1), new Point(3,0));

        Collections.addAll(fastestPath2,    new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3),
                new Point(1,3), new Point(2,3), new Point(2,2), new Point(3, 2),
                new Point(3, 1), new Point(3,0));

        /*
        mannedTile
        {false, false, true,  false}
        {false, true,  false, false}
        {false, true,  false, false}
        {true, false, false, false}
         */
        field.mannedTiles[2][0] = true;
        field.mannedTiles[1][1] = true;
        field.mannedTiles[1][2] = true;
        field.mannedTiles[0][3] = true;

        assertEquals(field.findWay(startPoint, endPoint).size(), 0);
    }
}