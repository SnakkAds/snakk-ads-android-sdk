package com.snakk.vastsdk;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import com.snakk.vastsdk.TVASTAdError.AdErrorCode;
import com.snakk.vastsdk.TVASTAdError.AdErrorType;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TVASTAdsLoader {

    public class TVASTAdsLoadedEvent {
        Context mContext;
        TVASTVideoAdsManager mAdManager;

        public TVASTAdsLoadedEvent(Context context, TVASTVideoAdsManager adManager) {
            mContext = context;
            mAdManager = adManager;
        }

        public TVASTVideoAdsManager getManager() {
            return mAdManager;
        }

        public Context getUserRequestContext() {
            return mContext;
        }
    }

    public interface TVASTAdsLoadedListener {
        void onAdsLoaded(TVASTAdsLoadedEvent event);
    }

    private Context mContext;
    private TVASTAdsRequest mAdRequest;
    private ArrayList<TVASTAdsLoadedListener> mLoadedListeners;
    private ArrayList<TVASTAdErrorListener> mErrorListeners;

    private String mRootErrorUri;

    // We don't use namespaces in parser
    private static final String namespace = null;

    protected class VASTParser {

        private double parseTimeString(String timeString) {
            String[] times = timeString.split(":");
            if (times.length == 3) {
                double timeInSeconds = Integer.parseInt(times[0]) * 3600 +
                        Integer.parseInt(times[1]) * 60 +
                        Integer.parseInt(times[2]);
                return timeInSeconds;
            }
            return 0;
        }

        protected TVASTAd parseVAST(InputStream inStream, TVASTAd trmaAd) throws XmlPullParserException, IOException {

            try {

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inStream, null);
                parser.nextTag();

                parser.require(XmlPullParser.START_TAG, namespace, "VAST");

                String vastVersion = parser.getAttributeValue(namespace, "version");

                boolean hasContents = false;
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if ("Error".equals(name)) {
                        hasContents = true;
                        mRootErrorUri = readText(parser);
                    } else if ("Ad".equals(name)) {
                        hasContents = true;
                        // handle the whole item here if the format is static
                        trmaAd = readAd(parser, trmaAd);
                    } else {
                        skip(parser);
                    }
                }

                if (!hasContents) {
                    mRootErrorUri = "No creatives available";
                }
            } finally {
                inStream.close();
            }

            return trmaAd;
        }

        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText().trim();
                parser.nextTag();
            }
            return result;
        }

        private String readCData(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            int eventType = parser.next();
            if (eventType == XmlPullParser.CDSECT) {
                parser.nextToken();
                result = parser.getText().trim();
                parser.nextTag();
            }
            return result;
        }

        private TVASTAd readAd(XmlPullParser parser, TVASTAd theAd) throws XmlPullParserException, IOException {

            if (theAd == null)
                theAd = new TVASTAd();

            String adId = parser.getAttributeValue(namespace, "id");
            String sequenceId = parser.getAttributeValue(namespace, "sequence");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("InLine")) {
                    theAd = readInLine(parser, theAd);
                } else if (name.equals("Wrapper")) {
                    theAd = readWrapper(parser, theAd);
                } else {
                    skip(parser);
                }
            }
            theAd.setAdId(adId);
            theAd.setSequenceId(sequenceId);

            return theAd;
        }

        private TVASTAd readInLine(XmlPullParser parser, TVASTAd theAd) throws XmlPullParserException, IOException {

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("AdSystem")) {
                    String adVersion = parser.getAttributeValue(namespace, "version");
                    String adSystem = readText(parser);
                    theAd.setVASTVersion(adVersion);
                    theAd.setAdSystem(adSystem);
                } else if (name.equals("AdTitle")) {
                    String adTitle = readText(parser);
                    theAd.setAdTitle(adTitle);
                } else if (name.equals("Description")) {
                    String description = readText(parser);
                    theAd.setDescription(description);
                } else if (name.equals("Advertiser")) {
                    String advertiser = readText(parser);
                    theAd.setAdvertiser(advertiser);
                } else if (name.equals("Pricing")) {
                    String model = parser.getAttributeValue(namespace, "model");
                    String currency = parser.getAttributeValue(namespace, "currency");
                    String price = readText(parser);
                    // need to set these to theAd.
                } else if (name.equals("Survey")) {
                    String surveyUri = readText(parser);
                    theAd.setSurveyURI(surveyUri);
                } else if (name.equals("Error")) {
                    String errorUri = readText(parser);
                    theAd.setErrorURI(errorUri);
                } else if (name.equals("Impression")) {
                    String impressionId = parser.getAttributeValue(namespace, "id");
                    String impressionUri = readText(parser);
                    HashMap<String, String> impressions = new HashMap<String, String>();
                    impressions.put(impressionId, impressionUri);
                    theAd.setImpressions(impressions);
                } else if (name.equals("Creatives")) {
                    ArrayList<TVASTCreative> creatives = readCreatives(parser, theAd.getCreatives());
                    theAd.setCreatives(creatives);

                    TVASTLinearAd linearAd = creatives.get(0).getLinearAd();
                    if (linearAd.getSelectedMediaIndex() > -1) {
                        TVASTMediaFile mediaFile = linearAd.getMediaFiles().get(linearAd.getSelectedMediaIndex());
                        theAd.setMediaUrl(mediaFile.getURIMediaFile());
                        theAd.setCreativeWidth(mediaFile.getWidth());
                        theAd.setCreativeHeight(mediaFile.getHeight());
                    } else {
                        theAd.setMediaUrl("");
                    }

                    double duration = parseTimeString(linearAd.getDuration());
                    theAd.setDuration(duration);
                } else {
                    skip(parser);
                }
            }
            theAd.setIs3rdPartyAd(false);
            return theAd;
        }

        private TVASTAd readWrapper(XmlPullParser parser, TVASTAd theAd) throws XmlPullParserException, IOException {

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("AdSystem")) {
                    String adVersion = parser.getAttributeValue(namespace, "version");
                    String adSystem = readText(parser);
                    theAd.setVASTVersion(adVersion);
                    theAd.setAdSystem(adSystem);
                } else if (name.equals("VASTAdTagURI")) {
                    String vastAdTagUri = readText(parser);
                    theAd.setMediaUrl(vastAdTagUri);
                } else if (name.equals("Error")) {
                    String errorUri = readText(parser);
                    theAd.setErrorURI(errorUri);
                } else if (name.equals("Impression")) {
                    String impressionId = parser.getAttributeValue(namespace, "id");
                    String impressionUri = readText(parser);
                    HashMap<String, String> impressions = new HashMap<String, String>();
                    impressions.put(impressionId, impressionUri);
                    theAd.setImpressions(impressions);
                } else if (name.equals("Creatives")) {
                    ArrayList<TVASTCreative> creatives = readCreatives(parser, theAd.getCreatives());
                    theAd.setCreatives(creatives);
                } else {
                    skip(parser);
                }
            }
            theAd.setIs3rdPartyAd(true);
            return theAd;
        }

        private ArrayList<TVASTCreative> readCreatives(XmlPullParser parser, ArrayList<TVASTCreative> creatives) throws XmlPullParserException, IOException {

            ArrayList<TVASTCreative> creativeList = (creatives == null) ? new ArrayList<TVASTCreative>() : creatives;

            int i = 0;
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("Creative")) {

                    String creativeId = parser.getAttributeValue(namespace, "id");
                    String sequence = parser.getAttributeValue(namespace, "sequence");
                    String adId = parser.getAttributeValue(namespace, "adID");
                    String apiFramework = parser.getAttributeValue(namespace, "apiFramework");

                    int iSequence = Integer.parseInt(sequence);
                    TVASTCreative creative = (creatives == null || creatives.get(i) == null) ? new TVASTCreative()
                            : creatives.get(i);
                    creative.setAdId(adId);
                    creative.setSequence(iSequence);
                    creative.setCreativeId(creativeId);
                    creative.setAPIFramework(apiFramework);

                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }

                        String innername = parser.getName();
                        if (innername.equals("Linear")) {
                            TVASTLinearAd linearAd = readLinear(parser, creative.getLinearAd());
                            creative.setLinearAd(linearAd);
                        } else if (innername.equals("CompanionAds")) {
                            List<TVASTCompanionAd> companionAds = readCompanionAds(parser);
                            creative.setCompanionAd(companionAds);
                        } else if (innername.equals("NonLinearAds")) {
                            List<TVASTNonlinearAd> nonLinearAds = readNonLinearAds(parser);

                            TVASTNonlinearAd theEmptyNonlinear = nonLinearAds.get(nonLinearAds.size() - 1);
                            Map<String, String> nonLinearTrackingEvents = theEmptyNonlinear.getTrackingEvents();
                            if (nonLinearTrackingEvents != null) {
                                creative.setNonlinearAdsTrackingEvents(nonLinearTrackingEvents);
                                nonLinearAds.remove(theEmptyNonlinear);
                            }

                            creative.setNonlinearAds(nonLinearAds);
                        } else {
                            skip(parser);
                        }
                    }

                    if (creativeList.contains(creative))
                        creativeList.set(i, creative);
                    else
                        creativeList.add(creative);
                    i++;
                } else {
                    skip(parser);
                }
            }
            return creativeList;
        }

        private TVASTLinearAd readLinear(XmlPullParser parser, TVASTLinearAd linearAd) throws XmlPullParserException, IOException {

            TVASTLinearAd theLinearAd = (linearAd == null) ? new TVASTLinearAd() : linearAd;

            String skipOffset = parser.getAttributeValue(namespace, "skipoffset");
            theLinearAd.setSkipOffset(skipOffset);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("AdParameters")) {
                    String xmlEncoded = parser.getAttributeValue(namespace, "xmlEncoded");
                    String adParameters = readText(parser);
                    theLinearAd.setAdParams(adParameters);
                    boolean isXmlEncoded = Boolean.parseBoolean(xmlEncoded);
                    theLinearAd.setAdParamsEncoded(isXmlEncoded);
                } else if (name.equals("Duration")) {
                    String duration = readText(parser);
                    theLinearAd.setDuration(duration);
                } else if (name.equals("MediaFiles")) {
                    List<TVASTMediaFile> mediaFiles = readMediaFiles(parser);
                    theLinearAd.setMediaFiles(mediaFiles);
                } else if (name.equals("TrackingEvents")) {
                    HashMap<String, String> trackingEvents = readTrackingEvents(parser, theLinearAd.getTrackingEvents());
                    theLinearAd.setTrackingEvents(trackingEvents);
                } else if (name.equals("VideoClicks")) {
                    // VAST Template is ambiguous of these VideoClicks.
                    //HashMap<String, String> videoClicks = new HashMap<String, String>();
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        String innerName = parser.getName();
                        if (innerName.equals("ClickThrough")) {
                            String clickId = parser.getAttributeValue(namespace, "id");
                            String clickThrough = readText(parser);
                            theLinearAd.setClickThroughId(clickId);
                            theLinearAd.setClickThrough(clickThrough);
                        } else if (innerName.equals("ClickTracking")) {
                            String clickId = parser.getAttributeValue(namespace, "id");
                            String clickTracking = readText(parser);
                            theLinearAd.setClickTrackingId(clickId);
                            theLinearAd.setClickTracking(clickTracking);
                        } else if (innerName.equals("CustomClick")) {
                            String clickId = parser.getAttributeValue(namespace, "id");
                            String customClick = readText(parser);
                            theLinearAd.setCustomClickId(clickId);
                            theLinearAd.setCustomClick(customClick);
                        } else {
                            skip(parser);
                        }
                    }
                } else if (name.equals("Icons")) {
                    List<TVASTLinearIcon> linearIcons = readIcons(parser);
                    theLinearAd.setIcons(linearIcons);
                } else {
                    skip(parser);
                }
            }
            return theLinearAd;
        }

        private List<TVASTMediaFile> readMediaFiles(XmlPullParser parser) throws XmlPullParserException, IOException {

            ArrayList<TVASTMediaFile> mediaFiles = new ArrayList<TVASTMediaFile>();
            int selectedBitrate = 0;

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("MediaFile")) {

                    String fileId = parser.getAttributeValue(namespace, "id");
                    String delivery = parser.getAttributeValue(namespace, "delivery");
                    String type = parser.getAttributeValue(namespace, "type");
                    String bitrate = parser.getAttributeValue(namespace, "bitrate");
                    String minBitrate = parser.getAttributeValue(namespace, "minBitrate");
                    String maxBitrate = parser.getAttributeValue(namespace, "maxBitrate");
                    String width = parser.getAttributeValue(namespace, "width");
                    String height = parser.getAttributeValue(namespace, "height");
                    String scalable = parser.getAttributeValue(namespace, "scalable");
                    String maintainAspectRatio = parser.getAttributeValue(namespace, "maintainAspectRatio");
                    String codec = parser.getAttributeValue(namespace, "codec");
                    String apiFramework = parser.getAttributeValue(namespace, "apiFramework");
                    String mediaFileUri = readText(parser);

                    TVASTMediaFile mediaFile = new TVASTMediaFile();
                    mediaFile.setFileId(fileId);
                    mediaFile.setIsStreaming(delivery.equalsIgnoreCase("streaming"));
                    mediaFile.setMimeType(type);
                    mediaFile.setBitrate(Integer.parseInt(bitrate));
                    mediaFile.setWidth(Integer.parseInt(width));
                    mediaFile.setHeight(Integer.parseInt(height));
                    mediaFile.setScalable(Boolean.parseBoolean(scalable));
                    mediaFile.setAPIFramework(apiFramework);
                    mediaFile.setURIMediaFile(mediaFileUri);

                    mediaFiles.add(mediaFile);
                } else {
                    skip(parser);
                }
            }
            return mediaFiles;
        }

        private HashMap<String, String> readTrackingEvents(XmlPullParser parser, HashMap<String, String> trackings) throws XmlPullParserException, IOException {

            HashMap<String, String> trackingEvents = (trackings == null) ? new HashMap<String, String>() : trackings;

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("Tracking")) {
                    String event = parser.getAttributeValue(namespace, "event");
                    String trackingUri = readText(parser);
                    trackingEvents.put(event, trackingUri);
                } else {
                    skip(parser);
                }
            }
            return trackingEvents;
        }

        private List<TVASTLinearIcon> readIcons(XmlPullParser parser) throws XmlPullParserException, IOException {

            ArrayList<TVASTLinearIcon> icons = new ArrayList<TVASTLinearIcon>();

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("Icon")) {
                    String staticResourceUri = null;
                    String staticRscCreativeType = null;
                    String iFrameResourceUri = null;
                    String htmlResourceUri = null;

                    String program = parser.getAttributeValue(namespace, "program");
                    String width = parser.getAttributeValue(namespace, "width");
                    String height = parser.getAttributeValue(namespace, "height");
                    String xPosition = parser.getAttributeValue(namespace, "xPosition");
                    String yPosition = parser.getAttributeValue(namespace, "yPosition");
                    String duration = parser.getAttributeValue(namespace, "duration");
                    String offset = parser.getAttributeValue(namespace, "offset");
                    String apiFramework = parser.getAttributeValue(namespace, "apiFramework");

                    TVASTLinearIcon theIcon = new TVASTLinearIcon();
                    theIcon.setIconProgram(program);
                    Rect iconRect = new Rect(Integer.parseInt(xPosition),
                            Integer.parseInt(yPosition),
                            Integer.parseInt(width) + Integer.parseInt(xPosition),
                            Integer.parseInt(height) + Integer.parseInt(yPosition));
                    theIcon.setIconRect(iconRect);

                    theIcon.setIconDuration(parseTimeString(duration));
                    theIcon.setIconOffset(parseTimeString(offset));
                    theIcon.setApiFramework(apiFramework);

                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }

                        String innername = parser.getName();
                        if (innername.equals("StaticResource")) {
                            staticRscCreativeType = parser.getAttributeValue(namespace, "creativeType");
                            staticResourceUri = readText(parser);
                        } else if (innername.equals("IFrameResource")) {
                            iFrameResourceUri = readText(parser);
                        } else if (innername.equals("HTMLResource")) {
                            htmlResourceUri = readText(parser);
                        } else if (innername.equals("IconClicks")) {
                            HashMap<String, String> iconClicks = new HashMap<String, String>();
                            while (parser.next() != XmlPullParser.END_TAG) {
                                if (parser.getEventType() != XmlPullParser.START_TAG) {
                                    continue;
                                }
                                String innerInnerName = parser.getName();
                                if (innerInnerName.equals("IconClickThrough")) {
                                    String clickThrough = readText(parser);
                                    theIcon.setIconClickThrough(clickThrough);
                                } else if (innerInnerName.equals("IconClickTracking")) {
                                    String clickId = parser.getAttributeValue(namespace, "id");
                                    String clickTracking = readText(parser);
                                    iconClicks.put(clickId, clickTracking);
                                } else {
                                    skip(parser);
                                }
                            }
                            theIcon.setIconClickTrackings(iconClicks);
                        } else if (innername.equals("IconViewTracking")) {
                            String viewTrackingUri = readText(parser);
                            theIcon.setIconViewTracking(viewTrackingUri);
                        } else {
                            skip(parser);
                        }
                    }
                    theIcon.setIconStaticResource(staticResourceUri);
                    theIcon.setIconCreativeType(staticRscCreativeType);
                    theIcon.setIconIFrameResource(iFrameResourceUri);
                    theIcon.setIconHTMLResource(htmlResourceUri);

                    icons.add(theIcon);
                } else {
                    skip(parser);
                }
            }
            return icons;
        }

        private List<TVASTCompanionAd> readCompanionAds(XmlPullParser parser) throws XmlPullParserException, IOException {

            ArrayList<TVASTCompanionAd> companionAds = new ArrayList<TVASTCompanionAd>();

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String required = parser.getAttributeValue(namespace, "required");

                String name = parser.getName();
                if (name.equals("Companion")) {
                    String staticResourceUri = null;
                    String staticRscCreativeType = null;
                    String iFrameResourceUri = null;
                    String htmlResourceUri = null;

                    String id = parser.getAttributeValue(namespace, "id");
                    String width = parser.getAttributeValue(namespace, "width");
                    String height = parser.getAttributeValue(namespace, "height");
                    String assetWidth = parser.getAttributeValue(namespace, "assetWidth");
                    String assetHeight = parser.getAttributeValue(namespace, "assetHeight");
                    String expndWidth = parser.getAttributeValue(namespace, "expandedWidth");
                    String expndHeight = parser.getAttributeValue(namespace, "expandedHeight");
                    String adSlotId = parser.getAttributeValue(namespace, "adSlotID");
                    String apiFramework = parser.getAttributeValue(namespace, "apiFramework");

                    TVASTCompanionAd theCompanion = new TVASTCompanionAd();
                    theCompanion.setIsRequired(Boolean.parseBoolean(required));
                    theCompanion.setCompId(id);
                    theCompanion.setWidth(Integer.parseInt(width));
                    theCompanion.setHeight(Integer.parseInt(height));
                    theCompanion.setAssetWidth(Integer.parseInt(assetWidth));
                    theCompanion.setAssetHeight(Integer.parseInt(assetHeight));
                    theCompanion.setExpandedWidth(Integer.parseInt(expndWidth));
                    theCompanion.setExpandedHeight(Integer.parseInt(expndHeight));
                    theCompanion.setAdSlotId(adSlotId);
                    theCompanion.setAPIFramework(apiFramework);

                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }

                        String innername = parser.getName();
                        if (innername.equals("StaticResource")) {
                            staticRscCreativeType = parser.getAttributeValue(namespace, "creativeType");
                            staticResourceUri = readText(parser);
                            theCompanion.setURIStaticResource(staticResourceUri);
                            theCompanion.setTypeStaticResource(staticRscCreativeType);
                        } else if (innername.equals("IFrameResource")) {
                            iFrameResourceUri = readText(parser);
                            theCompanion.setURIIFrameResource(iFrameResourceUri);
                        } else if (innername.equals("HTMLResource")) {
                            htmlResourceUri = readText(parser);
                            theCompanion.setDataHTMLResource(htmlResourceUri);
                        } else if (innername.equals("AdParameters")) {
                            String xmlEncoded = parser.getAttributeValue(namespace, "xmlEncoded");
                            String adParameters = readText(parser);
                            theCompanion.setAdParams(adParameters);
                            boolean isXmlEncoded = Boolean.parseBoolean(xmlEncoded);
                            theCompanion.setAdParamsEncoded(isXmlEncoded);
                        } else if (innername.equals("AltText")) {
                            String altText = readText(parser);
                            theCompanion.setAltText(altText);
                        } else if (innername.equals("CompanionClickThrough")) {
                            String clickThrough = readText(parser);
                            theCompanion.setClickThrough(clickThrough);
                        } else if (innername.equals("CompanionClickTracking")) {
                            String clickId = parser.getAttributeValue(namespace, "id");
                            String clickTracking = readText(parser);
                            theCompanion.setClickTracking(clickTracking);
                            theCompanion.setClickTrackingId(clickId);
                        } else if (innername.equals("TrackingEvents")) {
                            HashMap<String, String> trackingEvents = readTrackingEvents(parser, theCompanion.getTrackingEvents());
                            theCompanion.setTrackingEvents(trackingEvents);
                        } else {
                            skip(parser);
                        }
                    }
                    companionAds.add(theCompanion);
                } else {
                    skip(parser);
                }
            }
            return companionAds;
        }

        private ArrayList<TVASTNonlinearAd> readNonLinearAds(XmlPullParser parser) throws XmlPullParserException, IOException {
            ArrayList<TVASTNonlinearAd> nonlinearAds = new ArrayList<TVASTNonlinearAd>();

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("NonLinear")) {
                    String staticResourceUri = null;
                    String staticRscCreativeType = null;
                    String iFrameResourceUri = null;
                    String htmlResourceUri = null;

                    String id = parser.getAttributeValue(namespace, "id");
                    String width = parser.getAttributeValue(namespace, "width");
                    String height = parser.getAttributeValue(namespace, "height");
                    String expndWidth = parser.getAttributeValue(namespace, "expandedWidth");
                    String expndHeight = parser.getAttributeValue(namespace, "expandedHeight");
                    String scalable = parser.getAttributeValue(namespace, "scalable");
                    String maintainAspectRatio = parser.getAttributeValue(namespace, "maintainAspectRatio");
                    String minDuration = parser.getAttributeValue(namespace, "minSuggestedDuration");
                    String apiFramework = parser.getAttributeValue(namespace, "apiFramework");

                    TVASTNonlinearAd theNonlinear = new TVASTNonlinearAd();
                    theNonlinear.setAdId(id);
                    theNonlinear.setWidth(Integer.parseInt(width));
                    theNonlinear.setHeight(Integer.parseInt(height));
                    theNonlinear.setExpandedWidth(Integer.parseInt(expndWidth));
                    theNonlinear.setExpandedHeight(Integer.parseInt(expndHeight));
                    theNonlinear.setScalable(Boolean.parseBoolean(scalable));
                    theNonlinear.setKeepAspectRatio(Boolean.parseBoolean(maintainAspectRatio));
                    theNonlinear.setMinDuration(minDuration);
                    theNonlinear.setAPIFramework(apiFramework);

                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }

                        String innername = parser.getName();
                        if (innername.equals("StaticResource")) {
                            staticRscCreativeType = parser.getAttributeValue(namespace, "creativeType");
                            staticResourceUri = readText(parser);
                            theNonlinear.setURIStaticResource(staticResourceUri);
                            theNonlinear.setTypeStaticResource(staticRscCreativeType);
                        } else if (innername.equals("IFrameResource")) {
                            iFrameResourceUri = readText(parser);
                            theNonlinear.setURIIFrameResource(iFrameResourceUri);
                        } else if (innername.equals("HTMLResource")) {
                            htmlResourceUri = readText(parser);
                            theNonlinear.setDataHTMLResource(htmlResourceUri);
                        } else if (innername.equals("AdParameters")) {
                            String xmlEncoded = parser.getAttributeValue(namespace, "xmlEncoded");
                            String adParameters = readText(parser);
                            theNonlinear.setAdParams(adParameters);
                            boolean isXmlEncoded = Boolean.parseBoolean(xmlEncoded);
                            theNonlinear.setAdParamsEncoded(isXmlEncoded);
                        } else if (innername.equals("NonLinearClickThrough")) {
                            String clickThrough = readText(parser);
                            theNonlinear.setClickThrough(clickThrough);
                        } else if (innername.equals("NonLinearClickTracking")) {
                            String clickId = parser.getAttributeValue(namespace, "id");
                            String clickTracking = readText(parser);
                            theNonlinear.setClickTracking(clickTracking);
                            theNonlinear.setClickTrackingId(clickId);
                        } else {
                            skip(parser);
                        }
                    }
                    nonlinearAds.add(theNonlinear);
                } else if (name.equals("TrackingEvents")) {
                    HashMap<String, String> trackingEvents = readTrackingEvents(parser, null);
                    if (trackingEvents != null) {
                        TVASTNonlinearAd theNonlinear = new TVASTNonlinearAd();
                        theNonlinear.setTrackingEvents(trackingEvents);
                        nonlinearAds.add(theNonlinear);
                    }
                } else {
                    skip(parser);
                }
            }
            return nonlinearAds;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... urls) {
            try {
                TVASTAd trmaAd = loadXmlFromNetwork(urls[0], null);
                if (trmaAd != null) {
                    HashMap<String, List<TVASTAd>> adsMap = new HashMap<String, List<TVASTAd>>();
                    List<TVASTAd> videoAds = new ArrayList<TVASTAd>();
                    videoAds.add(trmaAd);
                    adsMap.put("videoAds", videoAds);
                    TVASTVideoAdsManager videoAdsManager = new TVASTVideoAdsManager("manager", mAdRequest, adsMap);
                    return videoAdsManager;
                }
                return null;
            } catch (IOException e) {
                return e;
            } catch (XmlPullParserException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {

            TVASTAdError adError = null;
            if (result instanceof TVASTVideoAdsManager) {
                TVASTAdsLoadedEvent loadedEvent = new TVASTAdsLoadedEvent(mContext, (TVASTVideoAdsManager) result);

                for (TVASTAdsLoadedListener listener : mLoadedListeners) {
                    listener.onAdsLoaded(loadedEvent);
                }
            }
            // error conditions
            else if (result == null) {
                // empty response, send back no creative
                adError = new TVASTAdError(AdErrorType.LOAD, AdErrorCode.UNEXPECTED_LOADING_ERROR, mRootErrorUri);
            }
            else if (result instanceof XmlPullParserException) {
                Log.d("SnakkVASTSDK", "XmlPullParserException: " + ((XmlPullParserException) result).toString());
                String message = ((XmlPullParserException) result).getMessage();
                if (message == null)
                    message = ((XmlPullParserException) result).toString();

                adError = new TVASTAdError(AdErrorType.LOAD, AdErrorCode.UNEXPECTED_LOADING_ERROR, message);
            } else if (result instanceof IOException) {
                Log.d("SnakkVASTSDK", "IOException: " + ((IOException) result).toString());
                String message = ((IOException) result).getMessage();
                if (message == null)
                    message = ((IOException) result).toString();

                adError = new TVASTAdError(AdErrorType.LOAD, AdErrorCode.VAST_LOAD_TIMEOUT, message);
            }

            if (adError != null) {
                doPostback(mRootErrorUri);
                TVASTAdErrorEvent errorEvent = new TVASTAdErrorEvent(mContext, adError);

                for (TVASTAdErrorListener listener : mErrorListeners) {
                    listener.onAdError(errorEvent);
                }
            }
        }

        private TVASTAd loadXmlFromNetwork(String urlString, TVASTAd trmaAd) throws XmlPullParserException, IOException {
            InputStream stream = null;

            VASTParser vastParser = new VASTParser();

            try {
                stream = downloadUrl(urlString);
                trmaAd = vastParser.parseVAST(stream, trmaAd);

                if (trmaAd != null && trmaAd.getIs3rdPartyAd()) {
                    stream.close();
                    stream = null;

                    String epochTime = String.format("%d", System.currentTimeMillis() / 1000);
                    String wrapperUri = trmaAd.getMediaUrl().replace("[timestamp]", epochTime).replace("[TIMESTAMP]", epochTime);
                    trmaAd = loadXmlFromNetwork(wrapperUri, trmaAd);
                }

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            return trmaAd;
        }

        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }
    }

    public void addAdErrorListener(TVASTAdErrorListener errorListener) {
        mErrorListeners.add(errorListener);
    }

    public void addAdsLoadedListener(TVASTAdsLoadedListener loadedListener) {
        mLoadedListeners.add(loadedListener);
    }

    public void removeAdErrorListener(TVASTAdErrorListener errorListener) {
        mErrorListeners.remove(errorListener);
    }

    public void removeAdsLoadedListener(TVASTAdsLoadedListener loadedListener) {
        mLoadedListeners.remove(loadedListener);
    }

    public void requestAds(TVASTAdsRequest adRequest) {
        mAdRequest = adRequest;

        String requestURL = mAdRequest.toString();
        Log.d("Snakk", "request URL: " + requestURL);

        // Process the ad request.
        new DownloadXmlTask().execute(requestURL);
    }

    public TVASTAdsLoader(Context context) {
        mContext = context;
        mAdRequest = null;
        mLoadedListeners = new ArrayList<TVASTAdsLoadedListener>();
        mErrorListeners = new ArrayList<TVASTAdErrorListener>();
    }

    private void doPostback(String uri) {
        final String postbackUri = uri;
        TVASTPostbackTask postbackTask = new TVASTPostbackTask(uri);
        postbackTask.setListener(new TVASTPostbackTask.TVASTPostbackListener() {

            @Override
            public void onSuccess(String data) {
                Log.d("", "Postback:" + postbackUri + "successful.");
            }

            @Override
            public void onFailure(Exception error) {
                TVASTAdError adError = new TVASTAdError(AdErrorType.LOAD, AdErrorCode.UNEXPECTED_LOADING_ERROR, error.getMessage());
                TVASTAdErrorEvent adErrorEvent = new TVASTAdErrorEvent(adError);
                for (TVASTAdErrorListener listener : mErrorListeners) {
                    listener.onAdError(adErrorEvent);
                }
            }
        });
    }
}
