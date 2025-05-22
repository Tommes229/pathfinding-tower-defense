package io.swapastack.dunetd.test;

import io.swapastack.dunetd.world.placeable.shaiHulud.ShaiHulud;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ShaiHuludTest {

    @Test
    void calculateDirection() {
        ShaiHulud shai = new ShaiHulud();
        Point Pos1 = new Point(0,0);
        Point Pos2 = new Point(0,0);

        //left-right
        Pos2.x = 1;
        assertTrue( shai.calculateDirection(Pos1, Pos2).x > 0 && shai.calculateDirection(Pos1, Pos2).y == 0);

        //right-left
        Pos1.x = 2;
        assertTrue( shai.calculateDirection(Pos1, Pos2).x < 0 && shai.calculateDirection(Pos1, Pos2).y == 0);

        //top-down
        Pos1.x = 1;

        Pos2.y = 1;
        assertTrue( shai.calculateDirection(Pos1, Pos2).y > 0 && shai.calculateDirection(Pos1, Pos2).x == 0);

        //bottom-up
        Pos1.y = 2;
        assertTrue( shai.calculateDirection(Pos1, Pos2).y < 0 && shai.calculateDirection(Pos1, Pos2).x == 0);
    }
}