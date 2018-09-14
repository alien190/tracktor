package com.elegion.tracktor.ui.result;

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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import toothpick.Scope;
import toothpick.Toothpick;

public class ResultFragment extends Fragment {

    @BindView(R.id.rvTrackList)
    public RecyclerView mRvTrackList;

    @Inject
    protected ResultViewModel mResultViewModel;
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

        View view = inflater.inflate(R.layout.fr_result, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mAdapter = new ResultAdapter();
        mResultViewModel.loadTracks();
        mResultViewModel.getTracks().observe(this, tracks -> mAdapter.submitList(tracks));
        mRvTrackList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvTrackList.setAdapter(mAdapter);
    }


}
