package io.swapastack.dunetd.world.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import io.swapastack.dunetd.world.placeable.turret.AbstractTurret;
import io.swapastack.dunetd.world.placeable.turret.BombTurret;
import io.swapastack.dunetd.world.placeable.turret.SoundTurret;
import io.swapastack.dunetd.world.GameWorld;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.awt.*;
import java.util.ArrayList;

/**
 * The BuyingHandler class.
 *
 * @author Mattis Bühler
 */
public class BuyingHandler {

    //GameScreen and GameWorld
    private final GameScreen screen;
    private final GameWorld world;

    //Scene
    private Scene placeableScene;
    private boolean sceneExists;
    private final Vector3 position = new Vector3();
    private final Vector3 scaling = new Vector3();

    //Begin of first phase
    public boolean firstTowerPlaced = false;

    //Buying
    private int kindOfButton;
    private boolean turretBought = false;
    private boolean thumpersCleared = false;
    private Vector3 mousePosition = new Vector3();

    //Turret costs
    private final int soundturretCost = MainMenuScreen.soundturretCost.get();
    private final int gunturretCost = MainMenuScreen.gunturretCost.get();
    private final int bombturretCost = MainMenuScreen.bombturretCost.get();
    private final int thumperCost = MainMenuScreen.thumperCost.get();

    //Sound
    private final Sound error;

    /**
     * Constructor of the BuyingHandler.
     *
     * @param screen the GameScreen the BuyingHandler exists in.
     * @param world the GameWorld the BuyingHandler exists in.
     */
    public BuyingHandler(GameScreen screen, GameWorld world) {
        this.screen = screen;
        this.world = world;
        error = Gdx.audio.newSound(Gdx.files.internal("sounds/error.wav"));
    }

    /**
     * This method updates everytime its GameScreen renders if the game is still running.
     * Checks if the current phase is the building phase, if it is building phase it calls the other methods in this
     * class so that it is possible for the player to buy turrets and thumpers.
     *
     * @author Mattis Bühler
     */
    public void update() {
        boolean phase = screen.getPhase();

        if (phase) {
            world.removeDebris();

            //checks if the current button is set on "nothing"
            if (screen.getTurret() == 0) {
                //remove Scene in fighting phase
                if (sceneExists) {
                    removeScene();
                }

            } else {
                mousePosition = screen.getCursorCoords();

                //check if mouse is inside the field
                if (cursorInField()) {
                    kindOfButton = screen.getTurret();
                    if (kindOfButton == 4) {
                        sellATurret();
                    } else if (kindOfButton == 5) {
                        buyThumper();
                    } else {
                        buyATurret();
                    }
                } else {
                    removeScene();
                }
            }

            if (thumpersCleared) {
                thumpersCleared = false;
            }
        }

        if (!phase) {
            mousePosition = screen.getCursorCoords();
            kindOfButton = screen.getTurret();

            if (cursorInField()) {
                if (kindOfButton == 5) {
                    buyThumper();
                }
            } else {
                removeScene();
            }

            if (!thumpersCleared) {
                thumpersCleared = true;
                if (world.thumpers.size() != 0) {
                    world.thumpers.get(0).destroyMe();
                }
                world.thumpers.clear();
            }
        }
    }

    /**
     * Checks weather the Cursor is in the Field or not.
     *
     * @return if the cursor is in the field.
     *
     * @author Mattis Bühler
     */
    private boolean cursorInField() {
        return -0.5f <= mousePosition.x && mousePosition.x <= (screen.getDimension().x - 0.5) && -0.5f <= mousePosition.z
                && mousePosition.z <= (screen.getDimension().y - 0.5);
    }

