package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

public enum TVASTAdType implements Parcelable {
    VIDEO;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<TVASTAdType> CREATOR = new Creator<TVASTAdType>() {
        @Override
        public TVASTAdType createFromParcel(final Parcel source) {
            return TVASTAdType.values()[source.readInt()];
        }

        @Override
        public TVASTAdType[] newArray(final int size) {
            return new TVASTAdType[size];
        }
    };

}
