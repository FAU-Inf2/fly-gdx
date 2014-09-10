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

import de.fau.cs.mad.fly.profile.Score;

public class PostHighscoreService {

	public class ResponseData{
		public int scoreID;
		public int rank;
		public List<ResponseItem> above = new ArrayList<ResponseItem>();
		public List<ResponseItem> below = new ArrayList<ResponseItem>();
	}
	public class ResponseItem {
		public int Score;
		public String Username;
		public int FlyID;
		public int rank;
	}

	public static class RequestData {
		public Score Score;
		public int LevelID;
		public int FlyID;
		public int LevelgroupID;
	}

	public PostHighscoreService(FlyHttpResponseListener listener, RequestData requestData) {
		this.listener = listener;
		this.requestData = requestData;
	}

	private final RequestData requestData;
	private final FlyHttpResponseListener listener;

	public void execute() {
		HttpRequest request = new HttpRequest(HttpMethods.POST);
		request.setTimeOut(RemoteServices.TIME_OUT);
		request.setHeader("Content-Type", "application/json");
		request.setUrl(RemoteServices.getServerURL() + "/highscores");
		String res = "{ \"highscore\": { \"points\": " + requestData.Score.getTotalScore() + ", \"user_id\": "
				+ requestData.FlyID + ", \"level_id\": " + requestData.LevelID + " } }";
		request.setContent(res);
		Gdx.app.log("PostHighscoreService", res);

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				HttpStatus status = httpResponse.getStatus();
				if (status.getStatusCode() == HttpStatus.SC_CREATED) {
					JsonReader reader = new JsonReader();
					JsonValue json = reader.parse(httpResponse.getResultAsStream());
					ResponseData response = new ResponseData();
					//List<ResponseItem> results = new ArrayList<ResponseItem>();
					JsonValue scoreJS = json.get("highscore");
					response.scoreID = scoreJS.getInt("id");
					response.rank = scoreJS.getInt("rank");					
					
					for (JsonValue item : json.get("above")) {
						ResponseItem res = new ResponseItem();
						res.Score = item.getInt("points");
						res.FlyID = item.get("user").getInt("id");
						res.Username = item.get("user").getString("name");
						response.above.add(res);
						Gdx.app.log("PostHighscoreService", "" + res.Score);
					}
					
					for (JsonValue item : json.get("below")) {
						ResponseItem res = new ResponseItem();
						res.Score = item.getInt("points");
						res.FlyID = item.get("user").getInt("id");
						res.Username = item.get("user").getString("name");
						response.below.add(res);
						Gdx.app.log("PostHighscoreService", "" + res.Score);
					}

					listener.successful(response);
				} else {
					listener.failed(String.valueOf(status.getStatusCode()));
				}
				Gdx.app.log("PostHighscoreService", "server return code: " + String.valueOf(status.getStatusCode()));
			}

			@Override
			public void failed(Throwable t) {
				listener.failed(t.getLocalizedMessage());
				Gdx.app.log("PostHighscoreService", "server return msg:" + t.getMessage());
			}

			@Override
			public void cancelled() {
				listener.cancelled();
			}
		});
	}

}
