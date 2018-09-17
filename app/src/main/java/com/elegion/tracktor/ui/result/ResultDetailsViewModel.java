package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.StringUtils;

import toothpick.Toothpick;

public class ResultDetailsViewModel extends ViewModel {
    private MutableLiveData<String> mStartDate = new MutableLiveData<>();
    private MutableLiveData<String> mDistance = new MutableLiveData<>();
    private MutableLiveData<String> mAverageSpeed = new MutableLiveData<>();
    private MutableLiveData<String> mDuration = new MutableLiveData<>();
    private MutableLiveData<String> mScreenShotBase64 = new MutableLiveData<>();
    private MutableLiveData<Integer> mAction = new MutableLiveData<>();

    private CurrentPreferences mCurrentPreferences;
    private IRepository<Track> mRepository;
    private Long mId;
    private Track mTrack;

    public ResultDetailsViewModel(IRepository<Track> repository,
                                  CurrentPreferences currentPreferences,
                                  Long id) {
        mCurrentPreferences = currentPreferences;
        mRepository = repository;
        mId = id;
    }

    public void loadTrack() {
        mTrack = mRepository.getItem(mId);
        if (mTrack != null) {
            mStartDate.postValue(StringUtils.getDateText(mTrack.getDate()));
            mScreenShotBase64.postValue(mTrack.getImage());
            mDuration.postValue(StringUtils.getDurationText(mTrack.getDuration()));
            mAverageSpeed.postValue(StringUtils.getSpeedText(mTrack.getAverageSpeed()));
            mAction.postValue(mTrack.getAction());
            mDistance.postValue(StringUtils.getDistanceText(mTrack.getDistance()));
            mAction.observeForever(this::updateTrackAction);
        }

    }

    public void updateTrackAction(Integer action) {
        if (mTrack.getAction() != action) {
            mTrack.setAction(action);
            mRepository.updateItem(mTrack);
        }
    }

    public void deleteTrack() {
        mRepository.deleteItem(mId);
    }

    @Override
    protected void onCleared() {
        Toothpick.closeScope("ResultDetail");
        super.onCleared();
    }

    public MutableLiveData<String> getStartDate() {
        return mStartDate;
    }

    public MutableLiveData<String> getDistance() {
        return mDistance;
    }

    public MutableLiveData<String> getAverageSpeed() {
        return mAverageSpeed;
    }

    public MutableLiveData<String> getDuration() {
        return mDuration;
    }

    public MutableLiveData<String> getScreenShotBase64() {
        return mScreenShotBase64;
    }

    public MutableLiveData<Integer> getAction() {
        return mAction;
    }
}
