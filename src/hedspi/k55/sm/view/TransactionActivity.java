package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.Transaction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class TransactionActivity extends SherlockActivity implements Iview,
		OnNavigationListener {
	private ListView transactionList;
	private ToggleButton sortbySymbol;
	private ToggleButton sortbyPrice;
	private ToggleButton sortbyShare;
	// private List<Stock> stockList;
	private Portfolio portfolio;
	private ArrayList<Transaction> transList;
	private String source[] = { "TYPE", "DATE", "SYM", "SHARES", "PRICE",
			"COMMISSION" };
	private int target[] = { R.id.transTableTypeDetail,
			R.id.transTableDateDetail, R.id.transTableSymbolDetail,
			R.id.transTableSharesDetail, R.id.transTablePriceDetail,
			R.id.transTableCommisionDetail };
	NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(THEME);
		setContentView(R.layout.transaction_history_activity);
		Context context = getSupportActionBar().getThemedContext();

		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(
				context, R.array.menu_list, R.layout.menu_list_item);
		list.setDropDownViewResource(R.layout.menu_dropdown_list_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setSelectedNavigationItem(2);
		transactionList = (ListView) findViewById(R.id.transaction_history_list);
		sortbyPrice = (ToggleButton) findViewById(R.id.transactionSortbyPriceTogglebtn);
		sortbyShare = (ToggleButton) findViewById(R.id.transactionSortbySharesTogglebtn);
		sortbySymbol = (ToggleButton) findViewById(R.id.transactionSortbySymbolTogglebtn);
		portfolio = new Portfolio(TransactionActivity.this, "DEFAULT_PORTFOLIO");
		transactionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (transList.get(arg2).getType() < 0) {
					Intent cash = new Intent(TransactionActivity.this,
							ViewCashActivity.class);
					cash.putExtra("ID", transList.get(arg2).getId());
					startActivity(cash);

				} else {
					Intent editTrans = new Intent(TransactionActivity.this,
							EditTransactionActivity.class);
					editTrans.putExtra("ID", transList.get(arg2).getId());
					startActivity(editTrans);
				}
			}
		});
		sortbyPrice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					sortbyPrice.setChecked(true);
					sortbyShare.setChecked(false);
					sortbySymbol.setChecked(false);
					viewAllTransaction("pricein DESC");
				} else {
					// sortbyPrice.setChecked(true);
					// sortbyShare.setChecked(false);
					// sortbySymbol.setChecked(false);
					return;
				}
			}
		});
		sortbyShare.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub

				if (arg1) {
					sortbyPrice.setChecked(false);
					sortbySymbol.setChecked(false);
					sortbyShare.setChecked(true);
					viewAllTransaction("shares DESC");
				} else {
					// sortbyShare.setChecked(true);
					// sortbyPrice.setChecked(false);
					// sortbySymbol.setChecked(false);
					return;
				}
			}
		});
		sortbySymbol.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// TODO
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					sortbySymbol.setChecked(true);
					sortbyPrice.setChecked(false);
					sortbyShare.setChecked(false);
					viewAllTransaction("symbol");
				} else {
					// sortbySymbol.setChecked(true);
					// sortbyPrice.setChecked(false);
					// sortbyShare.setChecked(false);
					return;
				}
			}
		});

		viewAllTransaction();
	}

	private void viewAllTransaction() {
		// TODO Auto-generated method stub
		transList = (ArrayList<Transaction>) portfolio.getAllTransaction();
		if (transList == null) {
			transactionList.setVisibility(View.GONE);
			transactionList.setVisibility(View.VISIBLE);
			return;
		}
		ArrayList<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < transList.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			String date = transList.get(i).getTime();
			String symbol = transList.get(i).getStockSymbol();
			String shares = numberFormat.format(transList.get(i).getShares());

			String price = numberFormat.format(transList.get(i).getPrice());
			String comm = String.format(Locale.ENGLISH, "%.2f", transList
					.get(i).getCommission());

			if (transList.get(i).getType() == Transaction.BUY) {
				map.put("TYPE", "Buy");
			}
			if (transList.get(i).getType() == Transaction.SELL) {
				map.put("TYPE", "Sell");
			}
			if (transList.get(i).getType() == Transaction.DEPOSITE) {
				map.put("TYPE", "Deposite");
			}
			if (transList.get(i).getType() == Transaction.WITHDRAW) {
				map.put("TYPE", "Withdraw");
			}
			map.put("DATE", date);
			map.put("SYM", symbol);
			map.put("SHARES", shares);
			map.put("PRICE", price);
			map.put("COMMISSION", comm);
			fillMaps.add(map);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, fillMaps,
				R.layout.transaction_history_detail, source, target);
		transactionList.setAdapter(simpleAdapter);
	}

	private void viewAllTransaction(String order) {
		// TODO Auto-generated method stub
		transList = (ArrayList<Transaction>) portfolio.getAllTransaction(order);
		if (transList == null) {
			transactionList.setVisibility(View.GONE);
			transactionList.setVisibility(View.VISIBLE);
			return;
		}
		ArrayList<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < transList.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			String date = transList.get(i).getTime();
			String symbol = transList.get(i).getStockSymbol();
			String shares = numberFormat.format(transList.get(i).getShares());
			String price = numberFormat.format(transList.get(i).getPrice());
			String comm = transList.get(i).getCommission() + "";

			if (transList.get(i).getType() == Transaction.BUY) {
				map.put("TYPE", "Buy");
			}
			if (transList.get(i).getType() == Transaction.SELL) {
				map.put("TYPE", "Sell");
			}
			if (transList.get(i).getType() == Transaction.DEPOSITE) {
				map.put("TYPE", "Deposite");
			}
			if (transList.get(i).getType() == Transaction.WITHDRAW) {
				map.put("TYPE", "Withdraw");
			}
			map.put("DATE", date);
			map.put("SYM", symbol);
			map.put("SHARES", shares);
			map.put("PRICE", price);
			map.put("COMMISSION", comm);
			fillMaps.add(map);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, fillMaps,
				R.layout.transaction_history_detail, source, target);
		transactionList.setAdapter(simpleAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		viewAllTransaction();
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case 0:
			Intent overview = new Intent(TransactionActivity.this,
					OverviewActivity.class);
			startActivity(overview);
			finish();
			break;
		case 1:
			Intent stock = new Intent(TransactionActivity.this,
					PortfolioActivity.class);

			startActivity(stock);
			finish();
			break;
		case 2:
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void refresh() {

	}
}
