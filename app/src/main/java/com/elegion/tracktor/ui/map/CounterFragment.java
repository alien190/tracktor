package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.di.main.MainModule;
import com.elegion.tracktor.service.CounterService;

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
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.buttonStart)
    Button buttonStart;
    @BindView(R.id.buttonStop)
    Button buttonStop;

    @Inject
    protected MainViewModel mViewModel;

    public static CounterFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CounterFragment fragment = new CounterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Scope scope = Toothpick.openScope("Main");
        Toothpick.inject(this, scope);

        View view = inflater.inflate(R.layout.fr_counter, container, false);
        ButterKnife.bind(this, view);

        mViewModel.getTimeText().observe(this, tvTime::setText);
        mViewModel.getDistanceText().observe(this, tvDistance::setText);
        mViewModel.getAverageSpeedText().observe(this, tvSpeed::setText);
        mViewModel.getStartEnabled().observe(this, buttonStart::setEnabled);
        mViewModel.getStopEnabled().observe(this, buttonStop::setEnabled);
        mViewModel.getIsShutdown().observe(this, this::stopService);

        return view;
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
