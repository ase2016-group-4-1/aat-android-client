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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.fragment.LecturesFragment;
import de.tum.ase.aatqrgenerator.fragment.QrFragment;
import de.tum.ase.aatqrgenerator.fragment.TemplateFragment;
import de.tum.ase.aatqrgenerator.service.ApiService;
import de.tum.ase.aatqrgenerator.service.UserService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private UserService userService;
    private ApiService apiService;

    private ProgressDialog progress;
    private MenuItem navUser;

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
                progress.dismiss();
                populateViewsAuthenticated();
            }

            @Override
            public void userSignInFailure() {
                progress.dismiss();
                // Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
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
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void showQr(String verificationToken) {
        QrFragment qrFragment = new QrFragment();
        qrFragment.setVerificationToken(verificationToken);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, qrFragment).addToBackStack("qr").commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lectures) {
            toolbar.setTitle("Lectures");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LecturesFragment()).addToBackStack("lecture").commit();
        } /*else if(id == R.id.nav_user) {
            toolbar.setTitle("User Info");
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new TemplateFragment()).commit();
        } else if(id == R.id.nav_setting) {
            toolbar.setTitle("Settings");
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new TemplateFragment()).commit();
        }*/ else if (id == R.id.nav_logout) {
            progress = ProgressDialog.show(this, "Google Sign in",
                    "Signing out, please wait...", true);
            userService.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
