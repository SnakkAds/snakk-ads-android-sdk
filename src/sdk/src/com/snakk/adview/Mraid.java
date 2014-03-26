package com.snakk.adview;

import android.content.pm.ActivityInfo;
import android.os.Build;

/**
 * MRAID related enums/consts/utils
 */
public final class Mraid {

    static enum MraidEvent {
        READY("ready"),
        STATECHANGE("stateChange"),
        SIZECHANGE("sizeChange"),
        VIEWABLECHANGE("viewableChange"),
        ERROR("error");

        public final String value;

        private MraidEvent(String val) {
            this.value = val;
        }

        public static MraidEvent parse(String val) {
            for(MraidEvent state : MraidEvent.values()) {
                if(state.value.equalsIgnoreCase(val)) {
                    return state;
                }
            }
            return null;
        }

    }

    static enum MraidPlacementType {
        INLINE("inline"),
        INTERSTITIAL("interstitial");

        public final String value;

        private MraidPlacementType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static enum MraidState {
      LOADING("loading"),
      DEFAULT("default"),
      RESIZED("resized"),
      EXPANDED("expanded"),
      HIDDEN("hidden");

      public final String value;

      private MraidState(String val) {
        this.value = val;
      }

      public static MraidState parse(String val) {
        for(MraidState state : MraidState.values()) {
          if(state.value.equalsIgnoreCase(val)) {
            return state;
          }
        }
        return null;
      }
    }

    static enum MraidCloseRegionPosition {
        TOP_LEFT("top-left"),
        TOP_CENTER("top-center"),
        TOP_RIGHT("top-right"), // DEFAULT
        CENTER("center"),
        BOTTOM_LEFT("bottom-left"),
        BOTTOM_CENTER("bottom-center"),
        BOTTOM_RIGTH("bottom-right");

        public final String value;

        MraidCloseRegionPosition(String val) {
            value = val;
        }

        public static MraidCloseRegionPosition parse(String val) {
            for(MraidCloseRegionPosition state : MraidCloseRegionPosition.values()) {
                if(state.value.equalsIgnoreCase(val)) {
                    return state;
                }
            }
            return null;
        }
    }

