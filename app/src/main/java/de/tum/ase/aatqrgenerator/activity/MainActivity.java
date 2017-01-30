package de.tum.ase.aatqrgenerator.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.fragment.LecturesFragment;
import de.tum.ase.aatqrgenerator.fragment.QrFragment;
import de.tum.ase.aatqrgenerator.service.UserService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private UserService userService;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LecturesFragment()).commit();
        }

        userService = new UserService(this);
        userService.setSignOutListener(new UserService.UserServiceSignOutListener() {
            @Override
            public void userSignOutSuccess() {
                progress.dismiss();
                Toast.makeText(MainActivity.this, "Sign out successfull", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void userSignOutFailure() {
                progress.dismiss();
                Toast.makeText(MainActivity.this, "Sign out failed", Toast.LENGTH_SHORT).show();
            }
        });
        userService.setSignInListener(new UserService.UserServiceSignInListener() {
            @Override
            public void userSignInSuccess(GoogleSignInAccount account) {
                Log.d("MainActivity", "userSignInSuccess(account)");
                // Don't have to save account as it is already saved in UserService.currentAccount
                progress.dismiss();
                populateViewsAuthenticated();
            }

            @Override
            public void userSignInFailure() {
                Log.d("MainActivity", "userSignInFailure()");
                progress.dismiss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(UserService.currentAccount == null) {
            progress = ProgressDialog.show(this, "Google Sign in",
                    "Signing in, please wait...", true);
            userService.silentSignIn();
        } else {
            populateViewsAuthenticated();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lectures");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void populateViewsAuthenticated(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment instanceof LecturesFragment){
            ((LecturesFragment) fragment).reload();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            if(f instanceof LecturesFragment){
                getSupportActionBar().setTitle("Lectures");
            } else if(f instanceof QrFragment) {
                getSupportActionBar().setTitle("Verification QR");
            } else {
                getSupportActionBar().setTitle("AAT");
            }
        }
    }

    public void showQr(String verificationToken) {
        QrFragment qrFragment = new QrFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, qrFragment).addToBackStack("qr").commit();
        getSupportFragmentManager().executePendingTransactions();
        getSupportActionBar().setTitle("Verification QR");
        qrFragment.setVerificationToken(verificationToken);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lectures) {
            getSupportActionBar().setTitle("Lectures");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LecturesFragment()).addToBackStack("lecture").commit();
        } else if (id == R.id.nav_logout) {
            progress = ProgressDialog.show(this, "Google Sign in",
                    "Signing out, please wait...", true);
            userService.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
