package com.elegion.tracktor.di.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.lightSensor.LightSensor;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.ui.common.TrackSharing;
import com.elegion.tracktor.ui.result.CommentDialogFragment;
import com.elegion.tracktor.utils.DistanceConverter;
import com.elegion.tracktor.utils.IDistanceConverter;
import com.elegion.tracktor.utils.PicassoCropTransform;
import com.squareup.picasso.Transformation;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.google.gson.Gson;

import toothpick.config.Module;

public class ApplicationModule extends Module {
    private RealmRepository mRealmRepository = new RealmRepository();
    private CurrentPreferences mCurrentPreferences = new CurrentPreferences();
    private SharedPreferences mSharedPreferences;
    private Gson mGson = new Gson();
    private Context mContext;
    private LightSensor mLightSensor;

    public ApplicationModule(Context context) {
        mContext = context;
        mCurrentPreferences.init(mContext);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mLightSensor = provideLightSensor();

        bind(IRepository.class).toInstance(mRealmRepository);
        bind(CustomViewModelFactory.class).toProvider(CustomViewModelFactoryProvider.class).providesSingletonInScope();
        bind(CurrentPreferences.class).toInstance(mCurrentPreferences);
        bind(CommentDialogFragment.class).toInstance(CommentDialogFragment.newInstance());
        bind(Transformation.class).toInstance(new PicassoCropTransform());
        bind(SharedPreferences.class).toInstance(mSharedPreferences);
        bind(Gson.class).toInstance(mGson);
        bind(MessageTemplate.class).toProvider(MessageTemplateProvider.class).providesSingletonInScope();
        bind(IDistanceConverter.class).toProvider(DistanceConverterProvider.class).providesSingletonInScope();
        bind(Context.class).toInstance(mContext);
        bind(TrackSharing.class).toProvider(TrackSharingProvider.class).providesSingletonInScope();
        bind(LightSensor.class).toInstance(mLightSensor);
    }

    private LightSensor provideLightSensor() {
        return new LightSensor((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE));
    }
}
