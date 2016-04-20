package com.hyena.framework.samples.scene;

import com.hyena.framework.animation.Director;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.app.fragment.scene.GameScene;
import com.hyena.framework.samples.layer.SamplesScrollLayer;

/**
 * Created by yangzc on 16/4/19.
 */
public class SampleScene extends GameScene {

    public SampleScene(GameFragment<? extends BaseUIFragmentHelper> gameFragment
            , Director director) {
        super(gameFragment, director);
        initScene();
    }

    private void initScene() {
        SamplesScrollLayer layer = SamplesScrollLayer.create(getDirector());
        addNode(layer, 0);
    }

}
