package com.elegion.tracktor.di.messageTemplate;

import android.content.SharedPreferences;

import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Provider;

public class MessageTemplateProvider implements Provider<MessageTemplate> {

    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    @Inject
    public MessageTemplateProvider(SharedPreferences sharedPreferences, Gson gson) {
        mSharedPreferences = sharedPreferences;
        mGson = gson;
    }

    @Override
    public MessageTemplate get() {
        return new MessageTemplate(mSharedPreferences, mGson);
    }
}
