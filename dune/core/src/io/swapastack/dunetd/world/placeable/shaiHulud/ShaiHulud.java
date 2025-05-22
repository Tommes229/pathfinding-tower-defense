package io.swapastack.dunetd.world.placeable.shaiHulud;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.world.placeable.turret.AbstractTurret;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The ShaiHulud class.
 *
 * @author Mattis Bühler
 */
public class ShaiHulud {

    //GameScreen and GameWorld
    private GameWorld world;
    private GameScreen screen;

    //Positions
    private Vector3 position;
    private Point startPosition;
    private Point endPosition;

    //ShaiHulud values
    private int rotation;
    private float speed;

    //Scene
    private Scene shaiHuludScene;
    public boolean destroyMe;

    //Lists of objects the shaiHulud can destroy
    private final ArrayList<GameObjectEnemy> enemiesInRange = new ArrayList<>();
    private final ArrayList<AbstractTurret> turretsInRange = new ArrayList<>();
    private final ArrayList<Thumper> thumpersInRange = new ArrayList<>();

    /**
     * The constructor of the ShaiHulud class.
     * Sets the direction in which the ShaiHulud will move and sets the row/column in which the ShaiHulud will be positioned.
     * Calls the create method.
     *
     * @param thumper1Position the position of the first thumper.
     * @param thumper2Position the position of the second thumper.
     * @param world the GameWorld in which the ShaiHulud exists.
     * @param screen the GameScreen in which the ShaiHulud exists.
     */
    public ShaiHulud(Point thumper1Position, Point thumper2Position, GameWorld world, GameScreen screen) {
        this.world = world;
        this.screen = screen;

        Vector2 direction = calculateDirection(thumper1Position, thumper2Position);

        if (direction.x == 0) {
            if (direction.y > 0) {
                //top-down
                startPosition = new Point(thumper1Position.x, -4);
                endPosition = new Point(thumper1Position.x, world.rows + 4);
                rotation = 90;
            } else {
                //down-top
                startPosition = new Point(thumper1Position.x, world.rows + 4);
                endPosition = new Point(thumper1Position.x, -4);
                rotation = 270;
            }
        }

        if (direction.y == 0) {
            if (direction.x > 0) {
                //left-right
                startPosition = new Point(-4, thumper1Position.y);
                endPosition = new Point(world.cols + 4, thumper1Position.y);
                rotation = 180;
            } else {
                //right-left
                startPosition = new Point(world.cols + 4, thumper1Position.y);
                endPosition = new Point(-4, thumper1Position.y);
                rotation = 0;
            }

        }

        if (startPosition != null) {
            position = new Vector3(startPosition.x, screen.groundTileDimensions.y, startPosition.y);
        }

        create();
    }

    /**
     * Constructor for testing purposes.
     */
    public ShaiHulud() {}


    /**
     * The method creates a Scene of the ShaiHulud and also adds it to the world.
     */
    public void create() {
        speed = 0.075f;

        shaiHuludScene = new Scene(screen.sceneAssetHashMap.get("low_poly_sandworm/scene.glb").scene);
        shaiHuludScene.modelInstance.transform.setToTranslation(position)
                .scale(0.025f, 0.025f, 0.025f).rotate(new Vector3(0f,1f,0f), rotation);
        screen.addingScene(shaiHuludScene);

        world.addShaiHulud(this);

    }

    /**
     * This method is called every time the GameScreen renders.
     * It moves the ShaiHulud in the given direction with constant speed.
     *
     * @author Mattis Bühler
     */
    public void update() {
        move(speed);

        shaiHuludScene.modelInstance.transform.setToTranslation(position)
                .scale(0.025f, 0.025f, 0.025f).rotate(new Vector3(0f,1f,0f), rotation);

        checkIntersection();

        if ((position.x >= endPosition.x-0.005) && (position.x <= endPosition.x+0.005) &&
                (position.z >= endPosition.y-0.005) && (position.z <= endPosition.y+0.005)) {
            screen.removingScene(shaiHuludScene);
            destroyMe = true;
        }
    }

