package io.swapastack.dunetd.world.placeable.turret;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The BombTurret class.
 *
 * @author Mattis Bühler
 */
public class BombTurret extends GunTurret {

    //AnimationControllers
    private final ArrayList<AnimationController> explosionAnimations = new ArrayList<>();

    //Scene
    private final ArrayList<Scene> explosionScenes = new ArrayList<>();

    /**
     * Constructor of the BombTurret class.
     * Call of the create method.
     *
     * @param position the position of the BombTurret.
     * @param world the GameWorld in which the BombTurret exists in.
     * @param screen the GameScreen in which the BombTurret exists in.
     */
    public BombTurret(Point position, GameWorld world, GameScreen screen) {
        super(position, world, screen, false);
        this.position = position;
        create();
    }

    /**
     * Adds a new BombTurret scene with the given values.
     */
    public void create() {
        radius = 0.01f * MainMenuScreen.bombturretRadius.get();
        startTime = TimeUtils.millis();
        scaling = new Vector3(1.5f,1.5f,1.5f);
        delayMS = (1000/MainMenuScreen.bombturretFrequence.get());

        turretScene = new Scene(screen.sceneAssetHashMap.get("weapon_cannon.glb").scene);
        turretScene.modelInstance.transform.setToTranslation(new Vector3(position.x, screen.groundTileDimensions.y, position.y)).scl(scaling);
        screen.addingScene(turretScene);
    }

    /**
     * updates the animationControllers.
     *
     * @param delta the time since the last rendering.
     *
     * @author Mattis Bühler
     */
    public void update(float delta) {
        super.update(delta);
        for (AnimationController explosionAnimation : explosionAnimations) {
            explosionAnimation.update(delta);
        }
    }

    /**
     * Adds a new Bomb at the position of the turret to the GameWorld, which will fly to the given enemy.
     *
     * @param enemy the enemy the bomb will fly to.
     *
     * @author Mattis Bühler
     */
    @Override
    public void shoot(GameObjectEnemy enemy) {
        Bomb bomb = new Bomb(enemy, screen, world, position, rotation, this);
        world.addBullet(bomb);
    }

    /**
     * Adds an explosion animation at the position of the enemy
     *
     * @param enemy the enemy, the explosion will be added at.
     *
     * @author Mattis Bühler
     */
    public void addExplosion(GameObjectEnemy enemy) {
        Timer timer = new Timer();

        Scene explosionScene = new Scene(screen.sceneAssetHashMap.get("explosion/explosion.glb").scene);
        Scene explosionScene2 = new Scene(screen.sceneAssetHashMap.get("explosion/explosion.glb").scene);
        Scene explosionScene3 = new Scene(screen.sceneAssetHashMap.get("explosion/explosion.glb").scene);

        explosionScene.modelInstance.transform.setToTranslation(enemy.position).scl(0.2f);
        explosionScene2.modelInstance.transform.setToTranslation(enemy.position).scl(0.2f);
        explosionScene3.modelInstance.transform.setToTranslation(enemy.position).scl(0.2f);

        screen.addingScene(explosionScene);
        screen.addingScene(explosionScene2);
        screen.addingScene(explosionScene3);

        AnimationController.AnimationListener acListener = new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {
            }
        };


        AnimationController acBomb = new AnimationController(explosionScene.modelInstance);
        acBomb.setAnimation("IcosphereAction", 1, 3f, acListener);

        AnimationController acBomb2 = new AnimationController(explosionScene.modelInstance);
        acBomb2.setAnimation("Icosphere.001Action", 1, 3f, acListener);

        AnimationController acBomb3 = new AnimationController(explosionScene3.modelInstance);
        acBomb3.setAnimation("Icosphere.001Action.001", 1, 3f, acListener);


        explosionAnimations.add(acBomb);
        explosionAnimations.add(acBomb2);
        explosionAnimations.add(acBomb3);

        explosionScenes.add(explosionScene);
        explosionScenes.add(explosionScene2);
        explosionScenes.add(explosionScene3);

        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                removeExplosion();
                removeExplosion();
                removeExplosion();
            }
        }, 0.3f);

    }

    /**
     * Removes all scenes from the SceneManager, also stops loading animations and scene.
     *
     * @author Mattis Bühler
     */
    private void removeExplosion() {
        explosionAnimations.remove(0);
        screen.removingScene(explosionScenes.get(0));
        explosionScenes.remove(0);
    }
}
