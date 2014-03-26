package com.snakk.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;

public class TVASTMediaFile implements Parcelable {

    private String mURIMediaFile;
    private String mFileId;
    private boolean mIsStreaming;
    private String mMimeType;
    private int mBitrate;
    private int mWidth;
    private int mHeight;
    private boolean mScalable;
    private boolean mKeepAspectRatio;
    private String mAPIFramework;

    public String getURIMediaFile() {
        return mURIMediaFile;
    }

    protected void setURIMediaFile(String uriMediaFile) {
        mURIMediaFile = uriMediaFile;
    }

    public String getFileId() {
        return mFileId;
    }

    protected void setFileId(String fileId) {
        mFileId = fileId;
    }

    public boolean getIsStreaming() {
        return mIsStreaming;
    }

    protected void setIsStreaming(boolean isStreaming) {
        mIsStreaming = isStreaming;
    }

    public String getMimeType() {
        return mMimeType;
    }

    protected void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public int getBitrate() {
        return mBitrate;
    }

    protected void setBitrate(int bitrate) {
        mBitrate = bitrate;
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

    public String getAPIFramework() {
        return mAPIFramework;
    }

    protected void setAPIFramework(String apiFramework) {
        mAPIFramework = apiFramework;
    }

    public TVASTMediaFile() {
        mURIMediaFile = null;
        mFileId = null;
        mMimeType = null;
        mAPIFramework = null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mURIMediaFile == null) ? 0 : mURIMediaFile.hashCode());
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
        TVASTMediaFile other = (TVASTMediaFile) obj;
        if (mURIMediaFile == null) {
            if (other.mURIMediaFile != null)
                return false;
        } else if (!mURIMediaFile.equals(other.mURIMediaFile))
            return false;
        return true;
    }

    public static final Creator<TVASTMediaFile> CREATOR = new Creator<TVASTMediaFile>() {

        @Override
        public TVASTMediaFile[] newArray(int size) {
            return new TVASTMediaFile[size];
        }

        @Override
        public TVASTMediaFile createFromParcel(Parcel source) {
            TVASTMediaFile mediaFile = new TVASTMediaFile();
            mediaFile.mURIMediaFile = source.readString();
            mediaFile.mFileId = source.readString();
            mediaFile.mIsStreaming = source.readInt() == 1;
            mediaFile.mMimeType = source.readString();
            mediaFile.mBitrate = source.readInt();
            mediaFile.mWidth = source.readInt();
            mediaFile.mHeight = source.readInt();
            mediaFile.mScalable = source.readInt() == 1;
            mediaFile.mKeepAspectRatio = source.readInt() == 1;
            mediaFile.mAPIFramework = source.readString();
            return mediaFile;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mURIMediaFile);
        dest.writeString(mFileId);
        dest.writeInt(mIsStreaming ? 1 : 0);
        dest.writeString(mMimeType);
        dest.writeInt(mBitrate);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeInt(mScalable ? 1 : 0);
        dest.writeInt(mKeepAspectRatio ? 1 : 0);
        dest.writeString(mAPIFramework);
    }
}
