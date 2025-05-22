package io.swapastack.dunetd.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImFloat;
import imgui.type.ImInt;

/**
 * This is the MainMenuScreen class.
 * This class is used to display the main menu.
 * It displays the name of the game in latin and japanese letters.
 * Multiple buttons for interaction with the app.
 *
 * @author Dennis Jehle
 */
public class MainMenuScreen implements Screen {

    // see: https://github.com/libgdx/libgdx/wiki/Orthographic-camera
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/OrthographicCamera.html
    private final OrthographicCamera camera;
    // see: https://github.com/libgdx/libgdx/wiki/Viewports
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/utils/viewport/ScreenViewport.html
    private final Viewport viewport;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/Stage.html
    private final Stage stage;
    // see: https://github.com/libgdx/libgdx/wiki/Spritebatch,-Textureregions,-and-Sprites
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/SpriteBatch.html
    private final SpriteBatch spriteBatch;
    // see: https://github.com/libgdx/libgdx/wiki/Skin
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/ui/Skin.html
    // see: https://github.com/czyzby/gdx-skins (!!! other skins available here)
    private final Skin skin;
    // see: https://libgdx.info/basic-label/
    private final FreeTypeFontGenerator bitmapFontGenerator;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/Texture.html
    private final Texture backgroundTexture;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/audio/Music.html
    private final Music backgroundMusic;

    // SpaiR/imgui-java
    public ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    long windowHandle;

    public static final ImInt height = new ImInt(6);
    public static final ImInt width = new ImInt(6);

    public static final ImInt playerLife = new ImInt(100);
    public static final ImInt startSpice = new ImInt(50);

    public static final ImInt bossunitLife = new ImInt(300);
    public static final ImInt infantryLife = new ImInt(40);
    public static final ImInt harvesterLife = new ImInt(100);

    public static final ImInt bossunitSpeed = new ImInt(45);
    public static final ImInt infantrySpeed = new ImInt(65);
    public static final ImInt harvesterSpeed = new ImInt(30);

    public static final ImInt bossunitDamage = new ImInt(20);
    public static final ImInt infantryDamage = new ImInt(5);
    public static final ImInt harvesterDamage = new ImInt(10);

    public static final ImInt bossunitScore = new ImInt(20);
    public static final ImInt infantryScore = new ImInt(5);
    public static final ImInt harvesterScore = new ImInt(10);

    public static final ImInt bossunitSpice = new ImInt(10);
    public static final ImInt infantrySpice = new ImInt(3);
    public static final ImInt harvesterSpice = new ImInt(6);

    public static final ImInt gunturretCost = new ImInt(15);
    public static final ImInt bombturretCost = new ImInt(40);
    public static final ImInt soundturretCost = new ImInt(20);
    public static final ImInt thumperCost = new ImInt(0);

    public static final ImInt gunturretRadius = new ImInt(225);
    public static final ImInt bombturretRadius = new ImInt(250);
    public static final ImInt soundturretRadius = new ImInt(150);

    public static final ImInt gunturretDamage = new ImInt(7);
    public static final ImInt bombturretDamage = new ImInt(10);
    public static final ImInt slowPercentage = new ImInt(60);

    public static final ImInt bulletSpeed = new ImInt(60);
    public static final ImInt bombSpeed = new ImInt(60);

    public static final ImFloat gunturretFrequence = new ImFloat(3f);
    public static final ImFloat bombturretFrequence = new ImFloat(1f);



