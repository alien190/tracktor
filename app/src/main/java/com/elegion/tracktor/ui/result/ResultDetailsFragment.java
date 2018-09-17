package com.elegion.tracktor.ui.result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.elegion.tracktor.di.resultDetails.ResultDetailsModule;
import com.elegion.tracktor.utils.ScreenshotMaker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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


    public static final String ID_KEY = "IdKey";
    Bitmap mScreenShot;
    private long mId;

    @Inject
    ResultDetailsViewModel mViewModel;

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
        Toothpick.closeScope("ResultDetail");
        Scope scope = Toothpick.openScopes("Application", "ResultDetail");
        scope.installModules(new ResultDetailsModule(this, mId));
        Toothpick.inject(this, scope);

        View view = inflater.inflate(R.layout.fr_result_details, container, false);
        ButterKnife.bind(this, view);

        initSpinner();

        mViewModel.getScreenShotBase64().observe(this, this::setScreenShot);
        mViewModel.getDuration().observe(this, tvDuration::setText);
        mViewModel.getDistance().observe(this, tvDistance::setText);
        mViewModel.getAction().observe(this, spAction::setSelection);
        mViewModel.getAverageSpeed().observe(this, tvAverageSpeed::setText);
        mViewModel.getStartDate().observe(this, tvStartDate::setText);
        mViewModel.loadTrack();

        setHasOptionsMenu(true);
        return view;
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
                String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), mScreenShot, "Мой маршрут", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, "Время: " + tvDuration.getText() + "\nРасстояние: " + tvDistance.getText());
                startActivity(Intent.createChooser(intent, "Результаты маршрута"));
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


}
