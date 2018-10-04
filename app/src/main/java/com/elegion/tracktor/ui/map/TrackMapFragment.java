package com.elegion.tracktor.ui.map;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.RequestRouteUpdateEvent;
import com.elegion.tracktor.common.event.RouteUpdateEvent;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.ui.result.ResultActivity;
import com.elegion.tracktor.utils.ScreenshotMaker;
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

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import toothpick.Scope;
import toothpick.Toothpick;

public class TrackMapFragment extends SupportMapFragment implements
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private static final int DEFAULT_ZOOM = 15;
    @Inject
    protected MainViewModel mViewModel;
    @Inject
    protected CurrentPreferences mCurrentPreferences;

    private GoogleMap mMap;
    private SingleObserver mMapSet;
    private Single<Boolean> mIsMapSet = new Single<Boolean>() {
        @Override
        protected void subscribeActual(SingleObserver<? super Boolean> observer) {
            mMapSet = observer;
        }
    };

    public static TrackMapFragment newInstance() {

        Bundle args = new Bundle();

        TrackMapFragment fragment = new TrackMapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint({"CheckResult", "MissingPermission"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Scope scope = Toothpick.openScope("MainActivity");
        Toothpick.inject(this, scope);

        TrackMapFragment fragment = this;

        mViewModel.getIsPermissionGranted()
                .zipWith(mIsMapSet, (first, second) -> true)
                .subscribe(aBoolean -> {
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMyLocationButtonClickListener(fragment);
                });
        if (savedInstanceState == null) {
            getMapAsync(this);
            setRetainInstance(true);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapSet.onSuccess(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSegmentToMap(SegmentForRouteEvent segmentForRouteEvent) {
        if (mMap != null) {
            mMap.addPolyline(new PolylineOptions().add(segmentForRouteEvent.points.first.point,
                    segmentForRouteEvent.points.second.point)
                    .color(ContextCompat.getColor(getContext(),
                            R.color.colorRouteLine)));

            animateCamera(segmentForRouteEvent.points.second.point);

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new RequestRouteUpdateEvent(null));
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRoute(StartRouteEvent event) {
        if (mMap != null) {
            mMap.clear();
            addMarker(event.firstPoint.point, getString(R.string.routeStart));
            animateCamera(event.firstPoint.point);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopRoute(StopRouteEvent event) {
        if (mMap != null && event.route.size() != 0) {
            addMarker(event.route.get(event.route.size() - 1), getString(R.string.routeStop));
        }
        //takeScreenshot(event, bitmap -> ResultActivity.start(getContext(), event, bitmap));
        takeScreenshot(event, bitmap ->
                {
                    String imageBase64 = ScreenshotMaker.toBase64(bitmap, mCurrentPreferences.getPictureQuality());
                    long id = mViewModel.saveResults(imageBase64);
                    ResultActivity.start(getContext(), id);
                }
        );
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
            Toast.makeText(getContext(), R.string.emptyRoute, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void animateCamera(LatLng latLng) {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRouteUpdate(RouteUpdateEvent event) {
        if (mMap != null && event.points.size() != 0) {
            mMap.clear();
            mMap.addPolyline(new PolylineOptions().addAll(event.points)
                    .color(ContextCompat.getColor(getContext(),
                            R.color.colorRouteLine)));
            addMarker(event.points.get(0), getString(R.string.routeStart));
            animateCamera(event.points.get(event.points.size() - 1));
        }
    }

    private void addMarker(LatLng position, String text) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(position).title(text));
        }
    }
}