    /**
     * This is the constructor for the MainMenuScreen class.
     *
     * @param parent reference to the parent object
     * @author Dennis Jehle
     */
    public MainMenuScreen(DuneTD parent) {
        // store reference to parent class
        // reference to the parent object
        // the reference is used to call methods of the parent object
        // e.g. parent_.get_window_dimensions()
        // the 'parent' object has nothing to do with inheritance in the accustomed manner
        // it is called 'parent' because the DuneTD class extends com.badlogic.gdx.Game
        // and each Game can have multiple classes which implement the com.badlogic.gdx.Screen
        // interface, so in this special case the Game is the parent of a Screen
        // initialize OrthographicCamera with current screen size
        // e.g. OrthographicCamera(1280.f, 720.f)
        camera = new OrthographicCamera((float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        // initialize ScreenViewport with the OrthographicCamera created above
        viewport = new ScreenViewport(camera);
        // initialize SpriteBatch
        spriteBatch = new SpriteBatch();
        // initialize the Stage with the ScreenViewport created above
        stage = new Stage(viewport, spriteBatch);
        // initialize the Skin
        skin = new Skin(Gdx.files.internal("glassy/skin/glassy-ui.json"));

        // create string for BitmapFont and Label creation
        String duneTD = "Dune TD";

        // initialize FreeTypeFontGenerator for BitmapFont generation
        bitmapFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/NotoSansCJKtc_ttf/NotoSansCJKtc-Bold.ttf"));
        // specify parameters for BitmapFont generation
        FreeTypeFontGenerator.FreeTypeFontParameter bitmapFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // set font size
        bitmapFontParameter.size = 60;
        // specify available letters
        bitmapFontParameter.characters = duneTD;
        // set font color in RGBA format (red, green, blue, alpha)
        bitmapFontParameter.color = new Color(1.f, 1.f, 0, 1.f);
        // other specifications
        bitmapFontParameter.borderWidth = 1;
        bitmapFontParameter.borderColor = Color.BLACK; // alternative enum color specification
        bitmapFontParameter.shadowOffsetX = 3;
        bitmapFontParameter.shadowOffsetY = 3;
        bitmapFontParameter.shadowColor = new Color(1.f, 1.f, 0, 0.25f);

        // generate BitmapFont with FreeTypeFontGenerator and FreeTypeFontParameter specification
        BitmapFont japanese_latin_font = bitmapFontGenerator.generateFont(bitmapFontParameter);

        // create a LabelStyle object to specify Label font
        Label.LabelStyle japanese_latin_label_style = new Label.LabelStyle();
        japanese_latin_label_style.font = japanese_latin_font;

        // create a Label with the main menu title string
        Label duneTDLabel = new Label(duneTD, japanese_latin_label_style);
        duneTDLabel.setFontScale(1, 1);
        duneTDLabel.setPosition(
                (float)Gdx.graphics.getWidth() / 2.f - duneTDLabel.getWidth() / 2.f
                , (float)Gdx.graphics.getHeight() / 2.f - duneTDLabel.getHeight() / 2.f
        );

        // add main menu title string Label to Stage
        stage.addActor(duneTDLabel);

        // load background texture
        backgroundTexture = new Texture("sharad-bhat-mZDOfgiQeE4-unsplash.jpg");

        // load background music
        // note: every game should have some background music
        //       feel free to exchange the current wav with one of your own music files
        //       but you must have the right license for the music file
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("piano/piano_loop.wav"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.05f);
        backgroundMusic.play();

        // create switch to GameScreen button
        Button gameScreenButton = new TextButton("GAME SCREEN", skin, "small");
        gameScreenButton.setPosition(
                (float)Gdx.graphics.getWidth() / 2.f - gameScreenButton.getWidth() / 2.f
                , (float)Gdx.graphics.getHeight() / 2.f - 125.f
        );
        // add InputListener to Button, and close app if Button is clicked
        gameScreenButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                parent.changeScreen(ScreenEnum.GAME);
            }
        });

        // add exit button to Stage
        stage.addActor(gameScreenButton);

