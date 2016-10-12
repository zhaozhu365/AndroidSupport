package com.hyena.framework.samples.widgets.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.samples.R;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/10/12.
 */
public class Pull2RefreshFooterPanel extends IPullRefresh {

    public Pull2RefreshFooterPanel(Context context) {
        super(context);
        init();
    }

    public Pull2RefreshFooterPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Pull2RefreshFooterPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.layout_pull2refresh_header, this);
    }

    @Override
    public void setStatus(int status) {
        LogUtil.v("yangzc", "status: " + status);
    }

    @Override
    public int getContentHeight() {
        return UIUtils.dip2px(50);
    }
}
