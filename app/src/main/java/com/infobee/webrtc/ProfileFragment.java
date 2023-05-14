package com.infobee.webrtc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private TextView emailTextView, nameTextView, phoneTextView;
    private AppCompatImageView profileImageView;

    private FirebaseAuth firebaseAuth;
    String emailFromMain;
    String s1,s2,s3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        emailTextView = view.findViewById(R.id.emailTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        profileImageView = view.findViewById(R.id.ivPic);

        emailFromMain = getArguments().getString("email");
        s1 = getArguments().getString("NAME");
        s2 = getArguments().getString("EMAIL");
        s3 = getArguments().getString("PHOTO");


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            // Set phone number
            String phoneNumber = user.getPhoneNumber();
            phoneTextView.setText(phoneNumber);

            System.out.println("qq:"+emailFromMain);
            System.out.println("qq:"+phoneNumber);
            // Set profile picture (if available)
            // You will need to implement your own logic to fetch and display the profile picture
            // using the user's information or a separate profile picture storage mechanism.
        }


        System.out.println("qqq:"+emailFromMain);
        System.out.println("qqq:"+s1);
        System.out.println("qqq:"+s2);
        System.out.println("qqq:"+s3);

        if(s1.equals("")){

            if (emailFromMain != null) {
                String[] parts = emailFromMain.split("@");
                nameTextView.setText(parts[0]);
            }

            emailTextView.setText(emailFromMain);

            Picasso.get()
                    .load(R.drawable.ic_place_holder)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(profileImageView);
        }

        else {
            nameTextView.setText(s1);
            emailTextView.setText(s2);
            Picasso.get()
                    .load(s3)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(profileImageView);
        }

    }

}
