package com.snakk.vastsdk.player;

import android.widget.VideoView;

public interface TVASTPlayer {

    public interface TVASTAdPlayerListener {
        void onVideoClick(TVASTPlayer player);

        void onVideoComplete(TVASTPlayer player);

        void onVideoError(TVASTPlayer player);

        void onVideoPause(TVASTPlayer player);

        void onVideoPlay(TVASTPlayer player);

        void onVideoProgress(TVASTPlayer player, int current, int max);

        void onVideoResume(TVASTPlayer player);

        void onVideoVolumeChanged(TVASTPlayer player, int volume);
    }

    public void addCallback(TVASTAdPlayerListener callback);

    public void playAd(String url);

    public void removeCallback(TVASTAdPlayerListener callback);

    public void stopAd();

    public VideoView getVideoView();
}
