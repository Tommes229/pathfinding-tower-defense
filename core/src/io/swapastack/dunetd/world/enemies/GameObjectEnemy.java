package io.swapastack.dunetd.world.enemies;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.field.Field;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The GameObjectEnemy class.
 *
 * @author Mattis Bühler
 */
public abstract class GameObjectEnemy {

    //Gamescreen, gameworld related
    public GameScreen screen;
    public boolean destroyMe = false;
    public GameWorld world;

    //Pathfinding
    public ArrayList<Point> path;
    private int counter = 0;
    public Vector3 position;

    //Enemy scene
    public float rotation;
    public Scene enemyScene;
    public Vector3 scaling;

    //Start- and endpoint
    private Point startPoint;
    private Point endPoint;

    //Enemy speed
    public float speed;

    //Enemyhitbox radius
    public float radius;

    //Soundturret values
    public float slowedSpeed;
    public float slowPercentage = 0.01f * MainMenuScreen.slowPercentage.get();
    public boolean isSlowed;

    //Player stats values
    public int lifePoints;
    public int damageToPlayer;
    public int scoreValue;
    public int spiceValue;

    /**
     * Constructor of the GameObjectEnemy.
     *
     * @param startPoint the position, the enemy will start from.
     * @param endPoint the position, the enemy will run to.
     * @param world the world in which the enemy exists.
     * @param screen the screen in which the enemy exists.
     */
    public GameObjectEnemy(Point startPoint, Point endPoint,GameWorld world, GameScreen screen) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.screen = screen;
        this.world = world;
        position = new Vector3(startPoint.x, screen.groundTileDimensions.y, startPoint.y);
    }


    /**
     * Constructor for testing purposes.
     *
     * @param position the position of the enemy in the world.
     */
    public GameObjectEnemy(Vector3 position) {
        this.position = position;
    }

    /**
     * Constructor for testing purposes.
     *
     * @param position the position of the enemy in the world.
     * @param path the path, the enemy has to walk, to reach the endportal.
     */
    public GameObjectEnemy(Vector3 position, ArrayList<Point> path) {
        this.position = position;
        this.path = path;
    }

    /**
     * This method is called evey time the GameScreen renders, if the enemy exists in the world
     *
     * @param delta The time in seconds since the last render.
     *
     * @author Mattis Bühler
     */
    public void update(float delta) {
        if (lifePoints <= 0) {
            destroyMe = true;
            screen.removingScene(enemyScene);
            addScore();
            addSpice();
        }

        if (isSlowed) {
            move(slowedSpeed);
        } else {
            move(speed);
        }
    }

    /**
     * Moves the enemy, point after point through the path, which the findWay method in the field class has calculated,
     * if the enemy reaches the end, the enemy will be removed from the world.
     *
     * @param speed the speed with which the enemy will move through the world.
     *
     * @author Mattis Bühler
     */
    public void move(float speed) {

        if (counter == path.size()) {
            destroyMe = true;
            damagePlayer();
            screen.removingScene(enemyScene);

        } else {

            //moves the Enemy to the right
            if (position.x <= path.get(counter).x - 0.005f) {
                position.x += speed;
                rotation = 90f;
            }

            //moves the Enemy to the left
            if (position.x >= path.get(counter).x + 0.005f) {
                position.x -= speed;
                rotation = 270f;
            }

            //moves the Enemy up
            if (position.z <= path.get(counter).y - 0.005f) {
                position.z += speed;
                rotation = 0f;
            }

            //moves the Enemy down
            if (position.z >= path.get(counter).y + 0.005f) {
                position.z -= speed;
                rotation = 180f;
            }

            //if one point of the path is reached, set the next point to be reached
            if ((position.x < (path.get(counter).x + 0.005f) && position.x > (path.get(counter).x - 0.005f))
                    && (position.z < (path.get(counter).y + 0.005f) && position.z > (path.get(counter).y - 0.005f))) {

                counter++;
            }
        }
    }

    /**
     * Sets the path to the current fastest path to the fastest path on given field.
     *
     * @param field the field on which the fastest path is calculated.
     */
    public void setPath(Field field) {
        path = field.findWay(startPoint, endPoint);
    }

    /**
     * Returns the x-coordinate of the enemy.
     *
     * @return the x-coordinate of the enemy.
     */
    public float getX() {
        return position.x;
    }

    /**
     * Returns the z-coordinate of the enemy.
     *
     * @return the z-coordinate of the enemy.
     */
    public float getZ() {
        return position.z;
    }

    /**
     * Returns the rotation of the enemy.
     *
     * @return the rotation of the enemy.
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Returns the counter of the enemy.
     *
     * @return the counter of the enemy.
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Returns the distance to the next point on the path.
     *
     * @return the distance to the next point.
     *
     * @author Mattis Bühler
     */
    public float getDistanceToPoint() {
        if (counter != path.size()) {
            return Math.abs(position.x - path.get(counter).x) + Math.abs(position.z - path.get(counter).y);
        }
        return 0.1f;
    }

    /**
     * Reduce the player life in the world.
     */
    public void damagePlayer() {
        world.playerLife -= damageToPlayer;
    }

    /**
     * Add value to the score in the world.
     */
    public void addScore() {
        world.score += scoreValue;
    }

    /**
     * Add value to spice in the world.
     */
    public void addSpice() {
        world.spice += spiceValue;
    }
}



