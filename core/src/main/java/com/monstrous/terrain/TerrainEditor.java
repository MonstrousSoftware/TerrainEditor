package com.monstrous.terrain;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.terrain.gui.GUI;
import com.monstrous.terrain.terrain.HeightMap;
import com.monstrous.terrain.terrain.HeightMapFromFile;
import com.monstrous.terrain.terrain.HeightMapGenerated;
import com.monstrous.terrain.terrain.Terrain;

public class TerrainEditor extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public Environment environment;
	public SpriteBatch batch;
	public GUI gui;
    public Terrain terrain;
    public HeightMap heightMap;

    private ModelBatch modelBatch;
	private float time;
    private CameraLoop camLoop;
    public Vegetation vegetation;
    public boolean showHeightmap = false;
    public boolean showCameraPath = false;
    public boolean flyCamera = false;


	@Override
	public void create() {
        heightMap = new HeightMapGenerated(2048);
        //heightMap = new HeightMapFromFile(Gdx.files.internal("terrain/everest_2048_2048_8bit.png"));

        terrain = new Terrain(heightMap,255, 7, 32f);
        vegetation = new Vegetation(terrain);

        gui = new GUI(this, terrain);

		// create perspective camera
		cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 50000, 0);
		cam.lookAt(0, 0, 0);
        // far distance is world distance of diagonal over height map
		cam.far =  terrain.heightMap.getSize() * terrain.getScale();
		cam.near = 10f;
		cam.update();

        camLoop = new CameraLoop(cam, terrain.getAmplitude());

		// add camera controller
		camController = new CameraInputController(cam);
        camController.scrollFactor = -100f;

		// input multiplexer to send inputs to GUI and to cam controller
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
		im.addProcessor(gui.stage); // set stage as first input processor
		im.addProcessor(camController);

		// define some lighting
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Fog, Color.SKY));

        modelBatch = new ModelBatch();

		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.update();

        batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);

		gui.resize(width, height);
	}

	@Override
	public void render() {
		// update camera positioning
		camController.update();
        float delta = Gdx.graphics.getDeltaTime();
		time += delta;
        if(flyCamera)
		    camLoop.moveCameraAlongSpline(time);
        else
            cam.lookAt(0, 0, 0);

//        float height = terrain.getHeight(cam.position.x, cam.position.z);
//        if(height + 10f > cam.position.y)
//            cam.position.y = height + 10f;

        if(!gui.freezeLoD )
            terrain.update(cam);

		// clear screen
        ScreenUtils.clear(Color.SKY, true);

        terrain.render(cam, environment);

        if(showCameraPath)
		    camLoop.renderPath();

        // enable this to demonstrate we can get accurate terrain height by placing blocks on the terrain
        vegetation.render(cam);

		if (showHeightmap) {
			batch.begin();
			batch.draw(terrain.getHeightMapTexture(), Gdx.graphics.getWidth()-256, Gdx.graphics.getHeight()-256, 256, 256);
			batch.end();
		}
		gui.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		batch.dispose();
        terrain.dispose();
        gui.dispose();
        vegetation.dispose();
        modelBatch.dispose();
	}



}

