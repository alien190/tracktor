package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.MutableLiveData;

public interface ICommentViewModel {
    MutableLiveData<String> getComment();
}
