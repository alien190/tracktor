package com.elegion.tracktor.ui.prefs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.tracktor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackLinePreferencesDialogFragment extends PreferenceDialogFragmentCompat {

    private int mLineWidthValue = 1;
    private int mColor = Color.GREEN;

    @BindView(R.id.ivSample)
    protected ImageView mImageView;
    @BindView(R.id.tvWidthValue)
    protected TextView mTvWidthValue;

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
        drawLine();
    }

    private void drawLine() {
        if (mImageView != null) {
            //Bitmap origBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            //Bitmap bitmap = Bitmap.createBitmap(origBitmap, 0, 0, 100, 10);
//            if (origBitmap != bitmap) {
//                origBitmap.recycle();
//            }

            mTvWidthValue.setText(String.valueOf(mLineWidthValue));
            int width = 100;
            Bitmap bitmap = Bitmap.createBitmap(width, mLineWidthValue, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(mColor);
            //paint.setStrokeWidth(1);
            //canvas.drawLine(0, 0, 100, 0, paint);
            canvas.drawRect(0, 0, width, mLineWidthValue, paint);
            mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @OnClick(R.id.ibIncreaseWithValue)
    protected void increaseWeightValue() {
        mLineWidthValue++;
        drawLine();
    }

    @OnClick(R.id.ibDecreaseWithValue)
    protected void decreaseWeightValue() {
        if (mLineWidthValue > 0) {
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
}
