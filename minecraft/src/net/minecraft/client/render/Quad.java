package net.minecraft.client.render;

import org.lwjgl.util.vector.Vector3f;

public class Quad {
    private Vector3f[] vertices;
    private float depth;

    public Quad(Vector3f[] vertices) {
        this.vertices = vertices;
        this.depth = 0.0f;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public float getDepth() {
        return depth;
    }

    public void calculateDepthRelativeToCamera(Vector3f cameraPosition) {
        float totalDepth = 0.0f;
        for (Vector3f vertex : vertices) {
            totalDepth += Vector3f.sub(vertex, cameraPosition, null).length();
        }
        this.depth = totalDepth / vertices.length;
    }
}
