/**
 * 
 */
package de.fau.cs.mad.fly.HttpClient;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author Qufang Fan
 * 
 */
public class RemoteServices {
    
    public static String getServerURL() {
    	return "https://fly.bellatrix.uberspace.de";
    }
    
    /**
     * The server level ID is globally unique and created out of the group ID
     * and the level ID.
     * 
     * @param clientLevelId
     * @param clientLevelGroupId
     * @return ServerLevelId
     */
    public static String getServerLevelId(int clientLevelGroupId, int clientLevelId) {
        return String.valueOf(clientLevelGroupId * 1000 + clientLevelId);
    }
    
    /**
     * The inverse operation of {@link #getServerLevelId(int, int)} to calculate
     * the client Level ID.
     * 
     * @param serverId
     * @return clientLevelID
     */
    public static int getClientLevelID(int serverId) {
        return serverId % 1000;
    }
    
    public static final int TIME_OUT = 1500;
    
    public static void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
        netHttpsJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }
    
    private static NetHttpsJavaImpl netHttpsJavaImpl = new NetHttpsJavaImpl();
    
    public static SSLSocketFactory getSSLSocketFactory() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return (SSLSocketFactory)SSLSocketFactory.getDefault();
    }
    
}
