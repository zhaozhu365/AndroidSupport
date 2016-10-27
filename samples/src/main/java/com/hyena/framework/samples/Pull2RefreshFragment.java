package com.hyena.framework.samples;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyena.framework.annotation.AttachViewId;
import com.hyena.framework.app.adapter.SingleRecycleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.SimpleRecycleView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.samples.widgets.pull.Pull2Refresh;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yangzc on 16/10/11.
 */
public class Pull2RefreshFragment extends BaseUIFragment {

    @AttachViewId(R.id.pr_pull2refresh)
    private Pull2Refresh mPull2Refresh;
    @AttachViewId(R.id.srv_list)
    private SimpleRecycleView mRecycleView;
    private ListAdapter mListAdapter;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("下拉刷新测试");
        View view = View.inflate(getActivity(), R.layout.layout_pull2refresh, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mRecycleView.addHeader(View.inflate(getActivity(), R.layout.layout_header, null));
        mRecycleView.addFooter(View.inflate(getActivity(), R.layout.layout_header, null));

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecycleView.setAdapter(mListAdapter = new ListAdapter(getActivity()));
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            items.add("init data " + i);
        }
        mListAdapter.setItems(items);
        
        mPull2Refresh.setRefreshListener(new Pull2Refresh.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadDefaultData(PAGE_FIRST);
            }

            @Override
            public void onLoadMore() {
                loadDefaultData(PAGE_MORE);
            }
        });
    }

    @Override
    protected void onContentVisibleSizeChange(int height, int rawHeight) {
        super.onContentVisibleSizeChange(height, rawHeight);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BaseObject result = new BaseObject();
        result.setErrorCode(BaseObject.OK);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mPull2Refresh.setRefreshing(false);
        mPull2Refresh.setLoadingMore(false);
        switch (pageNo) {
            case PAGE_FIRST: {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mListAdapter.getItems().add(0, "refresh data at " + sdf.format(new Date()));
                mListAdapter.notifyDataSetChanged();
                break;
            }
            case PAGE_MORE: {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mListAdapter.addItem("loadMore data at " + sdf.format(new Date()));
                mRecycleView.scrollToPosition(mListAdapter.getItemCount() -1);
                break;
            }
        }
    }

    class ListAdapter extends SingleRecycleViewAdapter<String> {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(HashViewHolder hashViewHolder, final int position) {
            final String title = getItem(position);
            ((TextView) hashViewHolder.itemView).setText(title);
            ((TextView) hashViewHolder.itemView).setHeight(100);
            hashViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtils.showShortToast(getActivity(), "click: " + title);
                }
            });
        }

        @Override
        public HashViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            TextView textView = new TextView(getActivity());
            textView.setTextColor(Color.BLACK);
            HashViewHolder holder = new HashViewHolder(textView);
            return holder;
        }
    }

}
