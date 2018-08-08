package com.elegion.tracktor.ui.result;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.common.SingleFragmentActivity;

public class ResultActivity extends SingleFragmentActivity {

    //    public static final String STOP_ROUTE_EVENT_KEY = "StopRouteEventKey";
//    public static final String SCREENSHOT_KEY = "ScreenShotKey";
    public static final String ID_KEY = "IdKey";
    public static final Long ID_LIST = -1L;

    @Override
    protected Fragment getFragment() {
        Bundle args = getIntent().getExtras();
        long id = args != null ? args.getLong(ID_KEY, ID_LIST) : ID_LIST;

        if (id == ID_LIST) {
            return ResultFragment.newInstance();
        } else {
            return ResultDetailsFragment.newInstance(id);
        }
    }

    public static void start(Context context, long id) {
        Intent intent = new Intent(context, ResultActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(STOP_ROUTE_EVENT_KEY, event);
//        bundle.putString(SCREENSHOT_KEY, ScreenshotMaker.toBase64(bitmap));
//        intent.putExtras(bundle);
        intent.putExtra(ID_KEY, id);
        context.startActivity(intent);
    }

}
