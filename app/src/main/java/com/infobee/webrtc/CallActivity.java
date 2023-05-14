package com.infobee.webrtc;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;
import java.util.UUID;

public class CallActivity extends AppCompatActivity{

    private String username = "";
    private String friendsUsername = "";
    private FirebaseAuth firebaseAuth;

    WebView webView;
    private boolean isPeerConnected = false;

    private DatabaseReference firebaseRef ;

    private boolean isAudio = true;
    private boolean isVideo = true;

    String uniqueId;
    String sanitizedUsername,sanitizedFriendname;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        webView = findViewById(R.id.webView);

//        username = getIntent().getStringExtra("username");

        firebaseRef = FirebaseDatabase.getInstance().getReference("users");

        //username = getIntent().getStringExtra("from_name");
        username = user.getPhoneNumber();
        friendsUsername = getIntent().getStringExtra("to_name");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        sanitizedUsername = username.replace(".", "_dot_")
                .replace("#", "_hash_")
                .replace("$", "_dollar_")
                .replace("[", "_leftBracket_")
                .replace("]", "_rightBracket_")
                .replace("+", "_plus_");

        sanitizedFriendname = friendsUsername.replace(".", "_dot_")
                .replace("#", "_hash_")
                .replace("$", "_dollar_")
                .replace("[", "_leftBracket_")
                .replace("]", "_rightBracket_")
                .replace("+", "_plus_");


        System.out.println("FROM:"+ sanitizedUsername);
        System.out.println("To:"+ sanitizedFriendname);

        setupWebView();



        findViewById(R.id.callBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // friendsUsername = ((EditText) findViewById(R.id.friendNameEdit)).getText().toString();

                sendCallRequest();
            }
        });


        findViewById(R.id.toggleAudioBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio = !isAudio;
                callJavascriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
                ((ImageView) findViewById(R.id.toggleAudioBtn)).setImageResource(isAudio ? R.drawable.ic_baseline_mic_none_24 : R.drawable.ic_baseline_mic_off_24);
            }
        });

        findViewById(R.id.toggleVideoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                callJavascriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                ((ImageView) findViewById(R.id.toggleVideoBtn)).setImageResource(isVideo ? R.drawable.ic_baseline_videocam_24 : R.drawable.ic_baseline_videocam_off_24);
            }
        });


    }

    private void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show();
            return;
        }


        firebaseRef.child(sanitizedFriendname).child("incoming").setValue(sanitizedUsername);
        //firebaseRef.child(sanitizedFriendname).child("isAvailable").setValue(true);
        //firebaseRef.child(sanitizedUsername).child("incoming").setValue("someValue");

        firebaseRef.child(sanitizedFriendname).child("isAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Boolean isAvailable = snapshot.getValue(Boolean.class);
                if (isAvailable != null && isAvailable.equals(Boolean.TRUE)) {
                    listenForConnId();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });



    }

    private void listenForConnId() {

        //firebaseRef.child(sanitizedUsername).child("connId").setValue(uniqueId);
        //firebaseRef.child(sanitizedUsername).child("connId").setValue(uniqueId);
        firebaseRef.child(sanitizedFriendname).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;
                switchToControls();
                callJavascriptFunction("javascript:startCall(\"" + snapshot.getValue() + "\")");
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }


    private void setupWebView() {
        //WebView webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.addJavascriptInterface(new MyJavascriptInterface(this),"Android");

        loadVideoCall();
    }

    private void loadVideoCall() {
        String filePath = "file:///android_asset/call.html";
        webView.loadUrl(filePath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                initializePeer();

            }
        });
    }

    private String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    private void initializePeer() {
        uniqueId = getUniqueID();
        callJavascriptFunction("javascript:init(\"" + uniqueId + "\")");
        firebaseRef.child(sanitizedUsername).child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                onCallRequest(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void onCallRequest(String caller) {
        if (caller == null)
        {
            System.out.println("CALLER:"+caller);
            return;
        }

        System.out.println("CALLER:"+caller);

        findViewById(R.id.callLayout).setVisibility(View.VISIBLE);
        TextView incomingCallTxt = findViewById(R.id.incomingCallTxt);
        incomingCallTxt.setText(caller + " is calling...");

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseRef.child(sanitizedUsername).child("connId").setValue(uniqueId);
                firebaseRef.child(sanitizedUsername).child("isAvailable").setValue(true);

                findViewById(R.id.callLayout).setVisibility(View.GONE);
                switchToControls();
            }
        });

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseRef.child(sanitizedUsername).child("incoming").setValue(null);
                findViewById(R.id.callLayout).setVisibility(View.GONE);
            }
        });
    }

    private void switchToControls() {
        findViewById(R.id.inputLayout).setVisibility(View.GONE);
        findViewById(R.id.callControlLayout).setVisibility(View.VISIBLE);
    }

    private void callJavascriptFunction(String functionString) {
        webView.post(() -> webView.evaluateJavascript(functionString, null));
    }

    public void onPeerConnected() {
        isPeerConnected = true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        firebaseRef.child(sanitizedUsername).setValue(null);
        webView.loadUrl("about:blank");
        super.onDestroy();
    }
}





