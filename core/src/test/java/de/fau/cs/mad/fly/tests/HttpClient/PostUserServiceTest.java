package de.fau.cs.mad.fly.tests.HttpClient;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.HttpClient.FlyHttpResponseListener;
import de.fau.cs.mad.fly.HttpClient.PostUserService;
import de.fau.cs.mad.fly.HttpClient.RemoteServices;

public class PostUserServiceTest {

	// @Test
	public void test() {
		FlyHttpResponseListener listener = new FlyHttpResponseListener() {
			@Override
			public void successful(Object obj) {
				System.out.print("ok");
			}

			@Override
			public void failed(String msg) {
				System.out.print(msg);

			}

			@Override
			public void cancelled() {
			}

		};
		PostUserService postUser = new PostUserService(listener);

		postUser.execute("fan");
	}

	// @Test
	public void Test1() {

		String subURL = "/users";
		String userData = "{ \"user\": { \"name\": \"" + "fanfan" + "\" } }";
		HttpRequest post = new HttpRequest(HttpMethods.POST);
		post.setHeader("Content-Type", "application/json");
		post.setHeader("Upgrade", "HTTP/1.1, HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11");
		post.setUrl(RemoteServices.getServerURL() + subURL);
		post.setTimeOut(RemoteServices.TIME_OUT);
		post.setContent(userData);

		// Gdx.app.log("PostUserService", "sending");
		Gdx.net.sendHttpRequest(post, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {

				HttpStatus status = httpResponse.getStatus();
				if (status.getStatusCode() == HttpStatus.SC_OK) {
					JsonReader reader = new JsonReader();
					JsonValue val = reader.parse(httpResponse.getResultAsStream());
					int id = val.getInt("id");
					System.out.print(id + "");
					Gdx.app.log("PostUserService", id + "");
					// listener.handleHttpResponse(id);
				} else {

				}
			}

			@Override
			public void failed(Throwable t) {

			}

			@Override
			public void cancelled() {
				// listener.cancelled();
			}
		});
	}

}
