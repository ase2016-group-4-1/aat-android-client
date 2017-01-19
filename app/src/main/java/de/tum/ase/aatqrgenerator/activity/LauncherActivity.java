package de.tum.ase.aatqrgenerator.activity;

/**
 * Created by Dat on 15.1.2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.tum.ase.aatqrgenerator.support.AppConstants;

public class LauncherActivity extends Activity {

    private static int LOGIN_REQUEST = 1000;
    private static int MAIN_ACTIVITY_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchCorrectActivity();
        //setContentView(R.layout.activity_launcher);
    }

    private void launchCorrectActivity() {
        if(userLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, MAIN_ACTIVITY_REQUEST);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
    }

    private boolean userLoggedIn() {
        return getSharedPreferences(AppConstants.CUSTOM_PREFERENCES, MODE_PRIVATE)
                .getBoolean(AppConstants.USER_LOGGED_IN, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                launchCorrectActivity();
            } else {
                finish();
            }
        } else if(requestCode == MAIN_ACTIVITY_REQUEST) {
            if(resultCode == AppConstants.USER_LOG_OUT) {
                launchCorrectActivity();
            } else {
                finish();
            }
        }
    }
}

