package com.elegion.tracktor.ui.map;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.elegion.tracktor.R;
import com.elegion.tracktor.di.main.MainModule;
import com.elegion.tracktor.job.LocationJob;
import com.elegion.tracktor.service.CounterService;
import com.elegion.tracktor.ui.prefs.PreferenceActivity;
import com.elegion.tracktor.ui.result.ResultActivity;
import com.elegion.tracktor.ui.weather.WeatherFragment;
import com.elegion.tracktor.utils.CommonUtils;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

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

        mViewModel.getServiceState().observe(this, this::serviceStateObserver);

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
                v -> mProgressBar.setVisibility(v ? View.VISIBLE : View.GONE));
        requestPermissions();

        checkIntentExtra(getIntent());

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntentExtra(intent);
    }

    private void checkIntentExtra(Intent intent) {
        if (intent != null && intent.getBooleanExtra("STOP_TRACK", false)) {
            mViewModel.stopRoute();
        }
    }

    private void startService() {
        //startService(getServiceIntent());

        PersistableBundleCompat bundleCompat = new PersistableBundleCompat();
        bundleCompat.putBoolean(LocationJob.RESCHEDULE_KEY, false);

        int jobId = new JobRequest.Builder(LocationJob.TAG)
                .setExact(TimeUnit.SECONDS.toMillis(1))
                .setBackoffCriteria(TimeUnit.SECONDS.toMillis(1), JobRequest.BackoffPolicy.LINEAR)
                .setExtras(bundleCompat)
                .build()
                .schedule();
    }

    private void stopService() {
        //stopService(getServiceIntent());
    }

    private Intent getServiceIntent() {
        return new Intent(this, CounterService.class);
    }

    private void serviceStateObserver(int state) {
        switch (state) {
            case MainViewModel.SERVICE_STATE_UNKNOWN: {
                mViewModel.getServiceState().postValue(
                        CommonUtils.isServiceRunningInForeground(this, CounterService.class) ?
                                MainViewModel.SERVICE_STATE_RUNNING : MainViewModel.SERVICE_STATE_STOPPING);
                break;
            }
            case MainViewModel.SERVICE_STATE_MUST_RUN: {
                startService();
                mViewModel.getServiceState().postValue(MainViewModel.SERVICE_STATE_RUNNING);
                break;
            }
            case MainViewModel.SERVICE_STATE_MUST_STOP: {
                stopService();
                mViewModel.getServiceState().postValue(MainViewModel.SERVICE_STATE_STOPPING);
                break;
            }
            default: {
                break;
            }
        }
    }
}
