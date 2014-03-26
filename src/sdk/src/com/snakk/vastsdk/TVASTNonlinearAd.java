package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class TVASTNonlinearAd implements Parcelable {

    private String mAdId;
    private int mWidth;
    private int mHeight;
    private int mExpandedWidth;
    private int mExpandedHeight;
    private boolean mScalable;
    private boolean mKeepAspectRatio;
    private String mMinDuration;
    private String mAPIFramework;
    private String mURIStaticResource;
    private String mTypeStaticResource;
    private String mURIIFrameResource;
    private String mDataHTMLResource;
    private String mAdParams;
    private boolean mAdParamsEncoded;
    private String mClickThrough;
    private String mClickTracking;
    private String mClickTrackingId;
    private HashMap<String, String> mTrackingEvents;

    public String getAdId() {
        return mAdId;
    }

    protected void setAdId(String adId) {
        mAdId = adId;
    }

    public int getWidth() {
        return mWidth;
    }

    protected void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    protected void setHeight(int height) {
        mHeight = height;
    }

    public int getExpandedWidth() {
        return mExpandedWidth;
    }

    protected void setExpandedWidth(int width) {
        mExpandedWidth = width;
    }

    public int getExpandedHeight() {
        return mExpandedHeight;
    }

    protected void setExpandedHeight(int height) {
        mExpandedHeight = height;
    }

    public boolean getScalable() {
        return mScalable;
    }

    protected void setScalable(boolean scalable) {
        mScalable = scalable;
    }

    public boolean getKeepAspectRatio() {
        return mKeepAspectRatio;
    }

    protected void setKeepAspectRatio(boolean keepAspectRatio) {
        mKeepAspectRatio = keepAspectRatio;
    }

    public String getMinDuration() {
        return mMinDuration;
    }

    protected void setMinDuration(String minDuration) {
        mMinDuration = minDuration;
    }

    public String getAPIFramework() {
        return mAPIFramework;
    }

    protected void setAPIFramework(String apiFramework) {
        mAPIFramework = apiFramework;
    }

    public String getURIStaticResource() {
        return mURIStaticResource;
    }

    protected void setURIStaticResource(String uriStaticResource) {
        mURIStaticResource = uriStaticResource;
    }

    public String getTypeStaticResource() {
        return mTypeStaticResource;
    }

    protected void setTypeStaticResource(String typeStaticResource) {
        mTypeStaticResource = typeStaticResource;
    }

    public String getURIIFrameResource() {
        return mURIIFrameResource;
    }

    protected void setURIIFrameResource(String uriIFrameResource) {
        mURIIFrameResource = uriIFrameResource;
    }

    public String getDataHTMLResource() {
        return mDataHTMLResource;
    }

    protected void setDataHTMLResource(String dataHTMLResource) {
        mDataHTMLResource = dataHTMLResource;
    }

    public String getAdParams() {
        return mAdParams;
    }

    protected void setAdParams(String adParams) {
        mAdParams = adParams;
    }

    public boolean getAdParamsEncoded() {
        return mAdParamsEncoded;
    }

    protected void setAdParamsEncoded(boolean adParamsEncoded) {
        mAdParamsEncoded = adParamsEncoded;
    }

    public String getClickThrough() {
        return mClickThrough;
    }

    protected void setClickThrough(String clickThrough) {
        mClickThrough = clickThrough;
    }

    public String getClickTracking() {
        return mClickTracking;
    }

    protected void setClickTracking(String clickTracking) {
        mClickTracking = clickTracking;
    }

    public String getClickTrackingId() {
        return mClickTrackingId;
    }

    protected void setClickTrackingId(String clickTrackingId) {
        mClickTrackingId = clickTrackingId;
    }

    public HashMap<String, String> getTrackingEvents() {
        return mTrackingEvents;
    }

    protected void setTrackingEvents(HashMap<String, String> trackingEvents) {
        mTrackingEvents = trackingEvents;
    }

    public TVASTNonlinearAd() {
        mAdId = null;
        mMinDuration = null;
        mAPIFramework = null;
        mURIStaticResource = null;
        mTypeStaticResource = null;
        mURIIFrameResource = null;
        mDataHTMLResource = null;
        mAdParams = null;
        mClickThrough = null;
        mClickTracking = null;
        mClickTrackingId = null;
        mTrackingEvents = null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mAdId == null) ? 0 : mAdId.hashCode());
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
        TVASTNonlinearAd other = (TVASTNonlinearAd) obj;
        if (mAdId == null) {
            if (other.mAdId != null)
                return false;
        } else if (!mAdId.equals(other.mAdId))
            return false;
        return true;
    }

    public static final Creator<TVASTNonlinearAd> CREATOR = new Creator<TVASTNonlinearAd>() {

        @Override
        public TVASTNonlinearAd[] newArray(int size) {
            return new TVASTNonlinearAd[size];
        }

        @Override
        public TVASTNonlinearAd createFromParcel(Parcel source) {
            TVASTNonlinearAd nonlinearAd = new TVASTNonlinearAd();
            nonlinearAd.mAdId = source.readString();
            nonlinearAd.mWidth = source.readInt();
            nonlinearAd.mHeight = source.readInt();
            nonlinearAd.mExpandedWidth = source.readInt();
            nonlinearAd.mExpandedHeight = source.readInt();
            nonlinearAd.mScalable = source.readInt() == 1;
            nonlinearAd.mKeepAspectRatio = source.readInt() == 1;
            nonlinearAd.mMinDuration = source.readString();
            nonlinearAd.mAPIFramework = source.readString();
            nonlinearAd.mURIStaticResource = source.readString();
            nonlinearAd.mTypeStaticResource = source.readString();
            nonlinearAd.mURIIFrameResource = source.readString();
            nonlinearAd.mDataHTMLResource = source.readString();
            nonlinearAd.mAdParams = source.readString();
            nonlinearAd.mAdParamsEncoded = source.readInt() == 1;
            nonlinearAd.mClickThrough = source.readString();
            nonlinearAd.mClickTracking = source.readString();
            nonlinearAd.mClickTrackingId = source.readString();
            nonlinearAd.mTrackingEvents = new HashMap<String, String>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                String value = source.readString();
                nonlinearAd.mTrackingEvents.put(key, value);
            }
            return nonlinearAd;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAdId);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeInt(mExpandedWidth);
        dest.writeInt(mExpandedHeight);
        dest.writeInt(mScalable ? 1 : 0);
        dest.writeInt(mKeepAspectRatio ? 1 : 0);
        dest.writeString(mMinDuration);
        dest.writeString(mAPIFramework);
        dest.writeString(mURIStaticResource);
        dest.writeString(mTypeStaticResource);
        dest.writeString(mURIIFrameResource);
        dest.writeString(mDataHTMLResource);
        dest.writeString(mAdParams);
        dest.writeInt(mAdParamsEncoded ? 1 : 0);
        dest.writeString(mClickThrough);
        dest.writeString(mClickTracking);
        dest.writeString(mClickTrackingId);
        dest.writeInt(mTrackingEvents.size());
        for (String key : mTrackingEvents.keySet()) {
            dest.writeString(key);
            dest.writeString(mTrackingEvents.get(key));
        }
    }
}
