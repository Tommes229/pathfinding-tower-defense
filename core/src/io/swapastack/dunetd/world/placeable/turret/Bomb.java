package io.swapastack.dunetd.world.placeable.turret;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.enemies.GameObjectEnemy;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Bomb class.
 *
 * @author Mattis Bühler
 */
public class Bomb extends Bullet {

    //explosion radius
    private final float explosionRadius;

    //BombTurret
    private final BombTurret bombTurret;

    /**
     * Constructor of the Bomb class.
     * Calls constructor of upper class.
     *
     * @param enemy the enemy the Bomb will fly towards.
     * @param screen the GameScreen the Bomb exists in.
     * @param world the GameWorld the Bomb exists in.
     * @param position the start position of the Bomb.
     * @param rotation the start rotation of the Bomb.
     * @param bombTurret the belonging BombTurret.
     */
    public Bomb(GameObjectEnemy enemy, GameScreen screen, GameWorld world, Point position, float rotation, BombTurret bombTurret) {
        super(enemy, screen, world, position, rotation, false);
        this.bombTurret = bombTurret;
        projectileSpeed = 0.001f * MainMenuScreen.bombSpeed.get();
        damage = MainMenuScreen.bombturretDamage.get();
        explosionRadius = 1.7f;

        createBombScene();
        setNormalizedVector();
    }

    /**
     * Adds a new Bomb scene with the given values.
     */
    private void createBombScene() {
        projectileScene = new Scene(screen.sceneAssetHashMap.get("bomb_rock/scene.gltf").scene);
        projectileScene.modelInstance.transform.setToTranslation(position).rotate(new Vector3(0.0f, 1.0f, 0.0f), rotation);
        screen.addingScene(projectileScene);
    }

    /**
     * Checks if the Bomb has collided with an enemy in the world.
     *
     * @author Mattis Bühler
     */
    @Override
    protected void checkCollision() {
        for (GameObjectEnemy enemy : enemies) {

            if (world.checkInRange(enemy, enemy.radius, position.x, position.z)) {
                bombTurret.addExplosion(enemy);

                ArrayList<GameObjectEnemy> explodingEnemies = world.getEnemiesInRange(explosionRadius, position.x, position.z);

                for (GameObjectEnemy explodingEnemy : explodingEnemies) {
                    explodingEnemy.lifePoints -= damage;
                }

                screen.removingScene(projectileScene);
                destroyMe = true;
            }
        }
    }
}
