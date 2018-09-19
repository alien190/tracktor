package com.elegion.tracktor.ui.prefs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.ui.common.SingleFragmentActivity;

public class PreferenceActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return MainPreferenceFragment.newInstance();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PreferenceActivity.class);
        context.startActivity(intent);
    }
}
