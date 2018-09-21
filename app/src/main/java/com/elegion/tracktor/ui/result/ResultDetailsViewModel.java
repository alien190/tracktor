package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import toothpick.Toothpick;

public class ResultDetailsViewModel extends ViewModel {
    private MutableLiveData<String> mStartDate = new MutableLiveData<>();
    private MutableLiveData<String> mDistance = new MutableLiveData<>();
    private MutableLiveData<String> mAverageSpeed = new MutableLiveData<>();
    private MutableLiveData<String> mDuration = new MutableLiveData<>();
    private MutableLiveData<String> mScreenShotBase64 = new MutableLiveData<>();
    private MutableLiveData<Integer> mAction = new MutableLiveData<>();
    private MutableLiveData<String> mCalories = new MutableLiveData<>();
    private MutableLiveData<String> mComment = new MutableLiveData<>();

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
        EventBus.getDefault().register(this);
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
            mComment.postValue(mTrack.getComment());
            mComment.observeForever(this::updateComment);
            mAction.observeForever(this::updateTrackAction);
            calculateCalories();
        }

    }

    public void updateTrackAction(Integer action) {
        if (mTrack.getAction() != action) {
            mTrack.setAction(action);
            mRepository.updateItem(mTrack);
            calculateCalories();
        }
    }

    public void updateComment(String comment) {
        if (mTrack.getComment() == null || comment == null || !mTrack.getComment().equals(comment)) {
            mTrack.setComment(comment);
            mRepository.updateItem(mTrack);
        }
    }

    public void updateCalories(double calories) {
        if (mTrack.getCalories() != calories) {
            mTrack.setCalories(calories);
            mRepository.updateItem(mTrack);
        }
    }

    private void calculateCalories() {

        double calories;

        switch (mTrack.getAction()) {
            case 0: { //ходьба
                calories = 0.035 * mCurrentPreferences.getWeight()
                        + Math.pow(mTrack.getAverageSpeed(), 2) /
                        mCurrentPreferences.getHeight() * 2.9 *
                        mCurrentPreferences.getWeight();
                break;
            }
            case 1: { //бег
                calories = mCurrentPreferences.getWeight() * mTrack.getDistance() / 1000;
                break;
            }
            case 2: { //велосипед
                calories = mCurrentPreferences.getWeight() / 70 * 300 * mTrack.getDuration() / 3600;
                break;
            }
            default: { //автомобиль и др.
                calories = mCurrentPreferences.getWeight() / 70 * 70 * mTrack.getDuration() / 3600;
                break;
            }
        }

        mCalories.postValue(StringUtils.getCaloriesText(calories));
        updateCalories(calories);
    }

    public void deleteTrack() {
        mRepository.deleteItem(mId);
    }

    @Override
    protected void onCleared() {
        Toothpick.closeScope("ResultDetail");
        EventBus.getDefault().unregister(this);
        super.onCleared();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCurentPreferencesChande(PreferencesChangeEvent preferencesChangeEvent) {
        calculateCalories();
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

    public MutableLiveData<String> getCalories() {
        return mCalories;
    }

    public MutableLiveData<String> getComment() {
        return mComment;
    }
}
