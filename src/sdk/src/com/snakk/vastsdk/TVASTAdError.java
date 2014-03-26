package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

public class TVASTAdError implements Parcelable {

    /// Possible error types while loading or playing ads.
    protected enum AdErrorType implements Parcelable {
        /// This may mean that the SDK wasn't loaded properly.
        UNKNOWN,
        /// An error occured while loading the ads.
        LOAD,
        /// An error occured while playing the ads.
        PLAY;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<AdErrorType> CREATOR = new Creator<AdErrorType>() {
            @Override
            public AdErrorType createFromParcel(final Parcel source) {
                return AdErrorType.values()[source.readInt()];
            }

            @Override
            public AdErrorType[] newArray(final int size) {
                return new AdErrorType[size];
            }
        };
    }

    /// Possible error codes raised while loading or playing ads.
    protected enum AdErrorCode implements Parcelable {
        /// Unknown error occured while loading or playing the ad.
        UNKNOWN_ERROR(0),            // = 0,
        /// There was an error playing the video ad.
        VIDEO_PLAY_ERROR(1003),        // = 1003,
        /// There was a problem requesting ads from the server.
        FAILED_TO_REQUEST_ADS(1004),    // = 1004,
        /// There was an internal error while loading the ad.
        INTERNAL_ERROR(2001),            // = 2001,
        /// No supported ad format was found.
        SUPPORTED_ADS_NOT_FOUND(2002),
        /// Ad Slot not visible
        ADSLOT_NOT_VISIBLE(2003),
        /// At least one VAST wrapper ad loaded successfully and a subsequent wrapper
        /// or inline ad load has timed out.
        VAST_LOAD_TIMEOUT(3001),        // = 3001,
        /// At least one VAST wrapper loaded and a subsequent wrapper or inline ad
        /// load has resulted in a 404 response code.
        VAST_INVALID_URL(3002),        // = 3002,
        /// The ad response was not recognized as a valid VAST ad.
        VAST_MALFORMED_RESPONSE(3003),// = 3003,
        /// A media file of a VAST ad failed to load or was interrupted mid-stream.
        VAST_MEDIA_ERROR(3004),        // = 3004,
        /// The maximum number of VAST wrapper redirects has been reached.
        VAST_TOO_MANY_REDIRECTS(3005),// = 3005,
        /// Assets were found in the VAST ad response, but none of them matched the
        /// video player's capabilities.
        VAST_ASSET_MISMATCH(3006),    // = 3006,
        /// No assets were found in the VAST ad response.
        VAST_ASSET_NOT_FOUND(3007),    // = 3007,
        /// Invalid arguments were provided to SDK methods.
        INVALID_ARGUMENTS(3101),        // = 3101,
        /// A companion ad failed to load or render.
        COMPANION_AD_LOADING_FAILED(3102),    // = 3102,
        /// The ad response was not understood and cannot be parsed.
        UNKNOWN_AD_RESPONSE(3103),    // = 3103,
        /// An unexpected error occurred while loading the ad.
        UNEXPECTED_LOADING_ERROR(3104),   // use unknown error
        /// An overlay ad failed to load.
        OVERLAY_AD_LOADING_FAILED(3105),    // = 3105,
        /// An overlay ad failed to render.
        OVERLAY_AD_PLAYING_FAILED(3106);    // = 3106,

        private int code;

        private AdErrorCode(int code) {
            this.code = code;
        }

        protected int getCode() {
            return code;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<AdErrorCode> CREATOR = new Creator<AdErrorCode>() {
            @Override
            public AdErrorCode createFromParcel(final Parcel source) {
                return AdErrorCode.values()[source.readInt()];
            }

            @Override
            public AdErrorCode[] newArray(final int size) {
                return new AdErrorCode[size];
            }
        };
    }

    private AdErrorType mErrorType;
    private AdErrorCode mErrorCode;
    private String mErrorMessage;

    public TVASTAdError(AdErrorType type, AdErrorCode code, String message) {
        mErrorType = type;
        mErrorCode = code;
        mErrorMessage = message;
    }

    public AdErrorCode getErrorCode() {
        return mErrorCode;
    }

    public AdErrorType getErrorType() {
        return mErrorType;
    }

    public String getMessage() {
        return mErrorMessage;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mErrorMessage == null) ? 0 : mErrorMessage.hashCode());
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
        TVASTAdError other = (TVASTAdError) obj;
        if (mErrorMessage == null) {
            if (other.mErrorMessage != null)
                return false;
        } else if (!mErrorMessage.equals(other.mErrorMessage))
            return false;
        return true;
    }

    public static final Creator<TVASTAdError> CREATOR = new Creator<TVASTAdError>() {

        @Override
        public TVASTAdError[] newArray(int size) {
            return new TVASTAdError[size];
        }

        @Override
        public TVASTAdError createFromParcel(Parcel source) {
            AdErrorType type = source.readParcelable(AdErrorType.class.getClassLoader());
            AdErrorCode code = source.readParcelable(AdErrorCode.class.getClassLoader());
            String message = source.readString();
            TVASTAdError adError = new TVASTAdError(type, code, message);
            return adError;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mErrorType, flags);
        dest.writeParcelable(mErrorCode, flags);
        dest.writeString(mErrorMessage);
    }
}
