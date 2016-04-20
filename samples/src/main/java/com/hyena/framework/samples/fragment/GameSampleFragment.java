package com.hyena.framework.samples.fragment;

import android.os.Bundle;
import android.view.View;

import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.samples.scene.SampleScene;

/**
 * Created by yangzc on 16/4/19.
 */
public class GameSampleFragment extends GameFragment {

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        showScene(new SampleScene(this, getDirector()));
    }
}
