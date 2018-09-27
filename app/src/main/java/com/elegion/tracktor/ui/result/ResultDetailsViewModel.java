package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.ui.common.ICommentViewModel;
import com.elegion.tracktor.utils.CommonUtils;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import toothpick.Toothpick;

public class ResultDetailsViewModel extends ViewModel implements ICommentViewModel {
    private MutableLiveData<String> mStartDate = new MutableLiveData<>();
    private MutableLiveData<String> mDistance = new MutableLiveData<>();
    private MutableLiveData<Double> mAverageSpeed = new MutableLiveData<>();
    private MutableLiveData<String> mDuration = new MutableLiveData<>();
    private MutableLiveData<String> mScreenShotBase64 = new MutableLiveData<>();
    private MutableLiveData<Integer> mAction = new MutableLiveData<>();
    private MutableLiveData<String> mCalories = new MutableLiveData<>();
    private MutableLiveData<String> mComment = new MutableLiveData<>();
    private MutableLiveData<String> mTemperature = new MutableLiveData<>();
    private MutableLiveData<String> mWeatherIcon = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsShowWeather = new MutableLiveData<>();

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
            mAverageSpeed.postValue(mTrack.getAverageSpeed());
            mAction.postValue(mTrack.getAction());
            mDistance.postValue(StringUtils.getDistanceText(mTrack.getDistance()));
            mComment.postValue(mTrack.getComment());
            mComment.observeForever(this::updateComment);
            mAction.observeForever(this::updateTrackAction);
            mTemperature.postValue(StringUtils.getTemperatureText(mTrack.getTemperature()));
            mWeatherIcon.postValue(mTrack.getWeatherIcon());
            mWeatherIcon.observeForever(icon ->
                    mIsShowWeather.postValue(icon != null && !icon.isEmpty()));
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
        double calories = CommonUtils.calculateCalories(mTrack, mCurrentPreferences);
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

    public MutableLiveData<Double> getAverageSpeed() {
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

    public MutableLiveData<String> getComment(long trackId) {
        return mComment;
    }

    public MutableLiveData<String> getTemperature() {
        return mTemperature;
    }

    public MutableLiveData<String> getWeatherIcon() {
        return mWeatherIcon;
    }
}
