package com.elegion.tracktor.ui.prefs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.ui.common.TrackDecoration;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import toothpick.Toothpick;

public class TrackDecorationPreferencesDialogFragment extends PreferenceDialogFragmentCompat {

    private static final String SAVE_STATE_PREFERENCE_VALUE = "TrackDecorationPreferencesDialogFragment.preferenceValue";

    private int mScreenWidth;

    private TrackDecoration mTrackDecoration;

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

    public static TrackDecorationPreferencesDialogFragment newInstance(String key) {

        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        TrackDecorationPreferencesDialogFragment fragment = new TrackDecorationPreferencesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initTrackDecoration(savedInstanceState.getString(SAVE_STATE_PREFERENCE_VALUE));
        } else {
            initTrackDecoration("");
        }
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

    private void initTrackDecoration(String value) {
        mTrackDecoration = new TrackDecoration();
        if (value != null && !value.isEmpty()) {
            mTrackDecoration.deserialize(value);
        } else {
            mTrackDecoration.deserialize(getPreferenceValue());
        }
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

    private void initMarkerButtons() {
        mIbMarkerTrack01.setImageResource(mCurrentPreferences.getMarkerResId(1));
        mIbMarkerTrack02.setImageResource(mCurrentPreferences.getMarkerResId(2));
        mIbMarkerTrack03.setImageResource(mCurrentPreferences.getMarkerResId(3));
    }

    private void drawLine() {
        try {
            mTvWidthValue.setText(String.valueOf(mTrackDecoration.getLineWidth()));
            int markerResId = mCurrentPreferences.getMarkerResId(mTrackDecoration.getMarkerType());
            mIvLeftMarker.setImageResource(markerResId);
            mIvRightMarker.setImageResource(markerResId);
            Bitmap bitmap = Bitmap.createBitmap(mScreenWidth, mTrackDecoration.getLineWidth(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(mTrackDecoration.getColor());
            canvas.drawRect(0, 0, mScreenWidth, mTrackDecoration.getLineWidth(), paint);
            mImageView.setImageBitmap(bitmap);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setPreferenceValue(mTrackDecoration.serialize());
        }
    }

    @OnClick(R.id.ibIncreaseWithValue)
    protected void increaseWeightValue() {
        if (mTrackDecoration.getLineWidth() < 100) {
            mTrackDecoration.setLineWidth(mTrackDecoration.getLineWidth() + 1);
            drawLine();
        }
    }

    @OnClick(R.id.ibDecreaseWithValue)
    protected void decreaseWeightValue() {
        if (mTrackDecoration.getLineWidth() > 1) {
            mTrackDecoration.setLineWidth(mTrackDecoration.getLineWidth() - 1);
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
            mTrackDecoration.setColor(((ColorDrawable) view.getBackground()).getColor());
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
        if (mTrackDecoration.getMarkerType() != newType) {
            mTrackDecoration.setMarkerType(newType);
            drawLine();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_STATE_PREFERENCE_VALUE, mTrackDecoration.serialize());
    }

    private String getPreferenceValue() {
        DialogPreference preference = getPreference();
        if (preference instanceof TrackDecorationPreference) {
            TrackDecorationPreference trackDecorationPreference = (TrackDecorationPreference) preference;
            return trackDecorationPreference.getValue();
        } else {
            return "";
        }
    }

    private void setPreferenceValue(String value) {
        DialogPreference preference = getPreference();
        if (preference instanceof TrackDecorationPreference) {
            TrackDecorationPreference trackDecorationPreference = (TrackDecorationPreference) preference;
            trackDecorationPreference.setValue(value);
        }
    }
}
