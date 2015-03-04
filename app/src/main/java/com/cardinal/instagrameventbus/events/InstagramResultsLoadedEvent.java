package com.cardinal.instagrameventbus.events;

import com.cardinal.instagrameventbus.controller.GetInstagramResult;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         14:09
 */
public class InstagramResultsLoadedEvent {

	public boolean success;

	public GetInstagramResult instagramResults;

	public InstagramResultsLoadedEvent(boolean successIn, GetInstagramResult instagramResultsIn) {
		success = successIn;
		instagramResults = instagramResultsIn;
	}

}
