/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * RecycleView Wrapper
 *
 * @author yangzc
 */
public class SimpleRecycleView extends RecyclerView {

    public SimpleRecycleView(Context context, AttributeSet attrs, int arg2) {
        super(context, attrs, arg2);
    }

    public SimpleRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleRecycleView(Context context) {
        super(context);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    @Override
    public void setItemAnimator(ItemAnimator animator) {
        super.setItemAnimator(animator);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }

}
