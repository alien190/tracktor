package com.elegion.tracktor.di.application;

import android.content.SharedPreferences;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Provider;

public class MessageTemplateProvider implements Provider<MessageTemplate> {

    private SharedPreferences mSharedPreferences;
    private Gson mGson;
    private CurrentPreferences mCurrentPreferences;

    @Inject
    public MessageTemplateProvider(SharedPreferences sharedPreferences, Gson gson, CurrentPreferences currentPreferences) {
        mSharedPreferences = sharedPreferences;
        mGson = gson;
        mCurrentPreferences = currentPreferences;
    }

    @Override
    public MessageTemplate get() {
        return new MessageTemplate(mSharedPreferences, mGson, mCurrentPreferences);
    }
}
