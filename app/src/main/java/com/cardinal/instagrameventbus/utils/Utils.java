package com.cardinal.instagrameventbus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         12:13
 */
public class Utils {

	private static final String TAG = "UtilsClass";

	public static boolean isIntentAvailable(Context context, Intent intent) {

        if (intent == null) {
            return false;
        }

		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

}
