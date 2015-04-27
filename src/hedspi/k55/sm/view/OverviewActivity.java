package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.DBmanager;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.User;
import hedspi.k55.sm.ultil.FileManager;
import hedspi.k55.sm.ultil.ListViewFix;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class OverviewActivity extends SherlockActivity implements Iview,
		ActionBar.OnNavigationListener {
	private TextView deposite;
	private String profitSource[] = { "SYMBOL", "GAIN_PERC" };
	private String loseSource[] = { "SYMBOL", "LOSE_PERC" };
	private int target[] = { R.id.overview_list_stockcode,
			R.id.overview_list_percent };
	ListView loseList;
	ListView profitList;
	TextView email;
	TextView name;
	TextView currentMoney;
	TextView investedMoney;
	TextView totalProfitValue;
	TextView totalProfitPercent;
	TextView expectedProfitPercent;
	TextView warningValue;
	TextView warningValuePercent;
	TextView expectedProfitValue;
	Portfolio portfolio;
	ArrayList<HashMap<String, String>> profitStockList;
	ArrayList<HashMap<String, String>> loseStockList;
	NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setTheme(THEME);
		setContentView(R.layout.extended_overview_activity);

		Context context = getSupportActionBar().getThemedContext();

		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(
				context, R.array.menu_list, R.layout.menu_list_item);
		list.setDropDownViewResource(R.layout.menu_dropdown_list_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setSelectedNavigationItem(0);
		// set name+email
		User user = new User(this);
		user.init();
		name = (TextView) findViewById(R.id.overview_name_detail);
		email = (TextView) findViewById(R.id.overview_email_detail);
		profitList = (ListView) findViewById(R.id.overview_profit_list);
		loseList = (ListView) findViewById(R.id.overview_losses_list);

		name.setText(user.getName());
		email.setText(user.getEmail());

		// set current money + invested money
		deposite = (Button) findViewById(R.id.overview_total_deposited_deposite);
		currentMoney = (TextView) findViewById(R.id.overview_cash_balance_detail);
		investedMoney = (TextView) findViewById(R.id.overview_total_deposited_detail);
		totalProfitValue = (TextView) findViewById(R.id.overview_current_profit_value);
		totalProfitPercent = (TextView) findViewById(R.id.overview_current_profit_percent);
		expectedProfitValue = (TextView) findViewById(R.id.overview_expected_profit_value);
		expectedProfitPercent = (TextView) findViewById(R.id.overview_expected_profit_percent);
		warningValue = (TextView) findViewById(R.id.overview_losses_warning_value);
		warningValuePercent = (TextView) findViewById(R.id.overview_losses_warning_percent);
		loseStockList = new ArrayList<HashMap<String, String>>();
		profitStockList = new ArrayList<HashMap<String, String>>();
		// set forecast stock
		deposite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent deposite = new Intent(OverviewActivity.this,
						CashActivity.class);
				deposite.putExtra("CURRENT_BALANCE",
						numberFormat.format(portfolio.getCurrentMoney()) + "$");
				startActivity(deposite);
			}
		});
		profitList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent detail = new Intent(OverviewActivity.this,
						StockDetailActitivy.class);
				detail.putExtra("symbol",
						profitStockList.get(position).get("SYMBOL"));
				startActivity(detail);
			}

		});
		loseList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent detail = new Intent(OverviewActivity.this,
						StockDetailActitivy.class);
				detail.putExtra("symbol",
						loseStockList.get(position).get("SYMBOL"));
				startActivity(detail);
			}

		});
		refresh();
	}

	public void setContent() {
		currentMoney.setText(numberFormat.format(portfolio.getCurrentMoney())
				+ "$");
		investedMoney.setText(numberFormat.format(portfolio.getInvestedMoney())
				+ "$");

		// set profit

		totalProfitValue.setText(numberFormat.format(portfolio.getProfit())
				+ "$");
		totalProfitPercent.setText("(~"
				+ numberFormat.format(portfolio.getPercProfit()) + "%)");

		Double totalGainExpected = portfolio.getTotalProfitStock();
		Double totalGainExpectedPerc = portfolio.getTotalProfitStockPerc();
		Double totalLoseValue = portfolio.getTotalLossesStock();
		Double totalLoseValuePerc = portfolio.getTotalLossesStockPerc();

		expectedProfitValue.setText(numberFormat.format(totalGainExpected)
				+ "$");
		expectedProfitPercent.setText("(~"
				+ String.format(Locale.ENGLISH, "%.2f", totalGainExpectedPerc)
				+ "%)");

		warningValue.setText(numberFormat.format(totalLoseValue) + "$");
		warningValuePercent.setText("(~"
				+ String.format(Locale.ENGLISH, "%.2f", totalLoseValuePerc)
				+ "%)");
		if (portfolio.getProfit() < 0) {
			totalProfitValue.setTextColor(Color.RED);
			totalProfitPercent.setTextColor(Color.RED);
		}
		if (portfolio.getProfit() > 0) {
			totalProfitValue.setTextColor(Color.GREEN);
			totalProfitPercent.setTextColor(Color.GREEN);
		}
		expectedProfitPercent.setTextColor(Color.GREEN);
		warningValuePercent.setTextColor(Color.RED);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadListTask().execute("");
	}

	public void viewList() {
		SimpleAdapter profitSimpleAdapter = new SimpleAdapter(
				OverviewActivity.this, profitStockList, R.layout.overview_list,
				profitSource, target);
		profitList.setAdapter(profitSimpleAdapter);
		ListViewFix.setListViewHeightBasedOnChildren(profitList);
		SimpleAdapter loseSimpleAdapter = new SimpleAdapter(
				OverviewActivity.this, loseStockList, R.layout.overview_list,
				loseSource, target);
		loseList.setAdapter(loseSimpleAdapter);
		ListViewFix.setListViewHeightBasedOnChildren(loseList);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case 0:
			break;
		case 1:
			Intent stock = new Intent(OverviewActivity.this,
					PortfolioActivity.class);
			startActivity(stock);
			finish();
			break;
		case 2:
			Intent transaction = new Intent(OverviewActivity.this,
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
	public boolean onCreateOptionsMenu(Menu menu) {

		com.actionbarsherlock.view.MenuInflater inflate = getSupportMenuInflater();
		inflate.inflate(R.menu.overview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_option_about:
			Intent aboutActivity = new Intent(OverviewActivity.this,
					AboutActivity.class);
			startActivity(aboutActivity);

			break;
		case R.id.over_option_setting:
			Intent setting = new Intent(OverviewActivity.this,
					InfoActivity.class);
			startActivity(setting);
			break;
		case R.id.over_option_export_all:
			FileManager fm = new FileManager();
			fm.exportCSV(this, "StockStatistic");
			Toast.makeText(this, "Data exported to StockStatistic.csv",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.over_option_logout:
			Intent login = new Intent(OverviewActivity.this,
					LoginActivity.class);
			startActivity(login);
			finish();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class LoadListTask extends AsyncTask<String, String, String> {
		public LoadListTask() {
		}

		@Override
		protected String doInBackground(String... params) {
			portfolio = new Portfolio(OverviewActivity.this, PORTFOLIO_NAME);
			DBmanager db = new DBmanager(OverviewActivity.this);
			if (db.isExist(DBmanager.TABLE_PORTFOLIO, DBmanager._NAME,
					PORTFOLIO_NAME)) {
				portfolio.init();
			}
			profitStockList = portfolio.getProfitStock();
			loseStockList = portfolio.getLossesStock();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			viewList();
			setContent();
			Log.i("LOAD LIST TASK", "Load done");
		}
	}

	@Override
	public void refresh() {
		new LoadListTask().execute("");

	}
}
