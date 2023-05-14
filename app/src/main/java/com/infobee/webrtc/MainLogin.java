package com.infobee.webrtc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
//import com.google.firebase.appcheck.FirebaseAppCheck;
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton,signUpButton;

    private SignInButton signinButton;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private String verificationId;
    String Email="test";

    String name="" ;
    String email="" ;
    String photoUrl="" ;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";

    @Override
    public void onBackPressed() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);



        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signUpButton = findViewById(R.id.buttonSignUp);

        //FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        //firebaseAppCheck.installAppCheckProviderFactory(
         //       PlayIntegrityAppCheckProviderFactory.getInstance());

        firebaseAuth = FirebaseAuth.getInstance();

        signinButton = findViewById(R.id.google_sign_in_button);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the sign-in method here

                signIn();

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainLogin.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseAuth.getInstance().signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                    name="" ;
                    email="" ;
                    photoUrl="" ;

                    loginWithUsernameAndPassword(username, password);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainLogin.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseAuth.getInstance().signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                    name="" ;
                    email="" ;
                    photoUrl="" ;

                    signUpWithUsernameAndPassword(username, password);
                }
            }
        });

        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This method will be called automatically if the verification is completed without entering the OTP manually
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Handle verification failure
                Toast.makeText(MainLogin.this, "Phone number verification failed. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                // Go to OTP verification activity and pass verificationId
                Intent intent = new Intent(MainLogin.this, OtpVerificationActivity.class);

                intent.putExtra("verificationId", verificationId);

                    intent.putExtra("NAME", name);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("PHOTO", photoUrl);
                    intent.putExtra("email", Email);





                startActivity(intent);
            }

        };


   /*     if (Util.hasInternetAccess(getApplicationContext())) {

            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            // Initialize Firebase Auth
           // mAuth = FirebaseAuth.getInstance();

            // Check if user is already signed in
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {


                currentUser.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User profile data refreshed successfully
                        name = currentUser.getDisplayName();
                        email = currentUser.getEmail();
                        System.out.println("Name: " + name);
                        System.out.println("Email: " + email);
                    } else {
                        // Failed to refresh user profile data
                        Exception exception = task.getException();
                        System.out.println("Error: " + exception.getMessage());
                    }
                });

                // User is already signed in, proceed to next activity
                // You can access the user's basic profile information like this:
//                name = currentUser.getDisplayName();
//                email = currentUser.getEmail();
//                System.out.println("eeee:"+name);
//                System.out.println("eeee:"+email);
//                photoUrl = currentUser.getPhotoUrl().toString();
//                Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                // TODO: Add code to proceed to next activity

                promptUserForPhoneNumber();

            }
        }
        else{
            Toast.makeText(this, "No Internet Connection " , Toast.LENGTH_SHORT).show();
        }*/
    }


    private void signIn() {

        if (Util.hasInternetAccess(getApplicationContext())){


            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);


        }
        else{
            Toast.makeText(this, "No Internet Connection " , Toast.LENGTH_SHORT).show();
        }


    }


    private String promptUserForPhoneNumber() {
        final String[] phoneNumber = {""}; // Create a string array to hold the phone number

        // Create an AlertDialog to prompt the user for their phone number
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Phone Number");

        // Create an EditText view to allow the user to enter their phone number
        final EditText editTextPhoneNumber = new EditText(this);
        editTextPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(editTextPhoneNumber);

        // Set up the positive button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the phone number entered by the user and assign it to the phoneNumber array
                phoneNumber[0] = editTextPhoneNumber.getText().toString().trim();

                sendVerificationCode(phoneNumber[0]);
                System.out.println("phoneNumber:"+phoneNumber[0]);
            }
        });

        // Set up the negative button click listener (optional)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancel button click if needed
            }
        });

        // Show the AlertDialog
        builder.show();



        return phoneNumber[0];
    }

    private void sendVerificationCode(String phoneNumber) {


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(verificationCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);



        //        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,
//                60, // Timeout duration in seconds
//                TimeUnit.SECONDS,
//                this,
//                verificationCallbacks
//        );

    }

    private void loginWithUsernameAndPassword(String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User successfully authenticated with username and password
                            Toast.makeText(MainLogin.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                            // Proceed to phone number verification

                            Email = username;

                            promptUserForPhoneNumber();
                            //sendVerificationCode(phoneNumber);
                        } else {
                            // Authentication failed
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainLogin.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainLogin.this, "Authentication failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void signUpWithUsernameAndPassword(String username, String password) {
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User sign-up successful
                            Toast.makeText(MainLogin.this, "Sign-up successful", Toast.LENGTH_SHORT).show();
                            // Proceed to phone number verification
                            Email = username;
                            String phoneNumber = promptUserForPhoneNumber();
                            //sendVerificationCode(phoneNumber);
                        } else {
                            // Sign-up failed
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                // User already exists
                                Toast.makeText(MainLogin.this, "User already exists with this email", Toast.LENGTH_SHORT).show();
                            } else {
                                // Other sign-up errors
                                Toast.makeText(MainLogin.this, "Sign-up failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User successfully authenticated with the phone number
                            Toast.makeText(MainLogin.this, "Phone number verification successful", Toast.LENGTH_SHORT).show();
                            // Proceed to the next activity or perform any required actions
                            // For example, you can start the MainActivity
                            startActivity(new Intent(MainLogin.this, MainActivity.class));
                            finish(); // Optional: Close the LoginActivity
                        } else {
                            // Phone number verification failed
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainLogin.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainLogin.this, "Phone number verification failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sign in failed, please try again later", Toast.LENGTH_SHORT).show();
                }


            }


        }


    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //updateUI(user);
                            name = user.getDisplayName();
                            email = user.getEmail();
                            photoUrl = user.getPhotoUrl().toString();
//                            Intent intent = new Intent(MainLogin.this, OtpVerificationActivity.class);
//                            intent.putExtra("NAME", name);
//                            intent.putExtra("EMAIL", email);
//                            intent.putExtra("PHOTO", photoUrl);
//                            Toast.makeText(getApplicationContext(), "Welcome " + name, Toast.LENGTH_SHORT).show();
//                            startActivity(intent);
//                            finish();

                            promptUserForPhoneNumber();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainLogin.this, "Connection failed.",
                Toast.LENGTH_SHORT).show();

    }
}
