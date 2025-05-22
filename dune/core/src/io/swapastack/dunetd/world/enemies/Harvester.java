package io.swapastack.dunetd.world.enemies;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;

/**
 * The Harvester class.
 * It extends the GameObjectEnemy class.
 *
 * @author Mattis Bühler
 */
public class Harvester extends GameObjectEnemy {

    /**
     * Constructor of the Harvester class.
     * Calls the constructor of it's upper class.
     * Calls the create method.
     *
     * @param startPoint the position, the Harvester will start from.
     * @param endPoint the position, the Harvester will run to.
     * @param world the world in which the Harvester exists.
     * @param screen the screen in which the Harvester exists.
     */
    public Harvester(Point startPoint, Point endPoint, GameWorld world, GameScreen screen) {
        super(startPoint, endPoint, world, screen);
        create();
    }

    /**
     * The create method which sets the values of the object, creates the harvester scene
     */
    public void create() {
        speed = 0.0001f * MainMenuScreen.harvesterSpeed.get();
        slowedSpeed = speed;
        lifePoints = MainMenuScreen.harvesterLife.get();
        damageToPlayer = MainMenuScreen.harvesterDamage.get();
        scoreValue = MainMenuScreen.harvesterScore.get();
        spiceValue = MainMenuScreen.harvesterSpice.get();

        scaling = new Vector3(0.7f, 0.8f, 0.7f);

        enemyScene = new Scene(screen.sceneAssetHashMap.get("enemy_ufoPurple.glb").scene);
        enemyScene.modelInstance.transform.setToTranslation(getX(), screen.groundTileDimensions.y+0.5f, getZ()).scale(scaling.x, scaling.y, scaling.z);
        screen.addingScene(enemyScene);

        radius = 0.3f;

        setPath(world.getField());

    }

    /**
     * This method is called every time the GameScreen renders, if the BossUnit exists in the world.
     * Updates the BossUnit object, it's scene, it's animationController, and it's upper class.
     *
     * @param delta The time in seconds since the last render.
     *
     * @author Mattis Bühler
     */
    public void update(float delta) {
        super.update(delta);

        isSlowed = false;

        enemyScene.modelInstance.transform.setToTranslation(getX(), screen.groundTileDimensions.y+0.5f, getZ()).scale(scaling.x, scaling.y, scaling.z)
                .rotate(new Vector3(0.0f, 1.0f, 0.0f), getRotation());
    }
}