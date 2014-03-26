package com.snakk.adview;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snakk.advertising.internal.*;
import com.snakk.advertising.internal.SnakkAdActivity;

public abstract class AdInterstitialBaseView extends AdView implements AdViewCore.OnAdDownload {

    @Deprecated
    public enum FullscreenAdSize {
        AUTOSIZE_AD     ( -1,  -1),
        MEDIUM_RECTANGLE(300, 250);

        public final int width;
        public final int height;
        FullscreenAdSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
        
        
    protected final Context context;
//    protected Context callingActivityContext;
    protected RelativeLayout interstitialLayout = null;
    protected boolean isLoaded = false;
    protected OnInterstitialAdDownload interstitialListener = null;
        
        
    public AdInterstitialBaseView(Context ctx, String zone) {
        super(ctx, zone);
        context = ctx;
        setOnAdDownload(this);
//        setOnAdClickListener(this);
        super.setUpdateTime(0); // disable add cycling
    }

    @Deprecated
    public final void setAdSize(FullscreenAdSize adSize) {}

    public abstract View getInterstitialView(Context ctx);

    protected void removeViews() {
        RelativeLayout parent = (RelativeLayout)this.getParent();
        if(parent != null) {
            (parent).removeAllViews();
        }
    }

//    public void closeInterstitial() {
//        final AdInterstitialBaseView adView = this;
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if(callingActivityContext == null) {
//                    // interstitial was never displayed; nothing to do here...
//                    return;
//                }
//                ((Activity)callingActivityContext).finish();
//                if(interstitialListener != null) {
//                    interstitialListener.didClose(adView);
//                }
//
//                removeViews();
//            }
//        });
//    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void load() {
        update(true);
    }

    public void showInterstitial() {
        if(interstitialListener != null) {
            interstitialListener.willOpen(this);
        }

        AdActivityContentWrapper wrapper = new AdActivityContentWrapper() {

            @Override
            public View getContentView(SnakkAdActivity activity) {
                setMraidExpandedActivity(activity);
                return AdInterstitialBaseView.this;
            }

            @Override
            public ViewGroup.LayoutParams getContentLayoutParams() {
                if (mraid) {
                    return new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Gravity.CENTER);
                }
                else {
                    return super.getContentLayoutParams();
                }
            }

            @Override
            public void done() {
                //To change body of implemented methods use File | Settings | File Templates.
                willDismissFullScreen();
                if(interstitialListener != null) {
                    interstitialListener.didClose(AdInterstitialBaseView.this);
                }
            }
        };

        if (mraid) {
            syncMraidState();
            fireMraidEvent(Mraid.MraidEvent.VIEWABLECHANGE, "true");
        }
        SnakkAdActivity.startActivity(context, wrapper);
    }

    /**
     * This event is fired before banner download begins.
     */
    public void begin(AdViewCore adView) {
        isLoaded = false;
        if(interstitialListener != null) {
            interstitialListener.willLoad(adView);
        }
    }

    /**
     * This event is fired after banner content fully downloaded.
     */
    public void end(AdViewCore adView) {
        isLoaded = true;
        if(interstitialListener != null) {
            interstitialListener.ready(adView);
        }
    }

    /**
     * This event is fired after a user taps the ad.
     * @param adView
     */
    public void clicked(AdViewCore adView) {
        if(interstitialListener != null) {
            interstitialListener.clicked(adView);
        }
    }

    /**
     * This event is fired just before the app will be sent to the background.
     * @param adView
     */
    public void willLeaveApplication(AdViewCore adView) {
        if(interstitialListener != null) {
            interstitialListener.willLeaveApplication(adView);
        }
    }

    /**
     * This event is fired after fail to download content.
     */
    @Override
    public void error(AdViewCore adView, String error) {
        if(interstitialListener != null) {
            interstitialListener.error(adView, error);
        }
    }

//    @Override
//    public void click(String url) {
//        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")){
//            if(interstitialListener != null) {
//                interstitialListener.willLeaveApplication(this);
//            }
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            Activity thisActivity = ((Activity)callingActivityContext);
//            thisActivity.startActivityForResult(intent,2);
//        }
//        else {
//            loadUrl(url);
//        }
//    }

    @Override
    public void willPresentFullScreen(AdViewCore adView) {
        // noop
    }

    @Override
    public void didPresentFullScreen(AdViewCore adView) {
        // noop
    }

    @Override
    public void willDismissFullScreen(AdViewCore adView) {
        // noop
//        if(interstitialListener != null) {
//            interstitialListener.didClose(adView);
//        }

    }

    public OnInterstitialAdDownload getOnInterstitialAdDownload() {
        return interstitialListener;
    }

    public void setOnInterstitialAdDownload(OnInterstitialAdDownload listener) {
        interstitialListener = listener;
    }

    /**
     * setUpdateTime(Integer) is not supported in AdFullscreenView
     */
    @Override
    public final void setUpdateTime(int updateTime) {
        // not supported for interstitials
    }

    /**
     * called once the interstitial action is full-screened
     */
    public void interstitialShowing() {
        // no-op
    }

    /**
     * called once the interstitial action is closed
     */
    public void interstitialClosing() {
        // no-op
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        Log.d("Snakk", "AdInterstitialBaseView.onKeyDown");
//        // Close interstitial properly on back button press
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            closeInterstitial();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * Allows lookup of resource id's from jars at runtime
     * http://stackoverflow.com/questions/1995004/packaging-android-resource-files-within-a-distributable-jar-file/2825174#7117422
     * @param packageName the package name of your app. e.g. context.getPackageName()
     * @param className e.g. layout, string, drawable
     * @param name the name of the resource you're looking for
     * @return the id of the resource
     */
    public static int getResourceIdByName(String packageName, String className, String name) {
       Class<?> r = null;
       int id = 0;
       try {
           r = Class.forName(packageName + ".R");

           Class<?>[] classes = r.getClasses();
           Class<?> desiredClass = null;

           for (int i = 0; i < classes.length; i++) {
               if(classes[i].getName().split("\\$")[1].equals(className)) {
                   desiredClass = classes[i];
                   break;
               }
           }

           if(desiredClass != null)
               id = desiredClass.getField(name).getInt(desiredClass);
       } catch (ClassNotFoundException e) {
           Log.e("Snakk", "An error occurred", e);
       } catch (IllegalArgumentException e) {
           Log.e("Snakk", "An error occurred", e);
       } catch (SecurityException e) {
           Log.e("Snakk", "An error occurred", e);
       } catch (IllegalAccessException e) {
           Log.e("Snakk", "An error occurred", e);
       } catch (NoSuchFieldException e) {
           Log.e("Snakk", "An error occurred", e);
       }

       return id;
    }
}
