package de.fau.cs.mad.fly.profile;

/*
 * Detail info of score.
 * 
 * @author Qufang Fan
 */
public class ScoreDetail {
	
	private String detailName;
	private String value;
	
	
	public ScoreDetail(String detailName, String value) {
		this.detailName = detailName;
		this.value = value;
	}
	
	public String getDetailName() {
		return detailName;
	}
	
	/*
	 * the local string for describing the detail
	 */
	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	

}
