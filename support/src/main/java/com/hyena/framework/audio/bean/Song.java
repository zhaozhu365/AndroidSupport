/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio.bean;

import java.io.File;
import java.io.Serializable;

import com.hyena.framework.audio.MusicDir;
import com.hyena.framework.security.MD5Util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 歌曲信息
 *
 * @author yangzc
 */
public class Song implements Serializable {

    // 歌曲远程URL
    public String mUrl;

    public Song() {
    }

    /**
     * 获得本地路径
     *
     * @return
     */
    public File getLocalFile() {
        return new File(MusicDir.getMusicDir(), MD5Util.encode(mUrl)
                + ".mp3");
    }

    /**
     * 是否是在线歌曲
     *
     * @return
     */
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            return ((Song) o).mUrl.equals(mUrl);
        }
        return super.equals(o);
    }
}
