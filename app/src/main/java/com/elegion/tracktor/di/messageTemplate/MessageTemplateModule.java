package com.elegion.tracktor.di.messageTemplate;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.elegion.tracktor.ui.common.CustomLayoutManager;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplateFragment;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplateListAdapter;

import toothpick.config.Module;

public class MessageTemplateModule extends Module {
    private MessageTemplateFragment mMessageTemplateFragment;
    private CustomLayoutManager mCustomLayoutManager;

    public MessageTemplateModule() {
        mMessageTemplateFragment = MessageTemplateFragment.newInstance();
        mCustomLayoutManager = new CustomLayoutManager();

        bind(MessageTemplate.class).toProvider(MessageTemplateProvider.class).providesSingletonInScope();
        bind(MessageTemplateListAdapter.class).toProvider(MessageTemplateListAdapterProvider.class).providesSingletonInScope();
        bind(MessageTemplateFragment.class).toInstance(mMessageTemplateFragment);
        bind(CustomLayoutManager.class).toInstance(mCustomLayoutManager);
        bind(ItemTouchHelper.Callback.class).toProvider(ItemTouchCallbackProvider.class).providesSingletonInScope();
        bind(ItemTouchHelper.class).toProvider(ItemTouchHelperProvider.class).providesSingletonInScope();
    }
}
