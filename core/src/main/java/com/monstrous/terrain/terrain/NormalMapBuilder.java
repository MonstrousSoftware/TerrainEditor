package com.monstrous.terrain.terrain;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;

public class NormalMapBuilder {

    /** use heights to calculate normals and store those in RGB channels */
    public static Pixmap generateNormalMap(ScaledHeightMap heightMap){
        int mapSize = heightMap.getSize();
        float horizontalScale = heightMap.getScale();
        Pixmap normalsPixmap = new Pixmap(mapSize, mapSize, Pixmap.Format.RGB888);
        final int numVertices = mapSize * mapSize;
        Vector3[] vertices = new Vector3[numVertices];
        Vector3[] normals = new Vector3[numVertices];
        Vector3 pos = new Vector3();

        for (int z = 0; z < mapSize; z++) {
            for (int x = 0; x < mapSize; x++) {
                float height =  heightMap.getFromIndex(x, z);
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



        for(int x = 0; x < mapSize; x++) {
            for(int y = 0; y < mapSize; y++) {
                int i = x*mapSize + y;
                // scale from [-1 to 1] to [0 to 1]
                normals[i].nor().scl(0.5f).add(0.5f, 0.5f, 0.5f);
                normalsPixmap.setColor(normals[i].x, normals[i].y, normals[i].z, 1.0f);
                normalsPixmap.drawPixel(x, y);
            }
        }
        return normalsPixmap;


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
