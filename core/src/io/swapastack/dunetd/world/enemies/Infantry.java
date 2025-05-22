package io.swapastack.dunetd.world.enemies;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;

/**
 * The Infantry class.
 * It extends the GameObjectEnemy class.
 *
 * @author Mattis Bühler
 */
public class Infantry extends GameObjectEnemy {

    //AnimationController
    private AnimationController acInfantry;

    /**
     * Constructor of the Infantry class.
     * Calls the constructor of it's super class.
     * Calls the create method.
     *
     * @param startPoint the position, the Infantry will start from.
     * @param endPoint the position, the Infantry will run to.
     * @param world the world in which the Infantry exists.
     * @param screen the screen in which the Infantry exists.
     */
    public Infantry(Point startPoint, Point endPoint, GameWorld world, GameScreen screen) {
        super(startPoint, endPoint, world, screen);
        create();
    }


    /**
     * The create method which sets the values of the object, creates the infantry scene and the animationController.
     */
    public void create() {
        speed = 0.0001f * MainMenuScreen.infantrySpeed.get();
        slowedSpeed = speed*(1-slowPercentage);
        lifePoints = MainMenuScreen.infantryLife.get();
        damageToPlayer = MainMenuScreen.infantryDamage.get();
        scoreValue = MainMenuScreen.infantryScore.get();
        spiceValue = MainMenuScreen.infantrySpice.get();

        scaling = new Vector3(0.028f, 0.070f, 0.028f);

        enemyScene = new Scene(screen.sceneAssetHashMap.get("cute_cyborg/scene.gltf").scene);
        enemyScene.modelInstance.transform.setToTranslation(getX(), screen.groundTileDimensions.y, getZ()).scl(scaling);
        screen.addingScene(enemyScene);

        radius = 0.3f;


        acInfantry = new AnimationController(enemyScene.modelInstance);
        acInfantry.setAnimation("RUN", -1);

        setPath(world.getField());

    }

    /**
     * This method is called every time the GameScreen renders, if the Infantry exists in the world.
     * Updates the Infantry object, it's scene, it's animationController, and it's upper class.
     *
     * @param delta The time in seconds since the last render.
     *
     * @author Mattis Bühler
     */
    public void update(float delta) {
        super.update(delta);

        isSlowed = false;

        invertRotation();

        enemyScene.modelInstance.transform.setToTranslation(getX(), screen.groundTileDimensions.y, getZ()).scl(scaling)
                .rotate(new Vector3(0.0f, 1.0f, 0.0f), getRotation());

        acInfantry.update(delta);
    }

    //robot-model looks in the opposite direction, so the rotation has to be inverted
    private void invertRotation() {
        rotation = rotation + 180;
    }


}
