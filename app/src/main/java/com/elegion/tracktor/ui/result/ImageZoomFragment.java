package com.elegion.tracktor.ui.result;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.utils.ScreenshotMaker;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

public class ImageZoomFragment extends Fragment {

    private ImageView mImageView;
    private View mView;
    @Inject
    protected ResultDetailsViewModel mViewModel;

    public static ImageZoomFragment newInstance() {

        Bundle args = new Bundle();

        ImageZoomFragment fragment = new ImageZoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Toothpick.inject(this, Toothpick.openScope("ResultDetail"));

        mView = inflater.inflate(R.layout.fr_image_zoom, container, false);
        mImageView = mView.findViewById(R.id.ivZoom);
        mImageView.setImageBitmap(ScreenshotMaker.fromBase64(mViewModel.getScreenShotBase64().getValue()));
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.setOnClickListener(view1 -> getActivity().onBackPressed());
    }

    @Override
    public void onPause() {
        mView.setOnClickListener(null);
        super.onPause();
    }
}
