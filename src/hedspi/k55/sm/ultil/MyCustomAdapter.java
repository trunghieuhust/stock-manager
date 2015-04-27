package hedspi.k55.sm.ultil;

import hedspi.k55.sm.R;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyCustomAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> data;
	private Activity activity;
	private static LayoutInflater inflater = null;

	public MyCustomAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.portfolio_stock_item, null);
		TextView companyName = (TextView) vi
				.findViewById(R.id.portfolio_item_companny_name);
		TextView stockCode = (TextView) vi
				.findViewById(R.id.portfolio_item_stock_code);
		TextView itemShares = (TextView) vi
				.findViewById(R.id.portfolio_item_shares);
		TextView currentPrice = (TextView) vi
				.findViewById(R.id.portfolio_item_current_price);
		TextView priceChange = (TextView) vi
				.findViewById(R.id.portfolio_item_price_change);

		HashMap<String, String> singleItem = new HashMap<String, String>();
		singleItem = data.get(position);

		companyName.setText(singleItem.get("COMPANY_NAME"));
		stockCode.setText(singleItem.get("SYMBOL"));
		itemShares.setText(singleItem.get("SHARES"));
		currentPrice.setText(singleItem.get("CURRENT_PRICE") + "$");
		// priceChange.setText(singleItem.get("CHANGE_PRICE"));
		if (Double.parseDouble(singleItem.get("CHANGE_PRICE")) < 0) {
			priceChange.setText(singleItem.get("CHANGE_PRICE") + "%");
			priceChange.setTextColor(Color.RED);
		}

		else {
			priceChange.setText("+" + singleItem.get("CHANGE_PRICE") + "%");
			priceChange.setTextColor(Color.GREEN);
		}
		return vi;
	}

}
