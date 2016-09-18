package com.hyena.framework.samples.widgets.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by yangzc on 16/9/16.
 */

public abstract class IPullRefresh extends RelativeLayout {

    public static final int STATUS_RESET = 1;
    public static final int STATUS_PULL = 2;
    public static final int STATUS_RELEASE = 3;
    public static final int STATUS_REFRESH = 4;
    public static final int STATUS_MANUAL_REFRESH = 5;
    public static final int STATUS_OVERSCROLLING = 6;

    public IPullRefresh(Context context) {
        super(context);
    }

    public IPullRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IPullRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    abstract void setStatus(int status);

    abstract int getContentHeight();
}
