package com.elegion.tracktor.ui.result;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResultFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;

    public static final String STOP_ROUTE_EVENT = "StopRouteEvent";


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
        StopRouteEvent stopRouteEvent = (StopRouteEvent) args.getSerializable(STOP_ROUTE_EVENT);

        if(stopRouteEvent != null) {
            tvTime.setText(stopRouteEvent.routeTime);
            tvDistance.setText(StringUtils.getDistanceText(stopRouteEvent.routeDistance));
        }

        return view;
    }

}
