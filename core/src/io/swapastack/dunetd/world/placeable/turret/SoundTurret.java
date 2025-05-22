package io.swapastack.dunetd.world.placeable.turret;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * This is the SoundTurret class.
 *
 * @author Mattis Bühler
 */
public class SoundTurret extends AbstractTurret {

    //Enemies
    ArrayList<GameObjectEnemy> enemies;

    //AnimationController
    AnimationController acSoundTurret;

    /**
     * Constructor of the SoundTurret.
     * Calls the create method.
     *
     * @param position the position of the SoundTurret.
     * @param world the GameWorld the SoundTurret exists in.
     * @param screen the GameScreen the SoundTurret exists in.
     */
    public SoundTurret(Point position, GameWorld world, GameScreen screen) {
        super(position, world, screen);
        create();
    }

    /**
     * Adds a new SoundTurret scene with the given values and adds the animationController.
     */
    public void create() {
        radius = 0.01f * MainMenuScreen.soundturretRadius.get();
        turretScene = new Scene(screen.sceneAssetHashMap.get("spaceship_orion/scene.gltf").scene);
        turretScene.modelInstance.transform.setToTranslation(new Vector3(position.x, screen.groundTileDimensions.y, position.y)).scale(0.25f, 0.25f, 0.25f);
        screen.addingScene(turretScene);

        acSoundTurret = new AnimationController(turretScene.modelInstance);
        acSoundTurret.setAnimation("Action", -1);
    }

    /**
     * This method is called everytime the render method of its GameScreen is called as long the game is still running.
     * Sets the state of all enemies in range to isSlowed.
     *
     * @param delta the time since the last rendering.
     *
     * @author Mattis Bühler
     */
    public void update(float delta) {
        super.update(delta);
        enemies = getEnemiesInRange(radius, position.x, position.y);
        for (GameObjectEnemy enemy : enemies) {
            enemy.isSlowed = true;
        }

        if (enemies.size() != 0) {
            acSoundTurret.update(delta);
        }
    }
}

