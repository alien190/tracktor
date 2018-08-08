package com.elegion.tracktor.ui.result;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivPreview)
    private ImageView mIvPreview;
    @BindView(R.id.tvId)
    private TextView mTvId;
    @BindView(R.id.tvDuration)
    private TextView mTvDuration;
    @BindView(R.id.tvDistance)
    private TextView mTvDistance;

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
        mTvDuration.setText(StringUtils.getTimerText(track.getDuration()));
        mTvDistance.setText(StringUtils.getDistanceText(track.getDistance()));
    }

    public void setOnClickListener(ResultAdapter.OnItemClickListener listener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onItemClick(mId);
                }
            }
        });
    }
}

