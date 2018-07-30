package com.elegion.tracktor.ui.result;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.common.SingleFragmentActivity;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.utils.ScreenshotMaker;

public class ResultActivity extends SingleFragmentActivity {

    public static final String STOP_ROUTE_EVENT_KEY = "StopRouteEventKey";
    public static final String SCREENSHOT_KEY = "ScreenShotKey";

    @Override
    protected Fragment getFragment() {
        Bundle args = getIntent().getExtras();
        return ResultFragment.newInstance(args);
    }

    public static void start(Context context, StopRouteEvent event, Bitmap bitmap){
        Intent intent = new Intent(context, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(STOP_ROUTE_EVENT_KEY, event);
        bundle.putString(SCREENSHOT_KEY, ScreenshotMaker.toBase64(bitmap));
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
