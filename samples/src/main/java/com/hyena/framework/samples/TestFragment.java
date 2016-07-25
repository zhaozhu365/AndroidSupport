package com.hyena.framework.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.framework.app.widget.CircleHintView;

/**
 * Created by yangzc on 16/7/25.
 */
public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_test, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CircleHintView hintView = (CircleHintView) view.findViewById(R.id.hintview);
        hintView.setText("1266666666666");
    }
}
