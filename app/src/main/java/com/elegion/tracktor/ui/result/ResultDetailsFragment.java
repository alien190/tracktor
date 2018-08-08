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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ResultDetailsFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.btShareRaw)
    Button btShareRaw;
    @BindView(R.id.ivScreenshot)
    ImageView ivScreenshot;

    //public static final String STOP_ROUTE_EVENT_KEY = "StopRouteEventKey";
    //public static final String SCREENSHOT_KEY = "ScreenShotKey";
    public static final String ID_KEY = "IdKey";
    private String mRawLocationDataText;
    Bitmap mScreenShot;


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

        View view = inflater.inflate(R.layout.fr_result, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        //StopRouteEvent stopRouteEvent = args.getParcelable(STOP_ROUTE_EVENT_KEY);
        //
        if (args != null) {
            long id = args.getLong(ID_KEY, 0);
            RealmRepository realmRepository = new RealmRepository();
            Track track = realmRepository.getItem(id);
            if (track != null) {
                mScreenShot = ScreenshotMaker.fromBase64(track.getImage());
                ivScreenshot.setImageBitmap(mScreenShot);
                tvTime.setText(StringUtils.getTimerText(track.getDuration()));
                tvDistance.setText(StringUtils.getDistanceText(track.getDistance()));
                //mRawLocationDataText = stopRouteEvent.mRawLocationDataText;
            }

        }

        setHasOptionsMenu(true);
        return view;
    }

    @OnClick(R.id.btShareRaw)
    public void onShareRawData() {
        if (mRawLocationDataText != null && !mRawLocationDataText.isEmpty()) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mRawLocationDataText);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } else {
            Toast.makeText(getActivity(), R.string.noRawData, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_result, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionShare) {
            String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), mScreenShot, "Мой маршрут", null);
            Uri uri = Uri.parse(path);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "Время: " + tvTime.getText() + "\nРасстояние: " + tvDistance.getText());
            startActivity(Intent.createChooser(intent, "Результаты маршрута"));
        }

        return super.onOptionsItemSelected(item);
    }
}
