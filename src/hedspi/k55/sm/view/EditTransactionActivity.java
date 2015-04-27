package hedspi.k55.sm.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.Transaction;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class EditTransactionActivity extends SherlockFragmentActivity implements
		OnDateSetListener, Iview {
	private RadioGroup type;
	private RadioButton rb_sell;
	private RadioButton rb_buy;
	private EditText price;
	private EditText shares;
	private EditText date;
	private EditText commisson;
	private Button add;
	private Button cancel;
	private CheckBox bonusStock;
	private Transaction transaction;
	private String old_price;
	private String commission;
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
		add = (Button) findViewById(R.id.Add);
		cancel = (Button) findViewById(R.id.Cancel);
		bonusStock = (CheckBox) findViewById(R.id.add_edit_is_bonus_check);
		rb_sell = (RadioButton) findViewById(R.id.type_sell);
		rb_buy = (RadioButton) findViewById(R.id.type_buy);
		add.setText("Save");
		date.setFocusable(false);
		date.setFocusableInTouchMode(false);
		int transactionId = getIntent().getIntExtra("ID", 0);
		if (transactionId == 0) {
			Log.i("EDIT TRANSACTION", "transaction id is invalid.");
			finish();
		}
		transaction = new Transaction(EditTransactionActivity.this);
		transaction.init(transactionId);
		if (transaction.getType() == Transaction.BUY) {
			rb_buy.setChecked(true);
			rb_sell.setChecked(false);
			rb_sell.setEnabled(false);
		} else {
			rb_buy.setChecked(false);
			rb_sell.setChecked(true);
			rb_buy.setEnabled(false);
			bonusStock.setEnabled(false);
		}
		if (transaction.getPrice() == 0.0) {
			bonusStock.setChecked(true);
			price.setFocusable(false);
			price.setFocusableInTouchMode(false);
			commisson.setFocusable(false);
			commisson.setFocusableInTouchMode(false);
		}
		price.setText(String.format(Locale.ENGLISH, "%.2f",
				transaction.getPrice()));
		getSupportActionBar().setTitle(
				"Edit transaction:" + transaction.getStockSymbol());
		shares.setText(transaction.getShares() + "");
		commisson.setText(String.format(Locale.ENGLISH, "%.2f",
				transaction.getCommission()));
		date.setText(transaction.getTime());
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
					Toast.makeText(EditTransactionActivity.this,
							"Please select date", Toast.LENGTH_SHORT).show();
					return;
				}
				if (shares_select.length() == 0) {
					Toast.makeText(EditTransactionActivity.this,
							"Please enter shares", Toast.LENGTH_SHORT).show();
					return;
				}
				if (price_select.length() == 0) {
					Toast.makeText(EditTransactionActivity.this,
							"Please enter prices", Toast.LENGTH_SHORT).show();
					return;
				}
				if (commisson_select.length() == 0) {
					Toast.makeText(EditTransactionActivity.this,
							"Please select commisson", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				double commission = 0.0;
				double price = 0.0;
				int share = 0;

				try {
					commission = Double.parseDouble(commisson_select);
					price = Double.parseDouble(price_select);
					share = Integer.parseInt(shares_select);
				} catch (NumberFormatException e) {
					Toast.makeText(EditTransactionActivity.this,
							"Wrong input!", Toast.LENGTH_SHORT).show();
					return;
				}
				if (bonusStock.isChecked()) {
					transaction.update(Transaction.BUY, date_select, share, 0,
							0);
					finish();
					return;
				}
				Boolean status = transaction.update(type_select, date_select,
						share, price, commission);
				if (!status) {
					if (type_select == Transaction.BUY) {
//						Toast.makeText(EditTransactionActivity.this,
//								"Not enough cash", Toast.LENGTH_SHORT).show();
						Portfolio portfolio = new Portfolio(EditTransactionActivity.this,
								"DEFAULT_PORTFOLIO");
						portfolio.init();
						AlertDialog.Builder simpleDialog = new AlertDialog.Builder(
								EditTransactionActivity.this);
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

					} else {
//						Toast.makeText(EditTransactionActivity.this,
//								"Not enough shares", Toast.LENGTH_SHORT).show();
						AlertDialog.Builder simpleDialog = new AlertDialog.Builder(
								EditTransactionActivity.this);
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
				// TODO Auto-generated method stub
				if (isChecked) {
					old_price = price.getText().toString();
					price.setText("0.00");
					commisson.setText("0.00");
					price.setFocusable(false);
					price.setFocusableInTouchMode(false);
					commisson.setFocusable(false);
					commisson.setFocusableInTouchMode(false);
				} else {
					price.setText(old_price);
					price.setFocusable(true);
					price.setFocusableInTouchMode(true);
					commisson.setFocusable(true);
					commisson.setFocusableInTouchMode(true);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == HOME_BUTTON) {
			finish();
		}
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
