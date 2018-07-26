package com.elegion.tracktor.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.R;
import com.elegion.tracktor.event.AddPointToRouteEvent;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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


    public CounterViewModel() {
        EventBus.getDefault().register(this);
    }

    public void startTimer() {
        startEnabled.postValue(false);
        stopEnabled.postValue(true);
        mRoute.clear();
        mDistance.postValue(0.0);
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> onTimerUpdate(seconds.intValue()));
    }

    public void stopTimer() {
        startEnabled.postValue(true);
        stopEnabled.postValue(false);
        timerDisposable.dispose();
    }

    private void onTimerUpdate(int totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        timeText.setValue(String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPointToRouteEvent(AddPointToRouteEvent event) {
        //mDistanceText.postValue(event.location.toString());
        mRoute.add(event.location);
        if(mRoute.size() >= 2) {
            Double distance = mDistance.getValue();
//            distance = SphericalUtil.computeLength(mRoute);
            distance = distance +
                    SphericalUtil.computeDistanceBetween(mRoute.get(mRoute.size()-1),
                            mRoute.get(mRoute.size()-2));
            mDistance.postValue(distance);
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