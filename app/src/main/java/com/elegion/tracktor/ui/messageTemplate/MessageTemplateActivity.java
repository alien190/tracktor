package com.elegion.tracktor.ui.messageTemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.ui.common.SingleFragmentActivity;

public class MessageTemplateActivity extends SingleFragmentActivity {
    @Override
    protected Fragment getFragment() {
        return MessageTemplateFragment.newInstance();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MessageTemplateActivity.class);
        context.startActivity(intent);
    }
}
