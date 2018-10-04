package com.elegion.tracktor.ui.prefs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import toothpick.Toothpick;

public class TrackLinePreferencesDialogFragment extends PreferenceDialogFragmentCompat {

    private int mLineWidthValue = 1;
    private int mColor = Color.GREEN;
    private int mMarkerType = 1;
    private int mScreenWidth;

    @BindView(R.id.ivSample)
    protected ImageView mImageView;
    @BindView(R.id.tvWidthValue)
    protected TextView mTvWidthValue;
    @BindView(R.id.ivLeftMarker)
    protected ImageView mIvLeftMarker;
    @BindView(R.id.ivRightMarker)
    protected ImageView mIvRightMarker;
    @BindView(R.id.ibMarkerTrack01)
    protected ImageButton mIbMarkerTrack01;
    @BindView(R.id.ibMarkerTrack02)
    protected ImageButton mIbMarkerTrack02;
    @BindView(R.id.ibMarkerTrack03)
    protected ImageButton mIbMarkerTrack03;
    @Inject
    CurrentPreferences mCurrentPreferences;

    public static TrackLinePreferencesDialogFragment newInstance(String key) {

        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        TrackLinePreferencesDialogFragment fragment = new TrackLinePreferencesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ButterKnife.bind(this, view);
        Toothpick.inject(this, Toothpick.openScope("Application"));
        initScreenWidth();
        initMarkerButtons();
        drawLine();
    }

    private void initScreenWidth() {
        try {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            mScreenWidth = size.x;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            mScreenWidth = 5000;
        }
    }

    private void initMarkerButtons(){
        mIbMarkerTrack01.setImageResource(mCurrentPreferences.getMarkerResId(1));
        mIbMarkerTrack02.setImageResource(mCurrentPreferences.getMarkerResId(2));
        mIbMarkerTrack03.setImageResource(mCurrentPreferences.getMarkerResId(3));
    }

    private void drawLine() {
        try {
            mTvWidthValue.setText(String.valueOf(mLineWidthValue));
            int markerResId = mCurrentPreferences.getMarkerResId(mMarkerType);
            mIvLeftMarker.setImageResource(markerResId);
            mIvRightMarker.setImageResource(markerResId);
            Bitmap bitmap = Bitmap.createBitmap(mScreenWidth, mLineWidthValue, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(mColor);
            canvas.drawRect(0, 0, mScreenWidth, mLineWidthValue, paint);
            mImageView.setImageBitmap(bitmap);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @OnClick(R.id.ibIncreaseWithValue)
    protected void increaseWeightValue() {
        if (mLineWidthValue < 100) {
            mLineWidthValue++;
            drawLine();
        }
    }

    @OnClick(R.id.ibDecreaseWithValue)
    protected void decreaseWeightValue() {
        if (mLineWidthValue > 1) {
            mLineWidthValue--;
            drawLine();
        }
    }

    @OnClick({R.id.ibColorTrack01,
            R.id.ibColorTrack02,
            R.id.ibColorTrack03,
            R.id.ibColorTrack04,
            R.id.ibColorTrack05,
            R.id.ibColorTrack06})
    protected void changeColor(View view) {
        if (view.getBackground() instanceof ColorDrawable) {
            mColor = ((ColorDrawable) view.getBackground()).getColor();
            drawLine();
        }
    }

    @OnClick({R.id.ibMarkerTrack01,
            R.id.ibMarkerTrack02,
            R.id.ibMarkerTrack03})
    protected void changeMarker(View view) {
        int newType;

        switch (view.getId()) {
            case R.id.ibMarkerTrack02: {
                newType = 2;
                break;
            }
            case R.id.ibMarkerTrack03: {
                newType = 3;
                break;
            }
            default: {
                newType = 1;
                break;
            }
        }
        if (mMarkerType != newType) {
            mMarkerType = newType;
            drawLine();
        }
    }
}
