/**
 * This is the Snakk Advertising module.
 *
 * <h1>Overview</h1>
 * The Snakk Advertising SDK provides app monetization functionality.
 *
 * <h3>Build requirements</h3>
 * Android SDK 2.2+ (API level 8) or above
 *
 * <h3>Setup</h3>
 * Update the {@code AndroidManifest.xml} with to include the following permissions.
 * These go outside of the {@code application} tag:
 *
 * <pre>
 * &lt;uses-permission android:name="android.permission.INTERNET"/&gt;
 * uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/&gt;
 * &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;
 *
 * &lt;!-- Optional permissions to enable ad geotargeting
 * &lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/&gt;
 * &lt;uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/&gt;
 * --&gt;
 * </pre>
 *
 * An activity is used to display full screen ads. The {@code activity} should be
 * defined inside of the {@code application} tag:
 *
 * <pre>
 * &lt;!-- inside of your application tag: --&gt;
 * &lt;activity
 *      android:name="com.snakk.advertising.internal.SnakkAdActivity"
 *      android:configChanges="keyboard|keyboardHidden|orientation|screenSize" /&gt;
 * </pre>
 *
 */
package com.snakk.advertising;