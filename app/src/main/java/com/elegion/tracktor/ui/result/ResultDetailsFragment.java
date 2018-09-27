package com.elegion.tracktor.ui.result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.di.resultDetails.ResultDetailsModule;
import com.elegion.tracktor.utils.CommonUtils;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplateActivity;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import toothpick.Scope;
import toothpick.Toothpick;


public class ResultDetailsFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView mTvDuration;
    @BindView(R.id.tvDistance)
    TextView mTvDistance;
    @BindView(R.id.tvAverageSpeed)
    TextView mTvAverageSpeed;
    @BindView(R.id.tvStartDate)
    TextView mTvStartDate;
    @BindView(R.id.ivScreenshot)
    ImageView mIvScreenshot;
    @BindView(R.id.spAction)
    Spinner mSpAction;
    @BindView(R.id.tvCalories)
    TextView mTvCalories;
    @BindView(R.id.tvComment)
    TextView mTvComment;
    @BindView(R.id.ivAverageSpeedIcon)
    ImageView mIvAverageSpeedIcon;
    @BindView(R.id.tvTemperature)
    TextView mTvTemperature;
    @BindView(R.id.ivWeather)
    ImageView mIvWeather;
    @BindView(R.id.tvWeatherStub)
    TextView mTvWeatherStub;

    public static final String ID_KEY = "IdKey";
    Bitmap mScreenShot;
    private long mId;

    @Inject
    ResultDetailsViewModel mViewModel;
    @Inject
    CommentDialogFragment mCommentDialogFragment;
    @Inject
    CurrentPreferences mCurrentPreferences;

    public static ResultDetailsFragment newInstance(long id) {
        ResultDetailsFragment fragment = new ResultDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getLong(ID_KEY, 0);
        }
        toothpickInject();
        View view = inflater.inflate(R.layout.fr_result_details, container, false);
        ButterKnife.bind(this, view);

        initUI();

        setHasOptionsMenu(true);
        return view;
    }

    private void toothpickInject() {
        Toothpick.closeScope("ResultDetail");
        Scope scope = Toothpick.openScopes("Application", "ResultDetail");
        scope.installModules(new ResultDetailsModule(this, mId));
        Toothpick.inject(this, scope);
        Toothpick.inject(mCommentDialogFragment, scope);
    }


        private void initUI() {
        initSpinner();
        mViewModel.getScreenShotBase64().observe(this, this::setScreenShot);
        mViewModel.getDuration().observe(this, mTvDuration::setText);
        mViewModel.getDistance().observe(this, mTvDistance::setText);
        mViewModel.getAction().observe(this, mSpAction::setSelection);
        mViewModel.getAverageSpeed().observe(this, this::setAverageSpeed);
        mViewModel.getStartDate().observe(this, mTvStartDate::setText);
        mViewModel.getCalories().observe(this, mTvCalories::setText);
        mViewModel.getComment().observe(this, this::setComment);
        mViewModel.getWeatherIcon().observe(this, this::setWeatherIcon);
        mViewModel.getTemperature().observe(this, mTvTemperature::setText);
        mViewModel.loadTrack();
    }

    private void setWeatherIcon(String iconBase64) {
        if (iconBase64 != null && !iconBase64.isEmpty()) {
            mIvWeather.setImageBitmap(ScreenshotMaker.fromBase64(iconBase64));
            mTvTemperature.setVisibility(View.VISIBLE);
            mIvWeather.setVisibility(View.VISIBLE);
            mTvWeatherStub.setVisibility(View.GONE);
        }
        else {
            mTvTemperature.setVisibility(View.GONE);
            mIvWeather.setVisibility(View.GONE);
            mTvWeatherStub.setVisibility(View.VISIBLE);
        }
    }

    private void setAverageSpeed(double speed) {
        mTvAverageSpeed.setText(StringUtils.getSpeedText(speed));
        mIvAverageSpeedIcon.setImageResource(CommonUtils.getDetectActionIconId(speed));
    }

    private void setComment(String comment) {
        if (comment == null || comment.isEmpty()) {
            comment = getString(R.string.no_comment);
        }
        mTvComment.setText(comment);
    }

    private void setScreenShot(String imageString) {
        mScreenShot = ScreenshotMaker.fromBase64(imageString);
        mIvScreenshot.setImageBitmap(mScreenShot);
    }

    private void initSpinner() {
        List<String> actions = mCurrentPreferences.getActions();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                actions);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpAction.setAdapter(arrayAdapter);

    }

    @OnItemSelected(R.id.spAction)
    public void spinnerItemSelected(Spinner spinner, int position) {
        mViewModel.getAction().postValue(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_result_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionShare: {
                doShare();
                return true;
            }
            case R.id.actionDelete: {
                mViewModel.deleteTrack();
                getActivity().onBackPressed();
                return true;
            }
            case R.id.actionEditTemplate: {
                MessageTemplateActivity.start(getContext());
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @OnClick(R.id.btnComment)
    void onAddCommentClickListener() {
        mCommentDialogFragment.show(getActivity().getSupportFragmentManager(), "commentDialog");
    }

    private void doShare() {
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), mScreenShot, "Мой маршрут", null);
        Uri uri = Uri.parse(path);

        String shareMessage = mViewModel.getSharingMessage();
        Intent intent = new Intent(Intent.ACTION_SEND);

        if(shareMessage.contains("[image]")) {  //todo переделать
            shareMessage = shareMessage.replace("[image]", "");
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        } else {
            intent.setType("text/plain");
        }

        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)));
    }

    @Override
    public void onResume() {
        super.onResume();
        mIvScreenshot.setOnClickListener(view -> {
            if(getActivity() instanceof IOnZoomClickListener) {
                ((IOnZoomClickListener)getActivity()).onZoomClick();
            }
        });
    }

    @Override
    public void onPause() {
        mIvScreenshot.setOnClickListener(null);
        super.onPause();
    }
}
