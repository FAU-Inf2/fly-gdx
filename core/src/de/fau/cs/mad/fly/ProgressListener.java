package de.fau.cs.mad.fly;

/**
 * Created by danyel on 01/07/14.
 */
public interface ProgressListener<T> {
    public void progressStarted();
    
    public void progressUpdated(float percent);
    
    public void progressFinished(T t);
    
    public static abstract class ProgressAdapter<T> implements ProgressListener<T> {
        @Override
        public void progressStarted() {
        }
        
        @Override
        public void progressUpdated(float percent) {
        }
        
        @Override
        public void progressFinished(T t) {
        }
    }
}