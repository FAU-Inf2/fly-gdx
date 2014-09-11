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
	
	public class ResponseData {
		public List<LevelRecords> records = new ArrayList< LevelRecords>();	
		
		public void addRecord( int levelId, String levelName, RecordItem record ){
			for(LevelRecords levelRecords : records) {
				if( levelRecords.levelID == levelId) {
					levelRecords.records.add(record);
					return;
				}				
			}
			
			LevelRecords levelRecords = new LevelRecords();
			levelRecords.levelID = levelId;
			levelRecords.levelName = levelName;
			levelRecords.records.add(record);
			records.add(levelRecords);
		}
	}
	public class LevelRecords {
		public int levelID;
		public String levelName;
		public List<RecordItem> records = new ArrayList<RecordItem>();		
	}

	public class RecordItem {
		public int score;
		public String username;
		public int flyID;
		public int rank;
	}

	public GetLevelHighScoreService(FlyHttpResponseListener listener) {
		this.listener = listener;
	}

	private final FlyHttpResponseListener listener;
	
	private static int TOP = 4;

	/**
	 * sent get level or level group highscores request to server
	 * @param type 1: get level high score; 2: get group high score
	 * @param levelID or level group id
	 */
	public void execute(int type, int levelID) {
		HttpRequest request = new HttpRequest(HttpMethods.GET);
		request.setTimeOut(2500);
		if( type==1){
		request.setUrl(RemoteServices.getServerURL() + "/levels/" + levelID + "/highscores?top=" + TOP);
		} else {
			request.setUrl(RemoteServices.getServerURL() + "/level_groups/" + levelID + "/highscores?top=" + TOP);
		}

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				HttpStatus status = httpResponse.getStatus();
				if (status.getStatusCode() == HttpStatus.SC_OK) {
					JsonReader reader = new JsonReader();
					JsonValue json = reader.parse(httpResponse.getResultAsStream());
					ResponseData response = new ResponseData();
					
					//List<ResponseItem> results = new ArrayList<ResponseItem>();
					for (JsonValue item : json) {
						RecordItem res = new RecordItem();
						res.score = item.getInt("points");
						res.rank = item.getInt("rank");
						res.flyID = item.get("user").getInt("id");
						res.username = item.get("user").getString("name");
						int levelID = item.get("level").getInt("id");
						String levelName = item.get("level").getString("name");
						response.addRecord(levelID, levelName, res);
					}
					listener.successful(response);
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
