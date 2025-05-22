package io.swapastack.dunetd.world;

import io.swapastack.dunetd.world.placeable.shaiHulud.Debris;
import io.swapastack.dunetd.world.enemies.BossUnit;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.enemies.Harvester;
import io.swapastack.dunetd.world.enemies.Infantry;
import io.swapastack.dunetd.world.field.Field;
import io.swapastack.dunetd.world.placeable.shaiHulud.ShaiHulud;
import io.swapastack.dunetd.world.placeable.shaiHulud.Thumper;
import io.swapastack.dunetd.world.placeable.turret.*;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.sqrt;

/**
 * The GameWorld class.
 *
 * @author Mattis Bühler
 */
public class GameWorld {

    // GameScreen of the gameWorld
    private GameScreen screen = null;

    // Field
    public final int rows = MainMenuScreen.height.get();
    public final int cols = MainMenuScreen.width.get();
    private Field field;

    // Lists which need to be updated
    private final ArrayList<GameObjectEnemy> enemies = new ArrayList<>();
    private final ArrayList<AbstractTurret> turrets = new ArrayList<>();
    public final ArrayList<Thumper> thumpers = new ArrayList<>();
    private final ArrayList<ShaiHulud> shaiHuluds = new ArrayList<>();
    private final ArrayList<Debris> debris = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    // Start- and end-position
    public Point startPoint = null;
    public Point endPoint = null;

    // Player stats
    public int playerLife = MainMenuScreen.playerLife.get();
    public int spice = MainMenuScreen.startSpice.get();
    public int score = 0;

    /**
     * Constructor which creates a random startPoint on left wall of the field and a random endPoint on the right wall of the field.
     * Also calls the create method.
     *
     * @param screen the screen, the world is created in.
     * @author Mattis Bühler
     */
    public GameWorld(GameScreen screen) {
        startPoint = new Point(0,((int)(Math.random() * rows)));
        endPoint = new Point((cols-1),((int)(Math.random() * rows)));
        this.screen = screen;

        create();
    }

    /**
     * Constructor for testing purposes
     */
    public GameWorld() {}

    /**
     * Is called in the constructor.
     * Creates a new field with the given dimensions and adds the start- and endportal to the scene.
     *
     * @author Mattis Bühler
     */
    public void create() {
        field = new Field(rows, cols);

        Scene startPortal = new Scene(screen.sceneAssetHashMap.get("towerRound_base.glb").scene);
        startPortal.modelInstance.transform.setToTranslation(startPoint.x, screen.groundTileDimensions.y, startPoint.y);
        screen.addingScene(startPortal);

        Scene endPortal = new Scene(screen.sceneAssetHashMap.get("towerRound_crystals.glb").scene);
        endPortal.modelInstance.transform.setToTranslation(endPoint.x, screen.groundTileDimensions.y, endPoint.y);
        screen.addingScene(endPortal);


        setManned(startPoint.x, startPoint.y);
        setManned(endPoint.x, endPoint.y);

    }

    /**
     * This method is called every time the GameScreen renders.
     * Updates all objects that are in the world, but also removes them from the update method if needed.
     * Also tracks the player's life.
     *
     * @param delta The time in seconds since the last render.
     */
    public void update(float delta) {
        //stop updating removed enemies
        for (int i = 0; i<enemies.size(); i++) {
            enemies.get(i).update(delta);
            if (enemies.get(i).destroyMe) {
                enemies.remove(enemies.get(i));
            }
        }

        //update turrets
        for (int i = 0; i<turrets.size(); i++) {
            turrets.get(i).update(delta);
            if (turrets.get(i).destroyMe) {
                turrets.remove(turrets.get(i));
            }
        }

        //update shai huluds
        for (int i = 0; i<shaiHuluds.size(); i++) {
            shaiHuluds.get(i).update();
            if (shaiHuluds.get(i).destroyMe) {
                shaiHuluds.remove(shaiHuluds.get(i));
            }
        }

        //update bullets
        for (int i = 0; i<bullets.size(); i++) {
            bullets.get(i).update();
            if (bullets.get(i).destroyMe) {
                bullets.remove(bullets.get(i));
            }
        }

        if (playerLife <= 0) {
            screen.endGame(false);
        }
    }

    /**
     * Searches for enemies that are in range of the given radius and the given point and returns them in an arraylist.
     *
     * @param radius the radius in which should be searched
     * @param xPos the x coordinate of the center
     * @param yPos the y coordinate of the center
     *
     * @return the arraylist with the enemies that are in range of the given parameters.
     *
     * @author Mattis Bühler
     */
    public ArrayList<GameObjectEnemy> getEnemiesInRange(float radius, float xPos, float yPos) {

        ArrayList<GameObjectEnemy> enemiesInRange = new ArrayList<>();

        for (GameObjectEnemy enemy : enemies) {
            if (checkInRange(enemy, radius, xPos, yPos)) {
                enemiesInRange.add(enemy);
            }
        }
        return enemiesInRange;
    }

    /**
     * Checks weather an enemy is in range of the radius or not with the pythagorean theorem.
     *
     * @param enemy the enemy which is checked.
     * @param radius the radius in which the enemy could be.
     * @param xPos the x coordinate of the center of the area.
     * @param yPos the y coordinate of the  center of the area.
     *
     * @return a boolean weather the enemy is in range or not.
     *
     * @author Mattis Bühler
     */
    public boolean checkInRange(GameObjectEnemy enemy, float radius, float xPos, float yPos) {
        float distance;
        //pythagorean theorem to get distance
        distance = (float) sqrt(((xPos - enemy.getX()) * (xPos - enemy.getX())) + ((yPos - enemy.getZ()) * (yPos - enemy.getZ())));

        return distance <= radius;
    }

