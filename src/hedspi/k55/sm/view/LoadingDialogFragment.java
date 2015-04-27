package hedspi.k55.sm.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class LoadingDialogFragment extends SherlockDialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog.Builder builder = new AlertDialog.Builder(
				getSherlockActivity());
		builder.setMessage("Please wait...");

		return null;
	}
}
