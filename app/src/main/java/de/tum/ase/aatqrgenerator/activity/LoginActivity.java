package de.tum.ase.aatqrgenerator.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;

import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.service.ApiService;
import de.tum.ase.aatqrgenerator.service.UserService;

/**
 * A login screen that offers login via google account
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ApiService apiService;
    private UserService userService;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        userService = new UserService(this);
        userService.setSignInListener(new UserService.UserServiceSignInListener() {
            @Override
            public void userSignInSuccess(GoogleSignInAccount account) {
                progress.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void userSignInFailure() {
                progress.dismiss();
            }
        });

        SignInButton signInButton = (SignInButton) findViewById(R.id.google_signin_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = ProgressDialog.show(LoginActivity.this, "Google Sign in",
                        "Signing in, please wait...", true);
                userService.signIn();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == UserService.RC_SIGN_IN) {
            userService.onActivityResult(data);
        }
    }
}

