package com.hyena.framework.samples.scene;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.animation.sprite.CSprite;
import com.hyena.framework.animation.texture.CTexture;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.samples.layer.MapScene;

/**
 * Created by yangzc on 16/4/19.
 */
public class SampleScene extends MapScene {

    private static final String TAG = "SampleScene";
    private GameFragment<? extends BaseUIFragmentHelper> mGameFragment;

    public SampleScene(GameFragment<? extends BaseUIFragmentHelper> gameFragment
            , Director director) {
        super(director);
        this.mGameFragment = gameFragment;
    }

    @Override
    public void load(String xml) {
        super.load(xml);
        setOnNodeClickListener(mNodeClickListener);
    }

    public BaseUIFragment getBaseUIFragment() {
        return mGameFragment;
    }

    private OnNodeClickListener mNodeClickListener = new OnNodeClickListener() {

        @Override
        public void onClick(CNode node) {
            if (node instanceof CTexture || node instanceof CSprite) {
                LogUtil.v(TAG, "onNodeClick: " + node.getTag());
            }
        }
    };
}
