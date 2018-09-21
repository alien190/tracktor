package com.elegion.tracktor.ui.result;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.common.ICommentViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import toothpick.Scope;
import toothpick.Toothpick;

public class CommentDialogFragment extends DialogFragment {

    @Inject
    ICommentViewModel mViewModel;
    @BindView(R.id.etComment)
    EditText edComment;

    public static CommentDialogFragment newInstance() {

        Bundle args = new Bundle();

        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fr_comment_dialog, null);
        ButterKnife.bind(this, view);
        //toothpickInject();
        mViewModel.getComment().observe(this, edComment::setText);

        builder.setView(view)
                .setPositiveButton(R.string.btn_save_label,
                        (dialogInterface, i) ->
                                mViewModel.getComment().postValue(edComment.getText().toString()))
                .setNegativeButton(R.string.btn_cancel_label, null)
                .setTitle(R.string.comment_dialog_title);

        return builder.create();
    }

    private void toothpickInject() {
        Scope scope = Toothpick.openScope("ResultDetail");
        Toothpick.inject(this, scope);
    }
}
