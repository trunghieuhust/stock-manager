package hedspi.k55.sm.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class DataFetch {
	private String result;
	private String url;

	public DataFetch(String url) {
		this();
		this.url = url;
		if (isHTTPS(url))
			result = getFromHTTPS(url);
		else
			result = getFromHTTP(url);
	}

	// default comtructor
	public DataFetch() {

	}

	public boolean isHTTPS(String url) {
		String str;
		str = url.toLowerCase(Locale.ENGLISH);
		return str.contains("https");
	}

	public void setURL(String url) {
		this.url = url;
		if (isHTTPS(url))
			result = getFromHTTPS(url);
		else
			result = getFromHTTP(url);
	}

	public String getURL() {
		return url;
	}

	protected String getFromHTTP(String url) {
		String string = null;

		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			string = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return string
		return string;
	}

	public String getResult() {
		return result;
	}

	protected String getFromHTTPS(String url) {
		String string = null;

		try {
			URL https_url = new URL(url);
			HttpsURLConnection https = (HttpsURLConnection) https_url
					.openConnection();
			string = getContent(https.getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return string;
	}

	private String getContent(InputStream is) {
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer(1000);
		String temp = null;
		try {
			while ((temp = bf.readLine()) != null) {
				sb.append(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
