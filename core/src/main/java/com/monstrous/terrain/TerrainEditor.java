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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
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
    private Model xyzModel;
    private ModelInstance xyzInstance;
    private Model capsuleModel;
    private ModelInstance capsuleInstance;
    private ModelBatch modelBatch;
    private CameraLoop camLoop;
    public Vegetation vegetation;
    private float time;
    private final Vector3 tmpVec3 = new Vector3();

    public boolean showHeightmap = false;
    public boolean showCameraPath = false;
    public boolean flyCamera = false;


	@Override
	public void create() {
        heightMap = new HeightMapGenerated(2048);
        //heightMap = new HeightMapFromFile(Gdx.files.internal("terrain/everest_2048_2048_8bit.png"));

        terrain = new Terrain(heightMap,255, 3, 23f, 450f);
        vegetation = new Vegetation(terrain);

        gui = new GUI(this, terrain);

		// create perspective camera
		cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float worldSize = terrain.heightMap.getSize() * terrain.getScale();
		cam.position.set(0, 150, 150);
		cam.lookAt(0, 0, 0);
        // far distance is world distance of diagonal over height map
		cam.far = 100f*worldSize;
		cam.near = 0.1f;
		cam.update();

        camLoop = new CameraLoop(cam, terrain.getAmplitude());

		// add camera controller
		camController = new CameraInputController(cam);
        camController.scrollFactor = -50f;

		// input multiplexer to send inputs to GUI and to cam controller
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
		im.addProcessor(gui.stage); // set stage as first input processor
		im.addProcessor(camController);

		// define some lighting
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Fog, Color.SKY));
        DirectionalLight light = new DirectionalLight();
        light.setDirection(0.3f, -0.8f, -0.2f);
        light.setColor(Color.WHITE);
        environment.add(light);

        modelBatch = new ModelBatch();

		batch = new SpriteBatch();

        ModelBuilder modelBuilder = new ModelBuilder();
        xyzModel = modelBuilder.createXYZCoordinates(10, new Material(),VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked );
        xyzInstance =  new ModelInstance(xyzModel);


        capsuleModel = modelBuilder.createCapsule(50f, 200f, 16,
            new Material(ColorAttribute.createDiffuse(Color.CYAN)), VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked|VertexAttributes.Usage.Normal);
        capsuleInstance = new ModelInstance(capsuleModel);
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
        setCapsuleHeight(capsuleInstance);

		// update camera positioning
		camController.update();
        cam.update();
        //Gdx.app.log("cam", cam.position.toString());



        float delta = Gdx.graphics.getDeltaTime();
		time += delta;
        if(flyCamera)
		    camLoop.moveCameraAlongSpline(time);
        else
            cam.lookAt(0, 0, 0);



        if(!gui.freezeLoD )
            terrain.update(cam);

		// clear screen
        ScreenUtils.clear(Color.SKY, true);

        terrain.render(cam, environment);

        modelBatch.begin(cam);
        modelBatch.render(xyzInstance);
        modelBatch.render(capsuleInstance, environment);
        modelBatch.end();

        if(showCameraPath)
		    camLoop.renderPath();

        // enable this to demonstrate we can get accurate terrain height by placing blocks on the terrain
        //vegetation.render(cam);

		if (showHeightmap) {
			batch.begin();
			batch.draw(terrain.getHeightMapTexture(), Gdx.graphics.getWidth()-512, Gdx.graphics.getHeight()-256, 256, 256);
            batch.draw(terrain.normalTexture, Gdx.graphics.getWidth()-256, Gdx.graphics.getHeight()-256, 256, 256);
			batch.end();
		}
		gui.render(Gdx.graphics.getDeltaTime());
	}

    private void setCapsuleHeight(ModelInstance instance){
        instance.transform.getTranslation(tmpVec3);
        float y = terrain.getHeight(tmpVec3.x, tmpVec3.z);
        tmpVec3.y = y +100f;
        instance.transform.setTranslation(tmpVec3);
        //camController.target.set(tmpVec3);
        cam.lookAt(tmpVec3);
        cam.up.set(Vector3.Y);
    }

	@Override
	public void dispose() {
		batch.dispose();
        terrain.dispose();
        gui.dispose();
        vegetation.dispose();
        modelBatch.dispose();
        xyzModel.dispose();
        capsuleModel.dispose();
	}



}