    /**
     * Checks if there is a mouse click, if there is a turret on the tile, the cursor hovers, which can be sold,
     * if so the turret is sold and the player gets half of the spice it costs.
     *
     * @author Mattis Bühler
     */
    private void sellATurret() {
        AbstractTurret selledTurret;

        int mouseX = Math.round(mousePosition.x);
        int mouseY = Math.round(mousePosition.z);

        if (world.getMannedTiles()[mouseX][mouseY]
                && !(world.startPoint.x == mouseX && world.startPoint.y == mouseY)
                && !(world.endPoint.x == mouseX && world.endPoint.y == mouseY)) {

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                selledTurret = world.destroyTurret(mouseX, mouseY);
                if (selledTurret instanceof SoundTurret) {
                    world.spice += (soundturretCost/2);
                } else if (selledTurret instanceof BombTurret) {
                    world.spice += (bombturretCost/2);
                } else {
                    world.spice += (gunturretCost/2);
                }
            }
        }
    }

    /**
     * Checks what kind of button is select, the scene of the selected turret will hover with the cursor if it
     * is possible to place a turret on the hovered tile.
     *
     * @author Mattis Bühler
     */
    private void buyATurret() {
        if (!sceneExists) {

            switch(kindOfButton) {
                case 1:
                    placeableScene = new Scene(screen.sceneAssetHashMap.get("spaceship_orion/scene.gltf").scene);
                    scaling.x = 0.25f;
                    scaling.y = 0.25f;
                    scaling.z = 0.25f;
                    break;
                case 2:
                    placeableScene = new Scene(screen.sceneAssetHashMap.get("weapon_blaster.glb").scene);
                    scaling.x = 1f;
                    scaling.y = 1f;
                    scaling.z = 1f;
                    break;
                case 3:
                    placeableScene = new Scene(screen.sceneAssetHashMap.get("weapon_cannon.glb").scene);
                    scaling.x = 1.5f;
                    scaling.y = 1.5f;
                    scaling.z = 1.5f;
                    break;
            }

            position.x = -1;
            position.y = mousePosition.y;
            position.z = -1;

            placeableScene.modelInstance.transform.setToTranslation(position).scl(scaling);
            screen.addingScene(placeableScene);
            sceneExists = true;
        }

        //check if field is manned
        if (!world.getMannedTiles()[Math.round(mousePosition.x)][Math.round(mousePosition.z)]) {

            //set field manned
            world.setManned(Math.round(mousePosition.x), Math.round(mousePosition.z));

            //get path
            ArrayList<Point> path = world.getPath();

            //if path not empty move Scene to position
            if (!path.isEmpty()) {

                //round x and y value to get the middle of the tile
                position.x = Math.round(mousePosition.x);
                position.z = Math.round(mousePosition.z);

                placeableScene.modelInstance.transform.setToTranslation(position).scl(scaling);
                checkBuy();
            } else {
                removeScene();
                turretBought = false;
            }

            if (!turretBought) {
                world.setUnmanned(Math.round(mousePosition.x), Math.round(mousePosition.z));
            }

        } else {
            removeScene();
        }
    }

    /**
     * Checks if there is enough spice to buy the selected turret.
     * If there is enough spice and the mouse is clicked, a new turret will be bought and be added at the given position
     * to the world. The player will lose spice of the amount of the turret cost.
     *
     * @author Mattis Bühler
     */
    private void checkBuy() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            switch (kindOfButton) {
                case 1:
                    if (world.spice >= soundturretCost) {
                        world.addSoundTurret((int) position.x, (int) position.z);
                        world.spice -= soundturretCost;
                        turretBought = true;

                    } else {
                        turretBought = false;
                        error.play();
                    }
                    break;
                case 2:
                    if (world.spice >= gunturretCost) {
                        world.addGunTurret((int) position.x, (int) position.z);
                        world.spice -= gunturretCost;
                        turretBought = true;

                    } else {
                        turretBought = false;
                        error.play();
                    }
                    break;
                case 3:
                    if (world.spice >= bombturretCost) {
                        world.addBombTurret((int)position.x, (int)position.z);
                        world.spice -= bombturretCost;
                        turretBought = true;

                    } else {
                        turretBought = false;
                        error.play();
                    }
                    break;
                case 5:
                    if (world.spice >= thumperCost) {
                        world.addThumper((int) position.x, (int) position.z);
                        world.spice -= thumperCost;
                        turretBought = true;
                    } else {
                        turretBought = false;
                        error.play();
                    }
            }

            if (!firstTowerPlaced && turretBought) {
                firstTowerPlaced = true;
                screen.phaseHandler.startBuilding();
            }

            if (kindOfButton != 5 && turretBought) {
                world.setManned((int) position.x, (int) position.z);
            }

        } else {
            turretBought = false;
        }
    }

    /**
     * Removes the "preview" scene.
     *
     * @author Mattis Bühler
     */
    public void removeScene() {
        //remove scene if the cursor is not inside the field
        if(sceneExists) {
            screen.removingScene(placeableScene);
            sceneExists = false;
        }
    }

    /**
     * Checks if it is possible to buy a thumper at the mouse position, it will hover a Thumper scene if it is possible.
     *
     * @author Mattis Bühler
     */
    private void buyThumper() {
        if (!sceneExists) {
            placeableScene = new Scene(screen.sceneAssetHashMap.get("thumper_dune/scene.gltf").scene);
            scaling.x = 1.5f;
            scaling.y = 1.5f;
            scaling.z = 1.5f;

            position.x = -1;
            position.y = mousePosition.y+0.15f;
            position.z = -1;

            placeableScene.modelInstance.transform.setToTranslation(position).scl(scaling);
            screen.addingScene(placeableScene);
            sceneExists = true;
        }

        int posX = (Math.round(mousePosition.x));
        int posY = (Math.round(mousePosition.z));

        if (!(posX == world.startPoint.x && posY == world.startPoint.y)
                && !(posX == world.endPoint.x && posY == world.endPoint.y)) {

            if (world.thumpers.size() == 0) {

                position.x = Math.round(mousePosition.x);
                position.z = Math.round(mousePosition.z);

                placeableScene.modelInstance.transform.setToTranslation(position).scl(scaling)
                        .rotate(new Vector3(1.0f, 0.0f, 0.0f), -20f);
                checkBuy();

            } else if (world.thumpers.size() == 1) {

                if (world.thumpers.get(0).isPossiblePosition(posX, posY)) {

                    position.x = Math.round(mousePosition.x);
                    position.z = Math.round(mousePosition.z);

                    placeableScene.modelInstance.transform.setToTranslation(position).scl(scaling)
                            .rotate(new Vector3(1.0f, 0.0f, 0.0f), -20f);
                    checkBuy();
                } else {
                    removeScene();
                }

            } else {
                removeScene();
                turretBought = false;
            }
        } else {
            removeScene();
        }
    }
}
