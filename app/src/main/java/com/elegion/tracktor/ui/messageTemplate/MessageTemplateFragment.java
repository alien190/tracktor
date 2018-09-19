package com.elegion.tracktor.ui.messageTemplate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.MessageTemplateUpdateEvent;
import com.elegion.tracktor.di.messageTemplate.MessageTemplateModule;
import com.elegion.tracktor.ui.common.CustomLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import toothpick.Scope;
import toothpick.Toothpick;

public class MessageTemplateFragment extends Fragment implements MessageTemplateListAdapter.IOnEditItemListener {

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.tvPreview)
    TextView mTvPreview;
    private View mView;

    @Inject
    protected MessageTemplate mMessageTemplate;
    @Inject
    protected MessageTemplateListAdapter mAdapter;
    @Inject
    protected CustomLayoutManager mCustomLayoutManager;
    @Inject
    protected ItemTouchHelper mItemTouchHelper;

    public static MessageTemplateFragment newInstance() {
        Bundle args = new Bundle();
        MessageTemplateFragment fragment = new MessageTemplateFragment();
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    public MessageTemplateFragment() {
    }

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, mView);

            Scope scope = Toothpick.openScopes("Application", "MessageTemplate");
            scope.installModules(new MessageTemplateModule());
            Toothpick.inject(this, scope);

            mMessageTemplate.load()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(b -> showPreview());

            mAdapter.setIOnEditItemListener(this);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mCustomLayoutManager);
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);

            setHasOptionsMenu(true);
        }
        return mView;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_template_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itmText) {
            return mAdapter.addItem(MessageTemplateListAdapter.ITEM_TYPE_TEXT);
        }
        return mAdapter.addItem(MessageTemplateListAdapter.ITEM_TYPE_PARAMETER);
    }

    @Override
    public void onEditTextItem(final int pos) {
        String text = "";
        if (pos != MessageTemplateListAdapter.NEW_ITEM) {
            text = mAdapter.getItemText(pos);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit_text_title)
                .setView(R.layout.dialog_text)
                .setPositiveButton(R.string.save_btn, null)
                .setNegativeButton(R.string.cancel_btn, null)
                .create();
        alertDialog.show();

        final EditText editText = alertDialog.findViewById(R.id.etText);
        editText.setText(text);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "", (dialogInterface, i) -> {
            String text1 = editText.getText().toString();
            if (pos == MessageTemplateListAdapter.NEW_ITEM) {
                mAdapter.addTextItem(text1);
            } else {
                mAdapter.updateTextItem(pos, text1);
            }
        });
    }

    @Override
    public void onEditParameterItem(final int pos) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit_parameter_dialog_title)
                .setNegativeButton(R.string.cancel_btn, null)
                .setItems(mAdapter.getParametersTypes(), (dialogInterface, type) -> {
                    if (pos == MessageTemplateListAdapter.NEW_ITEM) {
                        mAdapter.addParameterItem(type);
                    } else {
                        mAdapter.updateParameterItem(pos, type);
                    }
                    dialogInterface.dismiss();
                })
                .create();
        alertDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageTemplateUpdate(MessageTemplateUpdateEvent event) {
        showPreview();
    }

    private void showPreview() {
        mTvPreview.setText(mMessageTemplate.getMessage(null));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

}
