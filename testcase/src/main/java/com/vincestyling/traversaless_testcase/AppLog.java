package com.vincestyling.traversaless_testcase;

import android.util.Log;

public class AppLog {
	private static final String TAG = "TraversalessViewPager";

	public static void e(String text) {
		Log.e(TAG, text);
	}

	public static void e(String format, Object... args) {
		Log.e(TAG, String.format(format, args));
	}
}
