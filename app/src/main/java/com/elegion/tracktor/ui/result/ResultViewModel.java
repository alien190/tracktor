package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;

import java.util.List;

public class ResultViewModel extends ViewModel {
    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();

    public ResultViewModel(IRepository<Track> repository) {
        mTracks.postValue(repository.getAll());
    }

    public MutableLiveData<List<Track>> getTracks() {
        return mTracks;
    }
}
