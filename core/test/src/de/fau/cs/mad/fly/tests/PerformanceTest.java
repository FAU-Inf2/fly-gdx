package de.fau.cs.mad.fly.tests;

import java.util.ArrayList;

import org.junit.Test;

import com.badlogic.gdx.math.Matrix3;

/**
 * This test shows that solution 3 or 4 should be preferred when using for loops
 * in critical code sections. This has an effect, even for lists with few
 * elements.
 */
public class PerformanceTest {
    
    final float finalMemberVar = 0;
    float memberVar = 0;
    private final float PI_BY_180 = (float) Math.PI / 180.f;
    
    @Test
    public void testForLoops() {
        
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 10; i > 0; i--) {
            list.add(i);
        }
        
        // Method 1
        long millis = System.nanoTime();
        for (Integer i : list) {
            i = 2 * i;
        }
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("For loops: Iterator: " + delta + " ys.");
        
        list = new ArrayList<Integer>();
        for (int i = 10; i > 0; i--) {
            list.add(i);
        }
        
        // Method 2
        millis = System.nanoTime();
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) * 2);
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("For loops: .size() in every call: " + delta + " ys.");
        
        list = new ArrayList<Integer>();
        for (int i = 10; i > 0; i--) {
            list.add(i);
        }
        
        // Method 3
        millis = System.nanoTime();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.set(i, list.get(i) * 2);
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("For loops: local variable counted up: " + delta + " ys.");
        
        list = new ArrayList<Integer>();
        for (int i = 10; i > 0; i--) {
            list.add(i);
        }
        
        // Method 4
        millis = System.nanoTime();
        size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            list.set(i, list.get(i) * 2);
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("For loops: local variable counted down: " + delta + " ys.");
    }
    
    @Test
    public void testArrayResets() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(42);
        
        // Method 1
        long millis = System.nanoTime();
        list = new ArrayList<Integer>();
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("Arrary reset: Recreate Object: " + delta + " ys.");
        
        // Method 2
        millis = System.nanoTime();
        list.clear();
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("Arrary reset: call .clear(): " + delta + " ys.");
    }
    
    /**
     * When using primitive local variables, prefer them compared to outsourcing
     * them as members. Accessing members is always slower than accessing local
     * variables.
     */
    @Test
    public void testVariableRuse() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(42);
        
        // Method 1
        long millis = System.nanoTime();
        int size = 1000;
        int test = 0;
        for (int i = size - 1; i >= 0; i--) {
            float var = 0.1f;
            test += var;
        }
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("Variable reuse: Recreate variable: " + delta + " ys. test: " + test);
        
        // Method 2
        millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            memberVar = 0.1f;
            test += memberVar;
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("Variable reuse: use member: " + delta + " ys. test: " + test);
    }
    
    /**
     * Despite Object pooling is considered to increase the performance, it
     * should be done for heavy classes. For Mat3 it gets a performance gain for
     * more than 1 mio objects.
     */
    @Test
    public void testObjectPooling() {
        Matrix3Pool pool = new Matrix3Pool();
        
        // Method 1
        int size = 100000;
        int test = 0;
        long millis = System.nanoTime();
        Matrix3 mat;
        for (int i = size - 1; i >= 0; i--) {
            mat = pool.borrow();
            mat.inv();
            pool.giveBack(mat);
        }
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("Object pooling: use pool: " + delta + " ys. test: " + test);
        
        // Method 2
        millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            Matrix3 mat2 = new Matrix3();
            mat2.inv();
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("Object pooling: create new objects: " + delta + " ys. test: " + test);
    }
    
    /**
     * As said in {@link #testVariableRuse()} it is always more expensive to
     * access a member instead of a local variable. For some reasons this
     * behavior when executed several thousand times (about 5000). This may be
     * because of the garbage collection. Java recognizes the reuse of the
     * calculation, so you do not need to outsource it as a member constant.
     */
    @Test
    public void testAccessingConstants() {
        
        // Method 1
        int size = 10;
        float test = 0;
        long millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            test = memberVar * 1.01f;
        }
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("Accessing member: nonfinal: " + delta + " ys. test: " + test);
        
        // Method 2
        millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            test = finalMemberVar * 1.01f;
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("Accessing member: final: " + delta + " ys. test: " + test);
    }
    
    /**
     * AWhenever possible use final member for read only test.
     */
    @Test
    public void testFinalMemberOrNot() {
        
        // Method 1
        int size = 1000;
        float test = 0;
        long millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            float a = 100 + (float) Math.PI / 180.f;
            float b = 103 + (float) Math.PI / 180.f;
            float c = 142 + (float) Math.PI / 180.f;
            test = a + b + c;
        }
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("Accessing constants: recalculate: " + delta + " ys. test: " + test);
        
        // Method 2
        millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            float a = 100 + PI_BY_180;
            float b = 103 + PI_BY_180;
            float c = 142 + PI_BY_180;
            test = a + b + c;
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("Accessing constants: use member constant: " + delta + " ys. test: " + test);
    }
    
    /**
     * For {@link Matrix3}, recreating the Object has only little performance impact,
     * but it creates a lot of garbage object, which could be avoided, when
     * using {@link Matrix3#idt()} of an existing {@link Matrix3}. The performance difference becomes
     * visible, when creating a huge amount of objects, so that the garbage
     * collector is called.
     */
    @Test
    public void testMat3Performance() {
        
        // Method 1
        Matrix3 mat = new Matrix3();
        int size = 100000;
        float test = 0;
        long millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            mat.inv();
            mat.idt();
        }
        long delta = (System.nanoTime() - millis) / 100;
        System.out.println("Mat3 performance: resetting the mat: " + delta + " ys. test: " + test);
        
        // Method 2
        millis = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            mat.inv();
            mat = new Matrix3();
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("Mat3 performance: recreating object: " + delta + " ys. test: " + test);
    }
}
