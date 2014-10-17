package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for the btCollisionShapes.
 * <p>
 * Creates, stores and disposes all used collision shapes.
 * <p>
 * Available shape types: boxShape: if a simple box around the object is enough.
 * sphereShape: if a simple sphere fits the object better a box. convexShape: if
 * the object has a convex but not simple form. meshShape: if the object has
 * holes and they have to be identified by the collision detector.
 * 
 * @author Tobias Zangl
 */
public class CollisionShapeManager implements Disposable {
    
    /**
     * Maps to save the different shape types.
     */
    Map<String, btCollisionShape> meshShapeMap, convexShapeMap, boxShapeMap, sphereShapeMap;
    
    /**
     * CollisionShapeManager
     * <p>
     * SHAPE TYPES: btSphereShape, btBoxShape, btCylinderShape, btCapsuleShape,
     * btConeShape, btMultiSphereShape btConvexHullShape, btBvhTriangleMeshShape
     * btCompoundShape
     */
    public CollisionShapeManager() {
        meshShapeMap = new HashMap<String, btCollisionShape>();
        convexShapeMap = new HashMap<String, btCollisionShape>();
        boxShapeMap = new HashMap<String, btCollisionShape>();
        sphereShapeMap = new HashMap<String, btCollisionShape>();
    }
    
    /**
     * Getter for a mesh shape with given shapeId.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @return btCollisionShape
     */
    public btCollisionShape getMeshShape(String shapeId) {
        return meshShapeMap.get(shapeId);
    }
    
    /**
     * Getter for a convex shape with given shapeId.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @return btCollisionShape
     */
    public btCollisionShape getConvexShape(String shapeId) {
        return convexShapeMap.get(shapeId);
    }
    
    /**
     * Getter for a box shape with given shapeId.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @return btCollisionShape
     */
    public btCollisionShape getBoxShape(String shapeId) {
        return boxShapeMap.get(shapeId);
    }
    
    /**
     * Getter for a sphere shape with given shapeId.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @return btCollisionShape
     */
    public btCollisionShape getSphereShape(String shapeId) {
        return sphereShapeMap.get(shapeId);
    }
    
    /**
     * Creates a new static mesh shape out of the instance if the shape is not
     * already created.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @param box
     *            The game object to construct the shape.
     * @return btCollisionShape
     */
    public btCollisionShape createStaticMeshShape(String shapeId, final GameObject instance) {
        btCollisionShape shape = meshShapeMap.get(shapeId);
        if (shape != null) {
            return shape;
        }
        
        btCollisionShape meshShape = Bullet.obtainStaticNodeShape(instance.model.nodes);
        
        // Gdx.app.log("CollisionShapeManager", "Created static mesh shape: " +
        // shapeId);
        
        meshShapeMap.put(shapeId, meshShape);
        return meshShape;
    }
    
    /**
     * Creates a new btConvexHullShape out of the instance if the shape is not
     * already created.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @param instance
     *            The game object to construct the shape.
     * @return btCollisionShape
     */
    public btCollisionShape createConvexShape(String shapeId, final GameObject instance) {
        btCollisionShape shape = convexShapeMap.get(shapeId);
        if (shape != null) {
            return shape;
        }
        
        final Mesh mesh = instance.model.meshes.get(0);
        final btConvexHullShape hullShape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
        
        // now optimize the shape
        final btShapeHull hull = new btShapeHull(hullShape);
        hull.buildHull(hullShape.getMargin());
        final btConvexHullShape convexShape = new btConvexHullShape(hull);
        
        hullShape.dispose();
        hull.dispose();
        
        // Gdx.app.log("CollisionShapeManager", "Created convex shape: " +
        // shapeId);
        
        convexShapeMap.put(shapeId, convexShape);
        return convexShape;
    }
    
    /**
     * Creates a new btBoxShape with given box sizes if the shape is not already
     * created.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @param box
     *            The size of the box shape.
     * @return btCollisionShape
     */
    public btCollisionShape createBoxShape(String shapeId, Vector3 box) {
        btCollisionShape shape = boxShapeMap.get(shapeId);
        if (shape != null) {
            return shape;
        }
        
        btBoxShape boxShape = new btBoxShape(box);
        
        // Gdx.app.log("CollisionShapeManager", "Created box shape: " +
        // shapeId);
        
        boxShapeMap.put(shapeId, boxShape);
        return boxShape;
    }
    
    /**
     * Creates a new btSphereShape with given radius if the shape is not already
     * created.
     * 
     * @param shapeId
     *            The id of the needed shape.
     * @param radius
     *            The radius of the shape sphere.
     * @return btCollisionShape
     */
    public btCollisionShape createSphereShape(String shapeId, float radius) {
        btCollisionShape shape = sphereShapeMap.get(shapeId);
        if (shape != null) {
            return shape;
        }
        
        btSphereShape sphereShape = new btSphereShape(radius);
        
        // Gdx.app.log("CollisionShapeManager", "Created sphere shape: " +
        // shapeId);
        
        sphereShapeMap.put(shapeId, sphereShape);
        return sphereShape;
    }
    
    @Override
    public void dispose() {
        meshShapeMap.clear();
        convexShapeMap.clear();
        boxShapeMap.clear();
        sphereShapeMap.clear();
    }
}
