package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();
    private MutableLiveData<String> timeText = new MutableLiveData<>();
    private MutableLiveData<String> mDistanceText = new MutableLiveData<>();
    private SingleObserver mPermissionObserver;
    private Single<Boolean> mIsPermissionGranted = new Single<Boolean>() {
        @Override
        protected void subscribeActual(SingleObserver<? super Boolean> observer) {
            mPermissionObserver = observer;
        }
    };

//    {
//        @Override
//        protected void subscribeActual(Observer<? super Boolean> observer) {
//            mPermissionObserver = observer;
//        }
//    };

    private boolean isRouteStart;


    public MainViewModel() {
       // mIsPermissionGranted.setValue(false);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerUpdate(TimerUpdateEvent event) {
        timeText.postValue(StringUtils.getTimerText(event.seconds));
        mDistanceText.postValue(StringUtils.getDistanceText(event.distance));
        if (!isRouteStart) {
            startRoute();
        }
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
        //mIsPermissionGranted.setValue(true);
        mPermissionObserver.onSuccess(true);
       // mIsPermissionGranted.first(true);
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
}