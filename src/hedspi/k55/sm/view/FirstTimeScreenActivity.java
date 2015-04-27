package hedspi.k55.sm.view;

import hedspi.k55.sm.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class FirstTimeScreenActivity extends SherlockActivity {
	Button create;
	Button exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_time_intro);
		create = (Button) findViewById(R.id.first_time_create);
		exit = (Button) findViewById(R.id.first_time_exit);
		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent info = new Intent(FirstTimeScreenActivity.this,
						InfoActivity.class);
				startActivity(info);
				finish();
			}
		});
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
