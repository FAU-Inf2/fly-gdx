package de.fau.cs.mad.fly;

/**
 * Interface that defines an object, that can be loaded asynchronously, like
 * {@link Fly} or {@link Level}.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 * @param <T>
 *            The object that is loaded.
 */
public interface Loadable<T> {
    
    /**
     * Method to add a listener for the progress.
     * 
     * @param listener
     */
    public void addProgressListener(ProgressListener<T> listener);
    
    /**
     * Method that is called, everytime when the loading status should be
     * updated.
     */
    public void update();
}
