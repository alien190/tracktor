package com.elegion.tracktor.ui.map;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.di.main.MainModule;
import com.elegion.tracktor.ui.prefs.PreferenceActivity;
import com.elegion.tracktor.ui.result.ResultActivity;
import com.elegion.tracktor.ui.weather.WeatherFragment;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 99;

    @Inject
    MainViewModel mViewModel;
    private View mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressbar);

        Scope scope = Toothpick.openScopes("Application", "MainActivity");
        scope.installModules(new MainModule(this));
        Toothpick.inject(this, scope);

        if (savedInstanceState == null) {

            WeatherFragment weatherFragment = WeatherFragment.newInstance();
            Toothpick.inject(weatherFragment, scope);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.counterContainer, CounterFragment.newInstance())
                    .replace(R.id.mapContainer, TrackMapFragment.newInstance())
                    .replace(R.id.weatherContainer, weatherFragment)
                    .commit();
        }
        mViewModel.getIsScreenshotInProgress().observe(this,
                v -> mProgressBar.setVisibility(v ? View.VISIBLE : View.GONE)
        );
        requestPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionStatistic:
                ResultActivity.start(this, ResultActivity.ID_LIST);
                return true;
            case R.id.actionSettings:
                PreferenceActivity.start(this);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            mViewModel.onPermissionGranted();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permDialogTitle)
                    .setMessage(R.string.requestPermissionMessage)
                    .setPositiveButton(R.string.OkLabel, (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_REQUEST_CODE))
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 2 &&
                    permissions[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permDialogTitle)
                        .setMessage(R.string.notGrantedPermMessage)
                        .setPositiveButton(R.string.OkLabel, (dialogInterface, i) -> finish())
                        .create()
                        .show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Toothpick.closeScope("MainActivity");
        super.onDestroy();
    }
}
