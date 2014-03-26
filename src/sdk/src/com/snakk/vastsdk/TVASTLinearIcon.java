package com.snakk.vastsdk;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class TVASTLinearIcon implements Parcelable {

    private String mIconProgram;
    private double mIconDuration;
    private double mIconOffset;
    private Rect mIconRect;
    private String mIconApiFramework;
    private String mIconStaticResource;
    private String mIconCreativeType;
    private String mIconIFrameResource;
    private String mIconHTMLResource;
    private String mIconClickThrough;
    private Map<String, String> mIconClickTrackings;
    private String mIconViewTracking;

    public TVASTLinearIcon() {
        mIconProgram = null;
        mIconApiFramework = null;
        mIconStaticResource = null;
        mIconIFrameResource = null;
        mIconHTMLResource = null;
        mIconClickThrough = null;
        mIconClickTrackings = null;
        mIconViewTracking = null;
        mIconDuration = 0;
        mIconOffset = 0;
        mIconRect = new Rect(0, 0, 0, 0);
    }

    public String getIconProgram() {
        return mIconProgram;
    }

    protected void setIconProgram(String iconProgram) {
        mIconProgram = iconProgram;
    }

    public String getApiFramework() {
        return mIconApiFramework;
    }

    protected void setApiFramework(String iconApiFramework) {
        mIconApiFramework = iconApiFramework;
    }

    public String getStaticResource() {
        return mIconStaticResource;
    }

    protected void setIconStaticResource(String iconStaticResource) {
        mIconStaticResource = iconStaticResource;
    }

    public String getIconCreativeType() {
        return mIconCreativeType;
    }

    protected void setIconCreativeType(String iconCreativeType) {
        mIconCreativeType = iconCreativeType;
    }

    public String getIconIFrameResource() {
        return mIconIFrameResource;
    }

    protected void setIconIFrameResource(String iconIFrameResource) {
        mIconIFrameResource = iconIFrameResource;
    }

    public String getIconHTMLResource() {
        return mIconHTMLResource;
    }

    protected void setIconHTMLResource(String iconHTMLResource) {
        mIconHTMLResource = iconHTMLResource;
    }

    public String getIconClickThrough() {
        return mIconClickThrough;
    }

    protected void setIconClickThrough(String iconClickThrough) {
        mIconClickThrough = iconClickThrough;
    }

    public Map<String, String> getIconClickTrackings() {
        return mIconClickTrackings;
    }

    protected void setIconClickTrackings(Map<String, String> iconClickTrackings) {
        mIconClickTrackings = iconClickTrackings;
    }

    public String getIconViewTracking() {
        return mIconViewTracking;
    }

    protected void setIconViewTracking(String iconViewTracking) {
        mIconViewTracking = iconViewTracking;
    }

    public double getIconDuration() {
        return mIconDuration;
    }

    protected void setIconDuration(double iconDuration) {
        mIconDuration = iconDuration;
    }

    public double getIconOffset() {
        return mIconOffset;
    }

    protected void setIconOffset(double iconOffset) {
        mIconOffset = iconOffset;
    }

    public Rect getIconRect() {
        return mIconRect;
    }

    protected void setIconRect(Rect iconRect) {
        mIconRect = iconRect;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mIconClickThrough == null) ? 0 : mIconClickThrough.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TVASTLinearIcon other = (TVASTLinearIcon) obj;
        if (mIconClickThrough == null) {
            if (other.mIconClickThrough != null)
                return false;
        } else if (!mIconClickThrough.equals(other.mIconClickThrough))
            return false;
        return true;
    }

    public static final Creator<TVASTLinearIcon> CREATOR = new Creator<TVASTLinearIcon>() {

        @Override
        public TVASTLinearIcon[] newArray(int size) {
            return new TVASTLinearIcon[size];
        }

        @Override
        public TVASTLinearIcon createFromParcel(Parcel source) {
            TVASTLinearIcon linearIcon = new TVASTLinearIcon();
            linearIcon.mIconProgram = source.readString();
            linearIcon.mIconDuration = source.readInt();
            linearIcon.mIconOffset = source.readInt();
            int iconX = source.readInt();
            int iconY = source.readInt();
            int iconW = source.readInt();
            int iconH = source.readInt();
            linearIcon.mIconRect = new Rect(iconX, iconY, iconW, iconH);
            linearIcon.mIconApiFramework = source.readString();
            linearIcon.mIconStaticResource = source.readString();
            linearIcon.mIconCreativeType = source.readString();
            linearIcon.mIconIFrameResource = source.readString();
            linearIcon.mIconHTMLResource = source.readString();
            linearIcon.mIconClickThrough = source.readString();
            linearIcon.mIconClickTrackings = new HashMap<String, String>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                String value = source.readString();
                linearIcon.mIconClickTrackings.put(key, value);
            }
            linearIcon.mIconViewTracking = source.readString();
            return linearIcon;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIconProgram);
        dest.writeDouble(mIconDuration);
        dest.writeDouble(mIconOffset);
        dest.writeInt(mIconRect.left);
        dest.writeInt(mIconRect.top);
        dest.writeInt(mIconRect.right);
        dest.writeInt(mIconRect.bottom);
        dest.writeString(mIconApiFramework);
        dest.writeString(mIconStaticResource);
        dest.writeString(mIconCreativeType);
        dest.writeString(mIconIFrameResource);
        dest.writeString(mIconHTMLResource);
        dest.writeString(mIconClickThrough);
        dest.writeInt(mIconClickTrackings.size());
        for (String key : mIconClickTrackings.keySet()) {
            dest.writeString(key);
            dest.writeString(mIconClickTrackings.get(key));
        }
        dest.writeString(mIconViewTracking);
    }
}
