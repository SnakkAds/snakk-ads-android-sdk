package com.snakk.vastsdk.player;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.snakk.vastsdk.player.TVASTPlayer.TVASTAdPlayerListener;

import java.util.List;

/**
 * A thread that fires periodic progress events for a VideoView. Callbacks are run in the UI thread.
 */
public class TVASTVideoProgressThread extends Thread {

    private static final int UPDATE = 1;
    private static final int QUIT = 2;

    private List<TVASTAdPlayerListener> callbacks;
    //private VideoView video;
    private TVASTPlayer mPlayer;
    private boolean onPlaySent;

    /**
     * Handler that runs on the UI thread
     */
    private Handler uiHandler;
    /**
     * Handler that runs on the progress thread
     */
    private Handler threadHandler;

    public TVASTVideoProgressThread(TVASTPlayer player, List<TVASTAdPlayerListener> callbacks) {
        //this.video = video;
        this.mPlayer = player;
        // The callbacks list is shared with the caller, so it should only be used on the UI thread.
        this.callbacks = callbacks;
        setName("TVASTVideoProgressThread");
        uiHandler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return handleUiMessage(msg);
            }
        });
    }

    public void quit() {
        if (threadHandler != null)
            threadHandler.sendMessageAtFrontOfQueue(Message.obtain(threadHandler, QUIT));
    }

    @Override
    public void run() {
        Looper.prepare();
        threadHandler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return handleThreadMessage(msg);
            }
        });
        threadHandler.sendEmptyMessage(UPDATE);
        Looper.loop();
    }

    protected boolean handleThreadMessage(Message msg) {
        switch (msg.what) {
            case UPDATE:
                update();
                threadHandler.sendEmptyMessageDelayed(UPDATE, 1000);
                return true;
            case QUIT:
                threadHandler.removeCallbacksAndMessages(null);
                Looper.myLooper().quit();
                return true;
            default:
                return false;
        }
    }

    private void update() {
        try {
            if (!mPlayer.getVideoView().isPlaying()) {
                return;
            }
            // Send a message to the UI thread to fire a callback.
            uiHandler.sendMessage(Message.obtain(uiHandler,
                    UPDATE, mPlayer.getVideoView().getCurrentPosition() / 1000,
                    mPlayer.getVideoView().getDuration() / 1000));
        } catch (IllegalStateException e) {
            // Video view is stopped.
            Log.d("TVASTVideoProgressThread", "IllegalStateException during update");
        }
    }

    protected boolean handleUiMessage(Message msg) {
        switch (msg.what) {
            case UPDATE:
                int currentPosition = msg.arg1;
                int duration = msg.arg2;
                for (TVASTAdPlayerListener callback : callbacks) {
                    callback.onVideoProgress(mPlayer, currentPosition, duration);
                }
                // When the video starts playing, inform the callback only once.
                if (!onPlaySent && currentPosition > 0) {
                    onPlaySent = true;
                    //for (TVASTAdPlayerListener callback : callbacks) {
                    //callback.onVideoPlay(mPlayer);
                    //}
                }
                return true;
            default:
                return false;
        }
    }
}
