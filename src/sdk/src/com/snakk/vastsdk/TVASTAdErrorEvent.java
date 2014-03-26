package com.snakk.vastsdk;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class TVASTAdErrorEvent implements Parcelable {
    TVASTAdError mAdError;
    Context mContext;

    public TVASTAdErrorEvent(TVASTAdError adError) {
        mContext = null;
        mAdError = adError;
    }

    public TVASTAdErrorEvent(Context context, TVASTAdError adError) {
        mContext = context;
        mAdError = adError;
    }

    public TVASTAdError getError() {
        return mAdError;
    }

    public Context getUserRequestContext() {
        return mContext;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mAdError == null) ? 0 : mAdError.hashCode());
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
        TVASTAdErrorEvent other = (TVASTAdErrorEvent) obj;
        if (mAdError == null) {
            if (other.mAdError != null)
                return false;
        } else if (!mAdError.equals(other.mAdError))
            return false;
        return true;
    }

    public static final Creator<TVASTAdErrorEvent> CREATOR = new Creator<TVASTAdErrorEvent>() {

        @Override
        public TVASTAdErrorEvent[] newArray(int size) {
            return new TVASTAdErrorEvent[size];
        }

        @Override
        public TVASTAdErrorEvent createFromParcel(Parcel source) {
            TVASTAdError adError = source.readParcelable(TVASTAdError.class.getClassLoader());
//            Context context = source.readParcelable(Context.class.getClassLoader());
            Context context = null;
            TVASTAdErrorEvent adErrorEvent = new TVASTAdErrorEvent(context, adError);
            return adErrorEvent;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAdError, flags);
    }
}
