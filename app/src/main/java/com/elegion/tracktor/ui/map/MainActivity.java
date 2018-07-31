package com.elegion.tracktor.ui.map;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.ui.result.ResultActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {


    public static final int DEFAULT_ZOOM = 15;
    public static final int LOCATION_REQUEST_CODE = 99;

    private GoogleMap mMap;  //todo сделать сохранение состояния при изменении конфигурации


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSegmentToMap(SegmentForRouteEvent segmentForRouteEvent) {
        if (mMap != null) {
            mMap.addPolyline(new PolylineOptions().add(segmentForRouteEvent.points.first.point,
                    segmentForRouteEvent.points.second.point)
                    .color(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorRouteLine)));

            animateCamera(segmentForRouteEvent.points.second.point);
        }
    }

    @BindView(R.id.counterContainer)
    FrameLayout counterContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
            supportMapFragment.setRetainInstance(true);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.counterContainer, new CounterFragment())
                    .commit();
        }


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
                //todo add logic
                break;
            case R.id.actionSettings:
                //todo add logic
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRoute(StartRouteEvent event) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(event.firstPoint.point).title(getString(R.string.routeStart)));
            animateCamera(event.firstPoint.point);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopRoute(StopRouteEvent event) {
        if (mMap != null && event.route.size() != 0) {
            mMap.addMarker(new MarkerOptions().position(event.route.get(event.route.size() - 1))
                    .title(getString(R.string.routeStop)));
        }
        takeScreenshot(event, bitmap -> ResultActivity.start(this, event, bitmap));
    }

    private void takeScreenshot(StopRouteEvent event, GoogleMap.SnapshotReadyCallback snapshotReadyCallback) {

        if (event.route != null && event.route.size() > 0) {

            LatLngBounds.Builder latLngBounds = LatLngBounds.builder();
            for (LatLng latLng : event.route) {
                latLngBounds.include(latLng);
            }
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding);
            mMap.animateCamera(cu, 1, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mMap.snapshot(snapshotReadyCallback);
                }

                @Override
                public void onCancel() {
                }
            });

        } else {
            Toast.makeText(this, R.string.emptyRoute, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();
    }

    private void initMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Запрос разрешений на получение местоположения")
                    .setMessage("Нам необходимо знать Ваше местоположение, чтобы приложение работало")
                    .setPositiveButton("ОК", (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE))
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                initMap();
            } else {
                Toast.makeText(this, "Вы не дали разрешения!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void animateCamera(LatLng latLng) {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }
}
