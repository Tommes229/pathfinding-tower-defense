package io.swapastack.dunetd.world.enemies;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The BossUnit class.
 * It extends the GameObjectEnemy class.
 *
 * @author Mattis Bühler
 */
public class BossUnit extends GameObjectEnemy {

    //AnimationController
    private AnimationController acBoss;

    /**
     * Constructor of the BossUnit class.
     * Calls the constructor of it's upper class.
     * Calls the create method.
     *
     * @param startPoint the position, the BossUnit will start from.
     * @param endPoint the position, the BossUnit will run to.
     * @param world the world in which the BossUnit exists.
     * @param screen the screen in which the BossUnit exists.
     */
    public BossUnit(Point startPoint, Point endPoint, GameWorld world, GameScreen screen) {
        super(startPoint, endPoint, world, screen);
        create();
    }

    /**
     * Constructor for testing purposes.
     * Calls the constructor of it's upper class.
     *
     * @param position the position of the BossUnit in the world.
     */
    public BossUnit(Vector3 position) {
        super(position);
        radius = 0.3f;
    }

    /**
     * Constructor for testing purposes.
     * Calls the constructor of it's upper class.
     *
     * @param position the position of the BossUnit in the world.
     * @param path the path, the BossUnit has to walk, to reach the endportal.
     */
    public BossUnit(Vector3 position, ArrayList<Point> path) {
        super(position, path);
    }


    /**
     * The create method which sets the values of the object, creates the bossUnit scene and the animationController.
     */
    public void create() {
        speed = 0.0001f * MainMenuScreen.bossunitSpeed.get();
        slowedSpeed = speed*(1-slowPercentage);
        lifePoints = MainMenuScreen.bossunitLife.get();
        damageToPlayer = MainMenuScreen.bossunitDamage.get();
        scoreValue = MainMenuScreen.bossunitScore.get();
        spiceValue = MainMenuScreen.bossunitSpice.get();

        scaling = new Vector3(0.005f,0.005f,0.005f);

        enemyScene = new Scene(screen.sceneAssetHashMap.get("faceted_character/scene.gltf").scene);
        enemyScene.modelInstance.transform.setToTranslation(getX(), screen.groundTileDimensions.y, getZ()).scl(scaling);
        screen.addingScene(enemyScene);

        radius = 0.3f;

        acBoss = new AnimationController(enemyScene.modelInstance);
        acBoss.setAnimation("Armature|Run", -1);

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

        enemyScene.modelInstance.transform.setToTranslation(getX(), screen.groundTileDimensions.y, getZ()).scl(scaling)
                .rotate(new Vector3(0.0f, 1.0f, 0.0f), getRotation());

        acBoss.update(delta);
    }
}