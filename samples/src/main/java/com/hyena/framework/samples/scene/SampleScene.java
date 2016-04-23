package com.hyena.framework.samples.scene;

import com.hyena.framework.animation.Director;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.samples.layer.MapScene;

/**
 * Created by yangzc on 16/4/19.
 */
public class SampleScene extends MapScene {

    private GameFragment<? extends BaseUIFragmentHelper> mGameFragment;

    public SampleScene(GameFragment<? extends BaseUIFragmentHelper> gameFragment
            ,Director director) {
        super(director);
        this.mGameFragment = gameFragment;
    }

    public BaseUIFragment getBaseUIFragment() {
        return mGameFragment;
    }
}
