package com.cardinal.instagrameventbus.events;

import com.cardinal.instagrameventbus.controller.InstagramClient;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         14:49
 */
public class GetInstagramResultsEvent {

	public GetInstagramResultsEvent(String hashtag) { InstagramClient.getInstagramDataApi(hashtag); }

    public GetInstagramResultsEvent(String hashtag, String nextMaxID) { InstagramClient.getInstagramDataApi(hashtag, nextMaxID);}
}
