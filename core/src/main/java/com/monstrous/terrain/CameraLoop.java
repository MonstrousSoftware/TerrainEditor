package com.monstrous.terrain;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;

/** Helper class to move camera in a spline loop
 * Can also display the loop for debug purposes.
 */
public class CameraLoop {
    private CatmullRomSpline<Vector3> cameraSpline;
    private final ShapeRenderer shapeRenderer;
    private final Vector3[] pathPoints = new Vector3[100];	// to render spline (debug)
    private final Vector3 tmp = new Vector3();
    private final Camera cam;

    public CameraLoop(Camera cam, float terrainAltitude) {
        this.cam = cam;
        buildCameraPath(terrainAltitude);
        shapeRenderer = new ShapeRenderer();
    }


    // not guaranteed to not collide into terrain
    private void buildCameraPath(float terrainAltitude) {
        float ht = 0.5f*terrainAltitude;
        float scl = 16f;

        Vector3[] controlPoints = {
            new Vector3(-2000*scl, ht+400f*scl, 2000*scl),
            new Vector3(2000*scl, ht+500*scl, 2500*scl),

            new Vector3(2500*scl, ht+800*scl, -3000*scl),

            new Vector3(-1500*scl, ht+300*scl, -2400*scl),
            new Vector3(-500*scl, ht+800*scl, -500*scl),

            new Vector3(500*scl, ht+400*scl, 500*scl),

        };
        cameraSpline = new CatmullRomSpline<Vector3>(controlPoints, true);

        // fill array of points for debug render
        for(int i = 0; i < 100; i++) {
            Vector3 out = new Vector3();
            cameraSpline.valueAt(out, i/100f);
            pathPoints[i] = out;
        }
    }

    /** call this from render loop to advance camera position & orientation */
    public void moveCameraAlongSpline(float deltaTime) {
        float t = 0.015f*deltaTime;
        if (t > 1)
            t -= (int)t;
        cameraSpline.valueAt(tmp, t);
        cam.position.set(tmp);
        cameraSpline.derivativeAt(tmp, t);
        cam.direction.set(tmp);
        cam.up.set(Vector3.Y);
        cam.update();
    }

    /** render camera path as a red line (debug option) */
    public void renderPath() {
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        for(int i = 0; i < 100-1; i++)
        {
            shapeRenderer.line(pathPoints[i], pathPoints[i+1]);
        }
        shapeRenderer.line(pathPoints[99], pathPoints[0]);
        shapeRenderer.end();
    }
}
