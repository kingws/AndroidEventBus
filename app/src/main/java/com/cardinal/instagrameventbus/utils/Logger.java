package com.cardinal.instagrameventbus.utils;

import android.util.Log;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         11:50
 */
public class Logger {

	private static final boolean LOGGING = true;
	private static final String TAG = "Instagram Client App: ";

	public static void i(String className, String message) {
		if (LOGGING) {
			Log.i(TAG, className + " - " + message);
		}
	}

	public static void d(String className, String message) {
		if (LOGGING) {
			Log.d(TAG, className + " - " + message);
		}
	}

	public static void v(String className, String message) {
		if (LOGGING) {
			Log.v(TAG, className + " - " + message);
		}
	}

	public static void e(String className, String message, Exception e) {
		if (LOGGING) {
			Log.e(TAG, className + " - " + message, e);
		}
	}

}
