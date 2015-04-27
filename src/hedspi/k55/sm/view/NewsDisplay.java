package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NewsDisplay extends SherlockActivity implements Iview {
	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(getNewsTitle());
		webview = (WebView) findViewById(R.id.news_view);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.loadUrl(getURL());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MENU ITEM ID", item.getItemId() + "");
		switch (item.getItemId()) {
		case 16908332:
			finish();
			break;
		case R.id.homeAsUp:
			finish();
			break;
		case R.id.display_menu_refresh:
			Log.i("ACTION VIEW", item.getActionView().getId() + "");

			refresh();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.news_display, menu);
		return true;
	}

	private String getURL() {
		String url = null;
		url = getIntent().getStringExtra("URL");
		StringBuffer mUrl = new StringBuffer("http://www.google.com/gwt/x?u=");
		mUrl.append(url);
		return mUrl.toString();
	}

	private String getNewsTitle() {
		String title = null;
		title = getIntent().getStringExtra("TITLE");
		return title;
	}

	@Override
	public void refresh() {
		webview.reload();
	}

}
