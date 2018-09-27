package com.elegion.tracktor.di.messageTemplate;

import android.content.SharedPreferences;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplateListAdapter;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Provider;

public class MessageTemplateListAdapterProvider implements Provider<MessageTemplateListAdapter> {

   private MessageTemplate mMessageTemplate;
   private CurrentPreferences mCurrentPreferences;

    @Inject
    public MessageTemplateListAdapterProvider(MessageTemplate messageTemplate, CurrentPreferences currentPreferences) {
        mMessageTemplate = messageTemplate;
        mCurrentPreferences = currentPreferences;
    }

    @Override
    public MessageTemplateListAdapter get() {
        return new MessageTemplateListAdapter(mMessageTemplate, mCurrentPreferences);
    }
}
