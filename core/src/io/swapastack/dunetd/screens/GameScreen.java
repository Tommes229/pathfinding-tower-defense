package io.swapastack.dunetd.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import io.swapastack.dunetd.world.GameWorld;
import io.swapastack.dunetd.world.handlers.BuyingHandler;
import io.swapastack.dunetd.world.handlers.PhaseHandler;
import io.swapastack.dunetd.world.handlers.WaveHandler;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;


import java.util.HashMap;
import java.util.Locale;


/**
 * The GameScreen class.
 *
 * @author Dennis Jehle
 */
public class GameScreen implements Screen {

    private final DuneTD parent;

    // GDX GLTF
    private SceneManager sceneManager;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;

    // libGDX
    private PerspectiveCamera camera;

    // 3D models
    String basePath = "kenney_gltf/";
    String[] kenneyModels;
    public HashMap<String, SceneAsset> sceneAssetHashMap;

    // Grid Specifications
    private final int rows = MainMenuScreen.height.get();
    private final int cols = MainMenuScreen.width.get();

    // SpaiR/imgui-java
    public ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    long windowHandle;

    // GameWorld and Handlers
    private GameWorld world;
    public BuyingHandler buyingHandler;
    public PhaseHandler phaseHandler;
    public WaveHandler waveHandler;

    // Calculate cursor position
    private Plane plane;
    private final Vector3 intersect = new Vector3();
    public Vector3 groundTileDimensions;

    // Buying Selection
    private final ImInt tower = new ImInt(0);

    // End game and button at the end
    private final Skin skin = new Skin(Gdx.files.internal("glassy/skin/glassy-ui.json"));
    private Stage stage;
    private boolean endGame = false;

