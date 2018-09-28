package com.elegion.tracktor.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.service.CounterService;
import com.elegion.tracktor.utils.CommonUtils;
import com.elegion.tracktor.utils.IDistanceConverter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import toothpick.Scope;
import toothpick.Toothpick;

public class CounterFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvAverageSpeed)
    TextView tvSpeed;
    @BindView(R.id.buttonStart)
    Button buttonStart;
    @BindView(R.id.buttonStop)
    Button buttonStop;
    @BindView(R.id.ivAverageSpeedIcon)
    ImageView mIvAverageSpeedIcon;

    @Inject
    protected MainViewModel mViewModel;
    @Inject
    protected IDistanceConverter mDistanceConverter;

    public static CounterFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CounterFragment fragment = new CounterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Scope scope = Toothpick.openScope("MainActivity");
        Toothpick.inject(this, scope);

        View view = inflater.inflate(R.layout.fr_counter, container, false);
        ButterKnife.bind(this, view);

        mViewModel.getTimeText().observe(this, tvTime::setText);
        mViewModel.getDistanceText().observe(this, tvDistance::setText);
        mViewModel.getAverageSpeed().observe(this, this::setAverageSpeed);
        mViewModel.getStartEnabled().observe(this, buttonStart::setEnabled);
        mViewModel.getStopEnabled().observe(this, buttonStop::setEnabled);
        mViewModel.getIsShutdown().observe(this, this::stopService);

        return view;
    }

    private void setAverageSpeed(double speed) {
        tvSpeed.setText(mDistanceConverter.convertSpeed(speed));
        mIvAverageSpeedIcon.setImageResource(CommonUtils.detectActionIconId(speed));
    }
    @OnClick(R.id.buttonStart)
    void onStartClick() {
        mViewModel.startRoute();
        Intent serviceIntent = new Intent(getContext(), CounterService.class);
        getActivity().startService(serviceIntent);
    }

    @OnClick(R.id.buttonStop)
    void onStopClick() {
        mViewModel.stopRoute();
        stopService(true);
    }

    private void stopService(boolean stop) {
        if (stop) {
            Intent serviceIntent = new Intent(getContext(), CounterService.class);
            getActivity().stopService(serviceIntent);
        }

    }
}
