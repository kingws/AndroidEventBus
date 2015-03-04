package com.cardinal.instagrameventbus.view.instagram;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cardinal.instagrameventbus.R;
import com.cardinal.instagrameventbus.adapter.InstagramRecyclerView;
import com.cardinal.instagrameventbus.controller.BusProvider;
import com.cardinal.instagrameventbus.events.GetInstagramResultsEvent;
import com.cardinal.instagrameventbus.events.InstagramResultsLoadedEvent;
import com.cardinal.instagrameventbus.events.InstagramResultsLoadedNextPageEvent;
import com.cardinal.instagrameventbus.listeners.RecyclerItemClickListener;
import com.cardinal.instagrameventbus.model.Instagram;
import com.cardinal.instagrameventbus.utils.AppConstants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         08:00
 */
public class InstagramFragment extends Fragment implements Callback<List<Instagram>>,
                                                           SwipeRefreshLayout.OnRefreshListener {

	private static final String TAG = "InstagramFragment";

	private List<Instagram> mDataList = new ArrayList<Instagram>();

	private OnInstagramFragmentInteractionListener mListener;

    private InstagramResultsLoadedEvent mInstagramData;

    private SwipeRefreshLayout mSwipeLayout;

    private RecyclerView mRecyclerView;

    private InstagramRecyclerView mAdapter;

    private LinearLayoutManager mLayoutManager;

    private AlertDialog mAlertDialog;

    private boolean reloadingData;

    private String mNextMaxID;

    private String mHashtag;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

	public InstagramFragment() { }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mHashtag = b.getString(AppConstants.HASHTAG);
        reloadingData = true;
		BusProvider.getInstance().register(this);
		BusProvider.getInstance().post(new GetInstagramResultsEvent(mHashtag));
		setHasOptionsMenu(true);
	}

    private void createRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                mListener.onInstagramFragmentInteraction(
                                        mDataList.get(position).getImages().getStandardResolution().getUrl(),
                                        mDataList.get(position).getUser().getUsername(),
                                        mDataList.get(position).getLink());
                            }
                }));
        mAdapter = new InstagramRecyclerView(mDataList, R.layout.row_instagram, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateScrollListener() {

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (!reloadingData) {
                    if ( (visibleItemCount+pastVisiblesItems) >= totalItemCount) {
                        BusProvider.getInstance().post(new GetInstagramResultsEvent(mHashtag, mNextMaxID));
                    }
                }
            }
        });
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_instagram, container, false);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.instagram_swipe_container);
        mSwipeLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        createRecyclerView();
        updateScrollListener();

		return view;
	}

    @Subscribe
	public void instagramResultsLoadedEvent(InstagramResultsLoadedEvent event) {
        //Make sure that we succeeded and that this is not a next page event.
		if (event.success && !(event instanceof InstagramResultsLoadedNextPageEvent)) {
			mInstagramData = event;
            mDataList.clear();
			mDataList.addAll(mInstagramData.instagramResults.getData());
            mNextMaxID = mInstagramData.instagramResults.getPagination().getNextMaxTagId();
            createRecyclerView();
            mSwipeLayout.setRefreshing(false);
		}
        reloadingData = false;
	}

    @Subscribe
    public void instagramResultsLoadedNextPageEvent(InstagramResultsLoadedNextPageEvent event) {
        if (event.success) {
            mInstagramData = event;
            //Append the data from the next page to the list.
            mDataList.addAll(mInstagramData.instagramResults.getData());
            mNextMaxID = mInstagramData.instagramResults.getPagination().getNextMaxTagId();
            mSwipeLayout.setRefreshing(false);
        }
    }

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnInstagramFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		BusProvider.getInstance().unregister(this);
	}

    @Override
    public void onRefresh() {
        reloadingData = true;
        BusProvider.getInstance().post(new GetInstagramResultsEvent(mHashtag));
    }

    @Override
    public void success(List<Instagram> instagrams, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(getActivity().getApplicationContext(),
                "Error getting instagram results: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public interface OnInstagramFragmentInteractionListener {
		public void onInstagramFragmentInteraction(String imageUrl, String userName, String linkURL);
	}
}
