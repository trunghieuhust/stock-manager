package hedspi.k55.sm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Portfolio extends DBmanager {
	public static final String google_search_url = "http://www.google.com/finance/match?matchtype=matchall&q=";
	private int id;
	private String name;
	private double current_money;
	private double invested_money;
	private int number_of_stock;
	private double profit;
	private List<Stock> list_stock;

	public Portfolio(Context context) {
		super(context);
	}

	public Portfolio(Context context, String name) {
		super(context);
		this.name = "" + name;
		this.id = 0;
		this.current_money = 0.0;
		this.invested_money = 0.0;
		this.number_of_stock = 0;
		this.profit = 0.0;
		init();

	}

	public boolean init() {
		list_stock = new ArrayList<Stock>();

		SQLiteDatabase db = this.getReadableDatabase();
		// load info from database
		String query1 = " SELECT * FROM portfolio WHERE name like" + "\""
				+ this.name + "\"";
		Cursor cursor1 = db.rawQuery(query1, null);
		if (cursor1 != null) {
			if (cursor1.moveToFirst()) {
				this.id = Integer.parseInt(cursor1.getString(0));
				this.current_money = Double.parseDouble(cursor1.getString(2));
				this.invested_money = Double.parseDouble(cursor1.getString(3));
				this.number_of_stock = Integer.parseInt(cursor1.getString(4));
				if (cursor1.getString(5).length() != 0)
					this.profit = Double.parseDouble(cursor1.getString(5));
				else
					this.profit = 0;
			}
		}
		cursor1.close();

		// load list stock
		String query = "SELECT symbol,myshare FROM stock ;";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					if (Integer.parseInt(cursor.getString(1)) > 0) {
						Stock stock = new Stock(context, cursor.getString(0));
						System.out.println("TRANSACTION init(): symbol "
								+ stock.getSymbol());
						stock.init();
						// stock.refresh(); // check again ???
						this.list_stock.add(stock);
					}
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return true;
	}

	public List<Stock> getAll() {
		return this.list_stock;
	}

	public boolean update() {
		this.number_of_stock = list_stock.size();
		if (isExist("portfolio", "name", this.name)) {
			// update
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues value = new ContentValues();

			value.put("name", this.name);
			value.put("currentmoney", this.current_money);
			value.put("investedmoney", this.invested_money);
			value.put("numberstock", this.number_of_stock);
			value.put("profit", this.profit);

			int affect = db.update("portfolio", value, "name like "
					+ "\"" + this.name + "\"", null);
			db.close();
			if (affect != 1) {
				System.out.println("portfolio update fail");
				return false;
			}
			System.out.println("portfolio update -" + this.current_money);
			return true;
		} else {
			// add
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues value = new ContentValues();

			value.put("name", this.name);
			value.put("currentmoney", this.current_money);
			value.put("investedmoney", this.invested_money);
			value.put("numberstock", this.number_of_stock);
			value.put("profit", this.profit);

			int affect = (int) db.insert("portfolio", null, value);
			if (affect == -1) {
				db.close();
				return false;
			}
			db.close();
			return true;
		}
	}

	public ArrayList<HashMap<String, String>> getProfitStock() {
		// for(int i=0)
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < list_stock.size(); i++) {
			if (list_stock.get(i).getGain() > 0) {
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("SYMBOL", list_stock.get(i).getSymbol());
				item.put("GAIN_PERC", String.format(Locale.ENGLISH, "%.2f",
						list_stock.get(i).getGainPerc()));
//				item.put("GAIN", String.format(Locale.ENGLISH, "%.5f", list_stock.get(i).getGain()));
//				item.put("BASIC", String.format(Locale.ENGLISH, "%.5f", list_stock.get(i).getCostBasic()));
				list.add(item);
			}
		}
		return list;
	}

	public ArrayList<HashMap<String, String>> getLossesStock() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < list_stock.size(); i++) {
			if (list_stock.get(i).getGain() < 0) {
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("SYMBOL", list_stock.get(i).getSymbol());
				item.put("LOSE_PERC", String.format(Locale.ENGLISH, "%.2f",
						list_stock.get(i).getGainPerc()));
//				item.put("LOSE", String.format(Locale.ENGLISH, "%.5f", list_stock.get(i).getGain()));
//				item.put("BASIC", String.format(Locale.ENGLISH, "%.5f", list_stock.get(i).getCostBasic()));
				list.add(item);
			}
		}
		return list;
	}
	public double getTotalProfitStock(){
		double total = 0.0;
		for (int i = 0; i < list_stock.size(); i++) {
			if (list_stock.get(i).getGain() > 0) {
				total += list_stock.get(i).getGain();
			}
		}
		return total;
	}
	public double getTotalProfitStockPerc(){
		double cost = 0.0;
		double total = 0.0;
		for (int i = 0; i < list_stock.size(); i++) {
			if (list_stock.get(i).getGain() > 0) {
				cost += list_stock.get(i).getCostBasic();
				total += list_stock.get(i).getGain();
			}
		}
		if(cost == 0.0){
			return 0.0;
		}
		double perc = (total/cost)*100;
		return perc;
	}
	public double getTotalLossesStock(){
		double total = 0.0;
		for (int i = 0; i < list_stock.size(); i++) {
			if (list_stock.get(i).getGain() < 0) {
				total += list_stock.get(i).getGain();
			}
		}
		return total;
	}
	public double getTotalLossesStockPerc(){
		double cost = 0.0;
		double total = 0.0;
		for (int i = 0; i < list_stock.size(); i++) {
			if (list_stock.get(i).getGain() < 0) {
				cost += list_stock.get(i).getCostBasic();
				total += list_stock.get(i).getGain();
			}
		}
		if(cost == 0.0){
			return 0.0;
		}
		double perc = (total/cost)*100;
		return perc;
	}
	public String getName() {
		return this.name;
	}

	public Double getCurrentMoney() {
		return this.current_money;
	}

	public boolean setCurrentMoney(double money) {
		this.current_money = money;
		return true;
	}

	public boolean addCurrentMoney(double money) {
		this.current_money += money;
		return true;
	}

	public Double getInvestedMoney() {
		return this.invested_money;
	}

	public boolean setInvestedMoney(double money) {
		this.invested_money = money;
		return true;
	}

	public boolean addInvestedMoney(double money) {
		this.invested_money += money;
		return true;
	}

	public Integer getNumberStock() {
		return this.number_of_stock;
	}

	/**
	 * get stock from portfolio
	 * 
	 * @param stockCode
	 * @return Stock
	 */
	public Stock get(String stockCode) {
		return null;
	}

	/**
	 * Add stock to this portfolio
	 * 
	 * @param stock
	 * @return true if successfully, false if not.
	 * 
	 */
	public boolean add(Stock stock) {
		this.list_stock.add(stock);
		return true;
	}

	/**
	 * Empty or not
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return this.list_stock.isEmpty();
	}

	/**
	 * Delete a stock from portfolio
	 * 
	 * @param stock
	 * @return true if successfully
	 */
	public boolean remove(String stockSymbol) {
		for (int i = 0; i < this.list_stock.size(); i++) {
			Stock temp = this.list_stock.get(0);
			if (temp.getSymbol().equalsIgnoreCase(stockSymbol)) {
				this.list_stock.remove(temp);
				return true;
			}
		}
		return false;
	}

	/**
	 * Replaces the stock in the specified location with the specified stock.
	 * 
	 * @param location
	 * @param portfolio
	 * @return true if successfully.
	 */
	public boolean set(int location, Stock stock) {
		return false;
	}

	/**
	 * Reload portfolio content from database
	 * 
	 * @return true if successfully.
	 */

	public String[] searchByGoogle(String input) {
		StringBuffer url = new StringBuffer(google_search_url);
		String result[] = null;
		url.append(input);
		try {
			HttpGet get = new HttpGet(url.toString());
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);

			String json = EntityUtils.toString(response.getEntity());
			JSONObject obj = new JSONObject(json);
			JSONArray array = obj.getJSONArray("matches");
			// for (int i = 0; i < array.length(); i++) {
			// HashMap<String, String> element = new HashMap<String, String>();
			// String company = array.getJSONObject(i).getString("n");
			// String symbol = array.getJSONObject(i).getString("t");
			// String exchange = array.getJSONObject(i).getString("e");
			// element.put("COMPANY", company);
			// element.put("SYMBOL", symbol);
			// element.put("EXCHANGE", exchange);
			// result.add(element);
			//
			// }
			result = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				String company = array.getJSONObject(i).getString("n");
				String symbol = array.getJSONObject(i).getString("t");
				String exchange = array.getJSONObject(i).getString("e");
				String row = exchange + ":" + symbol + "  " + company;
				result[i] = row;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// loat list trans
	public List<Transaction> getAllTransaction() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Transaction> list = new ArrayList<Transaction>();

		String query2 = "SELECT * FROM _transaction ;";
		Cursor cursor2 = db.rawQuery(query2, null);
		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					Transaction trans = new Transaction(context,
							Integer.parseInt(cursor2.getString(1)),
							cursor2.getString(2), cursor2.getString(3),
							Integer.parseInt(cursor2.getString(4)),
							Double.parseDouble(cursor2.getString(5)),
							Double.parseDouble(cursor2.getString(6)),
							Double.parseDouble(cursor2.getString(7)));
					trans.setID(Integer.parseInt(cursor2.getString(0)));
					list.add(trans);
				} while (cursor2.moveToNext());
				cursor2.close();
				db.close();
				return list;
			}
		}
		cursor2.close();
		db.close();
		return null;
	}

	public List<Transaction> getAllTransaction(String order) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Transaction> list = new ArrayList<Transaction>();

		String query2 = "SELECT * FROM _transaction ORDER BY " + order + " ;";
		Cursor cursor2 = db.rawQuery(query2, null);
		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					Transaction trans = new Transaction(context,
							Integer.parseInt(cursor2.getString(1)),
							cursor2.getString(2), cursor2.getString(3),
							Integer.parseInt(cursor2.getString(4)),
							Double.parseDouble(cursor2.getString(5)),
							Double.parseDouble(cursor2.getString(6)),
							Double.parseDouble(cursor2.getString(7)));
					trans.setID(Integer.parseInt(cursor2.getString(0)));
					list.add(trans);
				} while (cursor2.moveToNext());
				cursor2.close();
				db.close();
				return list;
			}
		}
		cursor2.close();
		db.close();
		return null;
	}

	// gia tri TT
	public Double getMktValue() {
		Double total = 0.0;
		for (int i = 0; i < this.list_stock.size(); i++) {
			total += this.list_stock.get(i).getMktValue();
		}
		return total;
	}

	// gia tri
	public Double getCostBasic() {
		Double total = 0.0;
		for (int i = 0; i < this.list_stock.size(); i++) {
			total += this.list_stock.get(i).getCostBasic();
		}
		return total;
	}

	// lai/lo du kien
	public double getGain() {
		return this.getMktValue() - this.getCostBasic();
	}

	// % lai/lo du kien
	public double getGainPerc() {
		if (this.getCostBasic() == 0.0) {
			return 0.0;
		}
		return (this.getGain() / this.getCostBasic()) * 100;
	}

	// lai/lo thuc te
	public double getProfit() {
		return this.profit;
	}

	public boolean setProfit(double profit) {
		this.profit = profit;
		return true;
	}

	// %lai/lo thuc te
	public double getPercProfit() {
		if (this.invested_money == 0.0) {
			return 0.0;
		}
		return (this.profit / this.invested_money)*100;
	}

}
