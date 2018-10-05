package com.elegion.tracktor.ui.weather;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.common.IWeatherViewModel;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherFragment extends Fragment {
    @Inject
    protected IWeatherViewModel mViewModel;
    @Inject
    protected Transformation mTransformation;

    @BindView(R.id.layoutWeather)
    protected LinearLayout mLayoutWeather;
    @BindView(R.id.tvDegrees)
    protected TextView mTvDegrees;
    @BindView(R.id.ivWeather)
    protected ImageView mIvWeather;
    @BindView(R.id.pbWeather)
    ProgressBar mPbWeather;

    private View view;

    public static WeatherFragment newInstance() {

        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setRetainInstance(true);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fr_weather, container, false);
            ButterKnife.bind(this, view);
            mViewModel.getTemperature().observe(this, mTvDegrees::setText);
            mViewModel.getIsShowWeather().observe(this,
                    isShow -> mLayoutWeather.setVisibility(isShow ? View.VISIBLE : View.GONE));
            mViewModel.getWeatherIconURL().observe(this, this::showWeatherIcon);
            mViewModel.getIsWeatherRefreshing().observe(this,
                    isRefreshing -> mPbWeather.setVisibility(isRefreshing ? View.VISIBLE : View.GONE));
        }
        return view;
    }

    private void showWeatherIcon(String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .transform(mTransformation)
                    .into(mIvWeather, new Callback() {
                        @Override
                        public void onSuccess() {
                            mIvWeather.refreshDrawableState();
                            double ratio = (double) mIvWeather.getDrawable().getIntrinsicWidth() /
                                    mIvWeather.getDrawable().getIntrinsicHeight();
                            mIvWeather.setMinimumWidth((int) (mIvWeather.getHeight() * ratio));
                            mViewModel.setLastWeatherIcon(ScreenshotMaker.toBase64(
                                    ((BitmapDrawable) mIvWeather.getDrawable()).getBitmap(), true, 100));
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutWeather.setOnClickListener(view -> mViewModel.updateWeather());
    }

    @Override
    public void onPause() {
        mLayoutWeather.setOnClickListener(null);
        super.onPause();
    }
}

