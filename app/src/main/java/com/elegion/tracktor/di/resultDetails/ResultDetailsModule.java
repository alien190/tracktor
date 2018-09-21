package com.elegion.tracktor.di.resultDetails;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.elegion.tracktor.di.result.ResultViewModelProvider;
import com.elegion.tracktor.ui.common.ICommentViewModel;
import com.elegion.tracktor.ui.result.CommentDialogFragment;
import com.elegion.tracktor.ui.result.ResultDetailsViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModelFactory;
import com.elegion.tracktor.ui.result.ResultViewModel;

import toothpick.config.Module;

public class ResultDetailsModule extends Module {
    private Fragment mFragment;
    private long mId;

    public ResultDetailsModule(Fragment fragment, long id) {
        mFragment = fragment;
        mId = id;
        bind(Fragment.class).toInstance(mFragment);
        bind(Long.class).withName("TRACK_ID").toInstance(mId);
        bind(ResultDetailsViewModel.class).toProvider(ResultDetailsViewModelProvider.class).providesSingletonInScope();
        bind(ICommentViewModel.class).toProvider(ResultDetailsViewModelProvider.class).providesSingletonInScope();
        bind(ResultDetailsViewModelFactory.class).toProvider(ResultDetailsViewModelFactoryProvider.class).providesSingletonInScope();
    }
}
