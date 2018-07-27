package com.elegion.tracktor.ui.map.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.common.KalmanRoute;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.NewPointFromLocationClientEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CounterViewModel extends ViewModel {
    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();
    private MutableLiveData<String> timeText = new MutableLiveData<>();
    private MutableLiveData<Double> mDistance = new MutableLiveData<>();
    private Disposable timerDisposable;
    private List<LatLng> mRoute = new ArrayList<>();
    private List<LocationData> mRawLocationData = new ArrayList<>();
    private int mTotalSecond;
    private KalmanRoute mKalmanRoute;


    public CounterViewModel() {
        EventBus.getDefault().register(this);
    }

    public void startTimer() {

        startEnabled.postValue(false);
        stopEnabled.postValue(true);

        mRawLocationData.clear();
        mRoute.clear();
        mKalmanRoute = new KalmanRoute();
        mDistance.postValue(0.0);
        mTotalSecond = 0;
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> onTimerUpdate(seconds.intValue()));
        EventBus.getDefault().post(new StartRouteEvent());
    }

    public void stopTimer() {
        StringBuilder locationDataBuilder = new StringBuilder();

        locationDataBuilder.append("Сырые данные:\n");
        locationDataBuilder.append(StringUtils.getLocationDataText(mRawLocationData));
        locationDataBuilder.append("Отфильтрованные данные:\n");
        locationDataBuilder.append(mKalmanRoute.toString());

        EventBus.getDefault().post(new StopRouteEvent(timeText.getValue(), mDistance.getValue(),
                locationDataBuilder.toString()));
        startEnabled.postValue(true);
        stopEnabled.postValue(false);
        timerDisposable.dispose();
    }

    private void onTimerUpdate(int totalSeconds) {
        mTotalSecond = totalSeconds;
        timeText.setValue(StringUtils.getTimerText(totalSeconds));
        if (mRawLocationData.size() != 0) {
            mKalmanRoute.onRouteUpdate(new LocationData(mRawLocationData.get(mRawLocationData.size() - 1),
                    totalSeconds));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewPointFromLocationClientEvent(NewPointFromLocationClientEvent event) {
        mRawLocationData.add(new LocationData(event.location, mTotalSecond));
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

    public MutableLiveData<Double> getDistance() {
        return mDistance;
    }

    @Override
    protected void onCleared() {

        EventBus.getDefault().unregister(this);

        timerDisposable.dispose();
        timerDisposable = null;
        super.onCleared();
    }
}