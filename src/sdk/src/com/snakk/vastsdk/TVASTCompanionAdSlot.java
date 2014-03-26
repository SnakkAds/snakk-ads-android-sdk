package com.snakk.vastsdk;

import android.view.ViewGroup;

public class TVASTCompanionAdSlot {

    private ViewGroup mContainer;
    private int mWidth;
    private int mHeight;

    public ViewGroup getContainer() {
        return mContainer;
    }

    public void setContainer(ViewGroup container) {
        mContainer = container;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public TVASTCompanionAdSlot() {
        mWidth = 0;
        mHeight = 0;
        mContainer = null;
    }
}
