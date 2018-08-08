package com.elegion.tracktor.ui.result;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.common.SingleFragmentActivity;
import com.elegion.tracktor.common.event.ShowResultDetailEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ResultActivity extends SingleFragmentActivity {

    public static final String ID_KEY = "IdKey";
    public static final Long ID_LIST = -1L;

    @Override
    protected Fragment getFragment() {
        long id = getIntent().getLongExtra(ID_KEY, ID_LIST);

        if (id == ID_LIST) {
            return ResultFragment.newInstance();
        } else {
            return ResultDetailsFragment.newInstance(id);
        }
    }

    public static void start(Context context, long id) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(ID_KEY, id);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showResultDetailsFragment(ShowResultDetailEvent event) {
        changeFragment(ResultDetailsFragment.newInstance(event.id));
    }
}
