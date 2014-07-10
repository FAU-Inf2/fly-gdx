package de.fau.cs.mad.fly.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manager for the btRigidBodyConstructionInfo.
 * <p>
 * Creates, stores and disposes all used rigid body construction infos.
 * 
 * @author Tobias Zangl
 */
public class RigidBodyInfoManager implements Disposable {

	/**
	 * Map to save the different rigid body construction infos.
	 */
	Map<String, btRigidBodyConstructionInfo> rigidBodyInfoMap;

	public RigidBodyInfoManager() {
		rigidBodyInfoMap = new HashMap<String, btRigidBodyConstructionInfo>();
	}
	
	/**
	 * Getter for a rigid body construction info.
	 * @param mass			The id of the needed rigid body construction info.
	 * @return				btRigidBodyConstructionInfo
	 */
	public btRigidBodyConstructionInfo getRigidBodyInfo(String infoId) {
		return rigidBodyInfoMap.get(infoId);
	}
	
	/**
	 * Creates a new rigid body construction info if its not already created.
	 * @param infoId		The id of the needed rigid body construction info.
	 * @param shape			The used shape to calculate the local inertia.
	 * @param mass			The mass of the object.
	 * @return				btRigidBodyConstructionInfo
	 */
	public btRigidBodyConstructionInfo createRigidBodyInfo(String infoId, btCollisionShape shape, float mass) {
		btRigidBodyConstructionInfo constructionInfo = rigidBodyInfoMap.get(infoId);
		if(constructionInfo != null) {
			return constructionInfo;
		}
		
		Vector3 localInertia = new Vector3();

        if(mass > 0f) {
            shape.calculateLocalInertia(mass, localInertia);
        } else {
            localInertia.set(0.0f, 0.0f, 0.0f);
        }
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		
		//Gdx.app.log("RigidBodyInfoManager", "Created rigid body info: " + infoId + " with mass " + mass);
		
		rigidBodyInfoMap.put(infoId, constructionInfo);
		return constructionInfo;
	}
	
	@Override
	public void dispose() {
		for(btRigidBodyConstructionInfo info : rigidBodyInfoMap.values()) {
			info.dispose();
		}
		
		rigidBodyInfoMap.clear();
	}
}
