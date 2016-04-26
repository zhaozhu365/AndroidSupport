package com.hyena.framework.samples.parser.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapNodeSprite extends MapNode {

    public String mSrc;
    public List<MapNodeBlock> mBlocks;
    public List<MapNodeText> mTexts;

    public MapNodeSprite(String id, int width, int height) {
        super(id, width, height);
    }

    public void addMapBlock(MapNodeBlock block) {
        if (mBlocks == null)
            mBlocks = new ArrayList<MapNodeBlock>();
        mBlocks.add(block);
    }

    public List<MapNodeBlock> getBlocks() {
        return mBlocks;
    }

    public void addMapText(MapNodeText text) {
        if (mTexts == null)
            mTexts = new ArrayList<MapNodeText>();
        mTexts.add(text);
    }

    public List<MapNodeText> getTexts() {
        return mTexts;
    }
}
