package hedspi.k55.sm.view;

import com.actionbarsherlock.R;

public interface Iview {
	public static int THEME = R.style.Theme_Sherlock;
	public static int AUTO_REFRESH_TIME = 10;
	public static String PORTFOLIO_NAME = "DEFAULT_PORTFOLIO";
	public static int MAX_STATISTICS_VIEW = 5;
	public static int REFRESH_SPEED = 120000;
	public static final int HOME_BUTTON = 0x0102002c;// home button id

	public void refresh();
}
