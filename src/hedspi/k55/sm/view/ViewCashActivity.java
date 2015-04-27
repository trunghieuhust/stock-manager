package hedspi.k55.sm.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.Transaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class ViewCashActivity extends SherlockActivity implements Iview {
	private EditText amount;
	private EditText date;
	private Button add;
	private Button cancel;
	private RadioButton deposite;
	private RadioButton withdraw;
	static final int DEPOSITE = -1;
	static final int WITHDRAW = -2;
	NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cash_activity);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setTitle("Deposite/Withdraw");
		amount = (EditText) findViewById(R.id.cash_amount_box);
		date = (EditText) findViewById(R.id.cash_datepicker);
		add = (Button) findViewById(R.id.cash_save);
		add.setEnabled(false);
		cancel = (Button) findViewById(R.id.cash_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		date.setFocusable(false);
		date.setFocusableInTouchMode(false);
		amount.setFocusable(false);
		amount.setFocusableInTouchMode(false);
		deposite = (RadioButton) findViewById(R.id.cash_type_deposite);
		withdraw = (RadioButton) findViewById(R.id.cash_type_withdraw);

		int id = getIntent().getIntExtra("ID", 0);
		if (id == 0) {
			Log.i("EDIT TRANSACTION", "transaction id is invalid.");
			finish();
		}
		Transaction transaction = new Transaction(ViewCashActivity.this);
		transaction.init(id);
		if (transaction.getType() == Transaction.DEPOSITE) {
			deposite.setChecked(true);
			deposite.setEnabled(true);
			withdraw.setChecked(false);
			withdraw.setEnabled(false);
		} else {
			deposite.setEnabled(false);
			deposite.setChecked(false);
			withdraw.setChecked(true);
			withdraw.setEnabled(true);
		}
		amount.setFocusable(false);
		amount.setFocusable(false);
		date.setFocusable(false);
		date.setFocusable(false);

		amount.setText(numberFormat.format(transaction.getPrice()));
		date.setText(transaction.getTime());

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}
}
