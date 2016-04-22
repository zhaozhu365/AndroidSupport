package com.hyena.framework.samples.parser.action;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapActionTranslate extends MapAction {
    public int mFromX;
    public int mToX;
    public int mFromY;
    public int mToY;

    public MapActionTranslate(int duration, int repeate) {
        super(duration, repeate);
    }
}
