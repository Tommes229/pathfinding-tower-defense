package io.swapastack.dunetd.test;

import io.swapastack.dunetd.world.placeable.shaiHulud.Thumper;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ThumperTest {

    @Test
    void possiblePosition() {
        Point firstThumperPos = new Point(0,0);
        Point secondThumperPos = new Point(2, 0);
        Thumper thumper = new Thumper(firstThumperPos);

        //same row possible
        assertTrue(thumper.isPossiblePosition(secondThumperPos.x, secondThumperPos.y));

        //same col possible
        secondThumperPos.y = 3;
        secondThumperPos.x = 0;
        assertTrue(thumper.isPossiblePosition(secondThumperPos.x, secondThumperPos.y));

        //neither same col nor same row
        secondThumperPos.x = 1;
        assertFalse(thumper.isPossiblePosition(secondThumperPos.x, secondThumperPos.y));
    }

}