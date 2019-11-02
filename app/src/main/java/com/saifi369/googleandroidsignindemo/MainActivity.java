package com.saifi369.googleandroidsignindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int SIGN_IN_REQUEST_CODE = 1001;
    public static final String TAG = "MyTag";
    private GoogleSignInClient mGoogleSignInClient;

    private TextView mOutputText;
    private Button mBtnSignOut;
    private SignInButton mBtnGoogleSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputText = findViewById(R.id.tv_output);
        mBtnSignOut = findViewById(R.id.btn_signout);
        mBtnGoogleSignIn= findViewById(R.id.signInButton);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
//                        .requestProfile()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(mAuth.getCurrentUser());

        mBtnGoogleSignIn.setOnClickListener(this::signIn);
        mBtnSignOut.setOnClickListener(this::signOut);
    }

    private void signOut(View view) {

//        mGoogleSignInClient.revokeAccess()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
//                            updateUI(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
//
//                        }else{
//                            Toast.makeText(MainActivity.this, "some error", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        mAuth.signOut();
        updateUI(mAuth.getCurrentUser());

    }

    private void signIn(View view) {

        Intent singInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, SIGN_IN_REQUEST_CODE);

    }

    private void updateUI(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            mBtnSignOut.setVisibility(View.VISIBLE);
            mOutputText.setText(firebaseUser.getDisplayName() + "\n" +
                    firebaseUser.getEmail());
            mBtnGoogleSignIn.setVisibility(View.GONE);
        }else{
            mBtnSignOut.setVisibility(View.GONE);
            mOutputText.setText("User is not logged in");
            mBtnGoogleSignIn.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE){

            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleGoogleSignIn(accountTask);
        }
    }

    private void handleGoogleSignIn(Task<GoogleSignInAccount> accountTask) {

        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);

            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            mOutputText.setText(GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            Log.d(TAG, "handleGoogleSignIn: Error status code: "+e.getStatusCode());
            Log.d(TAG, "handleGoogleSignIn: Error status message: "
                    +GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
