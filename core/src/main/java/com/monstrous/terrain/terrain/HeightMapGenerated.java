package com.monstrous.terrain.terrain;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import java.nio.ByteBuffer;

// BROKEN?

public class HeightMapGenerated implements HeightMap, Disposable {
    final int PERLIN_GRID_SIZE = 560;

    public int mapSize;
    private float[][] heightMap;
    private Noise noise;
    private Texture heightMapTexture;
    private Texture normalTexture;
    private Pixmap pixmap;
    private Pixmap normalsPixmap;


    /** Create height map using Perlin noise */
    public HeightMapGenerated(int mapSize) {
        this.mapSize = mapSize;
        noise = new Noise();
        // generate a noise map
        heightMap = noise.generateSmoothedPerlinMap(mapSize, mapSize, 0,0, PERLIN_GRID_SIZE);
        Pixmap pixmap = noise.generatePixmap(heightMap, mapSize);
        heightMapTexture = new Texture(pixmap);

        normalsPixmap = new Pixmap(mapSize, mapSize, Pixmap.Format.RGB888);
        generateNormalMap(normalsPixmap);

        normalTexture = new Texture(normalsPixmap);

//        normalTexture = new Texture(Gdx.files.internal("normalMap.png"));
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

    @Override
    public Texture getNormalTexture(){
        return normalTexture;
    }

    /** get height at position (wx, wz). Coordinates must be in range [0.0 to 1.0]. */
    public float get(float wx, float wz){
        int x = Math.round(wx * mapSize);
        int z = Math.round(wz * mapSize);

        return heightMap[z][x];
    }

    public float getFromIndex(int x, int z){
        return heightMap[z][x];
    }

    @Override
    public void dispose() {
        if(heightMapTexture != null)
            heightMapTexture.dispose();
    }

    /** use heights to calculate normals and store those in RBG channels */
    public void generateNormalMap(Pixmap pixmap){
        final int numVertices = mapSize * mapSize;
        Vector3[] vertices = new Vector3[numVertices];
        Vector3[] normals = new Vector3[numVertices];
        Vector3 pos = new Vector3();
        float horizontalScale = 64;
        float amplitude = 15000;

        for (int z = 0; z < mapSize; z++) {
            for (int x = 0; x < mapSize; x++) {
                float height =  getFromIndex(x, z) * amplitude;
                pos.set(x*horizontalScale , height, z*horizontalScale );
                vertices[z * mapSize+ x] = new Vector3(pos);
                normals[z * mapSize + x] = new Vector3(Vector3.Zero);
            }
            if (z >= 1) {
                // add to index list to make a row of triangles using vertices at y and y-1
                int v0 = ((z - 1) * mapSize);    // vertex number at top left of this row
                for (short t = 0; t < mapSize-1; t++) {
                    // counter-clockwise winding
                    calcNormal(vertices, normals, v0, v0 + mapSize, v0 + 1);
                    calcNormal(vertices, normals, v0 + 1, v0 + mapSize, v0 + mapSize + 1);
                    v0++;                // next column
                }
            }
        }



        for(int z = 0; z < mapSize; z++) {
            for(int x = 0; x < mapSize; x++) {
                int i = z*mapSize + x;
                // scale from [-1 to 1] to [0 to 1]
                normals[i].nor().scl(0.5f).add(0.5f, 0.5f, 0.5f);
                pixmap.setColor(normals[i].x, normals[i].y, normals[i].z, 1.0f);
                pixmap.drawPixel(x,z);  // flip?
            }
        }


        //        ByteBuffer bb = pixmap.getPixels();
//        bb.clear();
//        int idx = 0;
//        for(int i = 0; i < mapSize*mapSize; i++){
//            normals[i].nor();
//            bb.put(idx++, floatToByte(normals[i].x));
//            bb.put(idx++, floatToByte(normals[i].y));
//            bb.put(idx++, floatToByte(normals[i].z));
//            //idx++;
//            //bb.put(idx++, (byte)255);
//        }
//        //pixmap.setPixels(bb);

    }


    private byte floatToByte(float u){
        byte b =  (byte) (u*128f);
        return b;
    }

    /*
     * Calculate the normal
     */
    private static Vector3 u = new Vector3();
    private static Vector3 v = new Vector3();
    private static Vector3 n = new Vector3();

    private static void calcNormal(final Vector3[] vertices, Vector3[] normals, int v0, int v1, int v2) {

        final Vector3 p0 = vertices[v0];
        final Vector3 p1 = vertices[v1];
        final Vector3 p2 = vertices[v2];

        v.set(p2).sub(p1);
        u.set(p0).sub(p1);
        n.set(v).crs(u).nor();

        normals[v0].add(n);
        normals[v1].add(n);
        normals[v2].add(n);
    }
}