    /**
     * Returns the field of the world.
     *
     * @return the field of the type Field.
     */
    public Field getField() {
        return field;
    }

    /**
     * Returns all enemies that are updated in the world.
     *
     * @return the enemies of the world
     */
    public ArrayList<GameObjectEnemy> getEnemies() {
        return enemies;
    }

    /**
     * Returns all turrets that are updated in the world
     *
     * @return the turrets of the world
     */
    public ArrayList<AbstractTurret> getTurrets() {
        return turrets;
    }

    /**
     * Sets a tile on the field to the state manned.
     *
     * @param xCoord the x coordinate of the tile.
     * @param yCoord the y coordinate of the tile.
     */
    public void setManned(int xCoord, int yCoord) {
        field.mannedTiles[xCoord][yCoord] = true;
    }

    /**
     * Sets a tile on the field to the state unmanned.
     *
     * @param xCoord the x coordinate of the tile.
     * @param yCoord the y coordinate of the tile.
     */
    public void setUnmanned(int xCoord, int yCoord) {
        field.mannedTiles[xCoord][yCoord] = false;
    }

    /**
     * Adds a new enemy of the type BossUnit to the world.
     */
    public void addBossUnit() {
        enemies.add(new BossUnit(startPoint, endPoint,this, screen));
    }

    /**
     * Adds a new enemy of the type Infantry to the world.
     */
    public void addInfantry() {
        enemies.add(new Infantry(startPoint, endPoint,this, screen));
    }

    /**
     * Adds a new enemy of the type Harvester to the world.
     */
    public void addHarvester() {
        enemies.add(new Harvester(startPoint, endPoint,this, screen));
    }

    /**
     * Returns the manned state of the field.
     *
     * @return an array with the manned states
     */
    public boolean[][] getMannedTiles() {
        return field.mannedTiles;
    }

    /**
     * Returns the fastest path from the start- to the endpoint on the field. Without going diagonal and without
     * visiting manned tiles.
     *
     * @return an arraylist of the points of path in order
     */
    public ArrayList<Point> getPath() {
        return field.findWay(startPoint, endPoint);
    }

    /**
     * Adds a new turret of the type Soundturret to the field at the given position.
     *
     * @param xPos the x coordinate of the tile, the new turret will stand on.
     * @param yPos the y coordinate of the tile, the new turret will stand on.
     */
    public void addSoundTurret(int xPos, int yPos) {
        turrets.add(new SoundTurret(new Point(xPos, yPos), this, screen));
    }

    /**
     * Adds a new turret of the type GunTurret to the field at the given position.
     *
     * @param xPos the x coordinate of the tile, the new turret will stand on.
     * @param yPos the y coordinate of the tile, the new turret will stand on.
     */
    public void addGunTurret(int xPos, int yPos) {
        turrets.add(new GunTurret(new Point(xPos, yPos), this, screen, true));
    }

    /**
     * Adds a new turret of the type BombTurret to the field at the given position.
     *
     * @param xPos the x coordinate of the tile, the new turret will stand on.
     * @param yPos the y coordinate of the tile, the new turret will stand on.
     */
    public void addBombTurret(int xPos, int yPos) {
        turrets.add(new BombTurret(new Point(xPos, yPos), this, screen));
    }

    /**
     * Removes a turret from the field at the given position.
     *
     * @param xPos the x coordinate of the tile the turret stands on.
     * @param yPos the y coordinate of the tile the turret stands on.
     *
     * @return the turret which is removed.
     */
    public AbstractTurret destroyTurret(int xPos, int yPos) {
        for (AbstractTurret turret : turrets) {
            if (turret.position.x == xPos && turret.position.y == yPos) {
                turret.destroyMe = true;
                return turret;
            }
        }
        return null;
    }

    /**
     * Adds a new thumper to the field at the given position.
     *
     * @param xPos the x coordinate of the tile, the thumper will stand on.
     * @param yPos the y coordinate of the tile, the thumper will stand on.
     */
    public void addThumper(int xPos, int yPos) {
        thumpers.add(new Thumper(new Point(xPos, yPos), this, screen));
    }

    /**
     * Adds a shaiHulud to the world, which then will be updated.
     *
     * @param shai the ShaiHulud which will be added to the world.
     */
    public void addShaiHulud(ShaiHulud shai) {
        shaiHuluds.add(shai);
    }

    /**
     * Adds a new debris to the field at the given position.
     *
     * @param xPos the x coordinate of the tile, the debris will stand on.
     * @param yPos the y coordinate of the tile, the debris wills stand on.
     */
    public void addDebris(int xPos, int yPos) {
        debris.add(new Debris(screen, this, xPos, yPos));
    }

    /**
     * Removes all debris that currently exist in the world from it.
     */
    public void removeDebris() {
        for (int i = 0; i<debris.size(); i++) {
            debris.get(i).destroyDebris();
            debris.remove(debris.get(i));
        }
    }

    /**
     * Returns all the thumpers that currently exist in the world.
     *
     * @return the arraylist of the thumpers of the world.
     */
    public ArrayList<Thumper> getThumpers() {
        return thumpers;
    }

    /**
     * Adds a bullet to the world, which now will be updated.
     *
     * @param bullet the bullet which will be updated now.
     */
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

}