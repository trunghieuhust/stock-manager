package hedspi.k55.sm.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;

public class Transaction extends DBmanager {
	public static final int SELL = 1;
	public static final int BUY = 2;
	public static final int DEPOSITE = -1;
	public static final int WITHDRAW = -2;
	private int id = 0;
	private int type = 0;
	private String stock_symbol = "";
	private String time = "";
	private int shares = 0;
	private double price = 0.0;
	private double commission = 0.0;
	private double profit = 0.0;

	public Transaction(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}

	// zend update(15/4): constructor and get method
	public Transaction(Context context, int type, String stock_symbol,
			String time, int shares, double price, double commission,
			double profit) {
		super(context);
		this.type = type;
		this.stock_symbol = stock_symbol;
		this.time = time;
		this.shares = shares;
		this.price = price;
		this.commission = commission;
		this.profit = profit;
	}

	public String getTypeStr() {
		if (this.type == 1) {
			return "Sell";
		}
		if (this.type == 2) {
			return "Buy";
		}
		if (this.type == -1) {
			return "Deposte Cash";
		}
		return "Withdraw Cash";
	}

	public String getTimeFormal() {
		String formal[] = this.time.split("/");
		String returnStr;
		returnStr = formal[0] + "-";
		if (Integer.parseInt(formal[1]) == 1) {
			returnStr += "Jan";
		}
		if (Integer.parseInt(formal[1]) == 2) {
			returnStr += "Feb";
		}
		if (Integer.parseInt(formal[1]) == 3) {
			returnStr += "Mar";
		}
		if (Integer.parseInt(formal[1]) == 4) {
			returnStr += "Apr";
		}
		if (Integer.parseInt(formal[1]) == 5) {
			returnStr += "May";
		}
		if (Integer.parseInt(formal[1]) == 6) {
			returnStr += "Jun";
		}
		if (Integer.parseInt(formal[1]) == 7) {
			returnStr += "Jul";
		}
		if (Integer.parseInt(formal[1]) == 8) {
			returnStr += "Aug";
		}
		if (Integer.parseInt(formal[1]) == 9) {
			returnStr += "Sep";
		}
		if (Integer.parseInt(formal[1]) == 10) {
			returnStr += "Oct";
		}
		if (Integer.parseInt(formal[1]) == 11) {
			returnStr += "Nov";
		}
		if (Integer.parseInt(formal[1]) == 12) {
			returnStr += "Dec";
		}
		returnStr += "-" + formal[2];
		return returnStr;
	}

