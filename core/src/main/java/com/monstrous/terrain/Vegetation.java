package com.monstrous.terrain;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.terrain.terrain.Terrain;

public class Vegetation implements Disposable {
    private final Terrain terrain;
    private Array<ModelInstance> vegetation;   // to show placement at terrain height
    private Model cube;
    private ModelBatch modelBatch;

    public Vegetation(Terrain terrain) {
        this.terrain = terrain;
        modelBatch = new ModelBatch();

        ModelBuilder builder = new ModelBuilder();
        float SZ = 250f;
        cube = builder.createBox(SZ, SZ, SZ, new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        assert cube != null;

        placeVegetation();
    }

    // randomly place little cubes on the terrain to demonstrate we can get terrain height correctly
    // call again whenever terrain scale or amplitude is changed
    public void placeVegetation(){
        final int N = 1000;
        final float worldSize = terrain.heightMap.getSize() * terrain.getScale();
        vegetation = new Array<>();

        for(int i = 0; i < N; i++){
            float x = ((float)Math.random() -0.5f) * worldSize;
            float z = ((float)Math.random() -0.5f) * worldSize;
            float h = 5f + terrain.getHeight(x*0.9f, z*0.9f);

            vegetation.add( new ModelInstance(cube, x*0.9f, h, z*0.9f));
        }
    }

    public void render(Camera cam){
        modelBatch.begin(cam);
        modelBatch.render(vegetation);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        cube.dispose();
    }
}
