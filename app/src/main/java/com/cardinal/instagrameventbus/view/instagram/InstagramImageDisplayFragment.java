package com.cardinal.instagrameventbus.view.instagram;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cardinal.instagrameventbus.R;
import com.cardinal.instagrameventbus.common.MainActivity;
import com.cardinal.instagrameventbus.controller.BusProvider;
import com.squareup.picasso.Picasso;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         11:52
 */
public class InstagramImageDisplayFragment extends Fragment {

	private static final String TAG = "InstagramImageDisplayFragment";

	private ImageView mImageView;

	private static String mImageUrl;

	private static String mUserName;

	private static Context mContext;

    private boolean toolbarWasShowing = true;

	public InstagramImageDisplayFragment() {}

	public static InstagramImageDisplayFragment newInstance(Context ctx, String imageUrlIn, String userName, String linkURL) {
		mImageUrl = imageUrlIn;
		mUserName = userName;
		mContext = ctx;

		InstagramImageDisplayFragment fragment = new InstagramImageDisplayFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (!((MainActivity)getActivity()).isToolbarShowing()) {
            ((MainActivity)getActivity()).showViews();
            toolbarWasShowing = false;
        }

        setMenuVisibility(false);
	}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		getActivity().setTitle("By @" + mUserName);
		View view = inflater.inflate(R.layout.fragment_instagram_image, container, false);

		mImageView = (ImageView) view.findViewById(R.id.instagram_image);

		Picasso.with(mContext)
		       .load(mImageUrl)
				.into(mImageView);

		return view;
	}

    @Override
    public void onDetach() {
        super.onDetach();
        if (!toolbarWasShowing) {
            ((MainActivity)getActivity()).hideViews();
        }
    }
}
