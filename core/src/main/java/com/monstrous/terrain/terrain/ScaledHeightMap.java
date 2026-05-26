package com.monstrous.terrain.terrain;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class ScaledHeightMap implements Disposable {
    public HeightMap heightMap;
    private float scale;        // world scale of per height map value
    private float amplitude;
    private float altitude;

    public ScaledHeightMap(HeightMap normalizedHeightMap, float scale, float amplitude) {
        this.heightMap = normalizedHeightMap;
        this.scale = scale;
        this.amplitude = amplitude;
    }

    public boolean isOffWorld(float worldX, float worldZ){
        float worldSize = heightMap.getSize() * scale;
        // scale [-0.5*worldSize .. 0.5*worldSize] to [0 .. 1]
        float u = (worldX / worldSize) + 0.5f;
        float v = (worldZ / worldSize) + 0.5f;
        return (u < 0 || u > 1f || v < 0 || v > 1f);
    }

    public float getHeight(float worldX, float worldZ){
        float worldSize = heightMap.getSize() * scale;
        // scale [-0.5*worldSize .. 0.5*worldSize] to [0 .. 1]
        float u = (worldX / worldSize) + 0.5f;
        float v = (worldZ / worldSize) + 0.5f;
        if(u < 0 || u > 1f || v < 0 || v > 1f)
            return 0;
        return amplitude * heightMap.get(u, v);
    }

    /** in samples per side */
    public int getSize(){
        return heightMap.getSize();
    }

    public float getFromIndex(int x, int z){
        return amplitude * heightMap.getFromIndex(x, z);
    }

    /** set terrain amplitude, i.e. height multiplication factor */
    public void setAmplitude(float amplitude){
        this.amplitude = amplitude;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }


    @Override
    public void dispose() {
        heightMap.dispose();
    }
}
