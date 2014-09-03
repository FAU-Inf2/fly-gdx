package de.fau.cs.mad.fly.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.overlay.GateIndicator;

public class GateIndicatorTest {
    
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
        
        v1 = new Vector3(5, 0, 0);
        v2 = new Vector3(0, 2, 0);
        result = GateIndicator.angleBetweenTwoVectors(v1, v2);
        assertEquals(90, result, 1f);
        
        v1 = new Vector3(1, 0, 0);
        v2 = new Vector3(1, 1, 0);
        result = GateIndicator.angleBetweenTwoVectors(v1, v2);
        assertEquals(45, result, 1f);
        
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
