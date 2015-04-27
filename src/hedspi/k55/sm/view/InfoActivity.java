package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class InfoActivity extends SherlockActivity {

	private EditText name;
	private EditText email;
	private EditText password;
	private Button save;
	private Button cancel;
	User currentUser;
	Portfolio portfolio;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_activity);
		name = (EditText) findViewById(R.id.info_user_name_box);
		email = (EditText) findViewById(R.id.info_email_box);
		password = (EditText) findViewById(R.id.info_password_box);
		save = (Button) findViewById(R.id.info_save);
		cancel = (Button) findViewById(R.id.info_cancel);
		currentUser = new User(InfoActivity.this, " ");
		currentUser.add("default");
		//portfolio = new Portfolio(this, "DEFAULT_PORTFOLIO");
		//portfolio.init();
		
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name_select = name.getText().toString();
				String email_select = email.getText().toString();
				String pass_select = password.getText().toString();

				if (name_select.length() == 0)
					Toast.makeText(InfoActivity.this, "Not input name yet!",
							Toast.LENGTH_SHORT).show();
				else if (email_select.length() == 0)
					Toast.makeText(InfoActivity.this, "Not input email yet!",
							Toast.LENGTH_SHORT).show();
				else if (pass_select.length() == 0)
					Toast.makeText(InfoActivity.this, "Password box is empty!",
							Toast.LENGTH_SHORT).show();
				else {
					//portfolio.setCurrentMoney(10000000);
					//portfolio.setInvestedMoney(10000000);
					//portfolio.update();
					currentUser.setPass(pass_select);
					currentUser.setName(name_select);
					currentUser.setEmail(email_select);
					currentUser.update();
					Intent login = new Intent(InfoActivity.this,
							LoginActivity.class);
					startActivity(login);
					finish();
				}

			}

		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
