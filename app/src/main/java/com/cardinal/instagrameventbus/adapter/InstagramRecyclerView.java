package com.cardinal.instagrameventbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardinal.instagrameventbus.R;
import com.cardinal.instagrameventbus.model.Instagram;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * InsatgramRecyclerView
 *
 * @author Shane King
 *         4 Mar 2015
 *         07:20
 */
public class InstagramRecyclerView extends RecyclerView.Adapter<InstagramRecyclerView.ViewHolder> {

	private List<Instagram> instagramItems;
	private int rowLayout;
	private Context mContext;

	public InstagramRecyclerView(List<Instagram> instagramItemsIn, int rowLayoutIn, Context contextIn) {
		this.instagramItems = instagramItemsIn;
		this.rowLayout = rowLayoutIn;
		this.mContext = contextIn;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		Instagram instagram = instagramItems.get(i);
		viewHolder.instagramAuthor.setText("By @" + instagram.getUser().getUsername());
        viewHolder.instagramUrl.setText(instagram.getLink());
		Picasso.with(mContext)
				.load(instagram.getImages().getStandardResolution().getUrl())
				.placeholder(R.color.black_opaque)
				.into(viewHolder.instagramImageView);
	}

	@Override
	public int getItemCount() {
		return instagramItems == null ? 0: instagramItems.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView instagramImageView;
        public TextView instagramAuthor;
        public TextView instagramUrl;

		public ViewHolder(View itemView) {
			super(itemView);
            instagramImageView = (ImageView) itemView.findViewById(R.id.instagram_thumbnail);
			instagramAuthor = (TextView) itemView.findViewById(R.id.instagram_author);
            instagramUrl = (TextView) itemView.findViewById(R.id.instagram_url);
		}
	}

}
