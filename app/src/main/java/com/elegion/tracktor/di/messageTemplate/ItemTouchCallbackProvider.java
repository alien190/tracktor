package com.elegion.tracktor.di.messageTemplate;

import com.elegion.tracktor.ui.messageTemplate.MessageTemplateListAdapter;
import com.elegion.tracktor.ui.messageTemplate.helper.ItemTouchAdapter;
import com.elegion.tracktor.ui.messageTemplate.helper.ItemTouchCallback;

import javax.inject.Inject;
import javax.inject.Provider;

public class ItemTouchCallbackProvider implements Provider<ItemTouchCallback> {

   private MessageTemplateListAdapter mAdapter;

    @Inject
    public ItemTouchCallbackProvider(MessageTemplateListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public ItemTouchCallback get() {
        return new ItemTouchCallback(mAdapter);
    }
}
