package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import hedspi.k55.sm.model.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class LoginActivity extends SherlockActivity implements Iview {
	ActionBar actionbar;
	Button btnLogin;
	User user;
	EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		user = new User(this);
		if (!user.hasPass()) {
			Intent info = new Intent(LoginActivity.this,
					FirstTimeScreenActivity.class);
			startActivity(info);
			finish();
		} else {
			setContentView(R.layout.login_activity);
			password = (EditText) findViewById(R.id.edit_enter_password);
			actionbar = getSupportActionBar();
			btnLogin = (Button) findViewById(R.id.btn_login);

			btnLogin.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (password.getText().length() != 0) {
						String pass = password.getText().toString();
						if (user.checkPass(pass)) {
							Intent overView = new Intent(LoginActivity.this,
									OverviewActivity.class);
							startActivity(overView);
							finish();
						} else
							Toast.makeText(LoginActivity.this,
									"Password incorect", Toast.LENGTH_SHORT)
									.show();
					} else
						Toast.makeText(LoginActivity.this,
								"Password cannot empty", Toast.LENGTH_SHORT)
								.show();

				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.login_activity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_option_about:
			Intent aboutActivity = new Intent(LoginActivity.this,
					AboutActivity.class);
			startActivity(aboutActivity);

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}
}
