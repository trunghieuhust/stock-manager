package hedspi.k55.sm.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class User extends DBmanager {

	private String name = "project_stock_manager_ver_1.0";
	private String email;
	private double current_cash;
	private double invested_cash;

	public User(Context context) {
		super(context);
	}

	public User(Context context, String name) {
		super(context);
		this.name = "" + name;
		this.email = "default@gmai.com";
		this.current_cash = 0.0;
		this.invested_cash = 0.0;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = "" + name;
	}

	public boolean deposite(int cash) {
		if (isValidCash(cash)) {
			this.current_cash += cash;
			this.invested_cash += cash;
			return true;
		}
		return false;
	}

	public boolean withdraw(int cash) {
		if (isValidCash(cash)) {
			if (cash >= this.current_cash) {
				this.current_cash -= cash;
				this.invested_cash -= cash;
				return true;
			}
		}
		return false;
	}

	/**
	 * check cash valid or not.
	 * 
	 * @param cash
	 * @return true if valid.
	 */
	private boolean isValidCash(int cash) {
		if (cash >= 0) {
			return true;
		}
		return false;
	}

	public boolean init() {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM _usermanager WHERE id = 1 ;";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				this.name = cursor.getString(1);
				this.email = cursor.getString(3);
				this.current_cash = Double.parseDouble(cursor.getString(4));
				this.invested_cash = Double.parseDouble(cursor.getString(5));

				cursor.close();
				db.close();
				return true;
			}
		}
		cursor.close();
		db.close();
		return false;
	}

	public boolean add(String pass) {
		if (isExist(TABLE_USER, _NAME, this.name)) {
			System.out.println("user " + this.name + "corrupted");
			return false;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();

		value.put("name", this.name);
		value.put("currentmoney", this.current_cash);
		value.put("investedmoney", this.invested_cash);
		value.put("email", this.email);
		value.put("_password", pass);

		int affect = (int) db.insert("_usermanager", null, value);
		if (affect == -1) {
			db.close();
			System.out.println("insert user fail");
			return false;
		}
		db.close();
		System.out.println("insert user success with id: " + affect);
		return true;
	}

	public boolean update() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();

		value.put("name", this.name);
		value.put("currentmoney", this.current_cash);
		value.put("investedmoney", this.invested_cash);
		value.put("email", this.email);

		int affectrows;
		affectrows = db.update("_usermanager", value, _ID + " = 1", null);
		if (affectrows == 1) {
			System.out.println("update table user " + this.name + " success");
			db.close();
			return true;
		}
		System.out.println("update table user " + this.name + " fail");
		db.close();
		return true;
	}

	public boolean setPass(String pass) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();

		value.put(_PASSWORD, pass);

		int affectrows;
		affectrows = db.update("_usermanager", value, _ID + " = 1", null);
		if (affectrows == 1) {
			System.out.println("update table user " + this.name + " success");
			db.close();
			return true;
		}
		System.out.println("update table user " + this.name + " fail");
		db.close();
		return true;
	}

	public boolean hasPass() {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM _usermanager WHERE id = 1 ;";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				System.out.println("HAS PASS: user id 1 = "
						+ cursor.getString(0) + "-" + cursor.getString(1) + "-"
						+ cursor.getString(2));
				if (cursor.getString(2).length() != 0) {
					cursor.close();
					db.close();
					return true;
				}
			}
		}
		cursor.close();
		db.close();
		return false;
	}

	public void setEmail(String email) {
		this.email = email + "";
	}

	public String getEmail() {
		return this.email;
	}

	public boolean checkPass(String pass) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM _usermanager WHERE id = 1 ;";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				System.out.println("CHECK PASS: user id 1 = "
						+ cursor.getString(0) + "-" + cursor.getString(1) + "-"
						+ cursor.getString(2));
				if (pass.equalsIgnoreCase(cursor.getString(2))) {
					db.close();
					cursor.close();
					return true;
				}
			}
		}
		System.out.println("check pass fail");
		cursor.close();
		return false;
	}
}
