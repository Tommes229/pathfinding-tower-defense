package io.swapastack.dunetd.test;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.enemies.BossUnit;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.placeable.turret.GunTurret;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GunTurretTest {

    @Test
    void findFastestEnemy() {
        ArrayList<Point> path = new ArrayList<>();
        path.add(new Point(0, 0));
        path.add(new Point(1,0));
        path.add(new Point(2,0));

        BossUnit bossUnit = new BossUnit(new Vector3(0,0,0), path);
        BossUnit bossUnit2 = new BossUnit(new Vector3(0,0,0), path);
        bossUnit2.move(0.05f);

        ArrayList<GameObjectEnemy> enemies = new ArrayList<>();

        enemies.add(bossUnit);
        enemies.add(bossUnit2);

        GunTurret gunTurret = new GunTurret();
        assertEquals(bossUnit2, gunTurret.findFastestEnemy(enemies));
    }
}