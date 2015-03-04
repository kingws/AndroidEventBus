package com.cardinal.instagrameventbus.controller;

import com.cardinal.instagrameventbus.events.InstagramResultsLoadedEvent;
import com.cardinal.instagrameventbus.events.InstagramResultsLoadedNextPageEvent;
import com.cardinal.instagrameventbus.utils.Logger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         14:10
 */
public class InstagramClient {

	private static final String TAG = "InstagramClient";

	private static InstagramApi service = InstagramRestAdapter.getInstance().create(InstagramApi.class);

	public static void getInstagramDataApi(String hashtag) {
		service.getInstagramImages(hashtag, new Callback<GetInstagramResult>() {
			@Override
			public void success(GetInstagramResult getInstagramResult, Response response) {
				Logger.d(TAG, "Success grabbing Instagram data!!");
				BusProvider.getInstance().post(new InstagramResultsLoadedEvent(true, getInstagramResult));
			}

			@Override
			public void failure(RetrofitError error) {
				Logger.e(TAG, "Error trying to get Instagram Images!!", error);
				BusProvider.getInstance().post(new InstagramResultsLoadedEvent(false, null));
			}
		});
	}

    public static void getInstagramDataApi(String hashtag, String maxID) {
        service.getInstagramImages(hashtag, maxID, new Callback<GetInstagramResult>() {

            public void success(GetInstagramResult getInstagramResult, Response response) {
                Logger.d(TAG, "Success grabbing Instagram data for the next page!!");
                BusProvider.getInstance().post(new InstagramResultsLoadedNextPageEvent(true, getInstagramResult));
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "Error trying to get Instagram Images for the next page!!", error);
                BusProvider.getInstance().post(new InstagramResultsLoadedNextPageEvent(false, null));
            }
        });
    }

}
