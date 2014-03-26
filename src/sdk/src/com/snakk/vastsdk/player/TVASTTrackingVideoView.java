package com.snakk.vastsdk.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.VideoView;
import com.snakk.vastsdk.player.TVASTPlayer.TVASTAdPlayerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A VideoView that intercepts various methods and reports them back to a set of
 * TVASTAdPlayerListener.
 */
public class TVASTTrackingVideoView extends VideoView implements OnCompletionListener, OnErrorListener {
    private static final String TAG = "Snakk";

    private enum PlaybackState {
        STOPPED, PAUSED, PLAYING
    }

    private final List<TVASTAdPlayerListener> callbacks = new ArrayList<TVASTAdPlayerListener>(1);
    private TVASTVideoProgressThread progressThread;
    private PlaybackState state = PlaybackState.STOPPED;
    private TVASTPlayer mPlayer;

    public TVASTTrackingVideoView(Context context) {
        super(context);
        init(null);
    }

    public TVASTTrackingVideoView(Context context, TVASTPlayer player) {
        super(context);
        init(player);
    }

    private void init(TVASTPlayer player) {
        super.setOnCompletionListener(this);
        super.setOnErrorListener(this);

        this.mPlayer = player;

        OnPreparedListener preparedListener = new OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                for (TVASTAdPlayerListener callback : callbacks) {
                    callback.onVideoPlay(mPlayer);
                }
            }
        };
        setOnPreparedListener(preparedListener);
    }

//    // called from DemoPlayer
//    public void onClick() {
//        SnakkLog.d(TAG, "TVASTTrackingVideoView.onClick");
//        stopPlayback();
//        for (TVASTAdPlayerListener callback : callbacks) {
//            callback.onVideoClick(mPlayer);
//        }
//    }
//
    // Overrides methods of VideoView
    @Override
    public void start() {
        super.start();
        PlaybackState oldState = state;
        state = PlaybackState.PLAYING;

        switch (oldState) {
            case STOPPED:
                progressThread = new TVASTVideoProgressThread(this.mPlayer, callbacks);
                progressThread.start();
                break;
            case PAUSED:
                for (TVASTAdPlayerListener callback : callbacks) {
                    callback.onVideoResume(mPlayer);
                }
                break;
            default:
                // Already playing; do nothing.
        }
    }

    @Override
    public void pause() {
        super.pause();
        state = PlaybackState.PAUSED;

        for (TVASTAdPlayerListener callback : callbacks) {
            callback.onVideoPause(mPlayer);
        }

        // pause state doesn't work well with streaming so call stopPlayback.
        stopPlayback();
    }

    @Override
    public void resume() {
        super.resume();

        for (TVASTAdPlayerListener callback : callbacks) {
            callback.onVideoResume(mPlayer);
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        onStop(null);
    }

    private void onStop(MediaPlayer mp) {

        if (state == PlaybackState.STOPPED) {
            return; // Already stopped; do nothing.
        }

        state = PlaybackState.STOPPED;

        if (progressThread != null) {
            progressThread.quit();
            try {
                progressThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            progressThread = null;
        }
    }

    // Method of OnCompletionListener
    @Override
    public void onCompletion(MediaPlayer mp) {
        onStop(mp);
        //synchronized(callbacks)
        {
            for (TVASTAdPlayerListener callback : callbacks) {
                callback.onVideoComplete(mPlayer);
            }
        }
    }

    // Method of OnErrorListener
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //synchronized(callbacks)
        {
            for (TVASTAdPlayerListener callback : callbacks) {
                callback.onVideoError(mPlayer);
            }
        }
        onStop(mp);

        // needs to call on Listeners about the error.
    /*
    what == MediaPlayer.MEDIA_ERROR_UNKNOWN
	what = MEDIA_ERROR_UNKNOWN;
	
	MEDIA_ERROR_IO, 
	MEDIA_ERROR_MALFORMED,
	MEDIA_ERROR_UNSUPPORTED,
	MEDIA_ERROR_TIMED_OUT
	*/

        // Returning true signals to MediaPlayer that we handled the error. This will prevent the
        // completion handler from being called.
        return true;
    }

    public void addCallback(TVASTAdPlayerListener callback) {
        if (callbacks.contains(callback))
            return;
        synchronized (callbacks) {
            callbacks.add(callback);
        }
    }

    public void removeCallback(TVASTAdPlayerListener callback) {
        synchronized (callbacks) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener l) {
        throw new UnsupportedOperationException();
    }
}
