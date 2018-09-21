package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;

import java.util.List;

public class ResultViewModel extends ViewModel {
    private IRepository<Track> mRepository;
    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();

    public ResultViewModel(IRepository<Track> repository) {
        mRepository = repository;
        mTracks.observeForever(list -> isEmpty.postValue(list!=null && list.isEmpty()));
    }

    public void loadTracks() {
        mTracks.postValue(mRepository.getAll());
    }

    public MutableLiveData<List<Track>> getTracks() {
        return mTracks;
    }

    public MutableLiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }
}
