package com.cardinal.instagrameventbus.controller;

import com.cardinal.instagrameventbus.utils.AppConstants;

import retrofit.RestAdapter;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         12:18
 */
public class InstagramRestAdapter {

	private static RestAdapter mInstance;

	public static RestAdapter getInstance() {
		if (mInstance == null) {

			mInstance = new RestAdapter.Builder().setEndpoint(AppConstants.INSTAGRAM_API_URL)
		                                         .build();
		}
		return mInstance;
	}
}
