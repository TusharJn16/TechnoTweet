package com.collegeapp.collegeapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.collegeapp.collegeapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {
    RelativeLayout RelativeLayout;
    @BindView(R.id.collegeid)
    EditText collegeid;
    @BindView(R.id.sigIn)
    TextView sigIn;
    ActionCodeSettings actionCodeSettings;

    private static final String TAG = "PasswordlessSignIn";
    private static final String KEY_PENDING_EMAIL = "key_pending_email";

    private FirebaseAuth mAuth;
    private String mPendingEmail;
    private String mEmailLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()==null)
        {
            Toast.makeText(this,"its",Toast.LENGTH_LONG).show();
        }
        else
        {
            startActivity(new Intent(Login.this,mainActivity.class));
            finish();
        }
        if (savedInstanceState != null) {
            mPendingEmail = savedInstanceState.getString(KEY_PENDING_EMAIL, null);
            collegeid.setText(mPendingEmail);
        }



        //checkIntent(getIntent());
        sigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendLinkClicked();
            }
        });
    }


//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        checkIntent(intent);
//    }

    private void checkIntent(@Nullable Intent intent) {
        if (intentHasEmailLink(intent)) {
            mEmailLink = intent.getData().toString();

            if(mAuth.getCurrentUser()!=null)
            {
                Toast.makeText(this,"user",Toast.LENGTH_LONG).show();
            }
           startActivity(new Intent(Login.this,mainActivity.class));
           finish();
        }
    }

    private boolean intentHasEmailLink(@Nullable Intent intent) {
        if (intent != null && intent.getData() != null) {
            String intentData = intent.getData().toString();
            if (mAuth.isSignInWithEmailLink(intentData)) {
                Toast.makeText(this,intentData,Toast.LENGTH_LONG).show();
                return true;
            }
        }

        return false;
    }

    private void onSendLinkClicked() {

        String email = collegeid.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(findViewById(R.id.viewSnack), "Field can't be Empty", Snackbar.LENGTH_LONG).show();
            return;
        }

        email=email+"@technonjr.org";
        sendSignInLink(email);
    }

    private void sendSignInLink(String email) {

        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        false, /* install if not available? */
                        null   /* minimum app version */)
                .setHandleCodeInApp(true)
                .setUrl("https://com.collegeapp.collegeapp/finishSignUp?cartId=1234")
                .build();
//
//        mAuth = FirebaseAuth.getInstance();
        mAuth.sendSignInLinkToEmail(email, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()) {
                            Log.d(TAG, "Link sent");
                            Snackbar.make(findViewById(R.id.viewSnack), "Sign-in link sent!", Snackbar.LENGTH_LONG).show();

                        } else {
                            Exception e = task.getException();
                            Log.d(TAG, "Could not send link", e);
                            Snackbar.make(findViewById(R.id.viewSnack), "Failed to send link.", Snackbar.LENGTH_LONG).show();
                            //showSnackbar("");

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                collegeid.setError("Invalid email address.");
                            }
                        }
                    }
                });

    }


}
