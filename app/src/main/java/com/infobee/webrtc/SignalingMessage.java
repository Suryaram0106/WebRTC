package com.infobee.webrtc;

public class SignalingMessage {

    enum Type {
        OFFER,
        ANSWER
    }

    private Type type;
    private String sdp;

    public SignalingMessage() {
        // Empty constructor required for Firebase
    }

    public SignalingMessage(Type type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

    public Type getType() {
        return type;
    }

    public String getSdp() {
        return sdp;
    }
}
