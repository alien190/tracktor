package com.elegion.tracktor.ui.weather;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.common.IWeatherViewModel;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherFragment extends Fragment {
    @Inject
    protected IWeatherViewModel mViewModel;

    @BindView(R.id.llWeather)
    protected LinearLayout mLlWeather;
    @BindView(R.id.tvDegrees)
    protected TextView mTvDegrees;
    @BindView(R.id.ivWeather)
    protected ImageView mIvWeather;

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
        mViewModel.getTemperature().observe(this, mTvDegrees::setText);
        mViewModel.getIsShowWeather().observe(this,
                isShow -> mLlWeather.setVisibility(isShow ? View.VISIBLE : View.GONE));
        mViewModel.getWeatherPictureURL().observe(this, this::showWeatherIcon);
        return view;
    }

    private void showWeatherIcon(String url){
        Picasso.get().load(url).into(mIvWeather);
    }

}

