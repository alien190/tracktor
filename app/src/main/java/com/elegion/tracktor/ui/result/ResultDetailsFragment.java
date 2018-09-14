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
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import toothpick.Scope;
import toothpick.Toothpick;


public class ResultDetailsFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;
    //    @BindView(R.id.btShareRaw)
//    Button btShareRaw;
    @BindView(R.id.ivScreenshot)
    ImageView ivScreenshot;
    @BindView(R.id.spAction)
    Spinner spAction;


    public static final String ID_KEY = "IdKey";
    private String mRawLocationDataText;
    Bitmap mScreenShot;
    private long mId;
    private Track mTrack;

    @Inject
    ResultViewModel mViewModel;

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
        Scope scope = Toothpick.openScope("Result");
        Toothpick.inject(this, scope);

        View view = inflater.inflate(R.layout.fr_result_details, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();

        initSpinner();

        if (args != null) {
            mId = args.getLong(ID_KEY, 0);
            mTrack = mViewModel.getItem(mId);
            if (mTrack != null) {
                mScreenShot = ScreenshotMaker.fromBase64(mTrack.getImage());
                ivScreenshot.setImageBitmap(mScreenShot);
                tvTime.setText(StringUtils.getTimerText(mTrack.getDuration()));
                tvDistance.setText(StringUtils.getDistanceText(mTrack.getDistance()));
                tvSpeed.setText(StringUtils.getSpeedText(mTrack.getAverageSpeed()));
                tvStartDate.setText(StringUtils.getDateText(mTrack.getDate()));
                spAction.setSelection(mTrack.getAction());
            }
        }

        setHasOptionsMenu(true);
        return view;
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
        mTrack.setAction(position);
        updateTrack();
    }

    private void updateTrack() {
        mViewModel.updateItem(mTrack);
    }

//    @OnClick(R.id.btShareRaw)
//    public void onShareRawData() {
//        if (mRawLocationDataText != null && !mRawLocationDataText.isEmpty()) {
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, mRawLocationDataText);
//            shareIntent.setType("text/plain");
//            startActivity(shareIntent);
//        } else {
//            Toast.makeText(getActivity(), R.string.noRawData, Toast.LENGTH_SHORT).show();
//        }
//    }

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
                intent.putExtra(Intent.EXTRA_TEXT, "Время: " + tvTime.getText() + "\nРасстояние: " + tvDistance.getText());
                startActivity(Intent.createChooser(intent, "Результаты маршрута"));
                return true;
            }
            case R.id.actionDelete: {
                mViewModel.deleteItem(mId);
                getActivity().onBackPressed();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


}
