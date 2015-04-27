package hedspi.k55.sm.view;

import hedspi.k55.sm.R;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class SearchDiaglog extends SherlockDialogFragment {
	String Symbol;
	String list[];
	int index;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle receive = getArguments();
		Log.i("RECEIVE", receive.toString());
		list = receive.getStringArray("RESULT");

		AlertDialog.Builder builder = new AlertDialog.Builder(
				getSherlockActivity());

		builder.setTitle(R.string.search_result_title);

		OnClickListener onclicklisten = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("RESULT SYMBOL", list[which]);
				index = which;

			}
		};
		builder.setSingleChoiceItems(list, 0, onclicklisten);
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				});
		builder.setPositiveButton(R.string.add,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer sb = new StringBuffer(list[index]);
						String selected = sb
								.substring(sb.indexOf(":") + 1,
										sb.indexOf("  ")).trim()
								.toUpperCase(Locale.ENGLISH);
						((PortfolioActivity) getActivity())
								.displayStockDetail(selected);
						Log.i("WHICH", selected);
					}
				});
		return builder.create();
	}
}
