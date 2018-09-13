package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.ShowResultDetailEvent;
import com.elegion.tracktor.data.RealmRepository;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultFragment extends Fragment {

    @BindView(R.id.rvTrackList)
    public RecyclerView mRvTrackList;

    private ResultViewModel mResultViewModel;
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
        View view = inflater.inflate(R.layout.fr_result, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mAdapter = new ResultAdapter();
        CustomViewModelFactory factory = new CustomViewModelFactory(new RealmRepository());
        mResultViewModel = ViewModelProviders.of(this, factory).get(ResultViewModel.class);
        mResultViewModel.loadTracks();
        mResultViewModel.getTracks().observe(this, tracks -> mAdapter.submitList(tracks));
        mRvTrackList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvTrackList.setAdapter(mAdapter);
    }


}
