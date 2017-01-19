package de.tum.ase.aatqrgenerator.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.tum.ase.aatqrgenerator.OnSaveQrRequestListener;
import de.tum.ase.aatqrgenerator.fragment.QrFragment;
import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.fragment.TemplateFragment;
import de.tum.ase.aatqrgenerator.support.AppConstants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnSaveQrRequestListener {

    Bitmap bitmap;
    Toolbar toolbar;

    @Override
    public void onSaveQrRequested(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap getSavedBitmap() {
        return bitmap;
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        if(bitmap != null) {
            toSave.putParcelable("bitmap", bitmap);
            Log.d("BITMAP", "saved");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////////////////////////////////////////////
        if (savedInstanceState != null) {
            bitmap = savedInstanceState.getParcelable("bitmap");
            Log.d("BITMAP", "loaded");
        }
        ////////////////////////////////////////////////
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new QrFragment()).commit();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Generate QR");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setResult(Activity.RESULT_OK);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_qr) {
            toolbar.setTitle("Generate QR");
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new QrFragment()).commit();
        } else if(id == R.id.nav_user) {
            toolbar.setTitle("User Info");
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new TemplateFragment()).commit();
        } else if(id == R.id.nav_setting) {
            toolbar.setTitle("Settings");
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new TemplateFragment()).commit();
        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences(AppConstants.CUSTOM_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(AppConstants.USER_LOGGED_IN, false);
            editor.commit();
            setResult(AppConstants.USER_LOG_OUT);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
