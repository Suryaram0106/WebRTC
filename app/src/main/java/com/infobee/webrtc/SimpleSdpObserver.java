package com.infobee.webrtc;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SimpleSdpObserver implements SdpObserver {

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        // Handle the creation of SDP offer/answer
    }

    @Override
    public void onSetSuccess() {
        // Handle the setting of local/remote description
    }

    @Override
    public void onCreateFailure(String s) {
        // Handle the failure of SDP creation
    }

    @Override
    public void onSetFailure(String s) {
        // Handle the failure of setting local/remote description
    }
}
