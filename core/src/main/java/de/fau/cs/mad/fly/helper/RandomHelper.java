package de.fau.cs.mad.fly.helper;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Helper class for calculating random stuff like random vectors.
 * 
 * @author Tobi
 * 
 */
public class RandomHelper {
    
    /**
     * Calculates a random vector with x, y and z value between 0.0f and max
     * value.
     * 
     * @param max
     *            The maximum size of the random vector.
     * @return random vector.
     */
    public static Vector3 getRandomVector(float max) {
        return getRandomVector(new Vector3(max, max, max));
    }
    
    /**
     * Calculates a random vector with x, y and z value between min and max
     * value.
     * 
     * @param min
     *            The minimum size of the random vector.
     * @param max
     *            The maximum size of the random vector.
     * @return random vector.
     */
    public static Vector3 getRandomVector(float min, float max) {
        return getRandomVector(new Vector3(min, min, min), new Vector3(max, max, max));
    }
    
    /**
     * Calculates a random vector positive or negative within a given absolute
     * size.
     * 
     * @param size
     *            The maximum absolute size of the random vector.
     * @return random vector.
     */
    public static Vector3 getRandomVectorInSize(final Vector3 size) {
        Vector3 v = new Vector3();
        v.x = MathUtils.random(-size.x, size.x);
        v.y = MathUtils.random(-size.y, size.y);
        v.z = MathUtils.random(-size.z, size.z);
        return v;
    }
    
    /**
     * Calculates a random vector between 0.0f and maximum vector.
     * 
     * @param max
     *            The maximum size of the random vector.
     * @return random vector.
     */
    public static Vector3 getRandomVector(final Vector3 max) {
        Vector3 v = new Vector3();
        v.x = MathUtils.random(0.0f, max.x);
        v.y = MathUtils.random(0.0f, max.y);
        v.z = MathUtils.random(0.0f, max.z);
        return v;
    }
    
    /**
     * Calculates a random vector between a min and a max vector.
     * 
     * @param min
     *            The minimum vector size.
     * @param max
     *            The maximum vector size.
     * @return random vector.
     */
    public static Vector3 getRandomVector(final Vector3 min, final Vector3 max) {
        Vector3 v = new Vector3();
        v.x = MathUtils.random(min.x, max.x);
        v.y = MathUtils.random(min.y, max.y);
        v.z = MathUtils.random(min.z, max.z);
        return v;
    }
    
}
