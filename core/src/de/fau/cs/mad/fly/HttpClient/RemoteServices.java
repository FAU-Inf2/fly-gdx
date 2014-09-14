/**
 * 
 */
package de.fau.cs.mad.fly.HttpClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import NetJavaImpl.NetHttpsJavaImpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;

/**
 * @author Qufang Fan
 * 
 */
public class RemoteServices {

	public static String getServerURL() {
		return "https://fly-devel.cloudapp.net";
	}

	public static int TIME_OUT = 1500;

	public static void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
		netHttpsJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
//		switch (Gdx.app.getType()) {
//		case Android:
//		case Desktop:
//			netHttpsJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
//			break;
//		default:
//			Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
//		}
	}

	private static NetHttpsJavaImpl netHttpsJavaImpl = new NetHttpsJavaImpl();

	public static SSLSocketFactory getSSLSocketFactory() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, KeyManagementException {

		// Load CAs from an InputStream
		// (could be from a resource or ByteArrayInputStream or ...)
		
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		// From https://www.washington.edu/itconnect/security/ca/load-der.crt

		FileHandle cafile = Gdx.files.internal("CA/server.crt");
		 
		InputStream caInput = new BufferedInputStream(cafile.read());//new FileInputStream(cafile..path()));
		Certificate ca;
		try {
			ca = cf.generateCertificate(caInput);
			// System.out.println("ca=" + ((X509Certificate)
			// ca).getSubjectDN());
		} finally {
			caInput.close();
		}

		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);

		// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);
		SSLSocketFactory fct = context.getSocketFactory();
		return fct;
	}

}
