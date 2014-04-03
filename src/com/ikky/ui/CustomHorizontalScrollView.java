package com.ikky.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CustomHorizontalScrollView extends HorizontalScrollView {

	public CustomHorizontalScrollView(Context context) {
		super(context);
	}
	
	public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	int targetX = 0;
	
	@Override
	protected void onLayout (boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.scrollTo(targetX, 0);
        targetX = 0;
    }
	
	public void scrollToX(int x) {
		this.targetX = x;
	}

	
	
}
