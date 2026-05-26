package com.monstrous.terrain;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.terrain.terrain.Terrain;

public class PlayerCharacter extends InputAdapter implements Disposable  {
    private static final float PLAYER_HEIGHT = 20f;
    private static final float PLAYER_RADIUS = PLAYER_HEIGHT/4f;
    private static final float SPEED = 250f;
    private static final float TURN_SPEED = 2f;

    private final Terrain terrain;
    private final Model capsuleModel;
    private final ModelInstance capsuleInstance;
    private final Vector3 position;
    private float directionAngle;   // in radians
    private boolean forwardPressed;
    private boolean backwardPressed;
    private boolean rotateLeftPressed;
    private boolean rotateRightPressed;


    public PlayerCharacter(Terrain terrain) {
        this.terrain = terrain;

        ModelBuilder modelBuilder = new ModelBuilder();
        capsuleModel = modelBuilder.createCapsule(PLAYER_RADIUS, PLAYER_HEIGHT, 16,
            new Material(ColorAttribute.createDiffuse(Color.CYAN)), VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked|VertexAttributes.Usage.Normal);
        capsuleInstance = new ModelInstance(capsuleModel);

        position = new Vector3();
        directionAngle = 0;
    }


    /** adjust player Y position to the terrain height at player X, Z position */
    public void adjustPlayerHeight(){
        capsuleInstance.transform.getTranslation(position);
        float y = terrain.getHeight(position.z, position.x);
        position.y = y + PLAYER_HEIGHT/2f;  // origin is at centre of model
        capsuleInstance.transform.setTranslation(position);
    }

    public Vector3 getPosition(){
        return position;
    }

    public void render(ModelBatch modelBatch, Environment environment){
        modelBatch.render(capsuleInstance, environment);
    }

    public void update(float deltaTime){
        if(forwardPressed){
            position.z -= (float)Math.cos(directionAngle) * deltaTime * SPEED;
            position.x -= (float)Math.sin(directionAngle) * deltaTime * SPEED;
            capsuleInstance.transform.setTranslation(position);
            adjustPlayerHeight();
        }
        if(backwardPressed){
            position.z += (float)Math.cos(directionAngle) * deltaTime * SPEED;
            position.x += (float)Math.sin(directionAngle) * deltaTime * SPEED;
            capsuleInstance.transform.setTranslation(position);
            adjustPlayerHeight();
        }
        if(rotateLeftPressed)
            directionAngle += deltaTime * TURN_SPEED;
        if(rotateRightPressed)
            directionAngle -= deltaTime * TURN_SPEED;

    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP)
            forwardPressed = true;
        if(keycode == Input.Keys.DOWN)
            backwardPressed = true;
        if(keycode == Input.Keys.LEFT)
            rotateLeftPressed = true;
        if(keycode == Input.Keys.RIGHT)
            rotateRightPressed = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP)
            forwardPressed = false;
        if(keycode == Input.Keys.DOWN)
            backwardPressed = false;
        if(keycode == Input.Keys.LEFT)
            rotateLeftPressed = false;
        if(keycode == Input.Keys.RIGHT)
            rotateRightPressed = false;
        return false;
    }

    @Override
    public void dispose() {
        capsuleModel.dispose();
    }
}
