package com.hyena.framework.samples.parser.action;


import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapFrameAction extends MapAction{
    public List<MapFrame> mMapFrames;

    public MapFrameAction(int duration, int repeate) {
        super(duration, repeate);
    }

    @Override
    public int getDuration() {
        int duration = 0;
        if (mMapFrames != null) {
            for (int i = 0; i < mMapFrames.size(); i++) {
                duration += mMapFrames.get(i).getDuration();
            }
        }
        return duration;
    }
}
