package com.elegion.tracktor.di.application;

import android.content.Context;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.ui.result.CommentDialogFragment;
import com.elegion.tracktor.utils.PicassoCropTransform;
import com.squareup.picasso.Transformation;

import toothpick.config.Module;

public class ApplicationModule extends Module {
    private RealmRepository mRealmRepository = new RealmRepository();
    private CurrentPreferences mCurrentPreferences = new CurrentPreferences();

    public ApplicationModule(Context context) {
        mCurrentPreferences.init(context);

        bind(IRepository.class).toInstance(mRealmRepository);
        bind(CustomViewModelFactory.class).toProvider(CustomViewModelFactoryProvider.class).providesSingletonInScope();
        bind(CurrentPreferences.class).toInstance(mCurrentPreferences);
        bind(CommentDialogFragment.class).toInstance(CommentDialogFragment.newInstance());
        bind(Transformation.class).toInstance(new PicassoCropTransform());
    }
}
