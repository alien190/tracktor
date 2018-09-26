package com.elegion.tracktor.ui.weather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.common.IWeatherViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherFragment extends Fragment {
    @Inject
    IWeatherViewModel mViewModel;

    @BindView(R.id.llWeather)
    protected LinearLayout mLlWeather;

    public static WeatherFragment newInstance() {

        Bundle args = new Bundle();

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_weather, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
