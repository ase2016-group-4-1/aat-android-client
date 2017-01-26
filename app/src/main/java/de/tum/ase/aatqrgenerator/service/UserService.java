package de.tum.ase.aatqrgenerator.service;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.tum.ase.aatqrgenerator.support.AppConstants;

public class UserService implements GoogleApiClient.OnConnectionFailedListener {
    public interface UserServiceSignInListener {
        void userSignInSuccess(GoogleSignInAccount account);
        void userSignInFailure();
    }

    public interface UserServiceSignOutListener {
        void userSignOutSuccess();
        void userSignOutFailure();
    }

    private static final String TAG = "UserService";
    public static final int RC_SIGN_IN = 1000;

    public static GoogleSignInAccount currentAccount = null;

    private FragmentActivity activity;
    private GoogleApiClient googleApiClient;
    private UserServiceSignInListener signInListener;
    private UserServiceSignOutListener signOutListener;

    public UserService(FragmentActivity activity){
        this.activity = activity;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(AppConstants.OAUTH_WEB_CLIENT_ID)
                .requestServerAuthCode(AppConstants.OAUTH_WEB_CLIENT_ID)
                .build();

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.getErrorMessage());
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if(status.isSuccess()) {
                            currentAccount = null;
                            signOutListener.userSignOutSuccess();
                        } else {
                            signOutListener.userSignOutFailure();
                        }
                    }
                });
    }

    public void silentSignIn() {
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            handleSignInResult(pendingResult.get());
        } else {
            // There's no immediate result ready, wait for the async callback.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    public void onActivityResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct != null) {
                currentAccount = acct;
                signInListener.userSignInSuccess(acct);
            }
        } else {
            // Signed out, show unauthenticated UI.
            currentAccount = null;
            signInListener.userSignInFailure();
        }
    }

    public void setSignInListener(UserServiceSignInListener signInListener) { this.signInListener = signInListener; }

    public void setSignOutListener(UserServiceSignOutListener signOutListener) { this.signOutListener = signOutListener; }
}