    static enum MraidOrientation {
        PORTRAIT("portrait", (Build.VERSION.SDK_INT >= 9)
                                ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
        LANDSCAE("landscape", (Build.VERSION.SDK_INT >= 9)
                                ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
        NONE("none", ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        public final String value;
        public final int orientation;

        MraidOrientation(String val, int orientation) {
            value = val;
            this.orientation = orientation;
        }

        public static MraidOrientation parse(String val) {
            for(MraidOrientation state : MraidOrientation.values()) {
                if(state.value.equalsIgnoreCase(val)) {
                    return state;
                }
            }
            return null;
        }
    }



    static final String MRAID_JS = "var mraid={getState:function(){return\"loading\"},listeners:[],addEventListener:function(e,t){this.listeners.push({eventName:e,callback:t})}};(function(e,t){function a(e,n){console.debug(\"fireEvent: \"+e+\"(\"+n+\")\");for(var i=0;i<r.length;i++){var s=r[i];if(s.eventName.toLowerCase()==e.toLowerCase()){console.debug(\"calling: \"+s.callback);if(n){s.callback.apply(t,n)}else{s.callback()}}}}function f(e,t,n){if(e!=\"log\"&&s.state==\"hidden\"){var r=\"Made a call to a disposed ad unit\";var i=e;a(\"error\",[r,i]);return}var f=\"NATIVECALL://\"+e;if(n){var l=e+\"-\"+o++;u[l]=n;t[\"__callback\"]=l}if(t){var c=true;for(var h in t){if(t.hasOwnProperty(h)){if(c){f+=\"?\";c=false}else{f+=\"&\"}f+=h+\"=\"+t[h]}}}var p=document.createElement(\"IFRAME\");p.setAttribute(\"src\",f);document.documentElement.appendChild(p);p.parentNode.removeChild(p);p=null}var n=/iphone|ipad|ipod/i.test(window.navigator.userAgent.toLowerCase());if(n){window.console={};console.log=function(e){f(\"log\",{message:e},t)};console.debug=console.info=console.warn=console.error=console.log;console.debug(\"console logging initialized\")}var r=e.listeners||[];var i={width:false,height:false,useCustomClose:false,isModal:true};var s={placementType:\"inline\",isVisible:false,state:\"loading\",screenWidth:false,screenHeight:false,x:false,y:false,height:false,width:false,supportedFeatures:[],maxWidth:false,maxHeight:false};e.getVersion=function(){console.debug(\"getVersion() => '2.0'\");return\"2.0\"};e.getPlacementType=function(){console.debug(\"getPlacement\");return f(\"getPlacementType\")};e.getState=function(){console.debug(\"getState: \"+s.state);return s.state};e.isViewable=function(){var e=s.isVisible;console.debug(\"isViewable() => \"+e);return s.isVisible};e.addEventListener=function(e,t){console.debug(\"addEventListener: \"+e+\", \"+t);r.push({eventName:e,callback:t})};e.removeEventListener=function(e,t){console.debug(\"removeEventListener\");for(var n=r.length-1;n>=0;n--){if(r[n].eventName==e&&(typeof t==\"undefined\"||r[n].callback==t)){console.debug(\"removing {\"+r[n].eventName+\", \"+r[n].callback+\"}\");r.splice(n,1)}}};e.close=function(){console.debug(\"close\");f(\"close\")};e.open=function(e){console.debug(\"open(\"+e+\")\");f(\"open\",{url:encodeURIComponent(e).replace(/%20/g,\"+\")})};e.setExpandProperties=function(e){var t=\"{\";for(var n in e){if(e.hasOwnProperty(n)){t+=\", \"+n+\": \"+e[n]}}t+=\"}\";console.debug(\"setExpandProperties: \"+t);if(\"width\"in e){i.width=e.width}if(\"height\"in e){i.height=e.height}if(\"useCustomClose\"in e){i.useCustomClose=e.useCustomClose}};e.getExpandProperties=function(){if(!i.width){i.width=s.maxWidth;i.height=s.maxHeight}var e={width:i.width,height:i.height,useCustomClose:i.useCustomClose,isModal:i.isModal};console.debug(\"getExpandProperties() => \"+JSON.stringify(e));return e};e.expand=function(t){console.debug(\"expand(\"+t+\")\");var n=e.getExpandProperties();if(typeof t!==\"undefined\"){n[\"url\"]=t}f(\"expand\",n)};e.useCustomClose=function(e){console.debug(\"useCustomClose(\"+e+\")\");i.useCustomClose=e;f(\"useCustomClose\",{useCustomClose:e})};var o=0;var u={};e._nativeResponse=function(e,t){if(typeof t!=\"undefined\"){console.debug(\"id defined!\");if(t in u){var n=u[t];delete u[t];n.apply(e)}}else{var r=\"data: \";for(var i in e){if(e.hasOwnProperty(i)){if(i in s){s[i]=e[i];r+=i+\": \"+e[i]+\" \"}else if(i==\"_fire_event_\"){var o=e[i];a(o.name,o.props)}}}console.debug(r)}};var l={x:0,y:0,width:0,height:0};var c={allowOrientationChange:true,forceOrientation:\"none\"};var h={width:0,height:0,offsetX:0,offsetY:0,customClosePosition:\"top-right\",allowOffscreen:true};e.getCurrentPosition=function(){var e={x:s.x,y:s.y,width:s.width,height:s.height};console.debug(\"getCurrentPosition(\"+JSON.stringify(e)+\")\");return e};e.getDefaultPosition=function(){console.debug(\"getDefaultPosition() => \"+JSON.stringify(l));return{x:l.x,y:l.y,width:l.width,height:l.height}};e.getScreenSize=function(){var e={width:s.screenWidth,height:s.screenHeight};console.debug(\"getScreenSize() => \"+JSON.stringify(e));return e};e.getOrientationProperties=function(){var e={allowOrientationChange:c.allowOrientationChange,forceOrientation:c.forceOrientation};console.debug(\"getOrientationProperties() => \"+JSON.stringify(e));return e};e.setOrientationProperties=function(e){console.debug(\"setOrientationProperties(\"+JSON.stringify(e)+\")\");if(\"allowOrientationChange\"in e){c.allowOrientationChange=e.allowOrientationChange}if(\"forceOrientation\"in e){c.forceOrientation=e.forceOrientation}f(\"setOrientationProperties\",c,t)};e.createCalendarEvent=function(e){console.debug(\"createCalendarEvent(\"+JSON.stringify(e)+\")\");f(\"createCalendarEvent\",e,t)};e.getMaxSize=function(){var e={width:s.maxWidth,height:s.maxHeight};console.debug(\"getMaxSize() => \"+JSON.stringify(e));return e};e.playVideo=function(e){console.debug(\"playVideo(\"+e+\")\");f(\"playVideo\",{url:e},t)};e.getResizeProperties=function(){console.debug(\"getResizeProperties() => \"+JSON.stringify(h));return h};e.setResizeProperties=function(e){console.debug(\"setResizeProperties(\"+JSON.stringify(e)+\")\");h.width=e.width;h.height=e.height;h.offsetX=e.offsetX;h.offsetY=e.offsetY;if(\"customClosePosition\"in e){h.customClosePosition=e.customClosePosition}if(\"allowOffscreen\"in e){h.allowOffscreen=e.allowOffscreen}};e.resize=function(){console.debug(\"resize()\");f(\"resize\",h)};e.storePicture=function(e){console.debug(\"storePicture(\"+e+\")\");f(\"storePicture\",{url:e},t)};e.supports=function(e){var t=s.supportedFeatures.indexOf(e)>-1;console.debug(\"supports(\"+e+\") => \"+t);return t}})(window.mraid)";
}
