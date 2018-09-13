package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;

import java.util.List;

public class ResultViewModel extends ViewModel {
    IRepository<Track> mRepository;
    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();

    public ResultViewModel(IRepository<Track> repository) {
        mRepository = repository;
    }

    public void loadTracks() {
        mTracks.postValue(mRepository.getAll());
    }

    public MutableLiveData<List<Track>> getTracks() {
        return mTracks;
    }
}
