package io.swapastack.dunetd.world.handlers;

import com.badlogic.gdx.utils.TimeUtils;
import io.swapastack.dunetd.screens.GameScreen;
import io.swapastack.dunetd.screens.MainMenuScreen;
import io.swapastack.dunetd.world.GameWorld;

/**
 * The WaveHandler class.
 *
 * @author Mattis Bühler
 */
public class WaveHandler {

    //GameWorld and GameScreen
    private final GameWorld world;
    private final GameScreen screen;

    //Wave values
    public boolean waveFinished = false;
    public int currentWave = 0;

    //TODO: Changeable if needed
    private final int maxWaves = 5;

    //enemy numbers
    private int infantryNumber;
    private int harvesterNumber;
    private int bossunitNumber;

    //enemy max per wave
    private int infantryMax;
    private int harvesterMax;
    private int bossunitMax;

    //delay between enemies
    private int delayInfantry;
    private int delayHarvester;
    private int delayBossunit;

    //time an enemy exists
    private long infantryTime;
    private long harvesterTime;
    private long bossunitTime;

    /**
     * Constructor of the WaveHandler.
     * @param screen GameScreen in which the WaveHandler exists.
     * @param world GameWorld in which the WaveHandler exists.
     */
    public WaveHandler (GameScreen screen, GameWorld world) {
        this.world = world;
        this.screen = screen;
    }

    /**
     * This method is called every time the GameScreen renders.
     * It checks if the current phase is a fighting phase, if so it starts a wave.
     * It will spawn a given amount of enemies with a given delay.
     *
     * @author Mattis Bühler
     */
    public void update() {

        if (waveFinished) {
            if (!screen.phaseHandler.buildingPhase) {
                screen.phaseHandler.startBuilding();
            }
        } else {

            if (infantryMax > infantryNumber && TimeUtils.timeSinceMillis(infantryTime) >= delayInfantry) {
                delayInfantry = (int)(0.5f*1000);
                world.addInfantry();
                infantryNumber++;
                infantryTime = TimeUtils.millis();
            }

            if (harvesterMax > harvesterNumber && TimeUtils.timeSinceMillis(harvesterTime) >= delayHarvester) {
                delayHarvester = (int) (1.5 * 1000);
                world.addHarvester();
                harvesterNumber++;
                harvesterTime = TimeUtils.millis();
            }

            if (bossunitMax > bossunitNumber && TimeUtils.timeSinceMillis(bossunitTime) >= delayBossunit) {
                world.addBossUnit();
                bossunitNumber++;
                bossunitTime = TimeUtils.millis();
            }


            //game won
            if (infantryMax == infantryNumber && harvesterMax == harvesterNumber && bossunitMax == bossunitNumber && world.getEnemies().size() == 0) {
                if (currentWave == maxWaves) {
                    System.out.println("won");
                    screen.endGame(true);
                }
                waveFinished = true;
            }
        }

    }

    /**
     * Sets up the values for the waves, with the size of the playing field and the number of the wave, the number
     * of enemies increases.
     *
     * @author Mattis Bühler
     */
    public void setUpWave() {
        infantryNumber = 0;
        harvesterNumber = 0;
        bossunitNumber = 0;

        infantryMax = 10 + 6 * (currentWave/2) * MainMenuScreen.height.get()/3 * MainMenuScreen.width.get()/3;
        harvesterMax = 4 + 2 * (currentWave/2) * MainMenuScreen.height.get()/3 * MainMenuScreen.width.get()/3;
        bossunitMax = 1 + (currentWave/2) * MainMenuScreen.height.get()/3 * MainMenuScreen.width.get()/3;

        delayInfantry = 0;
        delayHarvester = 0;
        delayBossunit = 4*1000;

        infantryTime = TimeUtils.millis();
        harvesterTime = infantryTime;
        bossunitTime = infantryTime;

        waveFinished = false;
        currentWave++;
    }
}
