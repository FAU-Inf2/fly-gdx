package de.fau.cs.mad.fly.tests;

import java.util.ArrayList;

import org.junit.Test;

/**
 * This test shows that solution 4 should be preferred when using for loops in
 * critical code sections. This has an effect, even for lists with few
 * elements
 */
public class LoopPerformanceTest {
    
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
        System.out.println("Iterator: " + delta + " ys.");
        
        // Method 2
        millis = System.nanoTime();
        for (int i = 0; i < list.size(); i++) {
            i = 2 * i;
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println(".size() in every call: " + delta + " ys.");
        
        // Method 3
        millis = System.nanoTime();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            i = 2 * i;
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("local variable: " + delta + " ys.");
        
        // Method 4
        millis = System.nanoTime();
        size = list.size();
        for (int i = size; i == 0; i--) {
            i = 2 * i;
        }
        delta = (System.nanoTime() - millis) / 100;
        System.out.println("local variable: " + delta + " ys.");
    }
}
