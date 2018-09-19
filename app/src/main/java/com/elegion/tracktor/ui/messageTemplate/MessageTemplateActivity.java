package com.elegion.tracktor.ui.messageTemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.di.messageTemplate.MessageTemplateModule;
import com.elegion.tracktor.ui.common.SingleFragmentActivity;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

public class MessageTemplateActivity extends SingleFragmentActivity {
    @Inject
    MessageTemplateFragment mMessageTemplateFragment;

    @Override
    protected Fragment getFragment() {
        Scope scope = Toothpick.openScopes("Application", "MessageTemplate");
        scope.installModules(new MessageTemplateModule());
        Toothpick.inject(this, scope);
        return mMessageTemplateFragment;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MessageTemplateActivity.class);
        context.startActivity(intent);
    }
}
