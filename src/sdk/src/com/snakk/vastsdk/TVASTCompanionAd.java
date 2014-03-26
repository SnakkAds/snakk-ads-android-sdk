package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class TVASTCompanionAd implements Parcelable {

    private String mCompId;
    private int mWidth;
    private int mHeight;
    private int mExpandedWidth;
    private int mExpandedHeight;
    private int mAssetWidth;
    private int mAssetHeight;
    private String mAPIFramework;
    private String mAdSlotId;
    private String mURIStaticResource;
    private String mTypeStaticResource;
    private String mURIIFrameResource;
    private String mDataHTMLResource;
    private String mAdParams;
    private boolean mAdParamsEncoded;
    private String mAltText;
    private HashMap<String, String> mTrackingEvents;
    private String mClickThrough;
    private String mClickTracking;
    private String mClickTrackingId;
    private boolean mRequired;

    public String getCompId() {
        return mCompId;
    }

    protected void setCompId(String compId) {
        mCompId = compId;
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

    public int getAssetWidth() {
        return mAssetWidth;
    }

    protected void setAssetWidth(int width) {
        mAssetWidth = width;
    }

    public int getAssetHeight() {
        return mAssetHeight;
    }

    protected void setAssetHeight(int height) {
        mAssetHeight = height;
    }

    public String getAPIFramework() {
        return mAPIFramework;
    }

    protected void setAPIFramework(String apiFramework) {
        mAPIFramework = apiFramework;
    }

    public String getAdSlotId() {
        return mAdSlotId;
    }

    protected void setAdSlotId(String adSlotId) {
        mAdSlotId = adSlotId;
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

    public String getAltText() {
        return mAltText;
    }

    protected void setAltText(String altText) {
        mAltText = altText;
    }

    public HashMap<String, String> getTrackingEvents() {
        return mTrackingEvents;
    }

    protected void setTrackingEvents(HashMap<String, String> trackingEvents) {
        mTrackingEvents = trackingEvents;
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

    public boolean getIsRequired() {
        return mRequired;
    }

    protected void setIsRequired(boolean isRequired) {
        mRequired = isRequired;
    }

    public TVASTCompanionAd() {
        mCompId = null;
        mAPIFramework = null;
        mAdSlotId = null;
        mURIStaticResource = null;
        mTypeStaticResource = null;
        mURIIFrameResource = null;
        mDataHTMLResource = null;
        mAdParams = null;
        mAltText = null;
        mTrackingEvents = null;
        mClickThrough = null;
        mClickTracking = null;
        mClickTrackingId = null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mCompId == null) ? 0 : mCompId.hashCode());
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
        TVASTCompanionAd other = (TVASTCompanionAd) obj;
        if (mCompId == null) {
            if (other.mCompId != null)
                return false;
        } else if (!mCompId.equals(other.mCompId))
            return false;
        return true;
    }

    public static final Creator<TVASTCompanionAd> CREATOR = new Creator<TVASTCompanionAd>() {

        @Override
        public TVASTCompanionAd[] newArray(int size) {
            return new TVASTCompanionAd[size];
        }

        @Override
        public TVASTCompanionAd createFromParcel(Parcel source) {
            TVASTCompanionAd companion = new TVASTCompanionAd();
            companion.mCompId = source.readString();
            companion.mWidth = source.readInt();
            companion.mHeight = source.readInt();
            companion.mExpandedWidth = source.readInt();
            companion.mExpandedHeight = source.readInt();
            companion.mAssetWidth = source.readInt();
            companion.mAssetHeight = source.readInt();
            companion.mAPIFramework = source.readString();
            companion.mAdSlotId = source.readString();
            companion.mURIStaticResource = source.readString();
            companion.mTypeStaticResource = source.readString();
            companion.mURIIFrameResource = source.readString();
            companion.mDataHTMLResource = source.readString();
            companion.mAdParams = source.readString();
            companion.mAdParamsEncoded = source.readInt() == 1;
            companion.mAltText = source.readString();
            companion.mTrackingEvents = new HashMap<String, String>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                String value = source.readString();
                companion.mTrackingEvents.put(key, value);
            }
            companion.mClickThrough = source.readString();
            companion.mClickTracking = source.readString();
            companion.mClickTrackingId = source.readString();
            companion.mRequired = source.readInt() == 1;
            return companion;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCompId);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeInt(mExpandedWidth);
        dest.writeInt(mExpandedHeight);
        dest.writeInt(mAssetWidth);
        dest.writeInt(mAssetHeight);
        dest.writeString(mAPIFramework);
        dest.writeString(mAdSlotId);
        dest.writeString(mURIStaticResource);
        dest.writeString(mTypeStaticResource);
        dest.writeString(mURIIFrameResource);
        dest.writeString(mDataHTMLResource);
        dest.writeString(mAdParams);
        dest.writeInt(mAdParamsEncoded ? 1 : 0);
        dest.writeString(mAltText);
        dest.writeInt(mTrackingEvents.size());
        for (String key : mTrackingEvents.keySet()) {
            dest.writeString(key);
            dest.writeString(mTrackingEvents.get(key));
        }
        dest.writeString(mClickThrough);
        dest.writeString(mClickTracking);
        dest.writeString(mClickTrackingId);
        dest.writeInt(mRequired ? 1 : 0);
    }
}
