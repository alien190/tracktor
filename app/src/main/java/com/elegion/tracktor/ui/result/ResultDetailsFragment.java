package com.elegion.tracktor.ui.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.di.resultDetails.ResultDetailsModule;
import com.elegion.tracktor.ui.weather.WeatherFragment;
import com.elegion.tracktor.utils.DetectActionUtils;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import toothpick.Scope;
import toothpick.Toothpick;


public class ResultDetailsFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvDuration;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvAverageSpeed)
    TextView tvAverageSpeed;
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;
    @BindView(R.id.ivScreenshot)
    ImageView ivScreenshot;
    @BindView(R.id.spAction)
    Spinner spAction;
    @BindView(R.id.tvCalories)
    TextView tvCalories;
    @BindView(R.id.tvComment)
    TextView tvComment;
    @BindView(R.id.ivAverageSpeedIcon)
    ImageView mIvAverageSpeedIcon;


    public static final String ID_KEY = "IdKey";
    Bitmap mScreenShot;
    private long mId;

    @Inject
    ResultDetailsViewModel mViewModel;
    @Inject
    CommentDialogFragment mCommentDialogFragment;
    @Inject
    WeatherFragment mWeatherFragment;

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
        replaceWeatherFragment();
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
        Toothpick.inject(mWeatherFragment, scope);
    }

    private void replaceWeatherFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm != null) {
            fm.beginTransaction()
                    .replace(R.id.weatherContainer, mWeatherFragment)
                    .commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultActivity) {

        }
    }

    private void initUI() {
        initSpinner();
        mViewModel.getScreenShotBase64().observe(this, this::setScreenShot);
        mViewModel.getDuration().observe(this, tvDuration::setText);
        mViewModel.getDistance().observe(this, tvDistance::setText);
        mViewModel.getAction().observe(this, spAction::setSelection);
        mViewModel.getAverageSpeed().observe(this, this::setAverageSpeed);
        mViewModel.getStartDate().observe(this, tvStartDate::setText);
        mViewModel.getCalories().observe(this, tvCalories::setText);
        mViewModel.getComment().observe(this, this::setComment);
        mViewModel.loadTrack();
    }

    private void setAverageSpeed(double speed) {
        tvAverageSpeed.setText(StringUtils.getSpeedText(speed));
        mIvAverageSpeedIcon.setImageResource(DetectActionUtils.getDetectActionIconId(speed));
    }

    private void setComment(String comment) {
        if (comment == null || comment.isEmpty()) {
            comment = getString(R.string.no_comment);
        }
        tvComment.setText(comment);
    }

    private void setScreenShot(String imageString) {
        mScreenShot = ScreenshotMaker.fromBase64(imageString);
        ivScreenshot.setImageBitmap(mScreenShot);
    }

    private void initSpinner() {
        String[] actions = getResources().getStringArray(R.array.actions);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                actions);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spAction.setAdapter(arrayAdapter);

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

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        String extraText = "Начало трека :" + tvStartDate.getText()
                + "\nВремя: " + tvDuration.getText()
                + "\nРасстояние: " + tvDistance.getText()
                + "\nСредняя скорость: " + tvAverageSpeed.getText()
                + "\nЗатрачено калорий: " + tvCalories.getText()
                + "\nВид деятельности: " + spAction.getSelectedItem().toString()
                + "\nКомментарий: " + tvComment.getText();

        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        startActivity(Intent.createChooser(intent, "Результаты маршрута"));
    }
}
