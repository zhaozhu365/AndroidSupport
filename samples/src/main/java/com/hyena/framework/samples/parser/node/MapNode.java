package com.hyena.framework.samples.parser.node;

import com.hyena.framework.samples.parser.action.MapAction;

import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapNode {

    private String mId;
    private int mX, mY;
    private int mWidth, mHeight;
    private String mTag;

    public List<MapAction> mActions;

    public MapNode(String id, int width, int height) {
        this.mId = id;
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public String getId() {
        return mId;
    }


    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }


}
