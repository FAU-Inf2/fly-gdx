package de.fau.cs.mad.fly.HttpClient;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class GetLevelHighScoreService {

	public class ResponseItem {
		public int Score;
		public String Username;
		public int FlyID;
	}

	public GetLevelHighScoreService(FlyHttpResponseListener listener) {
		this.listener = listener;
	}

	private final FlyHttpResponseListener listener;

	public void execute(int levelID) {
		HttpRequest request = new HttpRequest(HttpMethods.GET);
		request.setTimeOut(2500);
		request.setUrl(RemoteServices.getServerURL() + "/levels/" + levelID + "/highscores");

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				HttpStatus status = httpResponse.getStatus();
				if (status.getStatusCode() == HttpStatus.SC_OK) {
					JsonReader reader = new JsonReader();
					JsonValue json = reader.parse(httpResponse.getResultAsStream());
					List<ResponseItem> results = new ArrayList<ResponseItem>();
					for (JsonValue item : json) {
						ResponseItem res = new ResponseItem();
						res.Score = item.getInt("points");
						res.FlyID = item.get("user").getInt("id");
						res.Username = item.get("user").getString("name");
						results.add(res);
					}
					listener.successful(results);
				} else {
					listener.failed(status.toString());
				}

			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log("GetLevelHighScoreService", t.getMessage());
				listener.failed(t.getLocalizedMessage());
			}

			@Override
			public void cancelled() {
				listener.cancelled();
			}
		});
	}
}