    public GameScreen(DuneTD parent) {
        this.parent = parent;
    }


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     * @author Dennis Jehle
     */
    @Override
    public void show() {

        // SpaiR/imgui-java
        ImGui.createContext();
        windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 120");

        // GDX GLTF - Scene Manager
        sceneManager = new SceneManager(64);

        // GDX GLTF - Light
        DirectionalLightEx light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // GDX GLTF - Image Based Lighting
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // GDX GLTF - This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        // GDX GLTF - Cubemaps
        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // GDX GLTF - Skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);

        // Camera
        camera = new PerspectiveCamera();
        camera.position.set(10.0f, 10.0f, 10.0f);
        camera.lookAt(Vector3.Zero);
        sceneManager.setCamera(camera);

        // Camera Input Controller
        CameraInputController cameraInputController = new CameraInputController(camera);

        // Set Input Processor
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(cameraInputController);
        // TODO: add further input processors if needed
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Load all 3D models listed in kenney_assets.txt file in blocking mode
        FileHandle assetsHandle = Gdx.files.internal("kenney_assets.txt");
        String fileContent = assetsHandle.readString();
        kenneyModels = fileContent.split("\\r?\\n");
        for (String model : kenneyModels) {
            DuneTD.assetManager.load(basePath + model, SceneAsset.class);
        }

        // Load example enemy models
        DuneTD.assetManager.load("faceted_character/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("cute_cyborg/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("spaceship_orion/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("explosion/explosion.glb", SceneAsset.class);
        DuneTD.assetManager.load("red_shot/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("bomb_rock/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("thumper_dune/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("low_poly_sandworm/scene.glb", SceneAsset.class);
        DuneTD.assetManager.load("a_pile_of_sand/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("cactus/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.load("sandtile/sand_test.glb", SceneAsset.class);
        DuneTD.assetManager.load("bull_skull/scene.gltf", SceneAsset.class);
        DuneTD.assetManager.finishLoading();

        // Create scene assets for all loaded models
        sceneAssetHashMap = new HashMap<>();
        for (String kenneyModel : kenneyModels) {
            SceneAsset sceneAsset = DuneTD.assetManager.get(basePath + kenneyModel, SceneAsset.class);
            sceneAssetHashMap.put(kenneyModel, sceneAsset);
        }
        SceneAsset bossCharacter = DuneTD.assetManager.get("faceted_character/scene.gltf");
        sceneAssetHashMap.put("faceted_character/scene.gltf", bossCharacter);
        SceneAsset enemyCharacter = DuneTD.assetManager.get("cute_cyborg/scene.gltf");
        sceneAssetHashMap.put("cute_cyborg/scene.gltf", enemyCharacter);
        SceneAsset harvesterCharacter = DuneTD.assetManager.get("spaceship_orion/scene.gltf");
        sceneAssetHashMap.put("spaceship_orion/scene.gltf", harvesterCharacter);
        SceneAsset explosion = DuneTD.assetManager.get("explosion/explosion.glb");
        sceneAssetHashMap.put("explosion/explosion.glb", explosion);
        SceneAsset redShot = DuneTD.assetManager.get("red_shot/scene.gltf");
        sceneAssetHashMap.put("red_shot/scene.gltf", redShot);
        SceneAsset bombRock = DuneTD.assetManager.get("bomb_rock/scene.gltf");
        sceneAssetHashMap.put("bomb_rock/scene.gltf", bombRock);
        SceneAsset thumper = DuneTD.assetManager.get("thumper_dune/scene.gltf");
        sceneAssetHashMap.put("thumper_dune/scene.gltf", thumper);
        SceneAsset sandworm = DuneTD.assetManager.get("low_poly_sandworm/scene.glb");
        sceneAssetHashMap.put("low_poly_sandworm/scene.glb", sandworm);
        SceneAsset pileOfSand = DuneTD.assetManager.get("a_pile_of_sand/scene.gltf");
        sceneAssetHashMap.put("a_pile_of_sand/scene.gltf", pileOfSand);
        SceneAsset cactus = DuneTD.assetManager.get("cactus/scene.gltf");
        sceneAssetHashMap.put("cactus/scene.gltf", cactus);
        SceneAsset sandTile = DuneTD.assetManager.get("sandtile/sand_test.glb");
        sceneAssetHashMap.put("sandtile/sand_test.glb", sandTile);
        SceneAsset bullSkull = DuneTD.assetManager.get("bull_skull/scene.gltf");
        sceneAssetHashMap.put("bull_skull/scene.gltf", bullSkull);


        createMapExample(sceneManager);
        world = new GameWorld(this);
        phaseHandler = new PhaseHandler(this);
        buyingHandler = new BuyingHandler(this, world);
        waveHandler = new WaveHandler(this, world);


        //plane of the groundDimension
        plane = new Plane(new Vector3(0f,1f,0f), new Vector3(0, groundTileDimensions.y,0));
    }


    /**
     * Called when the screen should render itself.
     *
     * @author Dennis Jehle
     * @param delta - The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        if (!endGame) {
            world.update(delta);
            buyingHandler.update();
            phaseHandler.update();
            waveHandler.update();
        }
            // OpenGL - clear color and depth buffer
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            // SpaiR/imgui-java
            imGuiGlfw.newFrame();
            ImGui.newFrame();


            // GDX GLTF - update scene manager and render scene
            sceneManager.update(delta);
            sceneManager.render();

            ImGui.begin("Performance", ImGuiWindowFlags.AlwaysAutoResize);
            ImGui.text(String.format(Locale.US, "deltaTime: %1.6f", delta));
            ImGui.end();

            ImGui.begin("Menu", ImGuiWindowFlags.AlwaysAutoResize);
            if (ImGui.button("Back to menu")) {
                parent.changeScreen(ScreenEnum.MENU);
            }
            ImGui.end();


            ImGui.begin("Cursor", ImGuiWindowFlags.AlwaysAutoResize);
            ImGui.text("CursorXPos: " + Gdx.input.getX());
            ImGui.text("CursorYPos: " + Gdx.input.getY());
            ImGui.end();


            ImGui.begin("buy or sell turrets", ImGuiWindowFlags.AlwaysAutoResize);
            if (phaseHandler.buildingPhase) {
                ImGui.radioButton("nothing", tower, 0);

                ImGui.radioButton("buy soundturret", tower, 1);
                ImGui.sameLine();
                ImGui.text("  cost: " + MainMenuScreen.soundturretCost.get());

                ImGui.radioButton("buy gunturret", tower, 2);
                ImGui.sameLine();
                ImGui.text("    cost: " + MainMenuScreen.gunturretCost.get());

                ImGui.radioButton("buy bombturret", tower, 3);
                ImGui.sameLine();
                ImGui.text("   cost: " + MainMenuScreen.bombturretCost.get());

                ImGui.radioButton("sell turret", tower, 4);

            }

            ImGui.text("");

            ImGui.radioButton("buy thumper", tower, 5);
            ImGui.sameLine();
            ImGui.text("      cost: " + MainMenuScreen.thumperCost.get());

            ImGui.end();


            ImGui.begin("stats", ImGuiWindowFlags.AlwaysAutoResize);

            ImGui.text("life: " + world.playerLife);
            ImGui.text("score: " + world.score);
            ImGui.text("spice: " + world.spice);
            ImGui.text("");
            ImGui.text("remaining building phase time: " + phaseHandler.countdown + "s / " + phaseHandler.durationInS + "s");
            ImGui.text("wave number: " + waveHandler.currentWave);

            ImGui.end();

            //calculate Point where Cursor ray intersects with the ground
            Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Intersector.intersectRayPlane(ray, plane, intersect);

            ImGui.begin("Cursor", ImGuiWindowFlags.AlwaysAutoResize);
            ImGui.text("intersecting position: " + intersect);
            ImGui.end();

            // SpaiR/imgui-java
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (endGame) {
                stage.act(delta);
                stage.draw();
            }
    }

    @Override
    public void resize(int width, int height) {
        // GDX GLTF - update the viewport
        sceneManager.updateViewport(width, height);
    }

    @Override
    public void pause() {
        // TODO: implement pause logic if needed
    }

    @Override
    public void resume() {
        // TODO: implement resume logic if needed
    }

    @Override
    public void hide() {
        // TODO: implement hide logic if needed
    }

    /**
     * This function acts as a starting point.
     * It generate a simple rectangular map with decoration around.
     * It doesn't provide any functionality, but it uses some common ModelInstance specific functions
     *
     */
    private void createMapExample(SceneManager sceneManager) {

        groundTileDimensions = new Vector3();
        Scene gridTile;

        Vector3 scaling = new Vector3(1f,1f,1f);

        // Simple way to generate the example map
        for (int i = -10; i < rows+10; i++) {
            for (int k = -10; k < cols+10; k++) {
                Scene decoration = null;
                float rotation = 0f;
                gridTile = new Scene(sceneAssetHashMap.get("sandtile/sand_test.glb").scene);

                if ((-1 <= i && i < rows+1) && (-1 <= k && k < cols+1)) {
                    // Create a new Scene object from the tile_dirt gltf model
                    if (i == -1 || i == rows || k == -1 || k == cols) {
                        decoration = new Scene(sceneAssetHashMap.get("detail_dirt.glb").scene);
                        scaling.x = 1f + (float)((Math.random()*0.6f)-0.3f);
                        scaling.y = 1f + (float)((Math.random()*1f)-0.5f);
                        scaling.z = 1f + (float)((Math.random()*0.6f)-0.3f);
                        rotation = (int)(Math.random()*359);
                    }

                } else {

                    int randomNumber = (int)(Math.random()*180);

                    if (randomNumber >= 1 && randomNumber <= 6) {
                        decoration = new Scene(sceneAssetHashMap.get("detail_dirt.glb").scene);
                        scaling.x = 1f + (float)((Math.random()*0.6f)-0.3f);
                        scaling.y = 1f + (float)((Math.random()*1f)-0.5f);
                        scaling.z = 1f + (float)((Math.random()*0.6f)-0.3f);
                        rotation = (int)(Math.random()*359);

                    } else if (randomNumber >= 7 && randomNumber <= 12) {
                        decoration = new Scene(sceneAssetHashMap.get("cactus/scene.gltf").scene);
                        scaling.x = 0.08f;
                        scaling.y = (float)(0.03 + Math.random() * 0.04);
                        scaling.z = 0.08f;

                        if (Math.random() > 0.5) {
                            rotation = 0;
                        } else {
                            rotation = 180;
                        }

                    } else if (randomNumber == 100) {
                        decoration = new Scene(sceneAssetHashMap.get("bull_skull/scene.gltf").scene);
                        scaling.x = 0.1f;
                        scaling.y = 0.1f;
                        scaling.z = 0.1f;

                        if (Math.random() > 0.5) {
                            rotation = (int) ((Math.random()*20)-10);
                        } else {
                            rotation = 180 + (int) ((Math.random()*20)-10);
                        }
                    }
                }

                // Create a new BoundingBox, this is useful to check collisions or to get the model dimensions
                BoundingBox boundingBox = new BoundingBox();
                // Calculate the BoundingBox from the given ModelInstance
                gridTile.modelInstance.calculateBoundingBox(boundingBox);
                // Create Vector3 to store the ModelInstance dimensions
                Vector3 modelDimensions = new Vector3();
                // Read the ModelInstance BoundingBox dimensions
                boundingBox.getDimensions(modelDimensions);
                // TODO: refactor this if needed, e.g. if ground tiles are not all the same size
                groundTileDimensions.set(modelDimensions);
                // Set the ModelInstance to the respective row and cell of the map
                gridTile.modelInstance.transform.setToTranslation(k * modelDimensions.x, 0.0f, i * modelDimensions.z);

                // Add the Scene object to the SceneManager for rendering
                sceneManager.addScene(gridTile);

                if (decoration != null) {
                    decoration.modelInstance.transform.setToTranslation(k*modelDimensions.x, groundTileDimensions.y, i*modelDimensions.z)
                            .rotate(new Vector3(0f, 1f, 0f), rotation).scl(scaling);
                    sceneManager.addScene(decoration);
                }
            }
        }
    }

    /**
     * This function gets the coordinates of the cursor at the location,
     * where it is intersecting with the plane of the playing field
     *
     * @return the coordinates of the mouse cursor
     */
    public Vector3 getCursorCoords() {
        return intersect;
    }


    /**
     * Adds a scene to the sceneManager, which will be rendered in the next call of the render method
     *
     * @param scene the scene which should be added to the sceneManager
     */
    public void addingScene(Scene scene) {
        sceneManager.addScene(scene);
    }

    /**
     * Removes a scene from the sceneManager
     *
     * @param scene the scene which will be removed from the sceneManager
     */
    public void removingScene(Scene scene) {
        sceneManager.removeScene(scene);
    }

    /**
     * Returns the dimensions of the current playing field
     *
     * @return the dimensions of the playing field
     */
    public Vector2 getDimension() {
        return new Vector2(cols, rows);
    }

    /**
     * Returns if the current phase is the building- or fighting-phase
     *
     * @return true if the current phase is the building-phase
     */
    public boolean getPhase() {
        return phaseHandler.buildingPhase;
    }

    /**
     * Returns what kind of turret is currently selected in the buying-menu
     *
     * @return An integer which stands for a certain turret
     */
    public int getTurret() {
        return tower.get();
    }

    /**
     * Ends the current game, the render method will not be called anymore for the sceneManager and other updatin methods.
     * Adds a button to get back to the main menu and show the score the player earned during the game.
     *
     * @param result the result of the game.
     * @author Mattis Bühler
     */
    public void endGame(boolean result) {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Label label = new Label("Score: " + (world.score+world.playerLife+world.spice), skin);
        label.setPosition(
                (float)Gdx.graphics.getWidth() / 2.f - label.getWidth() / 2.f
                , (float)Gdx.graphics.getHeight() / 2.f - 90.f
        );


        String gameResult;

        if (result) {
            gameResult = "WON GAME";
        } else {
            gameResult = "LOST GAME";
        }

        Button mainMenuButton = new TextButton(gameResult, skin, "small");
        mainMenuButton.setPosition(
                (float)Gdx.graphics.getWidth() / 2.f - mainMenuButton.getWidth() / 2.f
                , (float)Gdx.graphics.getHeight() / 2.f - 50.f
        );
        // add InputListener to Button, and close app if Button is clicked
        mainMenuButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                parent.changeScreen(ScreenEnum.MENU);
            }
        });

        stage.addActor(mainMenuButton);
        stage.addActor(label);

        endGame = true;
    }

    @Override
    public void dispose() {
        // GDX GLTF - dispose resources
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();
    }
}



