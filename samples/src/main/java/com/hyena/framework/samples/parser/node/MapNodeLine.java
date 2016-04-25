package com.hyena.framework.samples.parser.node;


/**
 * Created by yangzc on 16/4/21.
 */
public class MapNodeLine extends MapNode {

    public String mStyle;

    public String mFromId;
    public String mToId;
    public String mColor;

    public MapNodeLine(String id, int width, int height) {
        super(id, width, height);
    }

}
