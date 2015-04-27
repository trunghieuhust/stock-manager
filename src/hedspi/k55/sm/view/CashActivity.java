package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.DBmanager;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.Transaction;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class CashActivity extends SherlockFragmentActivity implements
		OnDateSetListener, Iview {
	private RadioGroup type;
	private EditText amount;
	private EditText date;
	private Button add;
	private Button cancel;
	private Portfolio portfolio;
	static final int DEPOSITE = -1;
	static final int WITHDRAW = -2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cash_activity);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setTitle("Deposite/Withdraw");
		portfolio = new Portfolio(CashActivity.this, PORTFOLIO_NAME);
		DBmanager db = new DBmanager(CashActivity.this);
		if (db.isExist(DBmanager.TABLE_PORTFOLIO, DBmanager._NAME,
				PORTFOLIO_NAME)) {
			portfolio.init();
		}
		type = (RadioGroup) findViewById(R.id.cash_transaction_choice);
		amount = (EditText) findViewById(R.id.cash_amount_box);
		date = (EditText) findViewById(R.id.cash_datepicker);
		add = (Button) findViewById(R.id.cash_save);
		cancel = (Button) findViewById(R.id.cash_cancel);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		date.setText(day + "/" + (month + 1) + "/" + year);
		date.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					DialogFragment newFragment = new DatePickerFragment();
					newFragment.show(getSupportFragmentManager(), "datePicker");
					break;

				default:
					break;
				}

				return true;
			}

		});

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String symbol = null;
				;
				int type_select = 0;
				int rb = type.getCheckedRadioButtonId();
				switch (rb) {
				case R.id.cash_type_deposite:
					type_select = DEPOSITE;
					symbol = "DEPOSITE";
					break;

				default:
					type_select = WITHDRAW;
					symbol = "WITHDRAW";
					break;
				}

				String date_select = date.getText().toString();
				String amount_select = amount.getText().toString();
				double amount = 0.0;
				try {
					amount = Double.parseDouble(amount_select);
				} catch (NumberFormatException e) {
					Toast.makeText(CashActivity.this, "Wrong input",
							Toast.LENGTH_SHORT).show();
					return;
				}

				Transaction transaction = new Transaction(CashActivity.this,
						type_select, symbol, date_select, 0, amount, 0, 0);
				System.out.println("TRANSACTION " + type_select + ":"
						+ amount_select);
				if (transaction.add() == -1) {
					// Toast.makeText(CashActivity.this, "Not enough cash",
					// Toast.LENGTH_SHORT).show();
					String currentMoney = getIntent().getStringExtra(
							"CURRENT_BALANCE");
					AlertDialog.Builder simpleDialog = new AlertDialog.Builder(
							CashActivity.this);
					simpleDialog.setTitle("Not enough cash!");
					simpleDialog.setMessage("Your current cash balance is : "
							+ currentMoney);
					simpleDialog.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							});
					simpleDialog.show();

					return;
				}
				finish();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		monthOfYear++;
		date.setText(dayOfMonth + "/" + monthOfYear + "/" + year);

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}
}
