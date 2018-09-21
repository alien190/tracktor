package com.elegion.tracktor.ui.result;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.ShowResultDetailEvent;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivPreview)
    protected ImageView mIvPreview;
    @BindView(R.id.tvId)
    protected TextView mTvId;
    @BindView(R.id.tvDuration)
    protected TextView mTvDuration;
    @BindView(R.id.tvDistance)
    protected TextView mTvDistance;
    @BindView(R.id.tvStartDate)
    protected TextView mTvStartDate;
    @BindView(R.id.ivDetail)
    protected ImageView mIvDetail;
    @BindView(R.id.rlDetail)
    protected RelativeLayout mRlDetail;
    @BindView(R.id.tvSpeed)
    protected TextView mTvSpeed;
    @BindView(R.id.tvCalories)
    protected TextView mTvCalories;
    @BindView(R.id.tvAction)
    protected TextView mTvAction;
    @BindView(R.id.tvComment)
    protected TextView mTvComment;


    private View view;
    private long mId;
    private CurrentPreferences mCurrentPreferences;


    public ResultViewHolder(View itemView, CurrentPreferences currentPreferences) {
        super(itemView);
        view = itemView;
        mCurrentPreferences = currentPreferences;
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
        mIvDetail.setOnClickListener(view -> showDetail(!(mRlDetail.getVisibility() == View.VISIBLE)));
        mTvSpeed.setText(StringUtils.getSpeedText(track.getAverageSpeed()));
        mTvCalories.setText(StringUtils.getCaloriesText(track.getCalories()));
        mTvAction.setText(mCurrentPreferences.getActions().get(track.getAction()));
        mTvComment.setText(track.getComment());
    }

    private void showDetail(boolean isVisible) {
        if (isVisible) {
            mRlDetail.setVisibility(View.VISIBLE);
            mIvDetail.setImageResource(R.drawable.ic_expand_less_black_24dp);
        } else {
            mRlDetail.setVisibility(View.GONE);
            mIvDetail.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }
    }
}

