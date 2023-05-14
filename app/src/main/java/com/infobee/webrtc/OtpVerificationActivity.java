package com.infobee.webrtc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerificationActivity extends AppCompatActivity {


        private EditText otpEditText;
        private Button verifyButton;

        private FirebaseAuth firebaseAuth;
        private String verificationId;
        String email;
        String s1,s2,s3;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_otp_verification);

            otpEditText = findViewById(R.id.editTextOtp);
            verifyButton = findViewById(R.id.buttonVerify);

            firebaseAuth = FirebaseAuth.getInstance();

            // Retrieve the verification ID from the intent
            verificationId = getIntent().getStringExtra("verificationId");
            email = getIntent().getStringExtra("email");

            s1=getIntent().getStringExtra("NAME");
            s2=getIntent().getStringExtra("EMAIL");
            s3=getIntent().getStringExtra("PHOTO");

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String otp = otpEditText.getText().toString();
                    if (otp.isEmpty()) {
                        Toast.makeText(OtpVerificationActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            });
        }

        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                // User successfully authenticated
                                Toast.makeText(OtpVerificationActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                                // Proceed to the next activity

                                System.out.println("SSSS:"+firebaseAuth.getCurrentUser());
                                Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                intent.putExtra("Email",email);


                                intent.putExtra("NAME", s1);
                                intent.putExtra("EMAIL", s2);
                                intent.putExtra("PHOTO", s3);

                                startActivity(intent);
                                finish();
                            } else {
                                // Verification failed
                                Toast.makeText(OtpVerificationActivity.this, "Verification failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

}
