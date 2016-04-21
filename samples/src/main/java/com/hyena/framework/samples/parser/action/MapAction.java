package com.hyena.framework.samples.parser.action;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapAction {

    private int mRepeate;
    private int mDuration;

    public MapAction(int duration, int repeate) {
        this.mDuration = duration;
        this.mRepeate = repeate;
    }

    public int getDuration() {
        return mDuration;
    }

}
