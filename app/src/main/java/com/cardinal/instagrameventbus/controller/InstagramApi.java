package com.cardinal.instagrameventbus.controller;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         10:11
 */
public interface InstagramApi {

	@GET("/tags/{hashtag}/media/recent/?client_id=8a62cfd0ccc14a4b8a2b40da3467dc8e")
	void getInstagramImages(@Path("hashtag") String hashtag, Callback<GetInstagramResult> callback);

    @GET("/tags/{hashtag}/media/recent/?client_id=8a62cfd0ccc14a4b8a2b40da3467dc8e")
    void getInstagramImages(@Path("hashtag") String hashtag, @Query("max_tag_id") String maxId, Callback<GetInstagramResult> callback);
}
