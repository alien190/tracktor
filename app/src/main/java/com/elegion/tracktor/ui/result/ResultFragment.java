package com.elegion.tracktor.ui.result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.TrackCommentEditEvent;
import com.elegion.tracktor.common.event.TrackDeleteEvent;
import com.elegion.tracktor.common.event.TrackShareEvent;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.ScreenshotMaker;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import toothpick.Scope;
import toothpick.Toothpick;

public class ResultFragment extends Fragment {

    @BindView(R.id.rvTrackList)
    public RecyclerView mRvTrackList;
    @BindView(R.id.rl_stub)
    RelativeLayout mRlStub;

    MenuItem mMiSortOrder;
    MenuItem mMiSortBy;

    @Inject
    protected ResultViewModel mResultViewModel;
    @Inject
    protected CurrentPreferences mCurrentPreferences;
    @Inject
    protected CommentDialogFragment mCommentDialogFragment;

    private ResultAdapter mAdapter;


    public static ResultFragment newInstance() {

        Bundle args = new Bundle();

        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Scope scope = Toothpick.openScope("Result");
        Toothpick.inject(this, scope);
        Toothpick.inject(mCommentDialogFragment, scope);

        View view = inflater.inflate(R.layout.fr_result, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mAdapter = new ResultAdapter(mCurrentPreferences);
        mResultViewModel.loadTracks();
        mResultViewModel.getTracks().observe(this, tracks -> mAdapter.submitList(tracks));
        mRvTrackList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvTrackList.setAdapter(mAdapter);
        mResultViewModel.getIsEmpty().observe(this, isEmpty -> {
            if (isEmpty) {
                mRlStub.setVisibility(View.VISIBLE);
                mRvTrackList.setVisibility(View.GONE);
            } else {
                mRlStub.setVisibility(View.GONE);
                mRvTrackList.setVisibility(View.VISIBLE);
            }
        });
        mResultViewModel.getSortOrder().observe(this, this::setSortOrderIcon);
        mResultViewModel.getSortBy().observe(this, this::setSortByIcon);
        setHasOptionsMenu(true);
    }

    private void setSortOrderIcon(int sortOrder) {
        if (mMiSortOrder != null) {
            if (sortOrder == ResultViewModel.SORT_ORDER_ASC) {
                mMiSortOrder.setIcon(R.drawable.ic_arrow_downward_white_24dp);
            } else {
                mMiSortOrder.setIcon(R.drawable.ic_arrow_upward_white_24dp);
            }
        }
    }

    private void setSortByIcon(int sortBy) {
        if (mMiSortBy != null) {
            switch (sortBy) {
                case ResultViewModel.SORT_BY_DISTANCE: {
                    mMiSortBy.setIcon(R.drawable.ic_distance_white_24dp);
                    break;
                }
                case ResultViewModel.SORT_BY_DURATION: {
                    mMiSortBy.setIcon(R.drawable.ic_timelapse_white_24dp);
                    break;
                }
                default: {
                    mMiSortBy.setIcon(R.drawable.ic_start_white_24dp);
                    break;
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_result, menu);
        mMiSortOrder = menu.findItem(R.id.miSortOrder);
        mMiSortBy = menu.findItem(R.id.miSortBy);
        setSortOrderIcon(mResultViewModel.getSortOrder().getValue());
        setSortByIcon(mResultViewModel.getSortBy().getValue());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miSortOrder: {
                mResultViewModel.changeSortOrder();
                return true;
            }
            case R.id.miSortBy: {
                mResultViewModel.changeSortBy();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onTrackCommentEdit(TrackCommentEditEvent editEvent) {
        mResultViewModel.setTrackIdForComment(editEvent.mId);
        mCommentDialogFragment.show(getActivity().getSupportFragmentManager(), "result");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onTrackDelete(TrackDeleteEvent event) {
        mResultViewModel.deleteTrack(event.trackId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onTrackShare(TrackShareEvent event) {
        doShare(mResultViewModel.getTrack(event.trackId));
    }

    //todo переделать это
    private void doShare(Track track) {
        if (track != null) {
            Bitmap screenShot = ScreenshotMaker.fromBase64(track.getImage());
            String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), screenShot, "Мой маршрут", null);
            Uri uri = Uri.parse(path);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            String extraText = "Начало трека :" + StringUtils.getDateText(track.getDate())
                    + "\nВремя: " + StringUtils.getDurationText(track.getDuration())
                    + "\nРасстояние: " + StringUtils.getDistanceText(track.getDistance())
                    + "\nСредняя скорость: " + StringUtils.getSpeedText(track.getAverageSpeed())
                    + "\nЗатрачено калорий: " + StringUtils.getCaloriesText(track.getCalories())
                    + "\nВид деятельности: " + mCurrentPreferences.getActions().get(track.getAction())
                    + "\nКомментарий: " + track.getComment();

            intent.putExtra(Intent.EXTRA_TEXT, extraText);
            startActivity(Intent.createChooser(intent, "Результаты маршрута"));
        }
    }
}
