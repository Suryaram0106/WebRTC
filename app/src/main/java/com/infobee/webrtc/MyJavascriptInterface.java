package com.infobee.webrtc;

import android.webkit.JavascriptInterface;

public class MyJavascriptInterface {

    private CallActivity callActivity;

    public MyJavascriptInterface(CallActivity callActivity) {
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected() {
        callActivity.onPeerConnected();
    }

}



//class JavascriptInterface(val callActivity: CallActivity) {
//
//    @JavascriptInterface
//    public fun onPeerConnected() {
//        callActivity.onPeerConnected()
//    }
//
//}
