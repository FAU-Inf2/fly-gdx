package de.fau.cs.mad.fly.profile;

/**
 * Generic Interface for change listener.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 *
 * @param <T>
 */
public interface ChangeListener<T> {
    /** Is called when property T has changed */
    public void changed(T t);
}
