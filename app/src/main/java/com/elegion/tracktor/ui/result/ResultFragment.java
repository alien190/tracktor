package com.elegion.tracktor.ui.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.RawLocationData;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ResultFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.btShareRaw)
    Button btShareRaw;

    public static final String STOP_ROUTE_EVENT = "StopRouteEvent";
    private List<RawLocationData> mRawLocationData;


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
            mRawLocationData = stopRouteEvent.mRawLocationDataList;
        }

        return view;
    }

    @OnClick(R.id.btShareRaw)
    public void onShareRawData(){
        if(mRawLocationData != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, StringUtils.getLocationDataText(mRawLocationData));
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } else {
            Toast.makeText(getActivity(), R.string.noRawData, Toast.LENGTH_SHORT).show();
        }
    }
}
