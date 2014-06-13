package de.fau.cs.mad.fly.game;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.PHY_ScalarType;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btIndexedMesh;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manager for the btCollisionShapes.
 * <p>
 * Creates, stores and disposes all used collision shapes.
 * 
 * @author Tobias Zangl
 */
public class CollisionShapeManager implements Disposable {
	Map<String, btCollisionShape> meshShapeMap, convexShapeMap, boxShapeMap, sphereShapeMap;

	/**
	 * CollisionShapeManager
	 * <p>
	 * SHAPE TYPES:
	 * 		btSphereShape, btBoxShape, btCylinderShape, btCapsuleShape, btConeShape, btMultiSphereShape
	 * 		btConvexHullShape, btBvhTriangleMeshShape
	 * 		btCompoundShape
	 */
	public CollisionShapeManager() {
		meshShapeMap = new HashMap<String, btCollisionShape>();
		convexShapeMap = new HashMap<String, btCollisionShape>();
		boxShapeMap = new HashMap<String, btCollisionShape>();
		sphereShapeMap = new HashMap<String, btCollisionShape>();
	}
	
	/**
	 * Getter for a mesh shape with given shapeId.
	 */
	public btCollisionShape getMeshShape(String shapeId) {
		return meshShapeMap.get(shapeId);
	}
	
	/**
	 * Getter for a convex shape with given shapeId.
	 */
	public btCollisionShape getConvexShape(String shapeId) {
		return convexShapeMap.get(shapeId);
	}
	
	/**
	 * Getter for a box shape with given shapeId.
	 */
	public btCollisionShape getBoxShape(String shapeId) {
		return boxShapeMap.get(shapeId);
	}
	
	/**
	 * Getter for a sphere shape with given shapeId.
	 */
	public btCollisionShape getSphereShape(String shapeId) {
		return sphereShapeMap.get(shapeId);
	}
	
	/**
	 * Creates a new btBvhTriangleMeshShape out of the instance if the shape is not already created.
	 */
	public btCollisionShape createMeshShape(String shapeId, GameObject instance) {
		btCollisionShape shape = meshShapeMap.get(shapeId);
		if(shape != null) {
			return shape;
		}
		
		Mesh mesh = instance.model.meshes.first();
		
		short[] indices = new short[mesh.getNumIndices()];
		float[] vertices = new float[mesh.getNumVertices()*mesh.getVertexSize()/4];
		mesh.getIndices(indices);
		mesh.getVertices(vertices);
		ShortBuffer indexData = ShortBuffer.wrap(indices);
		FloatBuffer vertexData = FloatBuffer.wrap(vertices);
		
		btIndexedMesh indexedMesh = new btIndexedMesh();
		indexedMesh.setNumTriangles(mesh.getNumIndices()/3);
		indexedMesh.setNumVertices(mesh.getNumVertices());
		indexedMesh.setTriangleIndexStride(6);
		indexedMesh.setIndexType(PHY_ScalarType.PHY_SHORT);
		indexedMesh.setVertexStride(mesh.getVertexSize());
		indexedMesh.setVertexType(PHY_ScalarType.PHY_FLOAT);
		indexedMesh.setTriangleIndexBase(indexData);
		indexedMesh.setVertexBase(vertexData);

		btTriangleIndexVertexArray meshInterface = new btTriangleIndexVertexArray();
		meshInterface.addIndexedMesh(indexedMesh, PHY_ScalarType.PHY_SHORT);
		btBvhTriangleMeshShape meshShape = new btBvhTriangleMeshShape(meshInterface, true);
		
		Gdx.app.log("CollisionShapeManager", "Created mesh shape " + shapeId);
		
		meshShapeMap.put(shapeId, meshShape);
		return meshShape;
	}

	/**
	 * Creates a new btConvexHullShape out of the instance if the shape is not already created.
	 */
	public btCollisionShape createConvexShape(String shapeId, int userValue, GameObject instance) {
		btCollisionShape shape = convexShapeMap.get(shapeId);
		if(shape != null) {
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
		
		Gdx.app.log("CollisionShapeManager", "Created convex shape " + shapeId);

		convexShapeMap.put(shapeId, convexShape);
		return convexShape;
	}

	/**
	 * Creates a new btBoxShape with given box sizes if the shape is not already created.
	 */
	public btCollisionShape createBoxShape(String shapeId, Vector3 box) {
		btCollisionShape shape = boxShapeMap.get(shapeId);
		if(shape != null) {
			return shape;
		}
		
		btBoxShape boxShape = new btBoxShape(box);

		Gdx.app.log("CollisionShapeManager", "Created box shape " + shapeId);
		
		boxShapeMap.put(shapeId, boxShape);
		return boxShape;
	}
	
	/**
	 * Creates a new btSphereShape with given radius if the shape is not already created.
	 */
	public btCollisionShape createSphereShape(String shapeId, float radius) {
		btCollisionShape shape = sphereShapeMap.get(shapeId);
		if(shape != null) {
			return shape;
		}
		
		btSphereShape sphereShape = new btSphereShape(radius);

		Gdx.app.log("CollisionShapeManager", "Created sphere shape " + shapeId);
		
		sphereShapeMap.put(shapeId, sphereShape);
		return sphereShape;
	}
	
	@Override
	public void dispose() {
		for(Map.Entry<String, btCollisionShape> entry : meshShapeMap.entrySet()) {
			entry.getValue().dispose();

			/*
				  meshShape.delete();
				  meshShape = null;
				  meshInterface.delete();
				  meshInterface = null;
				  indexedMesh.dispose();
				  indexedMesh.delete();
				  indexedMesh = null;
			*/
		}
		for(Map.Entry<String, btCollisionShape> entry : convexShapeMap.entrySet()) {
			entry.getValue().dispose();
		}
		for(Map.Entry<String, btCollisionShape> entry : boxShapeMap.entrySet()) {
			entry.getValue().dispose();
		}
		for(Map.Entry<String, btCollisionShape> entry : sphereShapeMap.entrySet()) {
			entry.getValue().dispose();
		}

		Gdx.app.log("CollisionShapeManager", "Collision shapes disposed");
	}
}
