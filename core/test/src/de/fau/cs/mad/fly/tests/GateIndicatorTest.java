package de.fau.cs.mad.fly.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.overlay.GateIndicator;

public class GateIndicatorTest {
    
    /**
     * Tests the method
     * {@link GateIndicator#pojectPointToPlane(Vector3, Vector3, Vector3)}. The
     * test numbers are from https://de.wikipedia.org/wiki/Orthogonalprojektion
     */
    @Test
    public void testProjectToPlane() {
        Vector3 up = new Vector3(2, 1, 2);
        Vector3 right = new Vector3(2, -2, -1);
        Vector3 point = new Vector3(3, 9, 6);
        
        Vector3 result = GateIndicator.projectPointToPlane(point, right, up);
        assertEquals(2f, result.x, 0.1f);
        assertEquals(7f, result.y, 0.1f);
        assertEquals(8f, result.z, 0.1f);
        
        
        up = new Vector3(0, 1, 0);
        right = new Vector3(1, 0, 0);
        point = new Vector3(4, 6, 9);
        
        result = GateIndicator.projectPointToPlane(point, right, up);
        assertEquals(4f, result.x, 0.1f);
        assertEquals(6f, result.y, 0.1f);
        assertEquals(0f, result.z, 0.1f);
        
        
        up = new Vector3(0, 1, 0);
        right = new Vector3(1, 0, 0);
        point = new Vector3(-4, -6, 1);
        
        result = GateIndicator.projectPointToPlane(point, right, up);
        assertEquals(-4f, result.x, 0.1f);
        assertEquals(-6f, result.y, 0.1f);
        assertEquals(0f, result.z, 0.1f);
        
        
        up = new Vector3(0, 0, -1);
        right = new Vector3(1, 0, 0);
        point = new Vector3(-4, -6, 1);
        
        result = GateIndicator.projectPointToPlane(point, right, up);
        assertEquals(-4f, result.x, 0.1f);
        assertEquals(0f, result.y, 0.1f);
        assertEquals(1f, result.z, 0.1f);
    }
    
    @Test
    public void testAngleBetweenTwoVectors() {
        Vector3 v1 = new Vector3(1, 5, 0);
        Vector3 v2 = new Vector3(3, 7, 0);
        float result = GateIndicator.angleBetweenTwoVectors(v1, v2);
        assertEquals(12, result, 1f);
        
        v1 = new Vector3(1, 0, 0);
        v2 = new Vector3(0, 1, 0);
        result = GateIndicator.angleBetweenTwoVectors(v1, v2);
        assertEquals(90, result, 1f);
        
        v1 = new Vector3(1, 0, 0);
        v2 = new Vector3(-1, 1, 0);
        result = GateIndicator.angleBetweenTwoVectors(v1, v2);
        assertEquals(135, result, 0.1f);
        
        
        v1 = new Vector3(-1, 1, 0);
        v2 = new Vector3(1, 0, 0);
        result = GateIndicator.angleBetweenTwoVectors(v1, v2);
        assertEquals(135, result, 0.1f);
    }
    
}
