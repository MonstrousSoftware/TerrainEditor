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
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.terrain.gui.GUI;
import com.monstrous.terrain.terrain.HeightMap;
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

    private ModelBatch modelBatch;
    private CameraLoop camLoop;
    public Vegetation vegetation;
    public PlayerCharacter player;
    private float time;
    private final Vector3 tmpVec3 = new Vector3();

    public boolean showHeightmap = false;
    public boolean showCameraPath = false;
    public boolean flyCamera = false;


	@Override
	public void create() {
        heightMap = new HeightMapGenerated(2048);
        //heightMap = new HeightMapFromFile(Gdx.files.internal("terrain/everest_2048_2048_8bit.png"));

        terrain = new Terrain(heightMap,255, 4, 1f, 100f);
        vegetation = new Vegetation(terrain);

        gui = new GUI(this, terrain);

		// create perspective camera
		cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float worldSize = terrain.heightMap.getSize() * terrain.getScale();
        // far distance is world distance of diagonal over height map
		cam.far = 100f*worldSize;
		cam.near = 0.1f;


        camLoop = new CameraLoop(cam, terrain.getAmplitude());

		// add camera controller
		camController = new CameraInputController(cam);
        camController.scrollFactor = -1f;

        player = new PlayerCharacter(terrain);
        player.adjustPlayerHeight();
        Vector3 p = player.getPosition();
        cam.position.set(0, p.y + 5, p.z+10);
        cam.lookAt(p);
        cam.update();


		// input multiplexer to send inputs to GUI and to cam controller
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
        im.addProcessor(player);
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
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.update();

        batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);

		gui.resize(width, height);
	}

    private final Vector3 viewVector = new Vector3();

	@Override
	public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        viewVector.set(cam.position).sub(player.getPosition()); // camera relative to player
        player.update(delta);


		// update camera positioning
		camController.update();
        cam.position.set(viewVector).add(player.getPosition());
        cam.lookAt(player.getPosition());
        cam.up.set(Vector3.Y);
        cam.update();
        //Gdx.app.log("cam", cam.position.toString()+" player: "+player.getPosition().toString());


        if(flyCamera) {

            time += delta;
            if (flyCamera)
                camLoop.moveCameraAlongSpline(time);
        }



        if(!gui.freezeLoD )
            terrain.update(cam, player.getPosition());

		// clear screen
        ScreenUtils.clear(Color.SKY, true);

        terrain.render(cam, environment);

        modelBatch.begin(cam);
        modelBatch.render(xyzInstance);
        player.render(modelBatch, environment);
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


	@Override
	public void dispose() {
		batch.dispose();
        terrain.dispose();
        gui.dispose();
        vegetation.dispose();
        modelBatch.dispose();
        xyzModel.dispose();
        player.dispose();
	}



}

