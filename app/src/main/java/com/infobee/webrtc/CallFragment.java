package com.infobee.webrtc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.Manifest;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.webrtc.DataChannel;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionDependencies;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CallFragment extends Fragment implements AdapterListener{

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    List<Contact> contacts = new ArrayList<>();

    private final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private final int requestCode = 1;

    String emailFromMain;
    String s1,s2,s3;


    private DatabaseReference databaseReference;



    private static final String TAG = "CallFragment";
    DataChannel mainDataChannel;
    PeerConnection mainPeerConnection;
    PeerConnectionFactory factory;

    private static final int READ_CONTACTS_REQUEST_CODE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        emailFromMain = getArguments().getString("email");
        s1 = getArguments().getString("NAME");
        s2 = getArguments().getString("EMAIL");
        s3 = getArguments().getString("PHOTO");


        // Initialize the Firebase Realtime Database reference in the onCreate method or any suitable initialization method
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, proceed with accessing the contacts
            updateContacts();
        } else {
            // Permission is not granted, request the permission from the user
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
        }


        return  view;
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
    }

    private boolean isPermissionGranted() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, proceed with accessing the contacts
                System.out.println("SSSSS");
                updateContacts();
            } else {
                // Permission is denied, handle the scenario where the user has denied the permission
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Contact> getContacts() {
        contacts = new ArrayList<>();

        Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Contact contact = new Contact(name, phoneNumber);
                contacts.add(contact);
            }
            cursor.close();
        }

        return contacts;
    }

    @Override
    public void onClickCall(int position) {

        Contact contact = contacts.get(position);
        //Toast.makeText(getContext(), contact.getName(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), contact.getPhoneNumber(), Toast.LENGTH_SHORT).show();
        //startWebRTCCall(contact.getPhoneNumber());

        if (!isPermissionGranted()) {
            askPermissions();
        }

        FirebaseApp.initializeApp(getActivity());

        Intent intent = new Intent(getActivity(), CallActivity.class);

        intent.putExtra("from_name", s2);
        intent.putExtra("to_name", contact.getPhoneNumber());
//        if(s1.equals("")){
//            if (emailFromMain != null) {
//                String[] parts = emailFromMain.split("@");
//                intent.putExtra("from_name", parts[0]);
//            }
//        }
//        else {
//            intent.putExtra("from_name", s1);
//        }

        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clean up any resources or references here

        // Set RecyclerView and adapter references to null
        recyclerView = null;
        contactAdapter = null;

        // Clear the contacts list
        if (contacts != null) {
            contacts.clear();
            contacts = null;
        }
    }

//    private void startWebRTCCall(String phoneNumber) {
//
//        initializePeerConnectionFactory();
//        initializeMyPeerConnection(); // Connection Initialization.
//        startConnection(); //Getting the offer
//
//
//
//
//        String callId = databaseReference.push().getKey();
//
//        // Generate the SDP offer from the local peer connection
//        mainPeerConnection.createOffer(new SimpleSdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sdp) {
//                // Set the local description
//                mainPeerConnection.setLocalDescription(new SimpleSdpObserver(), sdp);
//
//                // Create a signaling message containing the SDP offer
//                SignalingMessage signalingMessage = new SignalingMessage(SignalingMessage.Type.OFFER, sdp.description);
//
//                // Store the SDP offer in the Firebase Realtime Database under the call ID
//                databaseReference.child(callId).setValue(signalingMessage);
//
//                // Listen for changes in the Firebase Realtime Database under the call ID to receive the remote SDP answer
//                databaseReference.child(callId).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Retrieve the signaling message from the database
//                        SignalingMessage remoteSignalingMessage = dataSnapshot.getValue(SignalingMessage.class);
//
//                        if (remoteSignalingMessage != null && remoteSignalingMessage.getType() == SignalingMessage.Type.ANSWER) {
//                            // Create a SessionDescription object from the remote SDP answer
//                            SessionDescription remoteDescription = new SessionDescription(SessionDescription.Type.ANSWER, remoteSignalingMessage.getSdp());
//
//                            // Set the remote description for the peer connection
//                            mainPeerConnection.setRemoteDescription(new SimpleSdpObserver(), remoteDescription);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // Handle the cancellation or failure of retrieving the remote SDP answer from the database
//                    }
//                });
//            }
//
//            @Override
//            public void onCreateFailure(String s) {
//                // Handle the failure of creating the SDP offer
//            }
//        }, new MediaConstraints());
//
//
//
//
//
//        }
//
//
//
//    private void initializePeerConnectionFactory() {
//
//        PeerConnectionFactory.InitializationOptions initializationOptions =
//                PeerConnectionFactory.InitializationOptions.builder(getContext())
//                        .setEnableInternalTracer(true)
//                        .createInitializationOptions();
//        PeerConnectionFactory.initialize(initializationOptions);
//
//        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//        factory = PeerConnectionFactory.builder()
//                .setOptions(options)
//                .createPeerConnectionFactory();
//
//    }
//
//    private void startConnection() {
//        Log.d(TAG, "startConnection: Starting Connection...");
//        //CreateOffer fires the request to get ICE candidates and finish the SDP. We can listen to all these events on the corresponding observers.
//        mainPeerConnection.createOffer(new SimpleSdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {
//                Log.d(TAG, "onCreateSuccess: " + sessionDescription.description);
//                mainPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
//            }
//
//            @Override
//            public void onCreateFailure(String s) {
//                Log.e(TAG, "onCreateFailure: FAILED:" + s);
//            }
//        }, new MediaConstraints());
//        Log.d(TAG, "startConnection: Start Connection end");
//    }
//
//    private void initializeMyPeerConnection() {
//        Log.d(TAG, "initializeMyPeerConnection: Starting Initialization...");
//        mainPeerConnection = createPeerConnection(factory);
//
//        mainDataChannel = mainPeerConnection.createDataChannel("sendDataChannel", new DataChannel.Init());//Setting the data channel.
//        mainDataChannel.registerObserver(new DataChannel.Observer() {
//            @Override
//            public void onBufferedAmountChange(long l) {
//
//            }
//
//            @Override
//            public void onStateChange() {
//                //Data channel state change
//                Log.d(TAG, "onStateChange: " + mainDataChannel.state().toString());
//            }
//
//            @Override
//            public void onMessage(DataChannel.Buffer buffer) {
//                Toast.makeText(getContext(), "Got the message!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        Log.d(TAG, "initializeMyPeerConnection: Finished Initializing.");
//    }
//
//    private PeerConnection createPeerConnection(PeerConnectionFactory factory) {
//        List<PeerConnection.IceServer> iceServers = new LinkedList<>();
//        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
//        PeerConnection.RTCConfiguration rtcConfiguration = new PeerConnection.RTCConfiguration(iceServers);
//        PeerConnection.Observer pcObserver = new MyPeerConnectionObserver(TAG, mainPeerConnection);
//
//        return factory.createPeerConnection(rtcConfiguration, pcObserver);
//    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for READ_CONTACTS permission and update the RecyclerView if granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            updateContacts();
        }
    }

    private void updateContacts() {

        contacts = getContacts();
        contactAdapter = new ContactAdapter(CallFragment.this,contacts);
        recyclerView.setAdapter(contactAdapter);
    }

}