    public void move(float speed) {
        switch (rotation) {
            //left
            case 0:
                position.x -= speed;
                break;
            //down
            case 90:
                position.z += speed;
                break;
            //right
            case 180:
                position.x += speed;
                break;
            //up
            case 270:
                position.z -= speed;
                break;
        }
    }

    /**
     * Checks if the ShaiHulud is intersecting with an enemy, a turret or a thumper. If so the intersecting object
     * is removed from the world.
     *
     * @author Mattis Bühler
     */
    private void checkIntersection() {
        ArrayList<GameObjectEnemy> enemies = world.getEnemies();
        ArrayList<AbstractTurret> turrets = world.getTurrets();
        ArrayList<Thumper> thumpers = world.getThumpers();

        enemiesInRange.clear();
        turretsInRange.clear();
        thumpersInRange.clear();

        for (GameObjectEnemy enemy : enemies) {

            if (rotation == 0 || rotation == 180) {
                if (isIntersecting(position.x, position.z, enemy.position.x, enemy.position.z)) {
                    enemiesInRange.add(enemy);
                }
            } else {
                if (isIntersecting(position.z, position.x, enemy.position.z, enemy.position.x)) {
                    enemiesInRange.add(enemy);
                }
            }
        }

        for (AbstractTurret turret : turrets) {
            if (rotation == 0 || rotation == 180) {
                if (isIntersecting(position.x, position.z, turret.position.x, turret.position.y)) {
                    turretsInRange.add(turret);
                }
            } else {
                if (isIntersecting(position.z, position.x, turret.position.y, turret.position.x)) {
                    turretsInRange.add(turret);
                }
            }
        }

        for (Thumper thumper : thumpers) {
            if (rotation == 0 || rotation == 180) {
                if (isIntersecting(position.x, position.z, thumper.position.x, thumper.position.y)) {
                    thumpersInRange.add(thumper);
                }
            } else {
                if (isIntersecting(position.z, position.x, thumper.position.y, thumper.position.x)) {
                    thumpersInRange.add(thumper);
                }
            }
        }

        for (GameObjectEnemy enemy : enemiesInRange) {
            enemy.lifePoints = 0;
        }

        for (AbstractTurret turret : turretsInRange) {
            turret.destroyMe = true;
            turret.destroyMeShaiHulud = true;
            world.addDebris(turret.position.x, turret.position.y);
        }

        for (Thumper thumper : thumpersInRange) {
            thumper.destroyMe();
        }

    }

    /**
     * Checks if one positions is intersecting with the ShaiHulud position.
     * position1 and position2 depend on the direction of the ShaiHulud.
     *
     * @param position1 first coordinate of the ShaiHulud.
     * @param position2 second coordinate of the ShaiHulud.
     * @param ePos1 first coordinate of the checked object.
     * @param ePos2 second coordinate of the checked object.
     *
     * @return if two objects are intersection, one has to be a ShaiHulud.
     */
    private boolean isIntersecting(float position1, float position2, float ePos1, float ePos2) {
        //length of shaiHulud (6.6)
        if (position1-3.3 <= ePos1 && position1+3.3 >= ePos1) {
            //width of shaiHulud(1)
            return position2 - 0.5 <= ePos2 && position2 + 0.5 >= ePos2;
        }
        return false;
    }

    /**
     * Calculates the direction the ShaiHulud will move.
     *
     * @param Pos1 position of the first thumper.
     * @param Pos2 positoin of the second thumper.
     *
     * @return a vector with direction that is formed by the thumpers.
     */
    public Vector2 calculateDirection(Point Pos1, Point Pos2) {
        return new Vector2(Pos2.x-Pos1.x, Pos2.y-Pos1.y);
    }
}
