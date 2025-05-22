package io.swapastack.dunetd.test;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.enemies.BossUnit;
import io.swapastack.dunetd.world.placeable.turret.Bullet;
import io.swapastack.dunetd.world.GameWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameWorldTest {

    @Test
    void checkInRange() {
        GameWorld world = new GameWorld();
        BossUnit enemy = new BossUnit(new Vector3(1,0,1));

        float towerPosX = 0;
        float towerPosY = 0;

        float towerRadius = 1.5f;
        assertTrue(world.checkInRange(enemy, towerRadius, towerPosX, towerPosY));
        towerRadius = 1.4f;
        assertFalse(world.checkInRange(enemy, towerRadius, towerPosX, towerPosY));
    }

    @Test
    void checkCollision() {
        GameWorld world = new GameWorld();
        BossUnit enemy = new BossUnit(new Vector3(1,0,0));

        //bossunit radius = 0.3f
        Bullet bullet = new Bullet(new Vector3((1+0.5f), 0, 0));

        assertFalse(world.checkInRange(enemy, enemy.radius, bullet.position.x, bullet.position.z));

        bullet.position.x = (1+0.2f);
        assertTrue(world.checkInRange(enemy, enemy.radius, bullet.position.x, bullet.position.z));
    }

}