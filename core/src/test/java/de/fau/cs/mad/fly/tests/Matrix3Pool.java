package de.fau.cs.mad.fly.tests;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.math.Matrix3;

public class Matrix3Pool {
    
    private Queue<Matrix3> ready;
    
    public Matrix3Pool() {
        int size = 10;
        ready = new LinkedList<Matrix3>();
        for(int i = size; i >= 0; i--) {
            Matrix3 mat = new Matrix3();
            ready.add(mat);
        }
    }
    
    public Matrix3 borrow() {
        return ready.remove();
    }
    
    public void giveBack(Matrix3 mat) {
        ready.add(mat);
    }
}
