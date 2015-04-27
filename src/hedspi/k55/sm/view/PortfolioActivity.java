package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.Stock;
import hedspi.k55.sm.ultil.MyCustomAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PortfolioActivity extends SherlockFragmentActivity implements
		OnNavigationListener, Iview {
	private ListView listview;
	private List<Stock> stockList;
	private Portfolio portfolio;
	private EditText search;
	private ImageButton searchButton;
	private EditText seachText;
	private MenuItem refreshItem;
	private MenuItem searchButtonItem;
	private MenuItem progressbarItem;
	private ImageButton add;
	private ImageButton resfresh;

	private TextView portfolioGainDetail;
	private TextView portfolioGainPercentDetail;
	private TextView portfolioCostBasicDetail;
	private TextView portfolioMktValueDetail;
	private List<HashMap<String, String>> filmaps;
	private NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portfolio_inside_activity);

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(
				context, R.array.menu_list, R.layout.menu_list_item);
		list.setDropDownViewResource(R.layout.menu_dropdown_list_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setSelectedNavigationItem(1);
		portfolioGainDetail = (TextView) findViewById(R.id.portfolioGainDetail);
		portfolioGainPercentDetail = (TextView) findViewById(R.id.portfolioGainPercentDetail);
		portfolioCostBasicDetail = (TextView) findViewById(R.id.portfolioCostBasicDetail);
		portfolioMktValueDetail = (TextView) findViewById(R.id.portfolioMktValueDetail);
		listview = (ListView) findViewById(R.id.portfolio_list_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.portfolio_menu, menu);
		searchButtonItem = menu.findItem(R.id.menu_add);
		refreshItem = menu.findItem(R.id.menu_refresh);
		progressbarItem = menu.findItem(R.id.menu_progress);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobi = cm
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi.isConnected() || mobi.isConnected())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void viewList() {

		filmaps = new ArrayList<HashMap<String, String>>();
		if (stockList != null && stockList.size() != 0) {
			for (int i = 0; i < stockList.size(); i++) {
				if (Integer.parseInt(stockList.get(i).getMyShares()) != 0) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("COMPANY_NAME", stockList.get(i).getCompanyName());
					map.put("SYMBOL", stockList.get(i).getSymbol());
					map.put("SHARES", "" + stockList.get(i).getMyShares());
					map.put("CURRENT_PRICE", stockList.get(i).getLastPrice());
					map.put("CHANGE_PRICE", stockList.get(i).getPercChange());
					filmaps.add(map);
				}
			}
			// simpleAdapter = new SimpleAdapter(this, filmaps,
			// R.layout.portfolio_stock_item, list_source,
			// list_destination);

			MyCustomAdapter adapter = new MyCustomAdapter(
					PortfolioActivity.this,
					(ArrayList<HashMap<String, String>>) filmaps);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					Intent detail = new Intent(PortfolioActivity.this,
							StockDetailActitivy.class);
					detail.putExtra("symbol", stockList.get(position)
							.getSymbol());
					startActivity(detail);

				}
			});
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						refresh();
					}
				});
			}
		}, 500, REFRESH_SPEED);
	}

	public void viewStatistic() {
		portfolioGainDetail.setText(numberFormat.format(portfolio.getGain())
				+ "$");
		portfolioGainPercentDetail.setText(numberFormat.format(portfolio
				.getGainPerc()) + "%");
		portfolioCostBasicDetail.setText(numberFormat.format(portfolio
				.getCostBasic()) + "$");
		portfolioMktValueDetail.setText(numberFormat.format(portfolio
				.getMktValue()) + "$");
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case 0:
			Intent overview = new Intent(PortfolioActivity.this,
					OverviewActivity.class);
			startActivity(overview);
			finish();
			break;
		case 1:
			break;
		case 2:
			Intent transaction = new Intent(PortfolioActivity.this,
					TransactionActivity.class);
			startActivity(transaction);
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setCustomView(R.layout.collapsiable_search);
			ViewGroup viewgroup = (ViewGroup) getSupportActionBar()
					.getCustomView();
			search = (EditText) viewgroup
					.findViewById(R.id.overview_search_edittext);
			search.requestFocus();
			searchButton = (ImageButton) viewgroup
					.findViewById(R.id.action_bar_search_button);

			searchButtonItem.setVisible(false);
			refreshItem.setVisible(false);
			searchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String keyword = search.getText().toString();
					if (keyword.length() != 0) {
						new SearchTask(keyword).execute("");
					}
					search.setVisibility(View.INVISIBLE);
					searchButton.setVisibility(View.INVISIBLE);
					searchButtonItem.setVisible(true);
					refreshItem.setVisible(true);
				}
			});
			break;
		case R.id.menu_refresh:
			refresh();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void displayStockDetail(String symbol) {
		Log.i("DISPLAY STOCK DETAIL", symbol);
		if (symbol == null || symbol.length() == 0) {
			Toast.makeText(PortfolioActivity.this, "Invalid stock symbol",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Intent stockdetail = new Intent(PortfolioActivity.this,
				StockDetailActitivy.class);
		stockdetail.putExtra("symbol", symbol);
		startActivity(stockdetail);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i("MENU_ITEM_SELECTED", item.toString());
		return super.onMenuItemSelected(featureId, item);
	}

	private class LoadListTask extends AsyncTask<String, String, String> {
		public LoadListTask() {
			refreshItem.setVisible(false);
			progressbarItem.setVisible(true);

		}

		@Override
		protected String doInBackground(String... params) {
			portfolio = new Portfolio(PortfolioActivity.this,
					"DEFAULT_PORTFOLIO");
			portfolio.init();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			stockList = portfolio.getAll();
			viewList();
			viewStatistic();
			refreshItem.setVisible(true);
			progressbarItem.setVisible(false);
		}
	}

	private class SearchTask extends AsyncTask<String, String, String[]> {
		String keyword;
		ProgressDialog dialog;

		public SearchTask(String keyword) {
			this.keyword = keyword;
			dialog = ProgressDialog.show(PortfolioActivity.this, "",
					"Searching. Please wait...", true);
			dialog.show();
		}

		@Override
		protected String[] doInBackground(String... params) {
			String result[] = null;
			portfolio.searchByGoogle(keyword);
			result = portfolio.searchByGoogle(search.getText().toString());

			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			dialog.dismiss();
			Bundle bundle = new Bundle();
			bundle.putStringArray("RESULT", result);
			DialogFragment newFragment = new SearchDiaglog();
			newFragment.setArguments(bundle);
			newFragment.show(getSupportFragmentManager(), "dialog");

			super.onPostExecute(result);
		}
	}

	@Override
	public void refresh() {
		new LoadListTask().execute("");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
