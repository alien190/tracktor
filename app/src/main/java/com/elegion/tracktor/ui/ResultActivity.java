package com.elegion.tracktor.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ResultActivity extends SingleFragmentActivity {
    @Override
    protected Fragment getFragment() {
        Bundle args = getIntent().getExtras();
        return ResultFragment.newInstance(args);
    }

}
