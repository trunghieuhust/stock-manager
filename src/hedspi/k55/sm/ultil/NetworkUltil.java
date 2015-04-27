package hedspi.k55.sm.ultil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUltil {
	public NetworkUltil() {
	}

	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobi = cm
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi.isConnected() || mobi.isConnected())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
