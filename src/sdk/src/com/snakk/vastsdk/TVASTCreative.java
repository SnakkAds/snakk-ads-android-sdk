package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TVASTCreative implements Parcelable {

    private String mCreativeId;
    private int mSequence;
    private String mAdId;
    private String mAPIFramework;
    private TVASTLinearAd mLinearAd;
    private List<TVASTNonlinearAd> mNonlinearAds;
    private List<TVASTCompanionAd> mCompanionAds;
    private Map<String, String> mNonlinearAdsTrackingEvents;

    public String getCreativeId() {
        return mCreativeId;
    }

    protected void setCreativeId(String creativeId) {
        mCreativeId = creativeId;
    }

    public int getSequence() {
        return mSequence;
    }

    protected void setSequence(int sequence) {
        mSequence = sequence;
    }

    public String getAdId() {
        return mAdId;
    }

    protected void setAdId(String adId) {
        mAdId = adId;
    }

    public String getAPIFramework() {
        return mAPIFramework;
    }

    protected void setAPIFramework(String apiFramework) {
        mAPIFramework = apiFramework;
    }

    public TVASTLinearAd getLinearAd() {
        return mLinearAd;
    }

    protected void setLinearAd(TVASTLinearAd linearAd) {
        mLinearAd = linearAd;
    }

    public List<TVASTNonlinearAd> getNonlinearAds() {
        return mNonlinearAds;
    }

    protected void setNonlinearAds(List<TVASTNonlinearAd> nonlinearAds) {
        mNonlinearAds = nonlinearAds;
    }

    public List<TVASTCompanionAd> getCompanionAds() {
        return mCompanionAds;
    }

    protected void setCompanionAd(List<TVASTCompanionAd> companionAds) {
        mCompanionAds = companionAds;
    }

    public Map<String, String> getNonlinearAdsTrackingEvents() {
        return mNonlinearAdsTrackingEvents;
    }

    protected void setNonlinearAdsTrackingEvents(Map<String, String> nonlinearAdsTrackingEvents) {
        mNonlinearAdsTrackingEvents = nonlinearAdsTrackingEvents;
    }

    public TVASTCreative() {
        super();
        mCreativeId = null;
        mAdId = null;
        mLinearAd = null;
        mNonlinearAds = null;
        mCompanionAds = null;
        mNonlinearAdsTrackingEvents = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mCreativeId == null) ? 0 : mCreativeId.hashCode());
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
        TVASTCreative other = (TVASTCreative) obj;
        if (mCreativeId == null) {
            if (other.mCreativeId != null)
                return false;
        } else if (!mCreativeId.equals(other.mCreativeId))
            return false;
        return true;
    }

    public static final Creator<TVASTCreative> CREATOR = new Creator<TVASTCreative>() {

        @Override
        public TVASTCreative[] newArray(int size) {
            return new TVASTCreative[size];
        }

        @Override
        public TVASTCreative createFromParcel(Parcel source) {
            TVASTCreative creative = new TVASTCreative();
            creative.mCreativeId = source.readString();
            creative.mSequence = source.readInt();
            creative.mAdId = source.readString();
            creative.mAPIFramework = source.readString();
            creative.mLinearAd = source.readParcelable(TVASTLinearAd.class.getClassLoader());
            creative.mNonlinearAds = new ArrayList<TVASTNonlinearAd>();
            source.readTypedList(creative.mNonlinearAds, TVASTNonlinearAd.CREATOR);
            creative.mCompanionAds = new ArrayList<TVASTCompanionAd>();
            source.readTypedList(creative.mCompanionAds, TVASTCompanionAd.CREATOR);
            creative.mNonlinearAdsTrackingEvents = new HashMap<String, String>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                String value = source.readString();
                creative.mNonlinearAdsTrackingEvents.put(key, value);
            }

            return creative;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCreativeId);
        dest.writeInt(mSequence);
        dest.writeString(mAdId);
        dest.writeString(mAPIFramework);
        dest.writeParcelable(mLinearAd, flags);
        dest.writeTypedList(mNonlinearAds);
        dest.writeTypedList(mCompanionAds);
        dest.writeInt(mNonlinearAdsTrackingEvents.size());
        for (String key : mNonlinearAdsTrackingEvents.keySet()) {
            dest.writeString(key);
            dest.writeString(mNonlinearAdsTrackingEvents.get(key));
        }
    }
}
