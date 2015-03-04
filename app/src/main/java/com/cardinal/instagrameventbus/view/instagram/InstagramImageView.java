package com.cardinal.instagrameventbus.view.instagram;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         12:00
 */
public class InstagramImageView extends ImageView {

    public InstagramImageView(Context context) {
        super(context);
    }

    public InstagramImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstagramImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Maintain aspect ration based on width.
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

}
