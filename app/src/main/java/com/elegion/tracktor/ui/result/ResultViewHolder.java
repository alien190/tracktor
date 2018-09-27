package com.elegion.tracktor.ui.result;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.ShowResultDetailEvent;
import com.elegion.tracktor.common.event.TrackCommentEditEvent;
import com.elegion.tracktor.common.event.TrackDeleteEvent;
import com.elegion.tracktor.common.event.TrackShareEvent;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.CommonUtils;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivPreview)
    protected ImageView mIvPreview;
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
    @BindView(R.id.ivAverageSpeedIcon)
    protected ImageView mIvAverageSpeedIcon;
    @BindView(R.id.tvTemperature)
    protected TextView mTvTemperature;
    @BindView(R.id.tvWeatherStub)
    protected TextView mTvWeatherStub;
    @BindView(R.id.ivWeather)
    protected ImageView mIvWeather;


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
        mTvDuration.setText(StringUtils.getDurationText(track.getDuration()));
        mTvDistance.setText(StringUtils.getDistanceText(track.getDistance()));
        mTvStartDate.setText(StringUtils.getDateText(track.getDate()));
        view.setOnClickListener(view -> EventBus.getDefault().post(new ShowResultDetailEvent(mId)));
        mIvDetail.setOnClickListener(view -> showDetail(!(mRlDetail.getVisibility() == View.VISIBLE)));
        mTvSpeed.setText(StringUtils.getSpeedText(track.getAverageSpeed()));
        mIvAverageSpeedIcon.setImageResource(CommonUtils.getDetectActionIconId(track.getAverageSpeed()));
        mTvCalories.setText(StringUtils.getCaloriesText(track.getCalories()));
        mTvAction.setText(mCurrentPreferences.getActions().get(track.getAction()));
        mTvComment.setText(StringUtils.getCommentText(track.getComment()));
        if (track.getWeatherIcon() != null && !track.getWeatherIcon().isEmpty()) {
            mIvWeather.setImageBitmap(ScreenshotMaker.fromBase64(track.getWeatherIcon()));
            mTvTemperature.setText(StringUtils.getTemperatureText(track.getTemperature()));
            mTvTemperature.setVisibility(View.VISIBLE);
            mIvWeather.setVisibility(View.VISIBLE);
            mTvWeatherStub.setVisibility(View.GONE);
        } else {
            mTvTemperature.setVisibility(View.GONE);
            mIvWeather.setVisibility(View.GONE);
            mTvWeatherStub.setVisibility(View.VISIBLE);
        }
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

    @OnClick(R.id.ibEditComment)
    protected void onEditCommentClick() {
        EventBus.getDefault().post(new TrackCommentEditEvent(mId));
    }

    @OnClick(R.id.ibDelete)
    protected void onDelete() {
        EventBus.getDefault().post(new TrackDeleteEvent(mId));
    }

    @OnClick(R.id.ibShare)
    protected void onShare() {
        EventBus.getDefault().post(new TrackShareEvent(mId));
    }
}

