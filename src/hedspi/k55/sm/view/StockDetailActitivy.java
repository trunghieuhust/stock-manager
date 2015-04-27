package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.DBmanager;
import hedspi.k55.sm.model.Stock;
import hedspi.k55.sm.ultil.AndroidSaxFeedParser;
import hedspi.k55.sm.ultil.ListViewFix;
import hedspi.k55.sm.ultil.Message;
import hedspi.k55.sm.ultil.NetworkUltil;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class StockDetailActitivy extends SherlockActivity implements Iview {
	private TextView lastTradedPrice;
	private TextView perfomanceText;
	private TextView lowText;
	private TextView low_52weekText;
	private TextView highText;
	private TextView high_52weekText;
	private TextView avg_vol;
	private TextView shares;
	private TextView betaText;
	private TextView delay;
	private TextView lastPriceTime;
	private TextView openText;
	private TextView volumeText;
	private TextView mktcapText;
	private TextView p_e;
	private TextView divyieldText;
	private TextView epsText;
	private TextView my_shares;
	private TextView avgPrice;
	private TextView annual;
	private TextView annual_perc;
	private ProgressBar progressbar;
	private ProgressBar news_progessbar;
	private ListView transactionList;
	private ListView newsList;
	private ImageView chart;
	private TextView currentProfit;
	private Stock stock;
	MenuItem refreshItem;
	MenuItem searchButtonItem;
	MenuItem progressbarItem;
	private Button next;
	private Button prev;
	private LinearLayout transactionStatisticLayout;
	private LinearLayout transactionListLayout;
	private TextView transactionEmpty;
	private ArrayList<HashMap<String, String>> transList;
	private List<Message> message;
	private int height;
	private TextView detail_text_chart_title;
	private String source[] = { "TIME", "PRICE", "SHARES", "PROFIT" };
	private int[] target = { R.id.stock_detail_transaction_item_date,
			R.id.stock_detail_transaction_item_price,
			R.id.stock_detail_transaction_item_shares,
			R.id.stock_detail_transaction_item_profit };
	private String newsSource[] = { "TITLE", "SOURCE", "DESCRIPTION" };
	private int[] newsTarget = { R.id.news_detail_title, R.id.news_source,
			R.id.news_description };
	private String[] current_chart = { "&p=1d&i=360", "&p=7d&i=2016",
			"&p=1M&i=86400", "&p=6M&i=345600", "&p=1Y&i=518400",
			"&p=5Y&i=1209600" };
	private String[] prev_chart = { "<< One day", "<< One week",
			"<< One month", "<< Six month", "<< One year", "<< Five year" };
	private String[] next_chart = { "One day >>", "One week >>",
			"One month >>", "Six month >>", "One year >>", "Five year >>" };
	private String[] cur_chart = { "One day", "One week", "One month",
			"Six month", "One year", "Five year" };
	private int current_chart_index = 0;
	private NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
	private TextView currentProfitPercent;
	private TextView totalProfitLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setTheme(THEME);
		setContentView(R.layout.stock_detail_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle("Loading...");
		// /////////////////////////////////////
		lastTradedPrice = (TextView) findViewById(R.id.stock_details_lastTradedPrice);
		perfomanceText = (TextView) findViewById(R.id.stock_details_perfomanceText);
		lastPriceTime = (TextView) findViewById(R.id.stock_details_lastPriceTime);
		openText = (TextView) findViewById(R.id.stock_details_openText);
		volumeText = (TextView) findViewById(R.id.stock_details_volumeText);
		mktcapText = (TextView) findViewById(R.id.stock_details_mktcapText);
		p_e = (TextView) findViewById(R.id.stock_details_peText);
		divyieldText = (TextView) findViewById(R.id.stock_details_divyieldText);
		epsText = (TextView) findViewById(R.id.stock_details_epsText);
		lowText = (TextView) findViewById(R.id.stock_details_lowText);
		low_52weekText = (TextView) findViewById(R.id.stock_details_low_52weekText);
		highText = (TextView) findViewById(R.id.stock_details_highText);
		high_52weekText = (TextView) findViewById(R.id.stock_details_high_52weekText);
		avg_vol = (TextView) findViewById(R.id.stock_details_avg_vol);
		shares = (TextView) findViewById(R.id.stock_details_shares);
		betaText = (TextView) findViewById(R.id.stock_details_betaText);
		delay = (TextView) findViewById(R.id.stock_details_delay);
		progressbar = (ProgressBar) findViewById(R.id.stock_detail_progressbar);
		news_progessbar = (ProgressBar) findViewById(R.id.stock_detail_news_progressbar);
		chart = (ImageView) findViewById(R.id.stock_detail_chart_view);
		newsList = (ListView) findViewById(R.id.stock_details_news_list_view);
		my_shares = (TextView) findViewById(R.id.stock_details_transaction_general_share_value);
		avgPrice = (TextView) findViewById(R.id.stock_details_transaction_general_avg_price_value);
		annual = (TextView) findViewById(R.id.stock_details_transaction_general_annual_value);
		annual_perc = (TextView) findViewById(R.id.stock_details_transaction_general_annual_perc_value);
		transactionList = (ListView) findViewById(R.id.stock_detail_transaction_list);
		transactionStatisticLayout = (LinearLayout) findViewById(R.id.stock_details_transaction_list);
		transactionListLayout = (LinearLayout) findViewById(R.id.stock_details_transaction_detail_list);
		transactionEmpty = (TextView) findViewById(R.id.stock_details_transaction_empty);
		next = (Button) findViewById(R.id.detail_btn_next_chart);
		prev = (Button) findViewById(R.id.detail_btn_prev_chart);
		detail_text_chart_title = (TextView) findViewById(R.id.detail_text_chart_title);
		currentProfit = (TextView) findViewById(R.id.stock_detail_total_profit);
		currentProfitPercent = (TextView) findViewById(R.id.stock_detail_total_profit_percent);
		totalProfitLinearLayout = (TextView) findViewById(R.id.stock_detail_total_profit_header);
		divyieldText.setHint("N/A");
		lastTradedPrice.setHint("N/A");
		perfomanceText.setHint("N/A");
		lastPriceTime.setHint("N/A");
		openText.setHint("N/A");
		volumeText.setHint("N/A");
		mktcapText.setHint("N/A");
		p_e.setHint("N/A");
		epsText.setHint("N/A");
		lowText.setHint("N/A");
		low_52weekText.setHint("N/A");
		highText.setHint("N/A");
		high_52weekText.setHint("N/A");
		avg_vol.setHint("N/A");
		shares.setHint("N/A");
		betaText.setHint("N/A");
		delay.setHint("Offline");
		height = getWindowManager().getDefaultDisplay().getHeight();
		ViewGroup.LayoutParams params = newsList.getLayoutParams();
		params.height = (int) (height * 0.6f);
		newsList.setLayoutParams(params);
		newsList.requestLayout();
		next.setText(next_chart[getNextChart(current_chart_index)]);
		prev.setText(prev_chart[getPrevChart(current_chart_index)]);
		detail_text_chart_title.setText(cur_chart[current_chart_index]);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				current_chart_index = getNextChart(current_chart_index);

				new DownloadImageTask(chart).execute(stock
						.getURLChart(current_chart[current_chart_index]));
				next.setText(next_chart[getNextChart(current_chart_index)]);
				prev.setText(prev_chart[getPrevChart(current_chart_index)]);
				detail_text_chart_title.setText(cur_chart[current_chart_index]);
			}
		});
		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				current_chart_index = getPrevChart(current_chart_index);

				new DownloadImageTask(chart).execute(stock
						.getURLChart(current_chart[current_chart_index]));
				next.setText(next_chart[getNextChart(current_chart_index)]);
				prev.setText(prev_chart[getPrevChart(current_chart_index)]);
				detail_text_chart_title.setText(cur_chart[current_chart_index]);
			}
		});
		getContent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.portfolio_menu, menu);
		searchButtonItem = menu.findItem(R.id.menu_add);
		refreshItem = menu.findItem(R.id.menu_refresh);
		progressbarItem = menu.findItem(R.id.menu_progress);
		return super.onCreateOptionsMenu(menu);
	}

	public void setView(Stock stock) {
		lastTradedPrice.setText(stock.getLastPrice());
		perfomanceText
				.setText(stock.getPercChange() + "% " + stock.getChange());
		if (Double.parseDouble(stock.getPercChange()) <= 0) {
			perfomanceText.setTextColor(Color.RED);
		} else
			perfomanceText.setTextColor(Color.GREEN);
		lastPriceTime.setText(stock.getTradeTime());
		openText.setText(stock.getOpen());
		volumeText.setText(stock.getVolume());
		mktcapText.setText(stock.getMarketCap());
		p_e.setText(stock.getPE());
		epsText.setText(stock.getEPS());
		lowText.setText(stock.getLow());
		low_52weekText.setText(stock.getLow52week());
		highText.setText(stock.getHigh());
		high_52weekText.setText(stock.getHigh52week());
		avg_vol.setText(stock.getAvgVolume());
		shares.setText(stock.getShares());
		betaText.setText(stock.getBeta());
		String divyield = stock.getDividend() + "/" + stock.getDividendYield();
		if (divyield.length() != 1) {
			divyieldText.setText(divyield);
		}

		if (stock.getGain() < 0.0) {
			annual.setTextColor(Color.RED);
			annual_perc.setTextColor(Color.RED);
		} else {
			annual.setTextColor(Color.GREEN);
			annual_perc.setTextColor(Color.GREEN);
		}
		divyieldText.setHint("N/A");
		lastTradedPrice.setHint("N/A");
		perfomanceText.setHint("N/A");
		lastPriceTime.setHint("N/A");
		openText.setHint("N/A");
		volumeText.setHint("N/A");
		mktcapText.setHint("N/A");
		p_e.setHint("N/A");
		epsText.setHint("N/A");
		lowText.setHint("N/A");
		low_52weekText.setHint("N/A");
		highText.setHint("N/A");
		high_52weekText.setHint("N/A");
		avg_vol.setHint("N/A");
		shares.setHint("N/A");
		betaText.setHint("N/A");
		delay.setHint("Offline");
		if (NetworkUltil.isNetworkAvailable(this)) {
			delay.setText(R.string.real_time);
			progressbar.setVisibility(View.GONE);
			chart.setBackgroundColor(0x0106000d);
			chart.setScaleType(ScaleType.FIT_XY);

		} else {
			new LoadStockOfflineData().execute("");
			setView(stock);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case HOME_BUTTON:
			finish();
			break;
		case R.id.homeAsUp:
			finish();
			break;
		case R.id.menu_add:
			Intent it = new Intent(StockDetailActitivy.this,
					AddTransactionActivity.class);
			it.putExtra("SYMBOL", stock.getSymbol());
			it.putExtra("PRICE", stock.getPrice());
			it.putExtra("EXCHANGE", stock.getExchange());
			it.putExtra("SHARES", stock.getMyShares());
			startActivity(it);
			break;
		case R.id.menu_refresh:
			refresh();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getContent() {
		String symbol = getIntent().getStringExtra("symbol");
		stock = new Stock(this, symbol);
		stock.init();
	}

	public void viewTransaction() {
		if (stock.isExist(DBmanager.TABLE_STOCK, "symbol", stock.getSymbol())) {
			my_shares.setText(numberFormat.format(Integer.parseInt(stock.getMyShares())));
			avgPrice.setText(stock.getAvgPrice());
			annual.setText(numberFormat.format(stock.getGain()));
			annual_perc.setText(String.format("%.2f", stock.getGainPerc())
					+ "%");
			transList = stock.getAllTransaction();
			if (transList == null) {
				transactionListLayout.setVisibility(View.GONE);
				transactionStatisticLayout.setVisibility(View.GONE);
				transactionEmpty.setVisibility(View.VISIBLE);
				totalProfitLinearLayout.setVisibility(View.GONE);
				return;
			} else {
				transactionListLayout.setVisibility(View.VISIBLE);
				transactionStatisticLayout.setVisibility(View.VISIBLE);
				transactionEmpty.setVisibility(View.GONE);
			}
			SimpleAdapter simpleadapter = new SimpleAdapter(this, transList,
					R.layout.stock_detail_transaction_item, source, target);

			transactionList.setAdapter(simpleadapter);
			ListViewFix.setListViewHeightBasedOnChildren(transactionList);
			transactionList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					int transID = (Integer.parseInt(transList.get(arg2).get(
							"ID")));
					Intent edit = new Intent(StockDetailActitivy.this,
							EditTransactionActivity.class);
					edit.putExtra("ID", transID);
					startActivity(edit);
				}
			});
			if (stock.getProfit() > 0) {
				totalProfitLinearLayout.setVisibility(View.VISIBLE);
				currentProfit.setVisibility(View.VISIBLE);
				currentProfitPercent.setVisibility(View.VISIBLE);
				currentProfit.setText("+"
						+ numberFormat.format(stock.getProfit()) + "$");
				currentProfit.setTextColor(Color.GREEN);
				currentProfitPercent.setText("+"
						+ String.format(Locale.ENGLISH, "%.2f",
								stock.getProfitPerc()) + "%");
				currentProfitPercent.setTextColor(Color.GREEN);
			}
			if (stock.getProfit() < 0) {
				totalProfitLinearLayout.setVisibility(View.VISIBLE);
				currentProfit.setVisibility(View.VISIBLE);
				currentProfitPercent.setVisibility(View.VISIBLE);
				currentProfit.setText(numberFormat.format(stock.getProfit())
						+ "$");
				currentProfit.setTextColor(Color.RED);
				currentProfitPercent.setText(String.format(Locale.ENGLISH,
						"%.2f", stock.getProfitPerc()) + "%");
				currentProfitPercent.setTextColor(Color.RED);
			}
			if(stock.getProfit() == 0) {
				totalProfitLinearLayout.setVisibility(View.VISIBLE);
				currentProfit.setVisibility(View.VISIBLE);
				currentProfitPercent.setVisibility(View.VISIBLE);
				currentProfit.setText("0$");
				currentProfitPercent.setText("0%");
			}
			
		} else {
			transactionListLayout.setVisibility(View.GONE);
			transactionStatisticLayout.setVisibility(View.GONE);
			transactionEmpty.setVisibility(View.VISIBLE);
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
						Log.i("AUTO REFRESH", "DONE");
					}
				});
			}
		}, 500, REFRESH_SPEED);

	}

	public void viewNews(ArrayList<HashMap<String, String>> fillmaps) {
		news_progessbar.setVisibility(View.GONE);
		newsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent webview = new Intent(StockDetailActitivy.this,
						NewsDisplay.class);

				String URL = message.get(arg2).getLink();
				String TITLE = message.get(arg2).getTitle();
				webview.putExtra("URL", URL);
				webview.putExtra("TITLE", TITLE);
				startActivity(webview);
			}
		});
		SimpleAdapter simpleadapter = new SimpleAdapter(this, fillmaps,
				R.layout.news_item, newsSource, newsTarget);
		newsList.setAdapter(simpleadapter);
		newsList.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_UP:
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;

				default:
					break;
				}
				v.onTouchEvent(event);
				return true;
			}
		});

	}

	private class DownloadImageTask extends AsyncTask<String, Void, Drawable> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
			progressbar.setVisibility(View.VISIBLE);
			// next.setOnClickListener(null);
			// prev.setOnClickListener(null);
		}

		@Override
		protected Drawable doInBackground(String... urls) {
			String urldisplay = urls[0];
			Drawable drawable = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				drawable = Drawable.createFromStream(in, "chart");

			} catch (Exception e) {
				e.printStackTrace();
			}
			return drawable;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			bmImage.setImageDrawable(result);
			progressbar.setVisibility(View.GONE);
		}
	}

	private int getPrevChart(int current_index) {
		if (current_index == 0) {
			return 5;
		} else {
			return current_index - 1;
		}
	}

	private int getNextChart(int current_index) {
		if (current_index == 5) {
			return 1;
		} else {
			return current_index + 1;
		}
	}

	private class LoadNewsTask extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		String symbol;

		public LoadNewsTask(String symbol) {
			this.symbol = symbol;
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			StringBuffer xmlOrigin = new StringBuffer(
					"https://www.google.com/finance/company_news?q=");
			xmlOrigin.append(symbol);
			xmlOrigin.append("&output=rss");
			String toParse = xmlOrigin.toString();
			try {
				AndroidSaxFeedParser rssReader = new AndroidSaxFeedParser(
						toParse);
				message = rssReader.parse();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<HashMap<String, String>> fillmaps = new ArrayList<HashMap<String, String>>();
			if (message.size() == 0)
				return fillmaps;
			for (int i = 1; i < message.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				String title = message.get(i).getTitle();
				String source = message.get(i).getSource();
				String desc = message.get(i).getDescription();
				map.put("TITLE", title);
				map.put("SOURCE", source);
				map.put("DESCRIPTION", desc);
				fillmaps.add(map);
			}

			return fillmaps;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			viewNews(result);
			refreshItem.setVisible(true);
			progressbarItem.setVisible(false);
		}

	}

	private class LoadStockOnlineData extends AsyncTask<String, Void, Void> {

		public LoadStockOnlineData() {
		}

		@Override
		protected Void doInBackground(String... params) {
			stock.refresh();
			stock.init();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			getSupportActionBar().setTitle(
					stock.getExchange().toUpperCase(Locale.ENGLISH) + ":"
							+ stock.getSymbol().toUpperCase(Locale.ENGLISH));
			setView(stock);
			viewTransaction();
		}
	}

	private class LoadStockOfflineData extends AsyncTask<String, Void, Void> {

		public LoadStockOfflineData() {
		}

		@Override
		protected Void doInBackground(String... params) {
			stock.init();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			getSupportActionBar().setTitle(
					stock.getExchange().toUpperCase(Locale.ENGLISH) + ":"
							+ stock.getSymbol().toUpperCase(Locale.ENGLISH));
			setView(stock);
		}
	}

	@Override
	public void refresh() {
		refreshItem.setVisible(false);
		progressbarItem.setVisible(true);

		if (NetworkUltil.isNetworkAvailable(StockDetailActitivy.this)) {
			new LoadStockOnlineData().execute("");
			new DownloadImageTask(chart).execute(stock
					.getURLChart(Stock.ONE_WEEK));
			new LoadNewsTask(stock.getSymbol()).execute("");

		} else {
			new LoadStockOfflineData().execute("");
		}

	}
}
