package io.swapastack.dunetd.world.placeable.turret;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The GunTurret class.
 *
 * @author Mattis Bühler
 */
public class GunTurret extends AbstractTurret {

    //Enemies
    protected ArrayList<GameObjectEnemy> enemies;
    protected GameObjectEnemy fastestEnemy;

    //Shooting
    protected float delayMS = (1000/ MainMenuScreen.gunturretFrequence.get());
    protected long startTime;

    //Turret values
    protected Point position;
    protected float rotation;
    protected Vector3 scaling;

    /**
     * Constructor of the GunTurret class.
     * Calls create method.
     *
     * @param position the position of the turret.
     * @param world the GameWorld in which the turret exists in.
     * @param screen the GameScreen in which the turret exists in.
     * @param gunTurret if it's a GunTurret.
     */
    public GunTurret(Point position, GameWorld world, GameScreen screen, boolean gunTurret) {
        super(position, world, screen);
        this.position = position;
        if (gunTurret) {
            create();
        }
    }

    /**
     * Constructor for testing purposes.
     */
    public GunTurret() {
        super();
    }

    /**
     * Adds a new GunTurret scene with the given values.
     */
    public void create() {
        radius = 0.01f * MainMenuScreen.gunturretRadius.get();
        startTime = TimeUtils.millis();
        scaling = new Vector3(1,1,1);

        turretScene = new Scene(screen.sceneAssetHashMap.get("weapon_blaster.glb").scene);
        turretScene.modelInstance.transform.setToTranslation(new Vector3(position.x, screen.groundTileDimensions.y, position.y)).scl(scaling);
        screen.addingScene(turretScene);
    }

    /**
     * This method updates everytime the render method of its GameScreen gets called.
     * Updates the upper class.
     * Calls shoot after the given delay and calls rotation method for the first enemy in range.
     *
     * @param delta the time since the last rendering.
     *
     * @author Mattis Bühler
     */
    public void update(float delta) {
        super.update(delta);
        enemies = getEnemiesInRange(radius, position.x, position.y);

        if (enemies.size() > 0) {
            fastestEnemy = findFastestEnemy(enemies);
            rotation = setRotation(fastestEnemy);
        }

        //shoot bullets in time intervals
        if (enemies.size() > 0) {
            if (TimeUtils.timeSinceMillis(startTime) > delayMS) {
                shoot(fastestEnemy);
                startTime = TimeUtils.millis();
            }
        }

        turretScene.modelInstance.transform.setToTranslation(position.x, screen.groundTileDimensions.y, position.y).scl(scaling)
                .rotate(new Vector3(0.0f, 1.0f, 0.0f), rotation);
    }


    /**
     * Returns the enemy which is the furthest on the path in the turrets range.
     *
     * @param enemies the enemies that are within the turrets range.
     *
     * @return the first enemy in range.
     *
     * @author Mattis Bühler
     */
    public GameObjectEnemy findFastestEnemy(ArrayList<GameObjectEnemy> enemies) {

        int counterMax = enemies.get(0).getCounter();
        int indexFastest = 0;
        float distanceMin = enemies.get(indexFastest).getDistanceToPoint();

        ArrayList<GameObjectEnemy> minCounterEnemies = new ArrayList<>();

        //get lowest counter value
        for (int i = 1; i<enemies.size(); i++) {
            if (enemies.get(i).getCounter() > counterMax) {
                counterMax = enemies.get(i).getCounter();
            }
        }

        //get all enemies with the lowest counter value
        for (GameObjectEnemy enemy : enemies) {
            if (enemy.getCounter() == counterMax) {
                minCounterEnemies.add(enemy);
            }
        }

        //if there's only one enemy in the list, we return it
        if (minCounterEnemies.size() == 1) {
            return minCounterEnemies.get(0);
        }

        //get the enemy with the lowest distance, out of the enemies with the lowest counter
        for (int i = 1; i<minCounterEnemies.size(); i++) {
            if (minCounterEnemies.get(i).getDistanceToPoint() < distanceMin) {
                distanceMin = minCounterEnemies.get(i).getDistanceToPoint();
                indexFastest = i;
            }
        }

        return minCounterEnemies.get(indexFastest);

    }

    /**
     * Returns the rotation of the turret to the rotation of the given enemy.
     *
     * @param enemy the enemy the turret would look at with the rotation.
     *
     * @return the rotation the turret needs to look at the enemy.
     *
     * @author Mattis Bühler
     */
    public float setRotation(GameObjectEnemy enemy) {

        float xDistance = position.x - enemy.getX();
        float yDistance = position.y - enemy.getZ();
        float absXDist = Math.abs(xDistance);
        float absYDist = Math.abs(yDistance);
        float atanYX = (float) Math.toDegrees(Math.atan(Math.toRadians(absYDist) / Math.toRadians(absXDist)));
        float atanXY = (float) Math.toDegrees(Math.atan(Math.toRadians(absXDist) / Math.toRadians(absYDist)));

        //left and right
        if (enemy.getX() == position.x) {
            if (xDistance > 0) {
                return 0f;
            }
            return 180f;
        }

        //up and down
        if (enemy.getZ() == position.y) {
            if (yDistance > 0) {
                return 270f;
            }
            return 90f;
        }

        //top-right corner angle
        if (xDistance < 0 && yDistance > 0) {
            if (absXDist >= absYDist) {
                return (270f + atanYX);
            }
            return (360f - atanXY);
        }

        //bottom-right corner angle
        if (xDistance < 0 && yDistance < 0) {
            if (absXDist <= absYDist) {
                return (180f + atanXY);
            }
            return (270f - atanYX);
        }

        //bottom-left corner angle
        if (xDistance > 0 && yDistance < 0) {
            if (absXDist >= absYDist) {
                return (90f + atanYX);
            }
            return (180f - atanXY);
        }

        //top-left corner angle
        if (xDistance > 0 && yDistance > 0) {
            if (absXDist <= absYDist) {
                return atanXY;
            }
            return (90f - atanYX);
        }

        return rotation;
    }

    /**
     * Adds a new Bullet at the position of the turret to the GameWorld, which will fly to the given enemy.
     *
     * @param enemy the enemy the bullet will fly to.
     *
     * @author Mattis Bühler
     */
    public void shoot(GameObjectEnemy enemy) {
        Bullet bullet = new Bullet(enemy, screen, world, position, rotation, true);
        world.addBullet(bullet);
    }

}