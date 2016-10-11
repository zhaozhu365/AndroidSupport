package com.hyena.framework.samples;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.annotation.AttachViewId;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ToastUtils;

/**
 * Created by yangzc on 16/7/25.
 */
public class TestFragment extends BaseUIFragment {

    @AttachViewId(R.id.test_listview)
    private ListView mListView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("列表测试");
        View view = View.inflate(getActivity(), R.layout.layout_test, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView.setAdapter(new ListAdapter(getActivity()));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastUtils.showShortToast(getActivity(), "position: " + i);
            }
        });
    }

    @Override
    protected void onContentVisibleSizeChange(int height, int rawHeight) {
        super.onContentVisibleSizeChange(height, rawHeight);
    }

    class ListAdapter extends SingleTypeAdapter<String> {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(getActivity());
            textView.setText("position: " + i);
            textView.setTextColor(Color.BLACK);
            return textView;
        }

        @Override
        public int getCount() {
            return 100;
        }
    }
}
