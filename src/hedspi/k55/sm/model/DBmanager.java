package hedspi.k55.sm.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author hieu
 * @see http://www.vogella.com/articles/AndroidSQLite/article.html
 * 
 */
public class DBmanager extends SQLiteOpenHelper {
	// cac bang trong CSDL
	public static final String TABLE_STOCK = "stock";
	public static final String TABLE_PORTFOLIO = "portfolio";
	public static final String TABLE_TRANSACTION = "_transaction";
	public static final String TABLE_USER = "_usermanager";

	// cac cot trong CSDL
	public static final String _ID = "id";
	public static final String _NAME = "name";
	public static final String _INFO = "info";
	public static final String _CURRENT_MONEY = "currentmoney";
	public static final String _INVESTED_MONEY = "investedmoney";
	public static final String _NUMBER_STOCK = "numberstock";
	public static final String _NOTES = "note";
	public static final String _SYMBOL = "symbol";
	public static final String _PORTFOLIO_ID = "portfolioid";
	public static final String _EXCHANGE_ID = "exchange_id";
	public static final String _PRICE_IN = "pricein";
	public static final String _LAST_PRICE = "lastprice";
	public static final String _SHARES = "shares";
	public static final String _OPEN = "open";
	public static final String _HIGH = "high";
	public static final String _LOW = "low";
	public static final String _MKT_CAP = "maket_capitalization";
	public static final String _VOLUME = "volume";
	public static final String _TYPE = "type";
	public static final String _STOCK_ID = "stock_id";
	public static final String _DATE = "date";
	public static final String _TIME = "time";
	public static final String _COMMISSION = "commission";
	public static final String _PASSWORD = "_password";
	public static final String _EMAIL = "email";
	public static final String _PROFIT = "profit";

	private static final String DATABASE_NAME = "STOCK_MANAGE_DATABASE";
	private static final int DATABASE_VERSION = 1;
	private static final String PORTFOLIO_NAME = "DEFAULT_PORTFOLIO";
	private static final String USER_NAME = "DEFAULT_NAME";

	// Database creation sql statement
	private static final String CREATE_PORTFOLIO = "CREATE TABLE "
			+ TABLE_PORTFOLIO + " (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + _NAME + " VARCHAR , "
			+ _CURRENT_MONEY + " DOUBLE DEFAULT 0, " + _INVESTED_MONEY
			+ " DOUBLE DEFAULT 0, " + _NUMBER_STOCK + " INTEGER DEFAULT 0, "
			+ _PROFIT + " DOUBLE DEFAULT 0 " + ");";
	private static final String CREATE_STOCK = "CREATE  TABLE " + "stock"
			+ " (" + " id" + "  INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ " symbol" + " VARCHAR, " + " companyname" + "  VARCHAR, "
			+ " exchange" + "  VARCHAR, " + " lasttradetime" + "  VARCHAR, "
			+ " change" + "  VARCHAR, " + " perc_change" + "  VARCHAR, "
			+ " openlastprice" + "  VARCHAR, " + " highlowprice" + " VARCHAR, "
			+ " volume" + " VARCHAR, " + " avgvolume" + " VARCHAR, "
			+ " highlow52week" + " VARCHAR, " + " market_cap" + " VARCHAR, "
			+ " pe" + " VARCHAR, " + " beta" + " VARCHAR, " + " eps"
			+ " VARCHAR, " + " shares" + " VARCHAR, " + " dividendyield"
			+ " VARCHAR, " + " avgprice" + " VARCHAR, " + " myshare"
			+ " VARCHAR);";
	private static final String CREATE_TRANSACTION = "CREATE TABLE "
			+ TABLE_TRANSACTION + " (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + _TYPE + " INTEGER , "
			+ _SYMBOL + " VARCHAR , " + _TIME
			+ " DATETIME  DEFAULT CURRENT_TIME, " + _SHARES
			+ " INTEGER DEFAULT 0, " + _PRICE_IN + " DOUBLE DEFAULT 0, "
			+ _COMMISSION + " DOUBLE DEFAULT 0, " + _PROFIT
			+ " DOUBLE DEFAULT 0 " + ");";
	private static final String CREATE_USER = "CREATE TABLE " + TABLE_USER
			+ "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + _NAME
			+ " VARCHAR , " + _PASSWORD + " VARCHAR , " + _EMAIL + " VARCHAR, "
			+ _CURRENT_MONEY + " DOUBLE DEFAULT 0, " + _INVESTED_MONEY
			+ " DOUBLE DEFAULT 0" + ");";

	// luu context
	protected Context context;

	public DBmanager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PORTFOLIO);
		db.execSQL(CREATE_STOCK);
		db.execSQL(CREATE_TRANSACTION);
		db.execSQL(CREATE_USER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_PORTFOLIO);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_STOCK);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_TRANSACTION);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_USER);
		onCreate(db);
	}

	public boolean isExist(String table, String check, String checkstr) {
		if (table.equalsIgnoreCase(TABLE_TRANSACTION)) {
			return false;
		} else {
			SQLiteDatabase db = this.getReadableDatabase();
			String query = "SELECT * FROM " + table + " WHERE " + check
					+ " like " + "\"" + checkstr + "\"";
			Cursor cursor = null;
			cursor = db.rawQuery(query, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					cursor.close();
					db.close();
					return true; // ton tai
				}

			}
			cursor.close();
			db.close();
			return false; // ko ton tai
		}
	}

	public Cursor doQuery(String string) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(string, null);
		return cursor;
	}
}
