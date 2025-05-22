package io.swapastack.dunetd.world.placeable.shaiHulud;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.world.GameWorld;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;

/**
 * The Debris class.
 *
 * @author Mattis Bühler
 */
public class Debris {
    //GameScreen and GameWorld
    private final GameScreen screen;
    private final GameWorld world;

    //Position
    private final Point position;

    //Scene
    private Scene debrisScene;

    /**
     * The Constructor of the Debris class.
     * Sets the position the Debris will be placed.
     * Calls the create method.
     *
     * @param screen the GameScreen the Debris object exists in.
     * @param world the GameWorld the Debris object exists in.
     * @param xPos the x-coordinate of the debris position.
     * @param yPos the y-coordinate of the debris position.
     */
    public Debris(GameScreen screen, GameWorld world, int xPos, int yPos) {
        this.screen = screen;
        this.world = world;
        position = new Point(xPos, yPos);
        create();
    }

    /**
     * The method creates the debris scene at the position of the object.
     */
    public void create() {
        debrisScene = new Scene(screen.sceneAssetHashMap.get("a_pile_of_sand/scene.gltf").scene);
        debrisScene.modelInstance.transform.setToTranslation(position.x+0.1f, screen.groundTileDimensions.y-0.2f, position.y-0.05f).
                scale(0.4f, 0.005f, 0.003f).rotate(new Vector3(1f,0f,0f), 63);
        screen.addingScene(debrisScene);
    }

    /**
     * The method destroys the debris and removes its scene and removes it from the world.
     */
    public void destroyDebris() {
        screen.removingScene(debrisScene);
        world.setUnmanned(position.x, position.y);
    }
}
