package com.elegion.tracktor.ui.map;

import android.annotation.SuppressLint;
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
import com.elegion.tracktor.service.CounterService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CounterFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.buttonStart)
    Button buttonStart;
    @BindView(R.id.buttonStop)
    Button buttonStop;

    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_counter, container, false);
        ButterKnife.bind(this, view);

        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getTimeText().observe(this, s -> tvTime.setText(s));
        viewModel.getDistanceText().observe(this, s -> tvDistance.setText(s));
        viewModel.getStartEnabled().observe(this, buttonStart::setEnabled);
        viewModel.getStopEnabled().observe(this, buttonStop::setEnabled);
        viewModel.getIsShutdown().observe(this, this::stopService);

        return view;
    }

    @OnClick(R.id.buttonStart)
    void onStartClick() {
        viewModel.startRoute();
        Intent serviceIntent = new Intent(getContext(), CounterService.class);
        getActivity().startService(serviceIntent);
    }

    @OnClick(R.id.buttonStop)
    void onStopClick() {
        viewModel.stopRoute();
        stopService(true);
    }

    private void stopService(boolean stop) {
        if (stop) {
            Intent serviceIntent = new Intent(getContext(), CounterService.class);
            getActivity().stopService(serviceIntent);
        }

    }
}
