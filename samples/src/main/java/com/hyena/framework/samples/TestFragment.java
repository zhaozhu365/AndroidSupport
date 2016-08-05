package com.hyena.framework.samples;

import android.os.Bundle;
import android.view.View;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.CircleHintView;

/**
 * Created by yangzc on 16/7/25.
 */
public class TestFragment extends UIFragment {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setStatusTintBarEnable(true);
        setStatusTintBarColor(getResources().getColor(R.color.color1));
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.layout_test, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);

        CircleHintView hintView = (CircleHintView) view.findViewById(R.id.hintview);
        hintView.setText("1266666666666");
        hintView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showFragment(BaseUIFragment.newFragment(getActivity(), SubFragment.class, null));
            }
        });
    }
}
