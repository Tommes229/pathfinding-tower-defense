package io.swapastack.dunetd.world.handlers;

import com.badlogic.gdx.utils.TimeUtils;
import io.swapastack.dunetd.screens.GameScreen;

/**
 * The PhaseHandler class.
 *
 * @author Mattis Bühler
 */
public class PhaseHandler {

    //GameScreen
    private final GameScreen screen;

    //current phase
    public boolean buildingPhase = true;
    private boolean startBuilding = false;

    //phase duration
    public int durationInS = 15;

    //Time
    private long startTime;
    public long countdown = 0;

    /**
     * Constructor of the PhaseHandler class.
     *
     * @param screen the GameScreen in which the PhaseHandler exists.
     */
    public PhaseHandler(GameScreen screen) {
        this.screen = screen;
    }

    /**
     * This method updates everytime its GameScreen renders if the game is still running.
     * It also switches after the first tower is placed and every building phase, after the given duration to the fighting phase.
     *
     * @author Mattis Bühler
     */
    public void update() {
        if (startBuilding) {
            buildingPhase = true;
            countdown = TimeUtils.timeSinceMillis(startTime)/1000;
            if (countdown > durationInS) {
                startBuilding = false;
                buildingPhase = false;
                screen.buyingHandler.removeScene();
                screen.waveHandler.setUpWave();
                countdown = durationInS;
            }
        }
    }

    /**
     * This method starts the building phase.
     *
     * @author Mattis Bühler
     */
    public void startBuilding() {
        startBuilding = true;
        startTime = TimeUtils.millis();
        countdown = 0;
    }
}
