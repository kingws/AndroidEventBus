package com.cardinal.instagrameventbus.controller;

import com.squareup.otto.Bus;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         08:05
 */
public class BusProvider {

	private static Bus mInstance;

	public static Bus getInstance() {

		if (mInstance == null) {
			mInstance = new Bus();
		}
		return mInstance;
	}

}
