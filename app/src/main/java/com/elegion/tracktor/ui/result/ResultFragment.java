package com.elegion.tracktor.ui.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ResultFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.btShareRaw)
    Button btShareRaw;

    public static final String STOP_ROUTE_EVENT = "StopRouteEvent";
    private String mRawLocationDataText;
    GoogleMap mMap;
    List<LatLng> mRoute;


    public static ResultFragment newInstance(Bundle args) {

        //Bundle args = new Bundle();

        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fr_result, container, false);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        StopRouteEvent stopRouteEvent = (StopRouteEvent) args.getParcelable(STOP_ROUTE_EVENT);

        if (stopRouteEvent != null) {
            tvTime.setText(stopRouteEvent.routeTime);
            tvDistance.setText(StringUtils.getDistanceText(stopRouteEvent.routeDistance));
            mRawLocationDataText = stopRouteEvent.mRawLocationDataText;
            mRoute = stopRouteEvent.route;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.setRetainInstance(true);
            mapFragment.getMapAsync(this);
            getChildFragmentManager().beginTransaction().replace(R.id.mapContainer, mapFragment).commit();
        }

        return view;
    }

    @OnClick(R.id.btShareRaw)
    public void onShareRawData() {
        if (mRawLocationDataText != null && !mRawLocationDataText.isEmpty()) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mRawLocationDataText);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } else {
            Toast.makeText(getActivity(), R.string.noRawData, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mRoute != null && mRoute.size() > 0) {
            mMap.addPolyline(new PolylineOptions().addAll(mRoute)
                    .color(ContextCompat.getColor(getActivity().getApplicationContext(),
                            R.color.colorRouteLine)));

            mMap.addMarker(new MarkerOptions().position(mRoute.get(0)).title(getString(R.string.routeStart)));
            mMap.addMarker(new MarkerOptions().position(mRoute.get(mRoute.size() - 1)).
                    title(getString(R.string.routeStop)));

            LatLngBounds.Builder latLngBounds = LatLngBounds.builder();
            for (LatLng latLng : mRoute) {
                latLngBounds.include(latLng);
            }
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding);
            mMap.animateCamera(cu);
        } else {
            Toast.makeText(getContext(), R.string.emptyRoute, Toast.LENGTH_SHORT).show();
        }


    }
}
