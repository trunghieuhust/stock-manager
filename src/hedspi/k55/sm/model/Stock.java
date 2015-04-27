package hedspi.k55.sm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Stock extends DBmanager {
	public static final String google_json_url = "https://www.google.com/finance/info?infotype=infoquoteall&q=";
	public static final String google_xml_url = "http://www.google.com/ig/api?stock=";
	// public static final String google_search_url =
	// "http://www.google.com/finance/match?matchtype=matchall&q=";
	public static final String google_new_url = "http://www.google.com/finance/company_news?q= &output=rss";
	public static final String google_chart_url = "https://www.google.com/finance/getchart?q=";
	public static final String ONE_DAY = "&p=1d&i=360";
	public static final String ONE_WEEK = "&p=7d&i=2016";
	public static final String ONE_MONTH = "&p=1M&i=86400";
	public static final String SIX_MONTH = "&p=6M&i=345600";
	public static final String ONE_YEAR = "&p=1Y&i=518400";
	public static final String FIVE_YEAR = "&p=5Y&i=1209600";
	// https://www.google.com/finance/getchart?q=XING&p=5d&i=400
	// https://www.google.com/finance/getchart?q=AAPL&p=6M&i=86400
	// 1D: i=360
	//
	/* Goole finance key */
	/* avvo : Average volume (float with multiplier, like '3.54M') */
	/* beta : Beta (float) */
	/* c : Amount of change while open (float) */
	/* ccol : (unknown) (chars) */
	/* cl : Last perc. change */
	/* cp : Change perc. while open (float) */
	/* e : Exchange (text, like 'NASDAQ') */
	/* ec : After hours last change from close (float) */
	/* eccol : (unknown) (chars) */
	/* ecp : After hours last chage perc. from close (float) */
	/* el : After. hours last quote (float) */
	/* el_cur : (unknown) (float) */
	/* elt : After hours last quote time (unknown) */
	/* eo : Exchange Open (0 or 1) */
	/* eps : Earnings per share (float) */
	/* fwpe : Forward PE ratio (float) */
	/* hi : Price high (float) */
	/* hi52 : 52 weeks high (float) */
	/* id : Company id (identifying number) */
	/* l : Last value while open (float) */
	/* l_cur : Last value at close (like 'l') */
	/* lo : Price low (float) */
	/* lo52 : 52 weeks low (float) */
	/* lt : Last value date/time */
	/* ltt : Last trade time (Same as "lt" without the data) */
	/* mc : Market cap. (float with multiplier, like '123.45B') */
	/* name : Company name (text) */
	/* op : Open price (float) */
	/* pe : PE ratio (float) */
	/* t : Ticker (text) */
	/* type : Type (i.e. 'Company') */
	/* vo : Volume (float with multiplier, like '3.54M') */
	private String symbol;
	private String companyName;
	private String exchange;
	private String lastTradeTime; // ex :Apr 12, 4:00PM EDT
	private String change; // Amount of change while open
	private String perc_change;// Change perc. while open
	private String openPrice;
	private String lastPrice;
	private String highPrice;
	private String lowPrice;
	private String volume;// ex 1,64M
	private String avg_volume;// Average volume (ex 2.13M)
	private String high52week;// 52 weeks high
	private String low52week;// 52 weeks low
	private String market_cap; // Market capitalization
	private String pe = ""; // P/E ratio - price to earning ratio
	private String fwpe = ""; // Forward PE ratio
	private String beta = "";
	private String eps = "";
	private String shares = ""; // tong so co phieu phat hanh
	private String ins_own = "";// institutions own
	private String type = ""; // company, etc...
	private String dividend = "";
	private String dividend_yield = "";
	// /////////////////////////

	// zend update: share - so co phieu, price_in - gia trung binh,portfolio_id
	// - truong muc
	private String avgprice = "0";
	private String my_shares = "0";
	private String portfolio_id = "0";

	// database

	/**
	 * load from database
	 * 
	 * @return
	 */
	public boolean init() {
		if (isExist("stock", "symbol", this.symbol)) {
			SQLiteDatabase db = this.getReadableDatabase();
			String SQLsyntax = "SELECT * FROM stock WHERE symbol like " + "\""
					+ this.symbol + "\"";
			Cursor cursor = db.rawQuery(SQLsyntax, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					String[] test;
					this.companyName = cursor.getString(2);
					this.exchange = cursor.getString(3);
					this.lastTradeTime = cursor.getString(4);
					this.change = cursor.getString(5);
					this.perc_change = cursor.getString(6);
					test = cursor.getString(7).split("/");
					this.openPrice = test[0];
					this.lastPrice = test[1];
					test = cursor.getString(8).split("/");
					this.highPrice = test[0];
					this.lowPrice = test[1];
					this.volume = cursor.getString(9);
					this.avg_volume = cursor.getString(10);
					test = cursor.getString(11).split("/");
					this.high52week = test[0];
					this.low52week = test[1];
					this.market_cap = cursor.getString(12);
					this.pe = cursor.getString(13);
					this.beta = cursor.getString(14);
					this.eps = cursor.getString(15);
					this.shares = cursor.getString(16);
					if (cursor.getString(17).length() == 1) {
						this.dividend = "0";
						this.dividend_yield = "0";
					} else {
						test = cursor.getString(17).split("/");
						this.dividend = test[0];
						this.dividend_yield = test[1];
					}
					this.avgprice = cursor.getString(18);
					this.my_shares = cursor.getString(19);
					cursor.close();
					db.close();
					return true;
				}
			}
			cursor.close();
			db.close();
		}
		return false;
	}
	public Stock(Context context, String symbol) {
		super(context);
		this.symbol = symbol;
	}

	public boolean refresh() {
		if (symbol != null) {
			String url = makeURL(symbol);
			JsonFetch jf = new JsonFetch(url);

			getJsonContent(jf.getObject());
			return true;
		} else
			return false;
	}

	private void getJsonContent(JSONObject json) {
		if (json == null)
			return;
		try {
			symbol = json.getString("t");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			exchange = json.getString("e");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			lastPrice = json.getString("l");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			lastTradeTime = json.getString("lt");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			change = json.getString("c");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			perc_change = json.getString("cp");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			openPrice = json.getString("op");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			highPrice = json.getString("hi");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			lowPrice = json.getString("lo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			volume = json.getString("vo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			avg_volume = json.getString("avvo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			high52week = json.getString("hi52");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			low52week = json.getString("lo52");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			market_cap = json.getString("mc");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			pe = json.getString("pe");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			fwpe = json.getString("fwpe");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			beta = json.getString("beta");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			eps = json.getString("eps");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			shares = json.getString("shares");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			ins_own = json.getString("inst_own");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			companyName = json.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			type = json.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			dividend = json.getString("div");
		} catch (JSONException e) {
			e.printStackTrace();
			dividend = "";
		}
		try {
			dividend_yield = json.getString("yld");
		} catch (JSONException e) {
			e.printStackTrace();
			dividend_yield = "";
		}
	}

	public String makeURL(String symbol) {
		StringBuffer sb = new StringBuffer(google_json_url);
		sb.append(symbol);
		return sb.toString();
	}

	public String getURLChart(String period) {
		StringBuffer buff = new StringBuffer(google_chart_url);
		buff.append(getSymbol().toUpperCase(Locale.ENGLISH));
		buff.append(period);
		return buff.toString();
	}

	public boolean update() {
		if (isExist(TABLE_STOCK, _SYMBOL, this.symbol)) {
			// update
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues value = new ContentValues();

			value.put("symbol", this.symbol);
			value.put("companyname", this.companyName);
			value.put("exchange", this.exchange);
			value.put("lasttradetime", this.lastTradeTime);
			value.put("change", this.change);
			value.put("perc_change", this.perc_change);
			value.put("openlastprice", this.openPrice + "/" + this.lastPrice);
			value.put("highlowprice", this.highPrice + "/" + this.lowPrice);
			value.put("volume", this.volume);
			value.put("avgvolume", this.avg_volume);
			value.put("highlow52week", this.high52week + "/" + this.low52week);
			value.put("market_cap", this.market_cap);
			value.put("pe", this.pe);
			value.put("beta", this.beta);
			value.put("eps", this.eps);
			value.put("shares", this.shares);
			value.put("dividendyield", this.dividend + "/"
					+ this.dividend_yield);
			value.put("avgprice", this.avgprice);
			value.put("myshare", this.my_shares);

			int affect = db.update("stock", value, "symbol like " + "\""
					+ this.symbol + "\"", null);
			db.close();
			if (affect != 1) {
				return false;
			}
			return true;
		} else {
			// add
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues value = new ContentValues();

			value.put("symbol", this.symbol);
			value.put("companyname", this.companyName);
			value.put("exchange", this.exchange);
			value.put("lasttradetime", this.lastTradeTime);
			value.put("change", this.change);
			value.put("perc_change", this.perc_change);
			value.put("openlastprice", this.openPrice + "/" + this.lastPrice);
			value.put("highlowprice", this.highPrice + "/" + this.lowPrice);
			value.put("volume", this.volume);
			value.put("avgvolume", this.avg_volume);
			value.put("highlow52week", this.high52week + "/" + this.low52week);
			value.put("market_cap", this.market_cap);
			value.put("pe", this.pe);
			value.put("beta", this.beta);
			value.put("eps", this.eps);
			value.put("shares", this.shares);
			value.put("dividendyield", this.dividend + "/"
					+ this.dividend_yield);
			value.put("avgprice", this.avgprice);
			value.put("myshare", this.my_shares);

			int affect = (int) db.insert("stock", null, value);
			db.close();
			if (affect == -1) {

				return false;
			}
			return true;
		}
	}

	public ArrayList<HashMap<String, String>> getAllTransaction() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String query = "SELECT * FROM _transaction WHERE ( symbol like " + "\""
				+ this.symbol + "\"" + " AND " + "type>0 )";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					int type = Integer.parseInt(cursor.getString(1));
					String strType;
					if (type == Transaction.BUY) {
						strType = "BUY";
					} else
						strType = "SELL";
					String time = cursor.getString(3);
					int share_int = Integer.parseInt(cursor.getString(4));
					String share;
					if (Integer.parseInt(cursor.getString(1)) == Transaction.SELL) {
						share = "-" + share_int;
					} else {
						share = "+" + share_int;
					}
					String price = String.format(Locale.ENGLISH, "%.2f",
							Double.parseDouble(cursor.getString(5)));
					String commission = String.format(Locale.ENGLISH, "%.2f",
							Double.parseDouble(cursor.getString(6)));
					String profit = String.format(Locale.ENGLISH, "%.2f",
							Double.parseDouble(cursor.getString(7)));
					String id = cursor.getString(0);

					HashMap<String, String> row = new HashMap<String, String>();
					row.put("ID", id);
					row.put("TIME", time);
					row.put("SHARES", share);
					row.put("PRICE", price);
					row.put("TYPE", strType);
					row.put("COMMISSION", commission);
					row.put("PROFIT", profit);
					list.add(row);
				} while (cursor.moveToNext());
				cursor.close();
				db.close();
				return list;
			}

			db.close();
		}
		return null;
	}

	// gia tri
	public double getCostBasic() {
		return Double.parseDouble(this.avgprice)
				* Double.parseDouble(this.my_shares);
	}

	public double getMktPrice() {
		// ktra ngay thoi diem hien tai: get highPrice or lastPrice???
		return Double.parseDouble(this.highPrice);
	}

	// gia tri thi truong
	public double getMktValue() {
		return (this.getMktPrice() * Double.parseDouble(this.my_shares));
	}

	// lai/lo du kien
	public double getGain() {
		if (getCostBasic() == 0) {
			return 0.0;
		}
		double gain = this.getMktValue() - this.getCostBasic();
		return gain;
	}

	// % lai/lo du kien
	public Double getGainPerc() {
		if (getCostBasic() == 0) {
			return 0.0;
		}
		return (this.getGain() / this.getCostBasic()) * 100;
	}

	// lai qua cac giao dich da thuc hien
	public Double getProfit() {
		double profit = 0.0;
		ArrayList<HashMap<String, String>> list = getAllTransaction();
		if (list == null) {
			return 0.0;
		}
		for (int i = 0; i < list.size(); i++) {
			profit += Double.parseDouble(list.get(i).get("PROFIT"));
		}
		return profit;
	}

	public String getSymbol() {
		return symbol;
	}

	public boolean setSymbol() {
		return false;
	}

	// Zend update
	public String getCompanyName() {
		return this.companyName;
	}

	public String getExchange() {
		return this.exchange;
	}

	public String getLastPrice() {
		return lastPrice;
	}

	public String getOpen() {
		return openPrice;
	}

	public String getHigh() {
		return highPrice;
	}

	public String getHigh52week() {
		return high52week;
	}

	public String getLow() {
		return lowPrice;
	}

	public String getLow52week() {
		return low52week;
	}

	public String getChange() {
		return change;
	}

	public String getPercChange() {
		return perc_change;
	}

	public Double getTotal() {
		return (Double.parseDouble(this.my_shares) * Double
				.parseDouble(this.avgprice));
	}

	public String getMyShares() {
		return this.my_shares;
	}

	public String getTradeTime() {
		return this.lastTradeTime;
	}

	public String getAvgPrice() {
		String formatted = "0.0";
		try {
			Double avg = Double.parseDouble(avgprice);
			formatted = String.format(Locale.ENGLISH, "%.2f", avg);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return formatted;
	}

	public String getAvgVolume() {
		return avg_volume;
	}

	// end update
	public String getPE() {
		return pe;
	}

	public String getfwPE() {
		return fwpe;
	}

	public String getEPS() {
		return eps;
	}

	public String getBeta() {
		return beta;
	}

	public String getRange() {
		return null;
	}

	public String getShares() {
		return shares;
	}

	public String getInstOwn() {
		return ins_own;
	}

	public String getType() {
		return type;
	}

	public String getVolume() {
		return volume;
	}

	public String getMarketCap() {
		return market_cap;
	}

	public boolean isEmpty() {
		return false;
	}

	public String getPrice() {
		return this.lastPrice;
	}

	public String getDividend() {
		return dividend;
	}

	public String getDividendYield() {
		return dividend_yield;
	}

	public void setMyShares(String myshares) {
		this.my_shares = myshares;
	}

	public void setAvgPrice(String avgprice) {
		this.avgprice = avgprice;
	}

	public Double getProfitPerc() {
		ArrayList<HashMap<String, String>> list = getAllTransaction();
		double total_invested = 0.0;
		double total_profit = 0.0;
		if (list == null) {
		return 0.0;
		}
		for (int i = 0; i < list.size(); i++) {
		if (list.get(i).get("TYPE").compareToIgnoreCase("BUY") == 0) {
		// buy
		double price = Double.parseDouble(list.get(i).get("PRICE"));
		String temp = list.get(i).get("SHARES").substring(1);
		double shares = Integer.parseInt(temp);
		total_invested += price * shares;
		}
		else{
		// sell
		total_profit += Double.parseDouble(list.get(i).get("PROFIT"));
		}
		}
		return (total_profit/total_invested)*100;
		}
}
