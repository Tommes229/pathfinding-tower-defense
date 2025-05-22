package io.swapastack.dunetd.world.placeable.shaiHulud;

import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.world.placeable.GameObjectPlaceable;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;

/**
 * The Thumper class.
 *
 * @author Mattis Bühler
 */
public class Thumper extends GameObjectPlaceable {

    //Thumper variable
    private static boolean secondThumper = true;

    //Thumper positions
    private static Point positionT1;
    private static Point positionT2;

    //Scene
    private Scene thumperScene = null;

    /**
     * Constructor of the Thumper class.
     * Creates a scene for the Thumper and adds the object to the world.
     * Also checks if it's the first or the second Thumper.
     * If it's the second Thumper a ShaiHulud will be added to the world.
     *
     * @param position the position of the Thumper.
     * @param world the GameWorld in which the Thumper exists.
     * @param screen the GameScreen in which the Thumper exists.
     *
     * @author Mattis Bühler
     */
    public Thumper(Point position, GameWorld world, GameScreen screen) {
        super(position, world, screen);

        secondThumper = !secondThumper;

        thumperScene = new Scene(screen.sceneAssetHashMap.get("thumper_dune/scene.gltf").scene);
        thumperScene.modelInstance.transform.setToTranslation(new Vector3(position.x, screen.groundTileDimensions.y+0.15f, position.y)).scl(1.5f)
                .rotate(new Vector3(1.0f, 0.0f, 0.0f), -20f);
        screen.addingScene(thumperScene);

        if (!secondThumper) {
            positionT1 = position;
        }

        if (secondThumper) {
            positionT2 = position;
            addShaiHulud();
        }
    }

    /**
     * Constructor for testing purposes.
     *
     * @param positionT1 the position of the first thumper.
     */
    public Thumper(Point positionT1) {
        this.positionT1 = positionT1;
    }

    /**
     * Adds a new ShaiHulud to the world.
     */
    private void addShaiHulud() {
        new ShaiHulud(positionT1, positionT2, world, screen);
    }

    /**
     * Checks if the given position is a possible position for a Thumper.
     *
     * @param xPos x-coordinate of the position.
     * @param yPos y-coordinate of the position.
     *
     * @return if position is a possible position for a Thumper object.
     */
    public boolean isPossiblePosition(int xPos, int yPos) {
        return (xPos == positionT1.x || yPos == positionT1.y) && !(xPos == positionT1.x && yPos == positionT1.y);
    }

    /**
     * Destroys the Thumpers scene and removes it from the world.
     */
    public void destroyMe() {
        screen.removingScene(thumperScene);
        secondThumper = true;
    }

}
