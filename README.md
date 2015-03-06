# Event Bus for Android

I developed this app to demo the benefits of using [Retrofit](http://square.github.io/retrofit/) (combined with [Gson](https://code.google.com/p/google-gson/)) and the [Otto](http://square.github.io/otto/) event bus for the upcoming Cincinnati Tech Talk at Cardinal Solutions.  In doing so, I figured I'd share it with everyone.  During my research I found several examples of the best way to pull this off, but not a one stop shop that covered everything.  I found a great article on [A smart way to use Retrofit](http://blog.robinchutaux.com/blog/a-smart-way-to-use-retrofit/), as well as a great article on [Durable Android REST Clients](http://www.mdswanson.com/blog/2014/04/07/durable-android-rest-clients.html).  This app usess Otto as the event bus and Retrofit for all the REST implementation.  The added bonuses are the utilization of the new RecyclerView, SwipeRefreshLayout, and the Endless Scroll Listener.  Lets get started...

## Configuration

Your build.gradle needs the following dependencies for the features outlined below:

* Android backwards compatability
 * compile 'com.android.support:appcompat-v7:21.0.3'
 * compile 'com.google.android.gms:play-services:6.5.87'
 * compile 'com.android.support:support-v4:21.0.3'
* Material Design
 * compile 'com.android.support:cardview-v7:21.0.3'
 * compile 'com.android.support:recyclerview-v7:21.0.3'
* JSON
 * compile 'com.google.code.gson:gson:2.2.3'
* Image injection
 * compile 'com.squareup.picasso:picasso:2.4.0'
* Event Bus
 * compile 'com.squareup:otto:1.3.5'
 * compile 'com.squareup.retrofit:retrofit:1.7.1'

## Otto

Otto is an event bus designed to decouple different parts of your application while still allowing them to communicate efficiently.  Forked from Guava, Otto adds unique functionality to an already refined event bus as well as specializing it to the Android platform.  The bus provider is a pretty straight forward singleton object:

```Java
public class BusProvider {

	private static Bus mInstance;

	public static Bus getInstance() {
		if (mInstance == null) {
			mInstance = new Bus();
		}
		return mInstance;
	}
}
```

In order to receive events, a class instance needs to register with the bus. I do this in the onCreate for the fragment.  I know what you're thinking... Why would you do that in onCreate?  Won't it fire every time the fragement starts?  Yes, yes it will.  For this app that's a good thing, we're looking at a live picture feed.  But, for other apps I've stored a shared preference boolean after the initial fetch runs.  Subsequent requests are only sent when the user does a pull to refresh.  At any rate, here we go:

```Java
BusProvider.getInstance().register(this);
```

Don't forget to unregister during lifecycle shutdown:

```Java
@Override
public void onDetach() {
	super.onDetach();
	BusProvider.getInstance().unregister(this);
}
```

NOTE: Registering will only find methods on the immediate class type.  Otto will not traverse the class hierarchy and add methods from base classes or interfaces that are annotated!!

Once you have your bus set up and registered, you simply publish your events for the subscribers of that type.  For the purposes of this app, we're publishing an event of type GetInstagramResultsEvent() and passing in a custom hashtag string to the API (more on that in the Retrofit section):

```Java
BusProvider.getInstance().post(new GetInstagramResultsEvent(mHashtag));
```

Now that we've published our event, we need to subscribe to that event so we can "do stuff" with the response when the event is finished.  To do that, we simply annotate a method with @Subscribe.  To listen for the GetInstagramResultsEvent, we add the following @Subscribe method:

```Java
@Subscribe
	public void instagramResultsLoadedEvent(InstagramResultsLoadedEvent event) {
       // update UI with event object...
}
```

That's all there is to it!!  Well, sort of...  That's all that's requred to publish events and recieve responses, but there's a little more that goes on behind the scenes.  This is where Retrofit will start to show up.  Lets take a look at the GetInstagramResultsEvent object that we published:

```Java
public class GetInstagramResultsEvent {
	public GetInstagramResultsEvent(String hashtag) { InstagramClient.getInstagramDataApi(hashtag); }

    public GetInstagramResultsEvent(String hashtag, String nextMaxID) { InstagramClient.getInstagramDataApi(hashtag, nextMaxID);}
}
```

It's simple, fairly straight forward, but what is that InstagramClient object??  What does it do?  Where did it come from?  Allow me to explain...

## Retrofit

Retrofit is a type-safe client for Android and Java.  With this library you can request the webservices of a REST api with POST, GET and more. This library is great, but we need a good architecture to do it correctly.  I'll dig deeper into each object, but for starters we'll need to set up the following:

* Rest Adapter
* API Service with Parameters
 * GET, POST, DELETE... for this app I'm only using GET
* POJOs

### Rest Adapter

The rest adapter is a singleton that returns an instance of the Retrofit RestAdapter, using the base URL endpoint that you pass in:

```Java
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
```

There are many other arguments you can set in your rest adapter, which at that point we'd want to call it a Rest Client, but I digress.  For example, if your POJOs will contain a date object, you can create a Gson object with a given date format, add that to your rest client, and the pattern will be used to parse the date field in all your POJOs.  This app doesn't use any dates so I didn't add the Gson object, but if it did our Rest Client would look something like this:

```Java
public class InstagramRestClient {

	private static RestAdapter mInstance;

	public static RestAdapter getInstance() {
		if (mInstance == null) {

			Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

			mInstance = new RestAdapter.Builder().setEndpoint(AppConstants.INSTAGRAM_API_URL)
                                                 .setConverter(new GsonConverter(gson))
		                                         .build();
		}
		return mInstance;
	}
}
```
moving on...

### API Service with Parameters

For this app, I'm only using the POST parameter, but syntactically there's no difference between GET, POST, etc.  We're making two web service calls in this app.  For both calls we're utilizing the @Path annotation to replace the hashtag piece of our URI.  For the second call, we're adding a @Query parameter of "max_tag_id" to our request (there's several more annotations you can add to your request, check out [Retrofit](http://square.github.io/retrofit/) for more details):

```Java
public interface InstagramApi {

	@GET("/tags/{hashtag}/media/recent/?client_id=8a62cfd0ccc14a4b8a2b40da3467dc8e")
	void getInstagramImages(@Path("hashtag") String hashtag, Callback<GetInstagramResult> callback);

    @GET("/tags/{hashtag}/media/recent/?client_id=8a62cfd0ccc14a4b8a2b40da3467dc8e")
    void getInstagramImages(@Path("hashtag") String hashtag, @Query("max_tag_id") String maxId, Callback<GetInstagramResult> callback);

}
```

But wait...there's more!!  What's that Callback of type GetInstagramResult?  I'm glad you asked...

If you don't add the Callback then your request is synchronized and your execution will block until the request is finished. If you want to create an asynchronous request you have to add a Callback to your methods and return void.  More details on the Callback to follow...

### POJOs

In my opinion, this little secret I'm going to tell you about is worth the cost of admission...  If you're using an existing API, in our case Instagram, you can save yourself a TON of time generating your POJOs by taking advantage of one of the (several) FREE JSON to POJO generators.  A couple of good ones are [jsonschema2pojo](http://www.jsonschema2pojo.org) and [JSON To Java](http://jsontojava.appspot.com).  They will generate the POJOs, add all the Gson (or others) annotations, and they'll even make the objects Parcelalbe for Android.  I know... nice right??  For this app I used [jsonschema2pojo](http://www.jsonschema2pojo.org).

Once you've taken advantage of the awesome sauce I mentioned in the previous paragraph, you can drop your newly created (and annotaded...and maybe Parcelable) POJOs into your project.  Now, back to the callback in the previous section.  This is where the proverbial magic happens...  

The GetInstagramResult object is where the serialization occurs.  When the service call is made, the Callback fires and the JSON reponse is deserialized into the GetInstagramResult object, which is returned to the method subscribed to that event.  Remember at the top of the README, when I said "we need to subscribe to that event so we can "do stuff" with the response when the event is finished."?  Well, now it's finished.  We've come full circle and have an object to update our UI with, which brings us to our next and final section that I so eloquently referred to as "Added Bonuses".  Man, I'm giving lots of free stuff out here.

## Added Bonuses
 
 For this app, I'm taking advantage of the RecyclerView, SwipeRefreshLayout, and the Endless Scroll Listener.  I won't go too deep into these objects, as it's not really the topic of this README, but I feel they deserve a little attention.

### RecyclerView

 RecyclerView came out with the Android L preview release and is part of the support library, if you're not using it yet you need to be.  The big take away is it's much (much) smoother and faster than the old ListView.  Using it is pretty straight forward, there's tons of good examples for it at this point.  What I really wanted to point out was the use of endless scrolling in this app, not something that comes out of the box with RecyclerView (or any Android ListView).  

In our fragment, we apply the scroll listener to the recycler view:

```Java
private void updateScrollListener() {

        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                BusProvider.getInstance().post(new GetInstagramResultsEvent(mHashtag, mNextMaxID));
            }
        });
    }
```

And here is our class that extends the RecyclerView.OnScrollListener:

```Java
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);

}
```

Pay close attention to the onScrolled overridden method.  We're calculating total items, visible items, and the first viewable item, and scrolling infinitely (or until we reach the end of our data list).  I know, it's a thing of beauty, and very simple.  You're welcome.

### SwipeRefreshLayout

Finally, a little bit about the SwipeRefreshLayout.  This handy little gem came out in the last support library update.  It's's a standard way to implement the common Pull to Refresh pattern in Android.  Hooking it up is pretty easy, and here's how we do it.

All you have to do is add a scrollable view to the layout.  For this app I'm using the RecyclerView:

```XML
<android.support.v4.widget.SwipeRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/instagram_swipe_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white"
    tools:context="com.cardinalsolutions.macys.mocandroid.view.instagram.InstagramFragment">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:scrollbars="vertical"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</android.support.v4.widget.SwipeRefreshLayout>
```

And in your Fragment (or Activity) get the layout, and assign the listener. The refreshing listener is a post delayed handler:

```Java
	mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.instagram_swipe_container);
 	mSwipeLayout.setOnRefreshListener(this);
```

Lastly we need to implement the SwipeRefreshLayout.OnRefreshListener, and override the onRefresh() method.  In our case, when the user swipes to refresh we make another API call to get the most recent results:

```Java
@Override
public void onRefresh() {
    reloadingData = true;
    BusProvider.getInstance().post(new GetInstagramResultsEvent(mHashtag));
}
```

That's all there is to it!!  Feel free to check out the project and dig in.  Happy Coding!!

## Author / License

Copyright Cardinal Solutions 2013. Licensed under the MIT license.
<img src="https://raw.github.com/CardinalNow/NSURLConnection-Debug/master/logo_footer.png"/>
