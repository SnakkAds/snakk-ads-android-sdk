package com.snakk.adview;

import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.snakk.advertising.internal.AdActivityContentWrapper;
import com.snakk.advertising.internal.SnakkAdActivity;
import com.snakk.adview.Mraid.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

enum MraidCommand implements IMraidCommand {
    CLOSE("close", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            SnakkAdActivity snakkAdActivity = adView.getMraidExpandedActivity();
            if (snakkAdActivity != null) {
                // closing expanded ad
                snakkAdActivity.close();
            }
            else {
                // closing resized ad
                adView.mraidClose();
            }
        }
    }),

    EXPAND("expand", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, final AdViewCore adView) {

            final boolean useCustomClose = "true".equalsIgnoreCase(params.get("useCustomClose"));

            AdActivityContentWrapper wrapper = new AdActivityContentWrapper() {
                private ViewGroup parent = null;

                @Override
                public View getContentView(final SnakkAdActivity activity) {
                    activity.setCloseButtonVisible(!useCustomClose);

                    // transplant view from current location to activity
                    parent  = (ViewGroup)adView.getParent();
                    parent.removeView(adView);
                    adView.setMraidExpandedActivity(activity);
                    return adView;
                }

                @Override
                public ViewGroup.LayoutParams getContentLayoutParams() {
                    // force the webview to take up the entire area
                    return new FrameLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                Gravity.CENTER);
                }

                @Override
                public void startContent() {
                    AdViewCore.OnAdDownload listener =  adView.getOnAdDownload();
                    if (listener != null) {
                        listener.willPresentFullScreen(adView);
                    }
                    adView.setMraidState(Mraid.MraidState.EXPANDED);
                    adView.syncMraidState();
                    adView.fireMraidEvent(Mraid.MraidEvent.STATECHANGE, adView.getMraidState().value);
                    adView.fireMraidEvent(Mraid.MraidEvent.SIZECHANGE, "["
                            + adView.pxToDip(adView.getWidth()) + ',' + adView.pxToDip(adView.getHeight()) + ']');
                }

                @Override
                public void done() {
                    // noop
                }

                @Override
                public void stopContent() {
                    // put the view back where we found it
                    //TODO investigate if there could be problems w/ the underlying activity being destroyed and recreated...
                    ViewGroup fosterParent = (ViewGroup)adView.getParent();
                    fosterParent.removeView(adView);
                    parent.addView(adView);
                    adView.mraidClose();
                }
            };

            SnakkAdActivity.startActivity(adView.getContext(), wrapper);
        }
    }),

    RESIZE("resize", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, final AdViewCore adView) {
            // tell adview to expand to full screen
            final int height = Integer.parseInt(params.get("height"));
            final int width = Integer.parseInt(params.get("width"));
            int offsetX = Integer.parseInt(params.get("offsetX"));
            int offsetY = Integer.parseInt(params.get("offsetY"));
            MraidCloseRegionPosition customClosePosition = MraidCloseRegionPosition.parse(params.get("customClosePosition"));
            boolean allowOffscreen = Boolean.parseBoolean(params.get("allowOffscreen"));

            boolean success = adView.resize(width, height, offsetX, offsetY, customClosePosition, allowOffscreen);
            if (success) {
                // post later to give the ui a chance to update (mraidSync sometimes gets old vals)
                adView.post(new Runnable() {
                    @Override
                    public void run() {
                        adView.setMraidState(Mraid.MraidState.RESIZED);
                        adView.syncMraidState();
                        adView.fireMraidEvent(Mraid.MraidEvent.STATECHANGE, adView.getMraidState().value);
                        adView.fireMraidEvent(Mraid.MraidEvent.SIZECHANGE, "[" + width + ',' + height + ']');
                        AdViewCore.OnAdDownload listener =  adView.getOnAdDownload();
                        if (listener != null) {
                            listener.didResize(adView);
                        }
                    }
                });
            }
            else {
                adView.fireMraidEvent(Mraid.MraidEvent.ERROR, "['failed to resize','resize']");
            }
        }
    }),

    OPEN("open", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            // tell adview to open url in in-app browser
            String url = params.get("url");
            adView.setOpenInInternalBrowser(true);
            adView.open(url);
        }
    }),

    CUSTOM_CLOSE_BUTTON("useCustomClose", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            // tell adview if we should render a close button
            boolean useCustomClose = "true".equalsIgnoreCase(params.get("useCustomClose"));
            adView.useCustomCloseButton(useCustomClose);
        }
    }),

    CREATE_CALENDAR_EVENT("createCalendarEvent", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            //{description: "Mayan Apocalypse/End of World",
            //location: "everywhere", start: "2012-12-21T00:00-05:00", end: "2012-12-22T00:00-05:00",
            //recurrence:{frequency: "daily"}}

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

            try {
                Date startDate = formatter.parse(params.get("start"));
                Date endDate = formatter.parse(params.get("end"));

                String location = params.get("location");
                String title = params.get("summary"); // the title comes in as description...
                String description = params.get("description"); // the title comes in as description...

                //TODO handle re-occurrence rules
//                String frequency = params.get("frequency");
//                String recurrence = "FREQ=DAILY";

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTime())
                        .putExtra(CalendarContract.Events.TITLE, title)
                        .putExtra(CalendarContract.Events.DESCRIPTION, description)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
//                        .putExtra(CalendarContract.Events.RRULE, recurrence);
                        ;
                adView.getContext().startActivity(intent);
            } catch (ParseException e) {
                Log.e(TAG, "Failed To parse dates", e);
            }
        }
    }),

    PLAY_VIDEO("playVideo", new IMraidCommand(){

        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            String videoUrl = params.get("url");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoUrl), "video/*");
            adView.getContext().startActivity(intent);
        }
    }),

    /**
     * only applies to expanded banners and interstitials (e.g. ads displayed from within an AdActivity)
     */
    SET_ORIENTATION_PROPERTIES("setOrientationProperties", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
//            boolean allowOrientationChange = Boolean.parseBoolean(params.get("allowOrientationChange"));
            MraidOrientation forceOrientation = MraidOrientation.parse(params.get("forceOrientation"));

            if (adView.getMraidExpandedActivity() != null) {
                adView.getMraidExpandedActivity().setRequestedOrientation(forceOrientation.orientation);
            }
            else {
                Log.d(TAG, "setOrientationProperties call ignored");
            }
        }
    });

    private static final String TAG = "Snakk";

    public final String command;
    private final IMraidCommand commandListener;

    private static final Pattern QUESTION_MARK_PATTERN = Pattern.compile("\\?");
    private static final Pattern DBL_SLASH_PATTERN = Pattern.compile("//");

    private MraidCommand(String commandName, IMraidCommand commandCode) {
        command = commandName;
        commandListener = commandCode;
    }

    public void execute(Map<String, String> params, AdViewCore adView) {
        commandListener.execute(params, adView);
    }

    public static MraidCommand marshalMraidCommand(String commandName) {
        for (MraidCommand cmd : MraidCommand.values()) {
            if (cmd.command.equalsIgnoreCase(commandName)) {
                return cmd;
            }
        }

        return null;
    }

    public static void routeRequest(String url, AdViewCore adView) {
        // parse string
        String parts[] = QUESTION_MARK_PATTERN.split(url, 2);
        String commandName = parts[0];
        Map<String, String> params = Collections.emptyMap();
        if(parts.length == 2) {
            // has query string
            try {
                params = Utils.parseUrlParams(url);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Failed to parse native MRAID QS params: " + url);
                return;
            }
        }
        parts = DBL_SLASH_PATTERN.split(commandName);
        if (parts.length != 2) {
            Log.e(TAG, "Failed to parse native MRAID command: " + url);
            return;
        }
        commandName = parts[1];

        // fire off command
        Log.d(TAG, "Command: " + commandName + '(' + params + ')');
        MraidCommand command = marshalMraidCommand(commandName);
        if (command != null) {
            command.execute(params, adView);
        }
    }
}