        // create switch to GameScreen button
        Button showcaseButton = new TextButton("SHOWCASE", skin, "small");
        showcaseButton.setPosition(
                (float)Gdx.graphics.getWidth() / 2.f - showcaseButton.getWidth() / 2.f
                , (float)Gdx.graphics.getHeight() / 2.f - 200.f
        );
        // add InputListener to Button, and close app if Button is clicked
        showcaseButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                parent.changeScreen(ScreenEnum.SHOWCASE);
            }
        });

        // add exit button to Stage
        stage.addActor(showcaseButton);

        // create exit application button
        Button exitButton = new TextButton("EXIT", skin, "small");
        exitButton.setPosition(
                10.0f
                , 10.0f
        );
        // add InputListener to Button, and close app if Button is clicked
        exitButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

        // add exit button to Stage
        stage.addActor(exitButton);

        // SpaiR/imgui-java
        ImGui.createContext();
        windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 120");

    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void show() {
        // this command is necessary that the stage receives input events
        // e.g. mouse click on exit button
        // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Input.html
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     * @author Dennis Jehle
     */
    @Override
    public void render(float delta) {
        // clear the client area (Screen) with the clear color (black)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera
        camera.update();

        // update the current SpriteBatch
        spriteBatch.setProjectionMatrix(camera.combined);

        // draw background graphic
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
        spriteBatch.end();

        // update the Stage
        stage.act(delta);
        // draw the Stage
        stage.draw();

        // SpaiR/imgui-java
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        //Configuration
        ImGui.begin("Configuration", ImGuiWindowFlags.AlwaysAutoResize);

        ImGui.inputInt("height", height);
        if (height.get() < 3) {
            height.set(3);
        }

        ImGui.inputInt("width", width);
        if (width.get() < 3) {
            width.set(3);
        }

        ImGui.inputInt("player life", playerLife);
        if(playerLife.get() < 1) {
            playerLife.set(1);
        }

        ImGui.inputInt("start spice", startSpice);
        if (startSpice.get() < 1) {
            startSpice.set(1);
        }

        if (ImGui.collapsingHeader("bossunit")) {
            ImGui.inputInt("bossunit life", bossunitLife);
            if (bossunitLife.get() < 1) {
                bossunitLife.set(1);
            }

            ImGui.inputInt("bossunit speed", bossunitSpeed);
            if (bossunitSpeed.get() < 1) {
                bossunitSpeed.set(1);
            }
            if (bossunitSpeed.get() > 80) {
                bossunitSpeed.set(80);
            }

            ImGui.inputInt("bossunit damage", bossunitDamage);
            if (bossunitDamage.get() < 1) {
                bossunitDamage.set(1);
            }

            ImGui.inputInt("bossunit score", bossunitScore);
            if (bossunitScore.get() < 1) {
                bossunitScore.set(1);
            }

            ImGui.inputInt("bossunit spice", bossunitSpice);
            if (bossunitSpice.get() < 1) {
                bossunitSpice.set(1);
            }
        }

        if (ImGui.collapsingHeader("infantry")) {
            ImGui.inputInt("infantry life", infantryLife);
            if (infantryLife.get() < 1) {
                infantryLife.set(1);
            }

            ImGui.inputInt("infantry speed", infantrySpeed);
            if (infantrySpeed.get() < 1) {
                infantrySpeed.set(1);
            }
            if (infantrySpeed.get() > 80) {
                infantrySpeed.set(80);
            }

            ImGui.inputInt("infantry damage", infantryDamage);
            if (infantryDamage.get() < 1) {
                infantryDamage.set(1);
            }

            ImGui.inputInt("infantry score", infantryScore);
            if (infantryScore.get() < 1) {
                infantryScore.set(1);
            }

            ImGui.inputInt("infantry spice", infantrySpice);
            if (infantrySpice.get() < 1) {
                infantrySpice.set(1);
            }
        }

        if (ImGui.collapsingHeader("harvester")) {
            ImGui.inputInt("harvester life", harvesterLife);
            if (harvesterLife.get() < 1) {
                harvesterLife.set(1);
            }

            ImGui.inputInt("harvester speed", harvesterSpeed);
            if (harvesterSpeed.get() < 1) {
                harvesterSpeed.set(1);
            }
            if (harvesterSpeed.get() > 80) {
                harvesterSpeed.set(80);
            }

            ImGui.inputInt("harvester damage", harvesterDamage);
            if (harvesterDamage.get() < 1) {
                harvesterDamage.set(1);
            }

            ImGui.inputInt("harvester score", harvesterScore);
            if (harvesterScore.get() < 1) {
                harvesterScore.set(1);
            }

            ImGui.inputInt("harvester spice", harvesterSpice);
            if (harvesterSpice.get() < 1) {
                harvesterSpice.set(1);
            }
        }


        if (ImGui.collapsingHeader("gunturret")) {
            ImGui.inputInt("gunturret cost", gunturretCost);
            if (gunturretCost.get() < 1) {
                gunturretCost.set(1);
            }

            ImGui.inputInt("gunturret radius", gunturretRadius);
            if (gunturretRadius.get() < 100) {
                gunturretRadius.set(100);
            }

            ImGui.inputInt("gunturret damage", gunturretDamage);
            if (gunturretDamage.get() < 1) {
                gunturretDamage.set(1);
            }

            ImGui.inputInt("bullet speed", bulletSpeed);
            if (bulletSpeed.get() < 20) {
                bulletSpeed.set(20);
            }

            ImGui.inputFloat("shooting frequence", gunturretFrequence, 0.100f);
            if (gunturretFrequence.get() < 0.1f) {
                gunturretFrequence.set(0.1f);
            }
        }


        if (ImGui.collapsingHeader("bombturret")) {
            ImGui.inputInt("bombturret cost", bombturretCost);
            if (bombturretCost.get() < 1) {
                bombturretCost.set(1);
            }

            ImGui.inputInt("bombturret radius", bombturretRadius);
            if (bombturretRadius.get() < 100) {
                bombturretRadius.set(100);
            }

            ImGui.inputInt("bombturret damage", bombturretDamage);
            if (bombturretDamage.get() < 1) {
                bombturretDamage.set(1);
            }

            ImGui.inputInt("bomb speed", bombSpeed);
            if (bombSpeed.get() < 20) {
                bombSpeed.set(20);
            }

            ImGui.inputFloat("shooting frequence", bombturretFrequence, 0.100f);
            if (bombturretFrequence.get() < 0.1f) {
                bombturretFrequence.set(0.1f);
            }
        }


        if (ImGui.collapsingHeader("soundturret")) {
            ImGui.inputInt("soundturret cost", soundturretCost);
            if (soundturretCost.get() < 1) {
                soundturretCost.set(1);
            }

            ImGui.inputInt("soundturret radius", soundturretRadius);
            if (soundturretRadius.get() < 100) {
                soundturretRadius.set(100);
            }

            ImGui.inputInt("slow in percent", slowPercentage);
            if (slowPercentage.get() < 1) {
                slowPercentage.set(1);
            }
            if (slowPercentage.get() > 99) {
                slowPercentage.set(99);
            }
        }

        if (ImGui.collapsingHeader("thumper")) {
            ImGui.inputInt("thumper cost", thumperCost);
            if (thumperCost.get() < 1) {
                thumperCost.set(1);
            }
        }

        ImGui.end();

        // SpaiR/imgui-java
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    /**
     * This method gets called after a window resize.
     *
     * @param width new window width
     * @param height new window height
     * @see ApplicationListener#resize(int, int)
     * @author Dennis Jehle
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * This method gets called if the application lost focus.
     *
     * @see ApplicationListener#pause()
     * @author Dennis Jehle
     */
    @Override
    public void pause() {}

    /**
     * This method gets called if the application regained focus.
     *
     * @see ApplicationListener#resume()
     * @author Dennis Jehle
     */
    @Override
    public void resume() {}

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     * @author Dennis Jehle
     */
    @Override
    public void hide() {
        backgroundMusic.stop();
    }

    /**
     * Called when this screen should release all resources.
     * @author Dennis Jehle
     */
    @Override
    public void dispose() {
        backgroundMusic.dispose();
        backgroundTexture.dispose();
        bitmapFontGenerator.dispose();
        skin.dispose();
        stage.dispose();
        spriteBatch.dispose();
    }
}
