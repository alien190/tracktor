package com.elegion.tracktor.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.LinearLayout;

import com.elegion.tracktor.common.RawLocationData;
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
    //private LatLng mLastPoint;
    private List<RawLocationData> mRawLocationData = new ArrayList<>();
    private int mTotalSecond;


    public CounterViewModel() {
        EventBus.getDefault().register(this);
    }

    public void startTimer() {

        startEnabled.postValue(false);
        stopEnabled.postValue(true);

        mRawLocationData.clear();
        mRoute.clear();
        mDistance.postValue(0.0);
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> onTimerUpdate(seconds.intValue()));
        EventBus.getDefault().post(new StartRouteEvent());
    }

    public void stopTimer() {
        EventBus.getDefault().post(new StopRouteEvent(timeText.getValue(), mDistance.getValue(), mRawLocationData));
        startEnabled.postValue(true);
        stopEnabled.postValue(false);
        timerDisposable.dispose();
    }

    private void onTimerUpdate(int totalSeconds) {
        mTotalSecond = totalSeconds;
        timeText.setValue(StringUtils.getTimerText(totalSeconds));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewPointFromLocationClientEvent(NewPointFromLocationClientEvent event) {
        //mLastPoint = event.location;
        mRawLocationData.add(new RawLocationData(event.location, mTotalSecond));

        //mDistanceText.postValue(event.location.toString());
//        mRoute.add(event.location);
//        if (mRoute.size() >= 2) {
//            Double distance = mDistance.getValue();
////            distance = SphericalUtil.computeLength(mRoute);
//            distance = distance +
//                    SphericalUtil.computeDistanceBetween(mRoute.get(mRoute.size() - 1),
//                            mRoute.get(mRoute.size() - 2));
//            mDistance.postValue(distance);
//        }
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