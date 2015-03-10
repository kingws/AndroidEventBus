package com.cardinal.instagrameventbus.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cardinal.instagrameventbus.utils.Logger;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         4 March 2015
 *         17:13
 */
public abstract class CustomRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = CustomRecyclerOnScrollListener.class.getSimpleName();

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public CustomRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        displayToolbar(recyclerView, dy);

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

    private void displayToolbar(RecyclerView recyclerView, int dy) {

        Logger.d(TAG, "Updating toolbar display...");

        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        //show views if first item is first visible position and views are hidden
        if (firstVisibleItem == 0) {
            if(!controlsVisible) {
                onShow();
                controlsVisible = true;
            }
        } else {
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
        }

        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
            scrolledDistance += dy;
        }
    }

    public abstract void onLoadMore(int current_page);
    public abstract void onHide();
    public abstract void onShow();

}
