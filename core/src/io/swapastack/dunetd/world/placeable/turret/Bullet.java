package io.swapastack.dunetd.world.placeable.turret;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Bullet class:
 *
 * @author Mattis Bühler
 */
public class Bullet {

    //GameWorld and GameScreen
    protected GameScreen screen;
    protected GameWorld world;

    //Direction
    protected Vector2 enemyPos;
    protected Vector2 normalVector;

    //Position
    public Vector3 position;
    protected float rotation;

    //Bullet values
    protected float projectileSpeed = 0.001f * MainMenuScreen.bulletSpeed.get();
    protected int damage;

    //Scene
    protected Scene projectileScene;
    private Vector3 scale;

    //Destroy
    public boolean destroyMe;

    //Enemies
    protected ArrayList<GameObjectEnemy> enemies = new ArrayList<>();

    /**
     * The Constructor of the Bullet class.
     * Calls methods to set the direction.
     *
     * @param enemy the enemy, the Bullet flies to.
     * @param screen the GameScreen the Bullet exists in.
     * @param world the GameWorld the Bullet exists in.
     * @param position the start position of the Bullet.
     * @param rotation the rotation of the Bullet.
     * @param bullet if it is a Bullet.
     */
    public Bullet(GameObjectEnemy enemy, GameScreen screen, GameWorld world, Point position, float rotation, boolean bullet) {
        this.screen = screen;
        this.world = world;
        enemyPos = new Vector2(enemy.getX(), enemy.getZ());
        this.position = new Vector3(position.x, screen.groundTileDimensions.y+0.3f, position.y);
        this.rotation = rotation;
        damage = MainMenuScreen.gunturretDamage.get();
        scale = new Vector3(0.01f, 0.01f, 0.01f);

        if (bullet) {
            createBulletScene();
        }
        setNormalizedVector();
    }

    /**
     * Constructor for testing purposes.
     *
     * @param position the position of the bullet.
     */
    public Bullet(Vector3 position) {
        this.position = position;
    }

    /**
     * Adds a new Bullet scene at the given values.
     */
    private void createBulletScene() {
        projectileScene = new Scene(screen.sceneAssetHashMap.get("red_shot/scene.gltf").scene);
        projectileScene.modelInstance.transform.setToTranslation(position).rotate(new Vector3(0.0f, 1.0f, 0.0f), rotation).scl(scale)
                .rotate(new Vector3(0f, 1.0f, 0f), rotation-90f);
        screen.addingScene(projectileScene);

    }

    /**
     * Gets the vector with the length 1 from start position of the bullet to the current enemy position and
     * save it in normal Vector
     *
     * @author Mattis Bühler
     */
    protected void setNormalizedVector() {
        //pythagoras theorem
        float length = (float) Math.sqrt(Math.pow((enemyPos.x-position.x), 2)+Math.pow((enemyPos.y-position.z), 2));
        normalVector = new Vector2(((enemyPos.x-position.x)/length), ((enemyPos.y-position.z)/length));
    }

    /**
     * This method updates everytime the render method of its GameScreen is called.
     * Moves the Bullet in the direction of the normalizedVector with a constant speed.
     * Removes the Bullet if it collides with en enemy.
     *
     * @author Mattis Bühler
     */
    public void update() {
        position.x += normalVector.x * projectileSpeed;
        position.z += normalVector.y * projectileSpeed;

        projectileScene.modelInstance.transform.setToTranslation(position).scl(scale)
                .rotate(new Vector3(0f, 1.0f, 0f), rotation-90f);

        enemies = world.getEnemies();
        checkCollision();

        //remove bullet, if out of map
        if (position.x > world.cols + 20 || position.x < -20 || position.z > world.rows + 20 || position.z < -20) {
            screen.removingScene(projectileScene);
            destroyMe = true;
        }

    }

    /**
     * Checks if the collides with an enemy on the field, if so the enemy will get damage.
     *
     * @author Mattis Bühler
     */
    protected void checkCollision() {
        for (GameObjectEnemy enemy : enemies) {

            if (world.checkInRange(enemy, enemy.radius, position.x, position.z)) {
                screen.removingScene(projectileScene);
                destroyMe = true;
                enemy.lifePoints -= damage;
            }
        }
    }
}
