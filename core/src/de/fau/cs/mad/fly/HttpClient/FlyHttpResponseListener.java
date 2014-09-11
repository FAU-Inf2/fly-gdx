package de.fau.cs.mad.fly.HttpClient;

/**
 * Listener to be able to do custom logic
 */
public interface FlyHttpResponseListener {
    
    /**
     * Called when the remote service has been processed and the response has
     * been converted to a business object.
     * 
     */
    void successful(Object obj);
    
    /**
     * Called if the remote service failed because an exception when processing
     * the HTTP request, could be a timeout any other reason or an HTTP error.
     * 
     * @param msg
     *            If the HTTP request failed because an Exception, it
     *            encapsulates it to give more information.
     */
    void failed(String msg);
    
    void cancelled();
}
