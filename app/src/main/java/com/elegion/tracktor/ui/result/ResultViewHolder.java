package com.elegion.tracktor.ui.result;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.ShowResultDetailEvent;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivPreview)
    public ImageView mIvPreview;
    @BindView(R.id.tvId)
    public TextView mTvId;
    @BindView(R.id.tvDuration)
    public TextView mTvDuration;
    @BindView(R.id.tvDistance)
    public TextView mTvDistance;
    @BindView(R.id.tvStartDate)
    public TextView mTvStartDate;

    private View view;
    private long mId;


    public ResultViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Track track) {
        mIvPreview.setImageBitmap(ScreenshotMaker.fromBase64(track.getImage()));
        mId = track.getId();
        mTvId.setText(String.valueOf(track.getId()));
        mTvDuration.setText(StringUtils.getDurationText(track.getDuration()));
        mTvDistance.setText(StringUtils.getDistanceText(track.getDistance()));
        mTvStartDate.setText(StringUtils.getDateText(track.getDate()));
        view.setOnClickListener(view -> EventBus.getDefault().post(new ShowResultDetailEvent(mId)));
    }
}

