Snakk Android SDK
=================

Version 2.0.0

This is the Android SDK for the Snakk! mobile ad network. Go to http://snakkads.com/ for more details and to sign up.

###[Download Snakk SDK](https://github.com/SnakkAds/snakk-ads-android-sdk/raw/master/dist/SnakkSDK.zip)<br/>
###[Example project source](https://github.com/SnakkAds/snakk-ads-android-sdk/tree/master/src/example)


Requrements:
------------
Android SDK 2.2+ (API level 8) or above


Usage:
------
*We've streamlined our API as of v1.8.0, but still support previous integrations.
 Check the [Old SDK Docs](https://github.com/SnakkAds/snakk-ads-android-sdk/blob/master/README_LEGACY.md)
for legacy API documentation.*

* To install, extract the [Snakk SDK Archive](https://github.com/SnakkAds/snakk-ads-android-sdk/raw/master/dist/SnakkSDK.zip) into your project's `/libs` folder, and add `SnakkSDK.jar` into the project's build path:

* Set `SnakkSDK.jar` to be exported as part of your apk file:

* Update your `AndroidManifest.xml` to include the following permissions and activity:

````xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>

<!-- Optional permissions to enable ad geotargeting
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
-->

<!-- inside of the application tag: -->
<activity
    android:name="com.snakk.adview.AdActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />

````
See [AndroidManifest.xml](https://github.com/SnakkAds/snakk-ads-android-sdk/blob/master/src/example/AndroidManifest.xml) for an example manifest file.

**NOTE:** Zones correspond to a specific ad type, which is specified through the Snakk dashboard.  Please ensure that you use the correct Zone ID for your ad units or you may experience un-expected results.

A sample project is included in this repo.  See [Example Code](https://github.com/SnakkAds/snakk-ads-android-sdk/tree/master/src/example) for a live demo.

AdPrompt Usage
--------------
AdPrompts are a simple ad unit designed to have a native feel. The user is given the option to download an app, and if they accept, they are taken to the app within the app marketplace.

*Example Usage*
````java
import com.snakk.advertising.*;

// ...

SnakkAdPrompt adPrompt = SnakkAdvertising.get().getAdPromptForZone(this, "YOUR_ADPROMPT_ZONE_ID");
adPrompt.show();
````

Advanced implementation can be found in the [Example Code](https://github.com/SnakkAds/snakk-ads-android-sdk/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)


Banner Usage
------------
Banners are inline ads that are shown alongside your apps interface.

*Xml Only Usage*
Add this in you layout xml:
````xml
<!-- Add banner to your layout xml -->
<!-- this will cause a 320x50 ad to be created, automatically kicking off ad rotation -->
<com.snakk.advertising.SnakkBannerAdView
    android:id="@+id/bannerAd"
    android:layout_width="320dp"
    android:layout_height="50dp"
    zone="YOUR_ZONE_ID" />
````

*Example Code Usage*
Add this in you layout xml: (note that "zone" is not specified)
````xml
<!-- Add banner to your layout xml -->
<!-- this will cause a 320x50 ad to be created, automatically kicking off ad rotation -->
<com.snakk.advertising.SnakkBannerAdView
    android:id="@+id/bannerAd"
    android:layout_width="320dp"
    android:layout_height="50dp" />
````

Add this to your activity:
````java
import com.snakk.advertising.*;

// ...

SnakkBannerAdView bannerAdView = (SnakkBannerAdView)findViewById(R.id.bannerAd);
bannerAdView.startRequestingAdsForZone("YOUR_BANNER_ZONE_ID");
````

Advanced implementation can be found in the [Example Code](https://github.com/SnakkAds/snakk-ads-android-sdk/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)


Interstitial Usage
------------------
Interstitials are best used at discrete stopping points in your app's flow, such as at the end of a game level, or when the player dies.

*Example Usage*
````java
import com.snakk.advertising.*;

// ...

SnakkInterstitialAd interstitialAd = SnakkAdvertising.get().getInterstitialAdForZone(this, "YOUR_INTERSTITIAL_ZONE_ID");
interstitialAd.show();
````

Advanced implementation can be found in the [Example Code](https://github.com/SnakkAds/snakk-ads-android-sdk/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)


Video Ads Usage
----------------
Video ads are interstitials that play a video.  They are best used at discrete
stopping points in your app's flow, such as at the end of a game level, or when the player dies.

*Example Usage*
````java
import com.snakk.advertising.*;

// ...

SnakkVideoInterstitialAd videoAd = SnakkAdvertising.get().getVideoInterstitialAdForZone(this, "YOUR_VIDEO_ZONE_ID");
videoAd.show();
````
Advanced implementation can be found in the [Example Code](https://github.com/SnakkAds/snakk-ads-android-sdk/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)
