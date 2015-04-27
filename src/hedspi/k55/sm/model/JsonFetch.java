package hedspi.k55.sm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonFetch extends DataFetch {
	private JSONObject json;
	private String get;

	public JsonFetch() {

	}

	public JsonFetch(String url) {
		setURL(url);
	}

	@Override
	public void setURL(String url) {
		DataFetch df = new DataFetch(url);
		String jsonString = convertToValidJsonString(df.getResult());
		get = jsonString;
		try {
			json = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String convertToValidJsonString(String string) {
		StringBuilder sb = new StringBuilder(string);
		sb.delete(0, 4);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public JSONObject getObject() {
		return json;
	}

	public String getStr() {
		return get;
	}
}
