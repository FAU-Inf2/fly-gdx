package de.fau.cs.mad.fly.tests.features.upgrades.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.fau.cs.mad.fly.features.upgrades.types.SpeedUpgradeEffect;

/**
 * Testclass for {@link SpeedUpgradeEffect}.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class TemporarySpeedUpgradeTest {
    
    private static float epsilon = 0.001f;
    
    @Test(expected = IllegalArgumentException.class)
    public void testZeroMaxSpeedupArgument() {
        @SuppressWarnings("unused")
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(0, 1, 1, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxSpeedupArgument() {
        @SuppressWarnings("unused")
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(-1, 1, 1, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeSpeedupTimeArgument() {
        @SuppressWarnings("unused")
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(1, -1, 1, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxTimeArgument() {
        @SuppressWarnings("unused")
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(1, 1, -1, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeSlowdownTimeArgument() {
        @SuppressWarnings("unused")
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(1, 1, 1, -1);
    }
    
    @Test
    public void testZeroTotalTimeArgument() {
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(1, 0, 0, 0);
        assertFalse(testUpgrade.isActive());
    }
    
    @Test
    public void testZeroSpeedupAndTearDownTime() {
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(2, 0, 100, 0);
        assertEquals(SpeedUpgradeEffect.NO_SPEEDUP, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        assertTrue(testUpgrade.isActive());
        
        testUpgrade.update(0);
        assertTrue(testUpgrade.isActive());
        assertEquals(testUpgrade.getCurrentSpeedupFactor(), 2f, epsilon);
        
        testUpgrade.update(50);
        assertTrue(testUpgrade.isActive());
        assertEquals(testUpgrade.getCurrentSpeedupFactor(), 2f, epsilon);
        
        testUpgrade.update(49);
        assertTrue(testUpgrade.isActive());
        assertEquals(testUpgrade.getCurrentSpeedupFactor(), 2f, epsilon);
        
        testUpgrade.update(1);
        assertFalse(testUpgrade.isActive());
        assertEquals(SpeedUpgradeEffect.NO_SPEEDUP, testUpgrade.getCurrentSpeedupFactor(), epsilon);
    }
    
    @Test
    public void testWithSpeedupAndTearDownTime() {
        float maxSpeedup = 2f;
        SpeedUpgradeEffect testUpgrade = new SpeedUpgradeEffect(maxSpeedup, 50, 100, 50);
        assertEquals(SpeedUpgradeEffect.NO_SPEEDUP, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        assertTrue(testUpgrade.isActive());
        
        testUpgrade.update(0);
        assertTrue(testUpgrade.isActive());
        assertEquals(SpeedUpgradeEffect.NO_SPEEDUP, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        
        testUpgrade.update(10);
        assertTrue(testUpgrade.isActive());
        float expectedSpeedUpFactor = SpeedUpgradeEffect.NO_SPEEDUP + (1f / 5f * (maxSpeedup - SpeedUpgradeEffect.NO_SPEEDUP));
        assertEquals(expectedSpeedUpFactor, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        
        testUpgrade.update(40);
        assertTrue(testUpgrade.isActive());
        assertEquals(maxSpeedup, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        
        testUpgrade.update(1);
        assertTrue(testUpgrade.isActive());
        assertEquals(maxSpeedup, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        
        testUpgrade.update(98);
        assertTrue(testUpgrade.isActive());
        assertEquals(maxSpeedup, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        
        testUpgrade.update(11);
        assertTrue(testUpgrade.isActive());
        float expectedSlowDownFactor = SpeedUpgradeEffect.NO_SPEEDUP + (4f / 5f * (maxSpeedup - SpeedUpgradeEffect.NO_SPEEDUP));
        assertEquals(expectedSlowDownFactor, testUpgrade.getCurrentSpeedupFactor(), epsilon);
        
        testUpgrade.update(100);
        assertFalse(testUpgrade.isActive());
        assertEquals(SpeedUpgradeEffect.NO_SPEEDUP, testUpgrade.getCurrentSpeedupFactor(), epsilon);
    }
}
