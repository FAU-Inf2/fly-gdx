package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameObject;

public class Gate implements Disposable {
    public final int id;
    public int score;
    public GameObject display;
    public GameObject goal;
    public int passedTimes = 0;
    public int[] successors;
    
    public Gate(int id) {
        this.id = id;
        this.score = 50;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            return id == ((Gate) o).hashCode();
        }
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public String toString() {
        return "#<Gate " + id + ">";
    }
    
    @Override
    public void dispose() {
        CollisionDetector.getInstance().removeRigidBody(display);
        CollisionDetector.getInstance().removeRigidBody(goal);
        
        display.dispose();
        goal.dispose();
    }
}
