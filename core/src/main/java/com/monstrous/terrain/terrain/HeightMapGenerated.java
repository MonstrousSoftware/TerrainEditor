package com.monstrous.terrain.terrain;




import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import java.nio.ByteBuffer;



public class HeightMapGenerated implements HeightMap, Disposable {
    final int PERLIN_GRID_SIZE = 256;

    public int mapSize;
    private float[][] heightMap;
    private Noise noise;
    private Texture heightMapTexture;
    private Pixmap pixmap;
    private Pixmap normalsPixmap;
    private Texture lod0texture;


    /** Create height map using Perlin noise */
    public HeightMapGenerated(int mapSize) {
        this.mapSize = mapSize;
        noise = new Noise();
        // generate a noise map
        heightMap = noise.generateSmoothedPerlinMap(mapSize, mapSize, 0,0, PERLIN_GRID_SIZE);
        Pixmap pixmap = Noise.generatePixmap(heightMap, mapSize);
        heightMapTexture = new Texture(pixmap);
    }

    /** generate a pixmap from a rectangle of the height map */
    public Pixmap rectToPixmap (float [][] map, int x, int y, int w, int h, int scale) {

        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.Alpha);
        int idx = 0;
        for(int ty = y; ty < y+h*scale; ty+=scale) {
            for(int tx = x; tx < x+w*scale; tx+=scale) {
                byte val;
                if(tx < 0 || tx >= map.length || ty < 0 || ty >= map[0].length)
                    val = 0;
                else
                    val = (byte) (map[tx][ty] * 255f);

                pixmap.getPixels().put(idx++, val);
            }
        }
        return pixmap;
    }

    /** get a height map for a LOD level
     * scale is 1, 2, 4, 8, etc.*/
    public void getLODTexture(Texture tex, int cx, int cy, int sizeInPixels, int scale){
        int x = cx - scale*sizeInPixels/2;
        int y = cy - scale*sizeInPixels/2;
        Pixmap pm = rectToPixmap(heightMap, x, y, sizeInPixels, sizeInPixels, scale);
        tex.draw(pm, 0, 0);
    }

    @Override
    public int getSize(){
        return mapSize;
    }

    public Texture getHeightMapTexture(){
        // create on demand
        if(heightMapTexture == null){
            // copy to a texture (for debug)
            pixmap = noise.generatePixmap(heightMap, mapSize);

            heightMapTexture = new Texture(pixmap);
            heightMapTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            heightMapTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return heightMapTexture;
    }


    /** get height at position (wx, wz). Coordinates must be in range [0.0 to 1.0]. */
    public float get(float wx, float wz){
        int x = Math.round(wx * mapSize);
        int z = Math.round(wz * mapSize);

        return heightMap[z][x];
    }

    @Override
    public float getFromIndex(int x, int z){
        return heightMap[z][x];
    }

    @Override
    public void dispose() {
        if(heightMapTexture != null)
            heightMapTexture.dispose();
    }
}
