package io.swapastack.dunetd.world.placeable;

import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;

import java.awt.*;

/**
 * The GameObjectPlaceable class.
 *
 * @author Mattis Bühler
 */
public abstract class GameObjectPlaceable {
    //GameWorld and GameScreen
    public GameWorld world;
    public GameScreen screen;

    //Object position
    public Point position;

    /**
     * Constructor of the GameObjectPlaceable.
     *
     * @param position the position of the object.
     * @param world the GameWorld in which the object exists.
     * @param screen the GameScreen in which the object exists.
     */
    public GameObjectPlaceable(Point position, GameWorld world, GameScreen screen) {
        this.position = position;
        this.world = world;
        this.screen = screen;
    }

    /**
     * Constructor for testing purposes
     */
    public GameObjectPlaceable() {}
}
