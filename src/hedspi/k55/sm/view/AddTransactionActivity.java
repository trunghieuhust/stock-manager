package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.Transaction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class AddTransactionActivity extends SherlockFragmentActivity implements
		OnDateSetListener {
	private RadioGroup type;
	private RadioButton rb_buy;
	private RadioButton rb_sell;
	private EditText price;
	private EditText shares;
	private EditText date;
	private EditText commisson;
	private Button add;
	private Button cancel;
	private CheckBox bonusStock;
	private String symbol;
	private String exchange;
	private String old_price;
	private String old_comission;
	private Portfolio portfolio;
	private NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_transaction_activity);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		type = (RadioGroup) findViewById(R.id.add_transaction_type);
		price = (EditText) findViewById(R.id.addTransactionPriceBox);
		shares = (EditText) findViewById(R.id.addTransactionSharesBox);
		date = (EditText) findViewById(R.id.addTransactionDatePicker);
		commisson = (EditText) findViewById(R.id.addTransactionCommision);
		bonusStock = (CheckBox) findViewById(R.id.add_edit_is_bonus_check);
		add = (Button) findViewById(R.id.Add);
		cancel = (Button) findViewById(R.id.Cancel);
		price.setText(getIntent().getStringExtra("PRICE"));
		symbol = getIntent().getStringExtra("SYMBOL");
		exchange = getIntent().getStringExtra("EXCHANGE");
		rb_buy = (RadioButton) findViewById(R.id.type_buy);
		rb_sell = (RadioButton) findViewById(R.id.type_sell);
		getSupportActionBar().setTitle(exchange + ":" + symbol);
		date.setFocusable(false);
		date.setFocusableInTouchMode(false);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		date.setText(day + "/" + (month+1) + "/" + year);

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
		add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int type_select = 0;
				int rb = type.getCheckedRadioButtonId();
				switch (rb) {
				case R.id.type_buy:
					type_select = Transaction.BUY;

					break;

				default:
					type_select = Transaction.SELL;
					break;
				}

				String date_select = date.getText().toString();
				String shares_select = shares.getText().toString();
				String price_select = price.getText().toString();
				String commisson_select = commisson.getText().toString();
				if (date_select.length() == 0) {
					Toast.makeText(AddTransactionActivity.this,
							"Please select date", Toast.LENGTH_SHORT).show();
					return;
				}
				if (shares_select.length() == 0) {
					Toast.makeText(AddTransactionActivity.this,
							"Please enter shares", Toast.LENGTH_SHORT).show();
					return;
				}
				if (price_select.length() == 0) {
					Toast.makeText(AddTransactionActivity.this,
							"Please enter prices", Toast.LENGTH_SHORT).show();
					return;
				}
				if (commisson_select.length() == 0) {
					Toast.makeText(AddTransactionActivity.this,
							"Please select commisson", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				try {
					Double.parseDouble(commisson_select);
				} catch (NumberFormatException e) {
					// TODO: handle exception
					Toast.makeText(AddTransactionActivity.this,
							"Wrong input Commission!", Toast.LENGTH_LONG)
							.show();
					return;
				}
				if (Integer.parseInt(shares_select) == 0) {
					Toast.makeText(AddTransactionActivity.this,
							"Wrong input shares!", Toast.LENGTH_LONG).show();
					return;
				}
				if (bonusStock.isChecked()) {
					Transaction transaction = new Transaction(
							AddTransactionActivity.this, Transaction.BUY,
							symbol, date_select, Integer
									.parseInt(shares_select), 0, 0, 0);
					transaction.add();
					finish();
					return;
				}
				Transaction trans = new Transaction(
						AddTransactionActivity.this, type_select, symbol,
						date_select, Integer.parseInt(shares_select), Double
								.parseDouble(price_select), Double
								.parseDouble(commisson_select), 0);
				if (trans.add() == -1) {
					if (type_select == Transaction.BUY) {
						portfolio = new Portfolio(AddTransactionActivity.this,
								"DEFAULT_PORTFOLIO");
						portfolio.init();
						AlertDialog.Builder simpleDialog = new AlertDialog.Builder(
								AddTransactionActivity.this);
						simpleDialog.setTitle("Not enough cash!");
						simpleDialog.setMessage("Your current cash balance is : "
								+ numberFormat.format(portfolio
										.getCurrentMoney()) + "$");
						simpleDialog.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								});
						simpleDialog.show();
						// Toast.makeText(AddTransactionActivity.this,
						// "Not enough cash", Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(AddTransactionActivity.this,
						// "Not enough shares", Toast.LENGTH_SHORT).show();
						AlertDialog.Builder simpleDialog = new AlertDialog.Builder(
								AddTransactionActivity.this);
						simpleDialog.setTitle("Not enough shares!");
						simpleDialog
								.setMessage("Your current shares of this stock is : "
										+ getIntent().getStringExtra("SHARES"));
						simpleDialog.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								});
						simpleDialog.show();
					}
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
		bonusStock.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					price.setFocusable(false);
					price.setFocusableInTouchMode(false);
					old_price = price.getText().toString();
					old_comission = commisson.getText().toString();
					price.setText("0.00");
					commisson.setText("0.00");
					commisson.setFocusable(false);
					commisson.setFocusableInTouchMode(false);
					rb_buy.setEnabled(false);
					rb_sell.setEnabled(false);
				} else {
					price.setFocusable(true);
					price.setFocusableInTouchMode(true);
					commisson.setFocusable(true);
					commisson.setFocusableInTouchMode(true);
					commisson.setText(old_comission);
					price.setText(old_price);
					rb_buy.setEnabled(true);
					rb_sell.setEnabled(true);
				}
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		monthOfYear++;
		date.setText(dayOfMonth + "/" + monthOfYear + "/" + year);

	}
}
