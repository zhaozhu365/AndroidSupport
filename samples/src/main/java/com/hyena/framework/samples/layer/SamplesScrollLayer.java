package com.hyena.framework.samples.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.hyena.framework.animation.CScrollLayer;
import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.nodes.CTextNode;
import com.hyena.framework.animation.sprite.CSprite;
import com.hyena.framework.animation.texture.CTexture;
import com.hyena.framework.samples.R;

/**
 * Created by yangzc on 16/4/19.
 */
public class SamplesScrollLayer extends CScrollLayer {

    public static SamplesScrollLayer create(Director director) {
        return new SamplesScrollLayer(director);
    }

    protected SamplesScrollLayer(Director director) {
        super(director);
        initLayer();
    }

    int height;

    private void initLayer() {
        Bitmap bitmap = BitmapFactory.decodeResource(getDirector().getContext().getResources(), R.drawable.pic);
        int y = 0;
        for (int i = 0; i < 100; i++) {
            CTexture texture = CTexture.create(getDirector(), bitmap);
            texture.setScale(0.25f, 0.25f);
            CSprite sprite = CSprite.create(getDirector(), texture);
            sprite.setPosition(new Point(0, y));
            addNode(sprite, 0);

            CTextNode textNode = CTextNode.create(getDirector());
            textNode.setPosition(new Point(0, y + 100));
            textNode.setText("" + (i + 1));
            addNode(textNode, 1);
            y += sprite.getHeight();
            height = y;
        }
    }

    @Override
    public int getHeight() {
        return height;
    }
}
