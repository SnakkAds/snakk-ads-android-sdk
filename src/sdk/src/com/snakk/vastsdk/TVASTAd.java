package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class TVASTAd implements Parcelable {

    public static final String VERSION = "1.0.3";

    private TVASTAdType mAdType;
    private String mAdId;
    private String mSequenceId;
    private float mCreativeWidth;
    private float mCreativeHeight;

    private boolean mIs3rdPartyAd;
    private String mMediaUrl;
    private int mMediaFileIndex;
    private String mAdSystem;
    private String mVASTVersion;
    private String mAdTitle;
    private String mDescription;
    private String mAdvertiser;
    private String mSurveyURI;
    private String mErrorURI;
    private HashMap<String, String> mImpressions;
    private ArrayList<TVASTCreative> mCreatives;
    private double mDuration;

    public TVASTAd() {
        mIs3rdPartyAd = false;
        mMediaFileIndex = -1;
        mMediaUrl = "";
        mDuration = 0;
    }

    public TVASTAdType getAdType() {
        return mAdType;
    }

    protected void setAdType(TVASTAdType adType) {
        mAdType = adType;
    }

    public String getAdId() {
        return mAdId;
    }

    protected void setAdId(String adId) {
        mAdId = adId;
    }

    public String getSequenceId() {
        return mSequenceId;
    }

    protected void setSequenceId(String sequenceId) {
        mSequenceId = sequenceId;
    }

    public float getCreativeWidth() {
        return mCreativeWidth;
    }

    protected void setCreativeWidth(float creativeWidth) {
        mCreativeWidth = creativeWidth;
    }

    public float getCreativeHeight() {
        return mCreativeHeight;
    }

    protected void setCreativeHeight(float creativeHeight) {
        mCreativeHeight = creativeHeight;
    }

    public boolean getIs3rdPartyAd() {
        return mIs3rdPartyAd;
    }

    protected void setIs3rdPartyAd(boolean is3rdPartyAd) {
        mIs3rdPartyAd = is3rdPartyAd;
    }

    public String getMediaUrl() {
        return mMediaUrl;
    }

    protected void setMediaUrl(String mediaUrl) {
        mMediaUrl = mediaUrl;
    }

    public int getMediaFileIndex() {
        return mMediaFileIndex;
    }

    protected void setMediaFileIndex(int index) {
        mMediaFileIndex = index;
    }

    public String getAdSystem() {
        return mAdSystem;
    }

    protected void setAdSystem(String adSystem) {
        mAdSystem = adSystem;
    }

    public String getVASTVersion() {
        return mVASTVersion;
    }

    protected void setVASTVersion(String vastVersion) {
        mVASTVersion = vastVersion;
    }

    public String getAdTitle() {
        return mAdTitle;
    }

    protected void setAdTitle(String adTitle) {
        mAdTitle = adTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    protected void setDescription(String description) {
        mDescription = description;
    }

    public String getAdvertiser() {
        return mAdvertiser;
    }

    protected void setAdvertiser(String advertiser) {
        mAdvertiser = advertiser;
    }

    public String getSurveyURI() {
        return mSurveyURI;
    }

    protected void setSurveyURI(String surveyURI) {
        mSurveyURI = surveyURI;
    }

    public String getErrorURI() {
        return mErrorURI;
    }

    protected void setErrorURI(String errorURI) {
        mErrorURI = errorURI;
    }

    public HashMap<String, String> getImpressions() {
        return mImpressions;
    }

    protected void setImpressions(HashMap<String, String> impressions) {
        mImpressions = impressions;
    }

    public ArrayList<TVASTCreative> getCreatives() {
        return mCreatives;
    }

    protected void setCreatives(ArrayList<TVASTCreative> creatives) {
        mCreatives = creatives;
    }

    public double getDuration() {
        return mDuration;
    }

    protected void setDuration(double duration) {
        mDuration = duration;
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
        TVASTAd other = (TVASTAd) obj;
        if (mAdId == null) {
            if (other.mAdId != null)
                return false;
        } else if (!mAdId.equals(other.mAdId))
            return false;
        return true;
    }

    public static final Creator<TVASTAd> CREATOR = new Creator<TVASTAd>() {

        @Override
        public TVASTAd[] newArray(int size) {
            return new TVASTAd[size];
        }

        @Override
        public TVASTAd createFromParcel(Parcel source) {
            TVASTAd trmaAd = new TVASTAd();
            trmaAd.mAdType = source.readParcelable(TVASTAdType.class.getClassLoader());
            trmaAd.mAdId = source.readString();
            trmaAd.mSequenceId = source.readString();
            trmaAd.mCreativeWidth = source.readFloat();
            trmaAd.mCreativeHeight = source.readFloat();
            trmaAd.mIs3rdPartyAd = source.readInt() == 1;
            trmaAd.mMediaUrl = source.readString();
            trmaAd.mMediaFileIndex = source.readInt();
            trmaAd.mAdSystem = source.readString();
            trmaAd.mVASTVersion = source.readString();
            trmaAd.mAdTitle = source.readString();
            trmaAd.mDescription = source.readString();
            trmaAd.mAdvertiser = source.readString();
            trmaAd.mSurveyURI = source.readString();
            trmaAd.mErrorURI = source.readString();
            trmaAd.mImpressions = new HashMap<String, String>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                String value = source.readString();
                trmaAd.mImpressions.put(key, value);
            }
            trmaAd.mCreatives = new ArrayList<TVASTCreative>();
            source.readTypedList(trmaAd.mCreatives, TVASTCreative.CREATOR);
            trmaAd.mDuration = source.readFloat();
            return trmaAd;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAdType, flags);
        dest.writeString(mAdId);
        dest.writeString(mSequenceId);
        dest.writeFloat(mCreativeWidth);
        dest.writeFloat(mCreativeHeight);
        dest.writeInt(mIs3rdPartyAd ? 1 : 0);
        dest.writeString(mMediaUrl);
        dest.writeInt(mMediaFileIndex);
        dest.writeString(mAdSystem);
        dest.writeString(mVASTVersion);
        dest.writeString(mAdTitle);
        dest.writeString(mDescription);
        dest.writeString(mAdvertiser);
        dest.writeString(mSurveyURI);
        dest.writeString(mErrorURI);
        dest.writeInt(mImpressions.size());
        for (String key : mImpressions.keySet()) {
            dest.writeString(key);
            dest.writeString(mImpressions.get(key));
        }
        dest.writeTypedList(mCreatives);
        dest.writeDouble(mDuration);
    }
}