	public boolean init(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM _transaction WHERE id = " + id + ";";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				this.id = id;
				this.type = Integer.parseInt(cursor.getString(1));
				this.stock_symbol = cursor.getString(2);
				this.time = cursor.getString(3);
				this.shares = Integer.parseInt(cursor.getString(4));
				this.price = Double.parseDouble(cursor.getString(5));
				this.commission = Double.parseDouble(cursor.getString(6));
				this.profit = Double.parseDouble(cursor.getString(7));
				cursor.close();
				db.close();
				return true;
			}
		}
		cursor.close();
		db.close();
		return false;
	}

	public int getId() {
		return id;
	}

	public int add() {
		if (this.id > 0) {
			return -1;
		}
		Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
		portfolio.init();

		if (this.type == SELL) {
			// sell

			// change stock
			Stock stock = new Stock(context, this.stock_symbol);
			stock.init(); // get information from database
			int old_shares = Integer.parseInt(stock.getMyShares());
			double old_price = Double.parseDouble(stock.getAvgPrice());
			if (this.shares > old_shares) {
				System.out.println("TRANSACTION SELL: dont have enough shares");
				return -1;
			}
			int new_shares = old_shares - this.shares;
			double new_price = old_price;
			stock.setMyShares(new_shares + "");
			stock.update();

			// "sell" make profit
			this.profit = (1 - this.commission * 0.01) * this.price
					* this.shares - Double.parseDouble(stock.getAvgPrice())
					* this.shares;
			double current_profit = portfolio.getProfit();
			portfolio.setProfit(current_profit + this.profit);

			// change current money
			double current = portfolio.getCurrentMoney();
			double newcurrent = current + (1 - this.commission * 0.01)
					* this.price * this.shares;
			portfolio.setCurrentMoney(newcurrent);
			portfolio.update();

		}

		if (this.type == BUY) {
			// buy

			// check cash
			double current = portfolio.getCurrentMoney();
			double trade_money = (1 + this.commission * 0.01) * this.price
					* this.shares;
			if (current < trade_money) {
				System.out.println("TRANSACTION BUY: dont have enough cash");
				return -1;
			}
			// check stock
			if (!isExist(TABLE_STOCK, "symbol", this.stock_symbol)) {
				// add new stock
				Stock stock = new Stock(context, this.stock_symbol);
				stock.refresh(); // load information from internet
				stock.setAvgPrice(this.price + "");
				stock.setMyShares(this.shares + "");
				stock.update();
			} else {
				// stock is exist in database
				Stock stock = new Stock(context, this.stock_symbol);
				stock.init(); // load information from database
				// set new avgprice and shares
				int old_shares = Integer.parseInt(stock.getMyShares());
				double old_price = Double.parseDouble(stock.getAvgPrice());
				int new_shares = old_shares + this.shares;
				double new_price = (old_shares * old_price + this.shares
						* this.price)
						/ new_shares;
				stock.setAvgPrice(new_price + "");
				stock.setMyShares(new_shares + "");
				stock.update();
			}

			// change current money
			portfolio.setCurrentMoney(current - trade_money);
			portfolio.update();
		}

		if (this.type == DEPOSITE) {
			double current = portfolio.getCurrentMoney();
			double invested = portfolio.getInvestedMoney();
			double deposite = this.price;
			portfolio.setCurrentMoney(current + deposite);
			portfolio.setInvestedMoney(invested + deposite);
			portfolio.update();
		}

		if (this.type == WITHDRAW) {
			double current = portfolio.getCurrentMoney();
			double invested = portfolio.getInvestedMoney();
			double withdraw = this.price;
			if (withdraw > current) {
				System.out
						.println("TRANSACTION WITHDRAW: dont have enough cash ");
				return -1;
			}
			portfolio.setCurrentMoney(current - withdraw);
			portfolio.setInvestedMoney(invested - withdraw);
			portfolio.update();
		}

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();

		value.put(_TYPE, this.type);
		value.put("symbol", this.stock_symbol);
		value.put("time", this.time);
		value.put("shares", this.shares);
		value.put("pricein", this.price);
		value.put("commission", this.commission);
		value.put("profit", this.profit);

		int id = (int) db.insert(TABLE_TRANSACTION, null, value);
		if (id != -1) {
			this.id = id;
			System.out.println("insert transaction success: " + this.id + "-"
					+ this.type + "-" + this.stock_symbol + "-" + this.time
					+ "-" + this.shares + "-" + this.price + "-"
					+ this.commission + "-" + this.profit);
		} else {
			System.out.println("insert transaction fail");
		}
		db.close();
		return id;
	}

	public boolean update(int type, String time, int shares, double price,
			double commission) {
		if (this.id <= 0) {
			Log.i("id < 0", "sss");
			return false;
		}
		if (this.type != type) {
			Log.i("TRANSACTION UPDATE", "different type");
			return false;
		}

		if (type == SELL) {
			Log.i("SELL", "sell");
			Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
			portfolio.init();
			Stock stock = new Stock(context, this.stock_symbol);
			stock.init();
			if ((this.shares + Integer.parseInt(stock.getMyShares())) < shares) {
				Log.i("TRANSACTION UPDATE", "sell: dont have enough shares");
				return false;
			}

			// tra lai gia tri ban dau
			int old_shares = Integer.parseInt(stock.getMyShares())
					+ this.shares;
			double old_avgprice = ((1 - this.commission * 0.01) * this.price
					* this.shares - this.profit)
					/ this.shares;
			double old_current_money = portfolio.getCurrentMoney()
					- (1 - this.commission * 0.01) * this.price * this.shares;

			// make change for new transaction
			int new_shares = old_shares - shares;
			double new_profit = (1 - commission * 0.01) * price * shares
					- old_avgprice * shares;
			double new_current_money = old_current_money
					+ (1 - commission * 0.01) * price * shares;

			stock.setMyShares(String.valueOf(new_shares));
			stock.update();
			portfolio.setCurrentMoney(new_current_money);
			portfolio.update();

			this.time = time;
			this.shares = shares;
			this.price = price;
			this.commission = commission;
			this.profit = new_profit;

		}
		if (type == BUY) {
			Log.i("BUY", "buy");
			Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
			portfolio.init();
			Stock stock = new Stock(context, this.stock_symbol);
			stock.init();

			double old_current = portfolio.getCurrentMoney()
					+ (1 + this.commission * 0.01) * this.price * this.shares;
			double trade_money = (1 + commission * 0.01) * price * shares;
			if (old_current < trade_money) {
				Log.i("TRANSACTION UPDATE", "buy: dont have enough cash");
				return false;
			}
			// new shares,avgprice for stock
			int new_shares = Integer.parseInt(stock.getMyShares())
					- this.shares + shares;
			double new_costbasic = Double.parseDouble(stock.getAvgPrice())
					* Integer.parseInt(stock.getMyShares()) - this.shares
					* this.price + shares * price;
			double new_avgprice = new_costbasic / new_shares;

			// new current money
			double new_current_money = old_current - trade_money;

			// update stock, portfolio
			stock.setMyShares(String.valueOf(new_shares));
			stock.setAvgPrice(String.valueOf(new_avgprice));
			stock.update();
			portfolio.setCurrentMoney(new_current_money);
			portfolio.update();

			this.time = time;
			this.shares = shares;
			this.price = price;
			this.commission = commission;
		}
		if (type == DEPOSITE) {
			Log.i("DEPOSITE", "deposite");
			Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
			portfolio.init();
			double old_current = portfolio.getCurrentMoney() - this.price;
			double old_invested = portfolio.getInvestedMoney() - this.price;
			double deposite = price;
			portfolio.setCurrentMoney(old_current + deposite);
			portfolio.setInvestedMoney(old_invested + deposite);
			portfolio.update();

			this.time = time;
			this.shares = 0;
			this.price = price;
			this.commission = 0.0;
		}
		if (type == WITHDRAW) {
			Log.i("WITHDRAW", "WITHDRAW");
			Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
			portfolio.init();
			double old_current = portfolio.getCurrentMoney() + this.price;
			double old_invested = portfolio.getInvestedMoney() + this.price;
			double withdraw = price;
			if (withdraw > old_current) {
				Log.i("TRANSACTION UPDATE", "withdraw: dont have enough cash");
				return false;
			}
			portfolio.setCurrentMoney(old_current - withdraw);
			portfolio.setInvestedMoney(old_invested - withdraw);
			portfolio.update();

			this.time = time;
			this.shares = 0;
			this.price = price;
			this.commission = 0.0;
		}
		Log.i("IF DONE", "    sss");
		// update database
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();

		value.put("type", this.type);
		value.put("symbol", this.stock_symbol);
		value.put("time", this.time.toString());
		value.put("shares", this.shares);
		value.put("pricein", this.price);
		value.put("commission", this.commission);
		value.put("profit", this.profit);

		int affect = db.update(TABLE_TRANSACTION, value, "id = " + this.id,
				null);
		db.close();
		if (affect != 1) {
			Log.i("AFFECT", " ");
			return false;
		}
		return true;
	}

	public int getType() {
		return this.type;
	}

	public String getStockSymbol() {
		return this.stock_symbol;
	}

	public String getTime() {
		return this.time;
	}

	public int getShares() {
		return this.shares;
	}

	public double getPrice() {
		return this.price;
	}

	public double getCommission() {
		return this.commission;
	}

	public double getProfit() {
		return this.profit;
	}

	// zend update(16/4): update set method
	public boolean setType(int type) {
		if (type == 1 || type == 2) {
			this.type = type;
			return true;
		}
		return false;
	}

	public boolean setID(int id) {
		if (id > 0) {
			this.id = id;
			return true;
		}
		return false;
	}

	public boolean setTime(Time time) {
		this.time = time.toString();
		return true;
	}

	public boolean setStockSymbol(String symbol) {
		this.stock_symbol = symbol;
		return true;
		// return false when symbol.length > ??
	}

	public boolean setShares(int share) {
		if (share > 0) {
			this.shares = share;
			return true;
		}
		return false;
	}

	public boolean setPrice(double price) {
		if (price > 0) {
			this.price = price;
			return true;
		}
		return false;
	}

	public boolean setCommission(Double commission) {
		if (commission > 0) {
			this.commission = commission;
			return true;
		}
		return false;
	}

	public boolean setNotes(double profit) {
		this.profit = profit;
		return true;
	}

	// zend update(16/4): complete method getAmount()
	public Double getAmount() {
		return (this.price * this.shares);
	}

}
