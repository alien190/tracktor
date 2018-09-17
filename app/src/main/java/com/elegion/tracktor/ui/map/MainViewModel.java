package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.common.event.ShutdownEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import io.reactivex.Single;
import io.reactivex.SingleObserver;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsShutdown = new MutableLiveData<>();
    private MutableLiveData<String> timeText = new MutableLiveData<>();
    private MutableLiveData<String> mDistanceText = new MutableLiveData<>();
    private MutableLiveData<String> mAverageSpeedText = new MutableLiveData<>();
    private long mTotalTime;
    private double mDistance;
    private double mAverageSpeed;
    private Date mStartDate;
    private SingleObserver mPermissionObserver;
    private Single<Boolean> mIsPermissionGranted = new Single<Boolean>() {
        @Override
        protected void subscribeActual(SingleObserver<? super Boolean> observer) {
            mPermissionObserver = observer;
        }
    };
    private boolean isRouteStart;
    IRepository mRealmRepository;


    public MainViewModel(IRepository repository) {
        // mIsPermissionGranted.setValue(false);
        EventBus.getDefault().register(this);
        mRealmRepository = repository;
        mIsShutdown.postValue(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerUpdate(TimerUpdateEvent event) {
        mTotalTime = event.seconds;
        mDistance = event.distance;
        mAverageSpeed = event.averageSpeed;
        mStartDate = event.startDate;

        timeText.postValue(StringUtils.getDurationText(mTotalTime));
        mDistanceText.postValue(StringUtils.getDistanceText(mDistance));
        mAverageSpeedText.postValue(StringUtils.getSpeedText(mAverageSpeed));
        if (!isRouteStart) {
            startRoute();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShutdown(ShutdownEvent event) {
        mIsShutdown.postValue(true);
        stopRoute();
    }

    public void startRoute() {
        isRouteStart = true;
        startEnabled.postValue(false);
        stopEnabled.postValue(true);
    }

    public void stopRoute() {
        isRouteStart = false;
        startEnabled.postValue(true);
        stopEnabled.postValue(false);
    }

    public void onPermissionGranted() {
        if (mPermissionObserver != null) {
            mPermissionObserver.onSuccess(true);
        } else {
            mIsPermissionGranted = Single.just(true);
        }
    }

    public MutableLiveData<String> getTimeText() {
        return timeText;
    }

    public MutableLiveData<Boolean> getStartEnabled() {
        return startEnabled;
    }

    public MutableLiveData<Boolean> getStopEnabled() {
        return stopEnabled;
    }

    public MutableLiveData<String> getDistanceText() {
        return mDistanceText;
    }

    @Override
    protected void onCleared() {
        EventBus.getDefault().unregister(this);
        super.onCleared();
    }

    public Single<Boolean> getIsPermissionGranted() {
        return mIsPermissionGranted;
    }

    public long saveResults(String imageBase64) {
        return mRealmRepository.createTrackAndSave(mTotalTime, mDistance, mAverageSpeed, mStartDate, imageBase64);
    }

    public MutableLiveData<Boolean> getIsShutdown() {
        return mIsShutdown;
    }

    public MutableLiveData<String> getAverageSpeedText() {
        return mAverageSpeedText;
    }
}