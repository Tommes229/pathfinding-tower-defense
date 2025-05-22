package io.swapastack.dunetd.world.placeable.turret;

import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.world.placeable.GameObjectPlaceable;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The AbstractTurret class.
 *
 * @author Mattis Bühler
 */
public abstract class AbstractTurret extends GameObjectPlaceable {

    //Radius
    public float radius;

    //Scene
    public Scene turretScene;

    //Destroy
    public boolean destroyMe = false;
    public boolean destroyMeShaiHulud = false;

    /**
     * Constructor of the AbstractTurret class.
     * Calls Constructor of upper class.
     *
     * @param position the position of the AbstractTurret.
     * @param world the GameWorld the AbstractTurret exists in.
     * @param screen the GameScreen the AbstractTurret exists in.
     *
     * @author Mattis Bühler
     */
    public AbstractTurret(Point position, GameWorld world, GameScreen screen) {
        super(position, world, screen);
        this.world = world;
    }

    /**
     * Constructor for testing purposes
     * Calls constructor of upper class.
     */
    public AbstractTurret() {
        super();
    }

    /**
     * This method is called everytime the render method of its GameScreen is called, as long the game is normally running.
     * This method removes the object from the world and removes it scene, if the variable destroyMe is set to true.
     *
     * @param delta the time since the last rendering.
     *
     * @author Mattis Bühler
     */
    public void update(float delta){
        if (destroyMe) {
            screen.removingScene(turretScene);
            if (!destroyMeShaiHulud) {
                world.setUnmanned(position.x, position.y);
            }
        }
    }

    /**
     * Returns all enemies that are within its range.
     *
     * @param radius the radius which is checked.
     * @param posX the x-coordinate of the center of the range.
     * @param posY the y-coordinate of the center of the range.
     *
     * @return a list of enemies that are within the given range.
     */
    public ArrayList<GameObjectEnemy> getEnemiesInRange(float radius, float posX, float posY) {
        return world.getEnemiesInRange(radius, posX, posY);
    }

}

