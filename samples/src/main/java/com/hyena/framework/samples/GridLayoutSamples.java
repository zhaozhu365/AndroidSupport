package com.hyena.framework.samples;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by yangzc on 16/5/3.
 */
public class GridLayoutSamples extends BaseUIFragment {

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return new CustomGridLayout(getActivity());
    }

    public class CustomGridLayout extends GridLayout {

        public CustomGridLayout(Context context) {
            super(context);
            setRowCount(2);
            setColumnCount(2);

            Spec rowSpec = GridLayout.spec(0);
            Spec columnSpec = GridLayout.spec(0, 2, CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            TextView textView = new TextView(getContext());
            textView.setText("Demo");
            textView.setBackgroundColor(Color.BLUE);
            addView(textView, params);

            rowSpec = GridLayout.spec(1);
            columnSpec = GridLayout.spec(0);
            params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            textView = new TextView(getContext());
            textView.setText("Demo1");
            textView.setBackgroundColor(Color.BLUE);
            addView(textView, params);

            rowSpec = GridLayout.spec(1);
            columnSpec = GridLayout.spec(1);
            params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            textView = new TextView(getContext());
            textView.setText("Demo2");
            textView.setBackgroundColor(Color.BLUE);
            addView(textView, params);
        }
    }

    @Override
    public void onError(Throwable e) {
//        super.onError(e);
        e.printStackTrace();
    }
}
