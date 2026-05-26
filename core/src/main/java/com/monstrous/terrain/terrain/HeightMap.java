package com.monstrous.terrain.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public interface HeightMap extends Disposable {

    /** returns a 2d texture with the height in the alpha channel. */
    Texture getHeightMapTexture();

    /** get height at position (ux, uz). Coordinates must be in range [0.0 to 1.0]. */
    float get(float ux, float uz);

    /** get height at coordinate (x,z)
     *  x and z range from 0 to getSize() */
    float getFromIndex(int x, int z);

    /** get number of values per side */
    int getSize();

    void getLODTexture(Texture tex, int cx, int cy, int size, int scale);

}
