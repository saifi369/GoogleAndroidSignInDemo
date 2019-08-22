package com.saifi369.googleandroidsignindemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    public static final int SIGN_IN_REQUEST_CODE = 1001;
    public static final String TAG = "MyTag";
    private GoogleSignInClient mGoogleSignInClient;

    private TextView mOutputText;
    private Button mBtnSignOut;
    private SignInButton mBtnGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputText = findViewById(R.id.tv_output);
        mBtnSignOut = findViewById(R.id.btn_signout);
        mBtnGoogleSignIn= findViewById(R.id.signInButton);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
//                        .requestProfile()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(account);

        mBtnGoogleSignIn.setOnClickListener(this::signIn);
        mBtnSignOut.setOnClickListener(this::signOut);
    }

    private void signOut(View view) {

        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
                            updateUI(GoogleSignIn.getLastSignedInAccount(MainActivity.this));

                        }else{
                            Toast.makeText(MainActivity.this, "some error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void signIn(View view) {

        Intent singInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, SIGN_IN_REQUEST_CODE);

    }

    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {
            mBtnSignOut.setVisibility(View.VISIBLE);
            mOutputText.setText(account.getDisplayName() + "\n"+
                    account.getEmail());
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
            updateUI(account);

        } catch (ApiException e) {
            mOutputText.setText(GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            Log.d(TAG, "handleGoogleSignIn: Error status code: "+e.getStatusCode());
            Log.d(TAG, "handleGoogleSignIn: Error status message: "
                    +GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }

    }
}
